package application.websocket;

public class HandleExeption extends Exception {
    public HandleExeption(String message, Throwable cause) {
        super(message, cause);
    }

    public HandleExeption(String message) {
        super(message);
    }
}
