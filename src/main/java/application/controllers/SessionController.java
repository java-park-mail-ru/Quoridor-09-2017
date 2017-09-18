package application.controllers;

import application.User;
import application.UserDB;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import utils.requests.*;
import utils.responses.SuccessResponse;

import javax.servlet.http.HttpSession;

@RestController
public class SessionController {
    private static UserDB db = new UserDB();

    @PostMapping(path = "/signup"/*, consumes = "application/json", produces = "application/json"*/)
    public ResponseEntity signup(@RequestBody SignUpRequest request,
                                 HttpSession httpSession) {
        final User user = new User(request.getLogin(), request.getPassword(), request.getEmail());
        final long id = db.addUser(user);
        httpSession.setAttribute("userId", id);
//        return ResponseEntity.ok(new SuccessResponse(user));
        return ResponseEntity.ok("User created");
    }

    @PostMapping(path = "/signin"/*, consumes = "application/json", produces = "application/json"*/)
    public ResponseEntity signin(@RequestBody SignInRequest request,
                                 HttpSession httpSession) {
        final long id = db.getId(request.getLoginOrEmail());
        if (db.checkPassword(id, request.getPassword())) {
            httpSession.setAttribute("userId", id);
//        final User user = db.getUser(id);
//        return ResponseEntity.ok(new SuccessResponse(user));
            return ResponseEntity.ok("Loged in");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong login or password");
        }
    }

    @PostMapping(path = "/signout")
    public ResponseEntity signout(HttpSession httpSession) {
        httpSession.invalidate();
        return ResponseEntity.ok("User session deleted");
    }

    @PostMapping(path = "/currentUser")
    public ResponseEntity getCurUser(HttpSession httpSession) {
        final Object id = httpSession.getAttribute("userId");
        final User user;
        if (id instanceof Long) {
            user = db.getUser((Long) id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid session");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid session");
        }
        return ResponseEntity.ok(new SuccessResponse(user));
    }

    @PostMapping(path = "/currentUser/changeLogin")
    public ResponseEntity changeLogin(@RequestBody ChangeLoginRequest request,
                                      HttpSession httpSession) {
        final Object id = httpSession.getAttribute("userId");
        final User user = db.getUser((Long) id);
        user.setLogin(request.getLogin());
        return ResponseEntity.ok("Login changed");
    }

    @PostMapping(path = "/currentUser/changeEmail")
    public ResponseEntity changeEmail(@RequestBody ChangeEmailRequest request,
                                      HttpSession httpSession) {
        final Object id = httpSession.getAttribute("userId");
        final User user = db.getUser((Long) id);
        user.setEmail(request.getEmail());
        return ResponseEntity.ok("Email changed");
    }

    @PostMapping(path = "/currentUser/changePass")
    public ResponseEntity changePass(@RequestBody ChangePassRequest request,
                                      HttpSession httpSession) {
        final Object id = httpSession.getAttribute("userId");
        final User user = db.getUser((Long) id);
        user.setEmail(request.getPassword());
        return ResponseEntity.ok("Password changed");
    }
}
