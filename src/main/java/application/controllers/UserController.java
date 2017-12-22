package application.controllers;

import application.dao.User;
import application.dao.UserService;
import application.utils.requests.*;
import application.utils.responses.*;
import application.utils.validators.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"https://jees-quoridor.herokuapp.com", "https://quoridor-jees.herokuapp.com", "http://localhost:8080", "http://127.0.0.1:8080"})
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/currentUser")
    public ResponseEntity getCurUser(HttpSession httpSession) {
        final Long id = (Long) httpSession.getAttribute("userId");
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        final User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        return ResponseEntity.ok(new SuccessResponse(user));
    }

    @PatchMapping(path = "/currentUser/changeLogin")
    public ResponseEntity changeLogin(@RequestBody ChangeLoginRequest request,
                                      HttpSession httpSession) {
        final Long id = (Long) httpSession.getAttribute("userId");
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        final ArrayList<String> errors = Validator.checkLogin(request.getLogin());
        if (errors != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(errors));
        }
        if ((userService.loginExists(request.getLogin()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Login already in use"));
        }

        final User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        if (userService.changeLogin(id, request.getLogin())) {
            return ResponseEntity.status(HttpStatus.OK).body(new InfoResponse("Login changed"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Can't update login"));
        }
    }

    @PatchMapping(path = "/currentUser/changeEmail")
    public ResponseEntity changeEmail(@RequestBody ChangeEmailRequest request,
                                      HttpSession httpSession) {
        final Long id = (Long) httpSession.getAttribute("userId");
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        final ArrayList<String> errors = Validator.checkEmail(request.getEmail());
        if (errors != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(errors));
        }
        if ((userService.emailExists(request.getEmail()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Email already in use"));
        }

        final User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        if (userService.changeEmail(id, request.getEmail())) {
            return ResponseEntity.status(HttpStatus.OK).body(new InfoResponse("Email changed"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Can't update email"));
        }
    }

    @PatchMapping(path = "/currentUser/changePass")
    public ResponseEntity changePass(@RequestBody ChangePassRequest request,
                                     HttpSession httpSession) {
        final Long id = (Long) httpSession.getAttribute("userId");
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        final User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Invalid session"));
        }

        if (!userService.checkPassword(id, request.getOldPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Wrong old password"));
        }

        final ArrayList<String> errors = Validator.checkPassword(request.getNewPassword());
        if (errors != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(errors));
        }

        if (userService.changePassword(id, request.getNewPassword())) {
            return ResponseEntity.status(HttpStatus.OK).body(new InfoResponse("Password changed"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Can't update password"));
        }
    }

    @GetMapping(path = "/scoreBoard")
    public ResponseEntity getScoreboard(@RequestParam long offset, @RequestParam long limit) {
        final List<User> scoreBoard = userService.getScoreBoard(offset, limit);
        if (scoreBoard != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ScoreboardResponse(scoreBoard));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse("Can't get scoreboard"));
        }

    }
}
