package application.utils.validators;

import application.utils.requests.SignInRequest;
import application.utils.requests.SignUpRequest;

import java.util.ArrayList;


//методы проверок возвращает null, если проверка прошла успешно
//если найдены ошибки, то возвращается ArrayList<String> c этими ошибками


public class Validator {

    private static final int MAX_EMAIL_LENGTH = 30;
    private static final int MIN_EMAIL_LENGTH = 4;

    private static final int MAX_LOGIN_LENGTH = 30;
    private static final int MIN_LOGIN_LENGTH = 3;

    private static final int MAX_PASSWORD_LENGTH = 30;
    private static final int MIN_PASSWORD_LENGTH = 4;

    public static ArrayList<String> checkEmail(String email) {
        final ArrayList<String> messages = new ArrayList<>();

        if (email == null) {
            messages.add("Incorrect Email");
            return messages;
        }

        if ((email.length() < MIN_EMAIL_LENGTH) || (email.length() > MAX_EMAIL_LENGTH)) {
            messages.add("Email should be " + MIN_EMAIL_LENGTH + " to " + MAX_EMAIL_LENGTH + " characters.");
            return messages;
        }

        if (!email.contains("@")) {
            messages.add("Incorrect Email");
            return messages;
        }

        return null;
    }

    public static ArrayList<String> checkLogin(String login) {
        final ArrayList<String> messages = new ArrayList<>();

        if (login == null) {
            messages.add("Incorrect Login");
            return messages;
        }

        if ((login.length() < MIN_LOGIN_LENGTH) || (login.length() > MAX_LOGIN_LENGTH)) {
            messages.add("Login should be " + MIN_LOGIN_LENGTH + " to " + MAX_LOGIN_LENGTH + " characters.");
            return messages;
        }

        if (!login.matches("^[a-zA-Z0-9_]+$")) {
            messages.add("In the login you can use only uppercase and lowercase Latin letters (a-Z),"
                    + " the numbers (0-9) and the symbol '_'.");
            return messages;
        }
        return null;
    }

    public static ArrayList<String> checkPassword(String password) {
        final ArrayList<String> messages = new ArrayList<>();

        if (password == null) {
            messages.add("Incorrect Password");
            return messages;
        }

        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            messages.add("Password should be " + MIN_PASSWORD_LENGTH + " to " + MAX_PASSWORD_LENGTH + " characters.");
            return messages;
        }

        return null;
    }

    public static ArrayList<String> checkEmailOrLogin(String loginOrEmail) {

        final ArrayList<String> emailError = checkEmail(loginOrEmail);
        final ArrayList<String> loginError = checkLogin(loginOrEmail);


        if (loginError != null && emailError != null) {
            final ArrayList<String> errors = new ArrayList<>();
            errors.add("Incorrect login or email");
            return errors;

        }
        return null;
    }

    public static ArrayList<String> checkSignIn(SignInRequest request) {
        final ArrayList<String> errors = new ArrayList<>();

        final ArrayList<String> emailAndLoginErrors = checkEmailOrLogin(request.getLoginOrEmail());
        if (emailAndLoginErrors != null) {
            errors.addAll(emailAndLoginErrors);
        }

        final ArrayList<String> passError = checkPassword(request.getPassword());
        if (passError != null) {
            errors.addAll(passError);
        }

        if (errors.isEmpty()) {
            return null;
        } else {
            return errors;
        }
    }

    public static ArrayList<String> checkSignUp(SignUpRequest request) {
        final ArrayList<String> errors = new ArrayList<>();

        final ArrayList<String> emailError = checkEmail(request.getEmail());
        if (emailError != null) {
            errors.addAll(emailError);
        }

        final ArrayList<String> loginError = checkLogin(request.getLogin());
        if (loginError != null) {
            errors.addAll(loginError);
        }

        final ArrayList<String> passError = checkPassword(request.getPassword());
        if (passError != null) {
            errors.addAll(passError);
        }

        if (errors.isEmpty()) {
            return null;
        } else {
            return errors;
        }
    }
}
