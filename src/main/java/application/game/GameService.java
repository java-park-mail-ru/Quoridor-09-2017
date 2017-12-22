package application.game;

import application.dao.UserService;
import application.game.logic.Point;
import application.game.messages.Coordinates;
import application.game.messages.InfoMessage;
import application.websocket.GameSocketService;
import application.websocket.HandleExeption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Service
public class GameService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    @NotNull
    private ConcurrentLinkedQueue<Long> waiters = new ConcurrentLinkedQueue<>();

    @NotNull
    private ConcurrentHashMap<Long, List<Point>> tasks = new ConcurrentHashMap<>();

    @NotNull
    private ConcurrentHashMap<Long, ScheduledFuture> timers = new ConcurrentHashMap<>();

    @NotNull
    private static final int THREAD_COUNT = 10;

    @NotNull
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_COUNT);

    @NotNull
    private final UserService userService;

    @NotNull
    private final GameSocketService gameSocketService;

    @NotNull
    private final GameSessionService gameSessionService;

    @NotNull
    private final ConcurrentHashMap<Long, Integer> anticipatedSteps = new ConcurrentHashMap<>();

    public ConcurrentHashMap<Long, Integer> getAnticipatedSteps() {
        return anticipatedSteps;
    }

    public GameService(UserService userService,
                       GameSocketService gameSocketService,
                       GameSessionService gameSessionService) {
        this.userService = userService;
        this.gameSocketService = gameSocketService;
        this.gameSessionService = gameSessionService;
    }

    public void addUser(@NotNull Long userId) {
        if (gameSessionService.isPlaying(userId)) {
            return;
        }
        waiters.add(userId);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("User with id " + userId + " added to the waiting list");
        }
        tryStartGame();
    }

    public void addPoints(@NotNull Long userId, @NotNull Coordinates coordinates) {
        if (!gameSessionService.isPlaying(userId)) {
            return;
        }
        final List<Point> points;
        try {
            points = coordinates.fromIntArrayToPointList();
        } catch (HandleExeption e) {
            try {
                final InfoMessage infoMessage = new InfoMessage();
                infoMessage.setMessage("repeat");
                gameSocketService.sendMessageToUser(userId, infoMessage);
            } catch (IOException ex) {
                LOGGER.warn("Failed to send RepeatGameMessage to user " + userId, e, ex);
            }
            return;
        }
        tasks.put(userId, points);
        gameStep();
    }

    private void setTimer(Long prevUserId, Long newUserId) {
        final ScheduledFuture oldTimer = timers.remove(prevUserId);
        if (oldTimer != null) {
            oldTimer.cancel(false);
        }
        final int stepCount;
        try {
            stepCount = gameSessionService.getGameSession(newUserId).getStepCount();
        } catch (NullPointerException e) {
            LOGGER.info("GameSession was already closed");
            return;
        }
        final Runnable task = () -> {
            final GameSession gameSession = gameSessionService.getGameSession(newUserId);
            if (gameSession != null && gameSession.compareAndSetStepCount(stepCount, stepCount + 1)) {
                if (Objects.equals(gameSession.getFirstUserId(), newUserId)) {
                    gameSession.setFirstResult(false);
                    gameSession.setSecondResult(true);
                    userService.increaseScore(gameSession.getSecondUserId());
                } else {
                    gameSession.setFirstResult(true);
                    gameSession.setSecondResult(false);
                    userService.increaseScore(gameSession.getFirstUserId());
                }
                gameSessionService.finishGame(gameSession);
                gameSessionService.forceTerminate(gameSession, false);
            }
        };
        timers.put(newUserId, executorService.schedule(task, 1, TimeUnit.MINUTES));
    }

    private void tryStartGame() {
        final Set<Long> matchedPlayers = new LinkedHashSet<>();
        while (waiters.size() >= 2 || waiters.size() >= 1 && matchedPlayers.size() >= 1) {
            final Long candidate = waiters.poll();
            if (!insureCandidate(candidate)) {
                continue;
            }
            matchedPlayers.add(candidate);
            if (matchedPlayers.size() == 2) {
                final Iterator<Long> iterator = matchedPlayers.iterator();
                final Long userId1 = iterator.next();
                final Long userId2 = iterator.next();
                anticipatedSteps.put(userId1, 0);
                anticipatedSteps.put(userId2, 0);
                gameSessionService.startGame(userId1, userId2);
                final Long waiter = gameSessionService.getGameSession(userId1).getWaiter();
                setTimer(waiter, gameSessionService.getGameSession(waiter).getAnotherPlayer(waiter));
                matchedPlayers.clear();
            }
        }
        waiters.addAll(matchedPlayers);
    }

    private boolean insureCandidate(@NotNull Long candidate) {
        return gameSocketService.isConnected(candidate)
                && userService.getUserById(candidate) != null;
    }

    //visible for testing
    void putCoordinates(@NotNull Long userId, @NotNull Coordinates coordinates) {
        try {
            tasks.put(userId, coordinates.fromIntArrayToPointList());
        } catch (HandleExeption ignored) {
            LOGGER.error(ignored.getMessage());
        }
    }

    @SuppressWarnings("OverlyComplexMethod")
    public Map<Long, List<Point>> gameStep() {
        final Map<Long, List<Point>> messagesToSend = new HashMap<>();
        final Set<Long> users = tasks.keySet();
        for (Long curUser : users) {
            final AbstractMap.SimpleEntry<Long, List<Point>> messageToSend = gameSessionService.handleTask(
                    curUser, tasks.remove(curUser), anticipatedSteps.get(curUser));
            if (messageToSend != null) {
                anticipatedSteps.put(curUser, anticipatedSteps.get(curUser) + 1);
                anticipatedSteps.put(messageToSend.getKey(), anticipatedSteps.get(messageToSend.getKey()) + 1);
                messagesToSend.put(messageToSend.getKey(), messageToSend.getValue());
                setTimer(curUser, messageToSend.getKey());
            }
        }

        final List<GameSession> sessionsToTerminate = new ArrayList<>();
        final Coordinates coordinates = new Coordinates();
        for (Map.Entry<Long, List<Point>> message : messagesToSend.entrySet()) {
            try {
                coordinates.fromPointListToIntArray(message.getValue());
                gameSocketService.sendMessageToUser(message.getKey(), coordinates);
            } catch (IOException e) {
                try {
                    final InfoMessage infoMessage = new InfoMessage();
                    infoMessage.setMessage("repeat");
                    gameSocketService.sendMessageToUser(gameSessionService.getGameSession(message.getKey())
                            .getAnotherPlayer(message.getKey()), infoMessage);
                } catch (IOException ex) {
                    sessionsToTerminate.add(gameSessionService.getGameSession(message.getKey()));
                    LOGGER.error("Can't send data to user ", e, ex);
                }
            }
        }

        final List<GameSession> sessionsToFinish = new ArrayList<>();
        for (GameSession session : gameSessionService.getSessions()) {
            if (session.tryFinishGame()) {
                if (session.getFirstResult()) {
                    userService.increaseScore(session.getFirstUserId());
                } else if (session.getSecondResult()) {
                    userService.increaseScore(session.getSecondUserId());
                }
                sessionsToFinish.add(session);
                continue;
            }
            if (!gameSessionService.checkHealthState(session)) {
                sessionsToTerminate.add(session);
            }
        }

        sessionsToTerminate.forEach(session -> gameSessionService.forceTerminate(session, true));
        sessionsToFinish.forEach(session -> gameSessionService.forceTerminate(session, false));

        return messagesToSend;
    }
}
