package application.utils.responses;

import java.util.ArrayList;

public class BadResponse {
    private String errors;

    public BadResponse(String error) {
        this.errors = error;
    }

    public BadResponse(ArrayList<String> errors) {
        final StringBuilder builder = new StringBuilder("");
        for (String error : errors) {
            builder.append(error);
            builder.append('\n');
        }
        this.errors = builder.toString();
    }

    @SuppressWarnings("unused")
    public String getErrors() {
        return errors;
    }
}
