package application.utils.responses;

import application.dao.User;

public class SuccessResponse {
    private String login;
    private String email;

    public SuccessResponse(User user) {
        this.login = user.getLogin();
        this.email = user.getEmail();
    }

    @SuppressWarnings("unused")
    public String getLogin() {
        return login;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }
}
