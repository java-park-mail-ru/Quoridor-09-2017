package application.utils.responses;

import application.dao.User;

import java.util.ArrayList;
import java.util.List;

class ScoreboardRow {
    private String userName;
    private Integer score;


    ScoreboardRow(String userName, Integer score) {
        this.userName = userName;
        this.score = score;
    }

    @SuppressWarnings("unused")
    public Integer getScore() {
        return score;
    }

    @SuppressWarnings("unused")
    public String getUserName() {
        return userName;
    }
}

public class ScoreboardResponse {
    private List<ScoreboardRow> scoreboard = new ArrayList<>();

    public ScoreboardResponse(List<User> users) {
        users.forEach((user) -> scoreboard.add(new ScoreboardRow(user.getLogin(), user.getScore())));
    }

    public List<ScoreboardRow> getScoreboard() {
        return scoreboard;
    }
}