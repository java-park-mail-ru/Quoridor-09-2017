package utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangePassRequest {
    private String password;

    @JsonCreator
    public ChangePassRequest(@JsonProperty(value = "password", required = true) String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
