package application.game;

import javax.validation.constraints.NotNull;
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
    private final Game game;

    public GameSession(@NotNull Long firstUserId,
                       @NotNull Long secondUserId) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.sessionId = ID_GENERATOR.getAndIncrement();

        //Магическое число!!!
        this.game = new Game(9);

        this.isFinished = false;
    }
}
