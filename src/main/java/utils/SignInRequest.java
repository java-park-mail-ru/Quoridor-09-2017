package utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignInRequest {
    private String login;
    private String password;

    @JsonCreator
    public SignInRequest(@JsonProperty(value = "login", required = true) String login,
                         @JsonProperty(value = "password", required = true) String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
