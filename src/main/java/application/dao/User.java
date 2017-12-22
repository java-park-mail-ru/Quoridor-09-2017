package application.dao;

@SuppressWarnings("unused")
public class User {
    private long id;
    private String login;
    private String password;
    private String email;
    private int score;

    public User(long id, String login, String password, String email) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.password = password;
        this.score = 0;
    }

    public User(String login, int score) {
        this.login = login;
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getScore() {
        return score;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
