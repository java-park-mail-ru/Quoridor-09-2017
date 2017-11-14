package application.game.models;

import application.websocket.Message;

public class InitGame extends Message {
    private Long self;
    private Long enemy;

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
}
