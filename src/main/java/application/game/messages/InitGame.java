package application.game.messages;

import application.websocket.Message;

@SuppressWarnings("unused")
public class InitGame extends Message {
    private String enemy;
    private Boolean isFirst;

    public String getEnemy() {
        return enemy;
    }

    public void setEnemy(String enemy) {
        this.enemy = enemy;
    }

    public Boolean getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(Boolean first) {
        isFirst = first;
    }
}
