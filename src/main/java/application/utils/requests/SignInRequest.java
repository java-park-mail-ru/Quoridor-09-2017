package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignInRequest {
    private String loginOrEmail;
    private String password;

    @JsonCreator
    public SignInRequest(@JsonProperty(value = "login", required = true) String loginOrEmail,
                         @JsonProperty(value = "password", required = true) String password) {
        this.loginOrEmail = loginOrEmail;
        this.password = password;
    }

    public String getLoginOrEmail() {
        return loginOrEmail;
    }

    public String getPassword() {
        return password;
    }
}
