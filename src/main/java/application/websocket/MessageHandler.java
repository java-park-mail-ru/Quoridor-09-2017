package application.websocket;

import javax.validation.constraints.NotNull;

public abstract class MessageHandler<T extends Message> {
    @NotNull
    private final Class<T> myClass;

    public MessageHandler(@NotNull Class<T> myClass) {
        this.myClass = myClass;
    }

    public void handleMessage(@NotNull Message message, @NotNull Long userId) throws HandleExeption {
        try {
            handle(myClass.cast(message), userId);
        } catch (ClassCastException ex) {
            throw new HandleExeption("Can't reed incoming message of type " + message.getClass(), ex);
        }
    }

    public abstract void handle(@NotNull T message, @NotNull Long userId) throws HandleExeption;
}
