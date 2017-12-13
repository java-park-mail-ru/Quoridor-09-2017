package application.utils.responses;

import application.dao.User;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ScoreboardResponse {
    private List<ScoreboardRow> scoreboard = new ArrayList<>();

    public ScoreboardResponse(List<User> users) {
        users.forEach((user) -> scoreboard.add(new ScoreboardRow(user.getLogin(), user.getScore())));
    }

    public List<ScoreboardRow> getScoreboard() {
        return scoreboard;
    }
}