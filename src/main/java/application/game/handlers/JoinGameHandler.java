package application.game.handlers;

import application.game.GameService;
import application.game.messages.JoinGame;
import application.websocket.MessageHandler;
import application.websocket.MessageHandlerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class JoinGameHandler extends MessageHandler<JoinGame> {
    @NotNull
    private GameService gameService;

    @NotNull
    private MessageHandlerContainer messageHandlerContainer;

    public JoinGameHandler(@NotNull GameService gameService,
                           @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(JoinGame.class);
        this.gameService = gameService;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(JoinGame.class, this);
    }

    @Override
    public void handle(@NotNull JoinGame message, @NotNull Long userId) {
        gameService.addUser(userId);
    }
}
