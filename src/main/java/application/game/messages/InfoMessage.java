package application.game.messages;

import application.websocket.Message;

public class InfoMessage extends Message {
    private String message;

    public InfoMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
