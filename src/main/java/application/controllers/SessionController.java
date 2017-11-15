package application.controllers;

import application.dao.User;
import application.dao.UserService;
import application.utils.validators.Validator;
import application.utils.responses.BadResponse;
import application.utils.responses.InfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import application.utils.requests.*;
import application.utils.responses.SuccessResponse;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@CrossOrigin(origins = {"https://jees-quoridor.herokuapp.com", "https://quoridor-jees.herokuapp.com", "http://localhost:8080", "http://127.0.0.1:8080"})
@RestController
public class SessionController {
    private final UserService userService;

    @Autowired
    public SessionController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/signup")
    public ResponseEntity signup(@RequestBody SignUpRequest request,
                                 HttpSession httpSession) {

        final ArrayList<String> errors = Validator.checkSignUp(request);
        if (errors != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(errors));
        }
        if ((userService.emailExists(request.getEmail()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Email already in use"));
        }
        if ((userService.loginExists(request.getLogin()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Login already in use"));
        }
        final long id = userService.addUser(request.getLogin(), request.getPassword(), request.getEmail());
        if (id < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Can't add user to DB"));
        }
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

    @DeleteMapping(path = "/signout")
    public ResponseEntity signout(HttpSession httpSession) {
        if (httpSession.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BadResponse("Unauthorized"));
        }
        httpSession.removeAttribute("userId");
        return ResponseEntity.status(HttpStatus.OK).body(new InfoResponse("Successful logout"));
    }
}
