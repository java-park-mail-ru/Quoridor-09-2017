package application.utils.responses;

import java.util.ArrayList;

public class BadResponse {
    private String errors;

    public BadResponse(String error) {
        this.errors = error;
    }

    public BadResponse(ArrayList<String> errors) {
        final StringBuilder builder = new StringBuilder("");
        int i = 0;
        for (; i < errors.size() - 1; i++) {
            builder.append(errors.get(i));
            builder.append('\n');
        }
        builder.append(errors.get(i));
        this.errors = builder.toString();
    }

    @SuppressWarnings("unused")
    public String getErrors() {
        return errors;
    }
}
