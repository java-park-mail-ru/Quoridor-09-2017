package application.utils.responses;

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
