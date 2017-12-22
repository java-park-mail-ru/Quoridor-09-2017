package application.game;

import application.game.logic.Game;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GameSession {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private boolean isFinished;

    @NotNull
    private final Long sessionId;

    @NotNull
    private final Long firstUserId;

    @NotNull
    private final Long secondUserId;

    @NotNull
    private Long waiter;

    private Boolean firstResult;
    private Boolean secondResult;

    @NotNull
    private final Game game;

    @NotNull
    private final GameSessionService gameSessionService;

    @NotNull
    private final AtomicInteger stepCounter = new AtomicInteger(0);

    private static final int FIELD_DIMENSION = 9;

    public GameSession(@NotNull Long firstUserId,
                       @NotNull Long secondUserId,
                       @NotNull Long waiter,
                       @NotNull GameSessionService gameSessionService) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.waiter = waiter;
        this.sessionId = ID_GENERATOR.getAndIncrement();
        this.gameSessionService = gameSessionService;

        this.game = new Game(FIELD_DIMENSION, firstUserId, secondUserId);

        this.isFinished = false;
        this.firstResult = false;
        this.secondResult = false;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public Long getFirstUserId() {
        return firstUserId;
    }

    public Long getSecondUserId() {
        return secondUserId;
    }

    public Boolean getFirstResult() {
        return firstResult;
    }

    public Boolean getSecondResult() {
        return secondResult;
    }

    public void setFirstResult(Boolean firstResult) {
        this.firstResult = firstResult;
    }

    public void setSecondResult(Boolean secondResult) {
        this.secondResult = secondResult;
    }

    public Game getGame() {
        return game;
    }

    public Long getWaiter() {
        return waiter;
    }

    public void setWaiter(Long waiter) {
        this.waiter = waiter;
    }

    public Long getAnotherPlayer(Long userId) {
        if (Objects.equals(userId, firstUserId)) {
            return secondUserId;
        }
        if (Objects.equals(userId, secondUserId)) {
            return firstUserId;
        }
        return null;
    }

    public int getStepCount() {
        return stepCounter.get();
    }

    public boolean compareAndSetStepCount(int expect, int update) {
        return stepCounter.compareAndSet(expect, update);
    }

    @Override
    public String toString() {
        return "GameSession{"
                + ", sessionId=" + sessionId
                + ", firstUserId=" + firstUserId
                + ", secondUserId=" + secondUserId
                + ", firstResult=" + firstResult
                + ", secondResult=" + secondResult
                + '}';
    }

    public boolean tryFinishGame() {
        if (game.isFinished()) {
            isFinished = true;
            if (Objects.equals(firstUserId, game.getWiner())) {
                firstResult = true;
                secondResult = false;
            } else {
                firstResult = false;
                secondResult = true;
            }
            gameSessionService.finishGame(this);
            return true;
        }
        return false;
    }
}
