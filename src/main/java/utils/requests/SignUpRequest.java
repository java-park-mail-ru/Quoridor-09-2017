package utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignUpRequest {
    private String login;
    private String email;
    private String password;

    @JsonCreator
    public SignUpRequest(@JsonProperty(value = "login", required = true) String login,
                         @JsonProperty(value = "email", required = true) String email,
                         @JsonProperty(value = "password", required = true) String password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
