package application.controllers;

import application.User;
import application.UserService;
import application.utils.Validators.Validator;
import application.utils.responses.BadResponse;
import application.utils.responses.InfoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import application.utils.requests.*;
import application.utils.responses.SuccessResponse;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

//Для CORS можно еще добавить сокеты локального хоста
//только надо договориться, какие именно

@SuppressWarnings("Duplicates")
@CrossOrigin(origins = {"https://quoridor-jees.herokuapp.com", "https://jees-quoridor.herokuapp.com"})
@RestController
public class SessionController {
    private static UserService userService = new UserService();

    @PostMapping(path = "/signup")
    public ResponseEntity signup(@RequestBody SignUpRequest request,
                                 HttpSession httpSession) {

        final ArrayList<String> errors = Validator.checkSignUp(request);
        if (errors != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(errors));
        }
        final long id = userService.addUser(request);
        httpSession.setAttribute("userId", id);
        return ResponseEntity.ok(new SuccessResponse(userService.getUser(id)));
    }

    @PostMapping(path = "/signin")
    public ResponseEntity signin(@RequestBody SignInRequest request,
                                 HttpSession httpSession) {

        final ArrayList<String> errors = Validator.checkSignIn(request);
        if (errors != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(errors));
        }

        final long id = userService.getId(request.getLoginOrEmail());
        if (id < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Wrong login or email"));
        }
        if (userService.checkPassword(id, request.getPassword())) {
            httpSession.setAttribute("userId", id);
        return ResponseEntity.ok(new SuccessResponse(userService.getUser(id)));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Wrong password"));
        }
    }

    @PostMapping(path = "/signout")
    public ResponseEntity signout(HttpSession httpSession) {
        if (httpSession.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BadResponse("Unauthorized"));
        }
        httpSession.invalidate();
        httpSession.removeAttribute("userId");
        return ResponseEntity.status(HttpStatus.OK).body(new InfoResponse("Successful logout"));
    }

    @PostMapping(path = "/currentUser")
    public ResponseEntity getCurUser(HttpSession httpSession) {
        final Object id = httpSession.getAttribute("userId");
        final User user;
        if (id instanceof Long) {
            user = userService.getUser((Long) id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }
        return ResponseEntity.ok(new SuccessResponse(user));
    }

    @PostMapping(path = "/currentUser/changeLogin")
    public ResponseEntity changeLogin(@RequestBody ChangeLoginRequest request,
                                      HttpSession httpSession) {
        final String error = Validator.checkLogin(request.getLogin());
        if (error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(error));
        }

        final Object id = httpSession.getAttribute("userId");
        final User user;
        if (id instanceof Long) {
            user = userService.getUser((Long) id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        userService.changeLogin((Long) id, user, request.getLogin());
        return ResponseEntity.status(HttpStatus.OK).body(new InfoResponse("Login changed"));

    }

    @PostMapping(path = "/currentUser/changeEmail")
    public ResponseEntity changeEmail(@RequestBody ChangeEmailRequest request,
                                      HttpSession httpSession) {
        final String error = Validator.checkEmail(request.getEmail());
        if (error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(error));
        }

        final Object id = httpSession.getAttribute("userId");
        final User user;
        if (id instanceof Long) {
            user = userService.getUser((Long) id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        userService.changeEmail((Long) id, user, request.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(new InfoResponse("Email changed"));
    }

    @PostMapping(path = "/currentUser/changePass")
    public ResponseEntity changePass(@RequestBody ChangePassRequest request,
                                      HttpSession httpSession) {
        String error = Validator.checkPassword(request.getNewPassword());
        if (error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(error));
        }
        error = Validator.checkPassword(request.getOldPassword());
        if (error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(error));
        }

        final Object id = httpSession.getAttribute("userId");
        final User user;
        if (id instanceof Long) {
            user = userService.getUser((Long) id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        userService.changePassword((Long) id, user, request.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK).body(new InfoResponse("Password changed"));
    }
}
