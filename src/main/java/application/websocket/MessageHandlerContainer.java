package application.websocket;

import javax.validation.constraints.NotNull;

public interface MessageHandlerContainer {
    void handle(@NotNull Message message, @NotNull Long userId) throws HandleExeption;

    <T extends Message> void registerHandler(@NotNull Class<T> myClass, MessageHandler<T> handler);
}
