package application.utils.responses;

import java.util.ArrayList;

public class BadResponse {
    private String error;

    public BadResponse(String error) {
        this.error = error;
    }

    public BadResponse(ArrayList<String> errors) {
        final StringBuilder builder = new StringBuilder("");
        for (String err : errors) {
            builder.append(err);
            builder.append('\n');
        }
        this.error = builder.toString();
    }

    @SuppressWarnings("unused")
    public String getError() {
        return error;
    }
}
