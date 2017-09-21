package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangePassRequest {
    private String newPassword;
    private String oldPassword;

    @JsonCreator
    public ChangePassRequest(@JsonProperty(value = "oldPassword", required = true) String oldPassword,
                             @JsonProperty(value = "newPassword", required = true) String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    @SuppressWarnings("unused")
    public String getNewPassword() {
        return newPassword;
    }

    @SuppressWarnings("unused")
    public String getOldPassword() {
        return oldPassword;
    }
}
