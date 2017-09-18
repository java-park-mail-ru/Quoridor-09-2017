package application;

import application.utils.requests.SignUpRequest;

import java.util.HashMap;

public class UserService {
    private HashMap<Long, User> db;
    private static long id = 1;

    public UserService() {
        db = new HashMap<>();
    }

    public long addUser(SignUpRequest request) {
        final User user = new User(request.getLogin(), request.getPassword(), request.getEmail());
        db.put(id, user);
        return id++;
    }

    public long addUser(User user) {
        db.put(id, user);
        return id++;
    }

    public User getUser(long userId) {
        return db.get(userId);
    }

    public long getId(String loginOrEmail) {
        for (long i = 1; i < id; i++) {
            if (db.get(i).getLogin().equals(loginOrEmail) || db.get(i).getEmail().equals(loginOrEmail)) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasLogin(String login) {
        for (long i = 1; i < id; i++) {
            if (db.get(i).getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEmail(String email) {
        for (long i = 1; i < id; i++) {
            if (db.get(i).getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public void changeEmail(long userId, User user, String newEmail) {
        user.setEmail(newEmail);
        db.put(userId, user);
    }

    public void changeLogin(long userId, User user, String newLogin) {
        user.setLogin(newLogin);
        db.put(userId, user);
    }

    public void changePassword(long userId, User user, String newPassword) {
        user.setPassword(newPassword);
        db.put(userId, user);
    }

    public boolean checkPassword(long userId, String password) {
        return this.getUser(userId).getPassword().equals(password);
    }
}
