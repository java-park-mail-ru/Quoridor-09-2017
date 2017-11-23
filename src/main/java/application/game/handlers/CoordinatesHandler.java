package application.game.handlers;

import application.game.GameService;
import application.game.messages.Coordinates;
import application.websocket.MessageHandler;
import application.websocket.MessageHandlerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class CoordinatesHandler extends MessageHandler<Coordinates> {
    @NotNull
    private GameService gameService;

    @NotNull
    private MessageHandlerContainer messageHandlerContainer;

    public CoordinatesHandler(@NotNull GameService gameService,
                              @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(Coordinates.class);
        this.gameService = gameService;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(Coordinates.class, this);
    }

    @Override
    public void handle(@NotNull Coordinates message, @NotNull Long userId) {
        gameService.addPoints(userId, message);
    }
}
