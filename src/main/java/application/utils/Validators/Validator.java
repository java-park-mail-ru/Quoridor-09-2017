package application.utils.Validators;

import application.utils.requests.SignInRequest;
import application.utils.requests.SignUpRequest;

import java.util.ArrayList;


//методы проверок возвращает null, если проверка прошла успешно
//если найдены ошибки, то возвращается String с этими ошибками или ArrayList<String>

//необходимо дописать методы chekEmail checkLogin checkPassword

public class Validator {
    public static String checkEmail(String email) {
        return null;
    }

    public static String checkLogin(String login) {
        return null;
    }

    public static String checkPassword(String password) {
        return null;
    }

    public static ArrayList<String> checkEmailOrLogin(String loginOrEmail) {
        final ArrayList<String> errors = new ArrayList<>();

        final String emailError = checkEmail(loginOrEmail);
        if (emailError != null) {
            errors.add(emailError);
        }

        final String loginError = checkLogin(loginOrEmail);
        if (loginError != null) {
            errors.add(loginError);
        }

        if (errors.isEmpty()) {
            return null;
        } else {
            return errors;
        }
    }

    public static ArrayList<String> checkSignIn(SignInRequest request) {
        final ArrayList<String> errors = new ArrayList<>();

        final ArrayList<String> emailAndLoginErrors = checkEmailOrLogin(request.getLoginOrEmail());
        if (emailAndLoginErrors != null) {
            errors.addAll(emailAndLoginErrors);
        }

        final String passError = checkPassword(request.getPassword());
        if (passError != null) {
            errors.add(passError);
        }

        if (errors.isEmpty()) {
            return null;
        } else {
            return errors;
        }
    }

    public static ArrayList<String> checkSignUp(SignUpRequest request) {
        final ArrayList<String> errors = new ArrayList<>();

        final String emailError = checkEmail(request.getEmail());
        if (emailError != null) {
            errors.add(emailError);
        }

        final String loginError = checkLogin(request.getLogin());
        if (loginError != null) {
            errors.add(loginError);
        }

        final String passError = checkPassword(request.getPassword());
        if (passError != null) {
            errors.add(passError);
        }

        if (errors.isEmpty()) {
            return null;
        } else {
            return errors;
        }
    }
}
