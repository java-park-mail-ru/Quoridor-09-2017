package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangeLoginRequest {
    private String login;

    @JsonCreator
    public ChangeLoginRequest(@JsonProperty(value = "login", required = true) String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
