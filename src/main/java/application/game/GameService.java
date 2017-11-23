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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class GameService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    @NotNull
    private ConcurrentLinkedQueue<Long> waiters = new ConcurrentLinkedQueue<>();

    @NotNull
    private ConcurrentHashMap<Long, List<Point>> tasks = new ConcurrentHashMap<>();

    @NotNull
    private final UserService userService;

    @NotNull
    private final GameSocketService gameSocketService;

    @NotNull
    private final GameSessionService gameSessionService;

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
            points = coordinates.fromStringToList();
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
                gameSessionService.startGame(iterator.next(), iterator.next());
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
            tasks.put(userId, coordinates.fromStringToList());
        } catch (HandleExeption ignored) {
            LOGGER.error(ignored.getMessage());
        }
    }

    public Map<Long, List<Point>> gameStep() {
        final Map<Long, List<Point>> messagesToSend = new HashMap<>();
        final Set<Long> users = tasks.keySet();
        for (Long curUser : users) {
            final AbstractMap.SimpleEntry<Long, List<Point>> messageToSend = gameSessionService.handleTask(
                    curUser, tasks.remove(curUser));
            if (messageToSend != null) {
                messagesToSend.put(messageToSend.getKey(), messageToSend.getValue());
            }
        }

        final List<GameSession> sessionsToTerminate = new ArrayList<>();
        final List<GameSession> sessionsToFinish = new ArrayList<>();
        for (GameSession session : gameSessionService.getSessions()) {
            if (session.tryFinishGame()) {
                sessionsToFinish.add(session);
                continue;
            }
            if (!gameSessionService.checkHealthState(session)) {
                sessionsToTerminate.add(session);
            }
        }

        final Coordinates coordinates = new Coordinates();
        for (Map.Entry<Long, List<Point>> message : messagesToSend.entrySet()) {
            try {
                coordinates.fromListToString(message.getValue());
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

        sessionsToTerminate.forEach(session -> gameSessionService.forceTerminate(session, true));
        sessionsToFinish.forEach(session -> gameSessionService.forceTerminate(session, false));

        return messagesToSend;
    }
}
