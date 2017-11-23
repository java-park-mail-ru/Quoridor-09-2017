package application.game.handlers;

import application.game.messages.InfoMessage;
import application.websocket.MessageHandler;
import application.websocket.MessageHandlerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

/* Class for maintaining of WebSocketConnection with clients
 * It is necessary that session did not close on timeout
 */
@Component
public class InfoMessageHandler extends MessageHandler<InfoMessage> {
    @NotNull
    private MessageHandlerContainer messageHandlerContainer;

    public InfoMessageHandler(@NotNull MessageHandlerContainer messageHandlerContainer) {
        super(InfoMessage.class);
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(InfoMessage.class, this);
    }

    @Override
    public void handle(@NotNull InfoMessage message, @NotNull Long userId) {
        //PASS
    }
}
