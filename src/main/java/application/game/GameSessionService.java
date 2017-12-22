package application.game;

import application.dao.UserService;
import application.game.logic.Point;
import application.game.messages.FinishGame;
import application.game.messages.InfoMessage;
import application.game.messages.InitGame;
import application.websocket.GameSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameSessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSessionService.class);

    @NotNull
    private final ConcurrentHashMap<Long, GameSession> gameSessions = new ConcurrentHashMap<>();

    @NotNull
    private final GameSocketService gameSocketService;

    @NotNull
    private final UserService userService;

    public GameSessionService(@NotNull GameSocketService gameSocketService,
                              @NotNull UserService userService) {
        this.gameSocketService = gameSocketService;
        this.userService = userService;
    }

    public boolean isPlaying(@NotNull Long userId) {
        return gameSessions.containsKey(userId);
    }

    //visible for testing
    Map<Long, GameSession> getGameSessions() {
        return gameSessions;
    }

    public Set<GameSession> getSessions() {
        return new HashSet<>(gameSessions.values());
    }

    public boolean checkHealthState(@NotNull GameSession gameSession) {
        return gameSocketService.isConnected(gameSession.getFirstUserId())
                && gameSocketService.isConnected(gameSession.getSecondUserId());
    }

    public GameSession getGameSession(Long userId) {
        return gameSessions.get(userId);
    }

    public void startGame(@NotNull Long first, @NotNull Long second) {
        final Long waiter;
        if ((new Random()).nextInt(2) == 0) {
            waiter = first;
        } else {
            waiter = second;
        }
        final GameSession gameSession = new GameSession(first, second, waiter, this);
        gameSessions.put(first, gameSession);
        gameSessions.put(second, gameSession);

        try {
            final InitGame initMessage1 = createInitMessage(first, second, waiter);
            gameSocketService.sendMessageToUser(first, initMessage1);
            final InitGame initMessage2 = createInitMessage(second, first, waiter);
            gameSocketService.sendMessageToUser(second, initMessage2);
            LOGGER.info("Game session " + gameSession.getSessionId() + " started. " + gameSession.toString());
        } catch (IOException e) {
            gameSocketService.closeConnection(first, CloseStatus.SERVER_ERROR);
            gameSocketService.closeConnection(second, CloseStatus.SERVER_ERROR);
            LOGGER.error("Can't start a game for users " + first + ", " + second, e);
        }
    }

    private InitGame createInitMessage(@NotNull Long self, @NotNull Long enemy, @NotNull Long waiter) {
        final InitGame initMessage = new InitGame();
        initMessage.setEnemy(userService.getUserById(enemy).getLogin());
        initMessage.setIsFirst(!(Objects.equals(self, waiter)));
        return initMessage;
    }

    public void finishGame(@NotNull GameSession gameSession) {
        final FinishGame finishGameMessage = new FinishGame();
        try {
            finishGameMessage.setWon(gameSession.getFirstResult());
            gameSocketService.sendMessageToUser(gameSession.getFirstUserId(), finishGameMessage);
        } catch (IOException e) {
            LOGGER.warn("Failed to send FinishGameMessage to user " + gameSession.getFirstUserId(), e);
        }
        try {
            finishGameMessage.setWon(gameSession.getSecondResult());
            gameSocketService.sendMessageToUser(gameSession.getSecondUserId(), finishGameMessage);
        } catch (IOException e) {
            LOGGER.warn("Failed to send FinishGameMessage to user " + gameSession.getSecondUserId(), e);
        }
    }

    public void forceTerminate(@NotNull GameSession gameSession, boolean error) {
        final Long firstUserId = gameSession.getFirstUserId();
        final Long secondUserId = gameSession.getSecondUserId();
        final boolean exists = (gameSessions.remove(firstUserId) != null
                && gameSessions.remove(secondUserId) != null);
        final CloseStatus status;
        if (error) {
            status = CloseStatus.SERVER_ERROR;
        } else {
            status = CloseStatus.NORMAL;
        }
        if (exists) {
            gameSocketService.closeConnection(firstUserId, status);
            gameSocketService.closeConnection(secondUserId, status);
        }
        final StringBuilder info = new StringBuilder();
        info.append("Game session ").append(gameSession.getSessionId());
        if (error) {
            info.append(" was terminated due to error. ");
        } else {
            info.append(" was cleaned. ");
        }
        info.append(gameSession.toString());
        LOGGER.info(info.toString());
    }

    public void handleUnexpectedEnding(@NotNull Long userId, @NotNull FinishGame message, int curCount) {
        final GameSession session = gameSessions.get(userId);
        final Long anotherUser;
        try {
            anotherUser = session.getAnotherPlayer(userId);
        } catch (NullPointerException e) {
            LOGGER.info("GameSession was already closed");
            return;
        }
        message.setWon(true);
        if (session.compareAndSetStepCount(curCount, curCount + 1)) {
            if (gameSocketService.isConnected(anotherUser)) {
                try {
                    gameSocketService.sendMessageToUser(anotherUser, message);
                } catch (IOException e) {
                    LOGGER.warn("Failed to send FinishGameMessage to user " + anotherUser, e);
                }
            }
            forceTerminate(session, true);
            userService.increaseScore(anotherUser);
        }
    }

    public AbstractMap.SimpleEntry<Long, List<Point>> handleTask(Long userId, List<Point> points, int curCount) {
        final GameSession session = gameSessions.get(userId);
        if (session.isFinished()) {
            return null;
        }
        final InfoMessage infoMessage = new InfoMessage();
        if (!Objects.equals(session.getWaiter(), userId)) {
            final List<Point> resultPoints = session.getGame().iterationOfGame(points);
            if (resultPoints == null) {
                try {
                    if (session.getGame().getError() != null) {
                        infoMessage.setMessage("repeat " + session.getGame().getError());
                        gameSocketService.sendMessageToUser(userId, infoMessage);
                    }
                } catch (IOException e) {
                    LOGGER.warn("Failed to send RepeatGameMessage to user " + userId, e);
                }
            } else {
                if (session.compareAndSetStepCount(curCount, curCount + 1)) {
                    session.setWaiter(userId);
                    return new AbstractMap.SimpleEntry<>(session.getAnotherPlayer(userId), resultPoints);
                }
            }
        } else {
            try {
                infoMessage.setMessage("wait");
                gameSocketService.sendMessageToUser(userId, infoMessage);
            } catch (IOException e) {
                LOGGER.warn("Failed to send WaitGameMessage to user " + userId, e);
            }
        }
        return null;
    }
}
