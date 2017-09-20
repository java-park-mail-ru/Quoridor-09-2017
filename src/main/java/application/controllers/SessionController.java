package application.controllers;

import application.User;
import application.UserService;
import application.utils.validators.Validator;
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
        final long id = userService.addUser(request.getLogin(), request.getPassword(), request.getEmail());
        httpSession.setAttribute("userId", id);
        return ResponseEntity.ok(new SuccessResponse(userService.getUserById(id)));
    }

    @PostMapping(path = "/signin")
    public ResponseEntity signin(@RequestBody SignInRequest request,
                                 HttpSession httpSession) {
        if (!(userService.emailExists(request.getLoginOrEmail()) || userService.loginExists(request.getLoginOrEmail()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Wrong login or email"));
        }
        final User user = userService.getUserByEmailOrLogin(request.getLoginOrEmail());
        final long id = userService.getId(user);
        if (userService.checkPassword(id, request.getPassword())) {
            httpSession.setAttribute("userId", id);
            return ResponseEntity.ok(new SuccessResponse(user));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Wrong password"));
        }
    }

    @PostMapping(path = "/signout")
    public ResponseEntity signout(HttpSession httpSession) {
        if (httpSession.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BadResponse("Unauthorized"));
        }
        httpSession.removeAttribute("userId");
        return ResponseEntity.status(HttpStatus.OK).body(new InfoResponse("Successful logout"));
    }

    @PostMapping(path = "/currentUser")
    public ResponseEntity getCurUser(HttpSession httpSession) {
        final Long id;
        try {
            id = (Long) httpSession.getAttribute("userId");
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        final User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }
        return ResponseEntity.ok(new SuccessResponse(user));
    }

    @PostMapping(path = "/currentUser/changeLogin")
    public ResponseEntity changeLogin(@RequestBody ChangeLoginRequest request,
                                      HttpSession httpSession) {
        final ArrayList<String> errors = Validator.checkLogin(request.getLogin());
        if (errors != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(errors));
        }

        final Long id;
        try {
            id = (Long) httpSession.getAttribute("userId");
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        final User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        userService.changeLogin(id, request.getLogin());
        return ResponseEntity.status(HttpStatus.OK).body(new InfoResponse("Login changed"));
    }

    @PostMapping(path = "/currentUser/changeEmail")
    public ResponseEntity changeEmail(@RequestBody ChangeEmailRequest request,
                                      HttpSession httpSession) {
        final ArrayList<String> errors = Validator.checkEmail(request.getEmail());
        if (errors != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(errors));
        }

        final Long id;
        try {
            id = (Long) httpSession.getAttribute("userId");
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        final User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        userService.changeEmail(id, request.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(new InfoResponse("Email changed"));
    }

    @PostMapping(path = "/currentUser/changePass")
    public ResponseEntity changePass(@RequestBody ChangePassRequest request,
                                      HttpSession httpSession) {
        ArrayList<String> errors = Validator.checkPassword(request.getNewPassword());
        if (errors != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(errors));
        }
        errors = Validator.checkPassword(request.getOldPassword());
        if (errors != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(errors));
        }

        final Long id;
        try {
            id = (Long) httpSession.getAttribute("userId");
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        final User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        userService.changePassword(id, request.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK).body(new InfoResponse("Password changed"));
    }
}
