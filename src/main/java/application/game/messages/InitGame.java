package application.game.messages;

import application.websocket.Message;

@SuppressWarnings("unused")
public class InitGame extends Message {
    private Long self;
    private Long enemy;
    private Boolean isFirst;

    public Long getSelf() {
        return self;
    }

    public void setSelf(Long self) {
        this.self = self;
    }

    public Long getEnemy() {
        return enemy;
    }

    public void setEnemy(Long enemy) {
        this.enemy = enemy;
    }

    public Boolean getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(Boolean isFirst) {
        this.isFirst = isFirst;
    }
}
