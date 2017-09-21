package application;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserService {
    private HashMap<Long, User> db;
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    public UserService() {
        db = new HashMap<>();
    }

    public long addUser(String login, String password, String email) {
        final long id = ID_GENERATOR.getAndIncrement();
        final User user = new User(id, login, password, email);
        db.put(id, user);
        return id;
    }

    public User getUserById(long userId) {
        return db.get(userId);
    }

    private User getUserByEmail(String email) {
        for (User user : db.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    private User getUserByLogin(String login) {
        for (User user : db.values()) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }

    public User getUserByEmailOrLogin(String emailOrLogin) {
        User user = this.getUserByEmail(emailOrLogin);
        if (user != null) {
            return user;
        }
        user = this.getUserByLogin(emailOrLogin);
        return user;
    }

    public boolean emailExists(String email) {
        return this.getUserByEmail(email) != null;
    }

    public boolean loginExists(String login) {
        return this.getUserByLogin(login) != null;
    }

    public long getId(User user) {
        return user.getId();
    }

    public void changeEmail(long userId, String newEmail) {
        final User user = this.getUserById(userId);
        user.setEmail(newEmail);
        db.put(userId, user);
    }

    public void changeLogin(long userId, String newLogin) {
        final User user = this.getUserById(userId);
        user.setLogin(newLogin);
        db.put(userId, user);
    }

    public void changePassword(long userId, String newPassword) {
        final User user = this.getUserById(userId);
        user.setPassword(newPassword);
        db.put(userId, user);
    }

    public boolean checkPassword(long userId, String password) {
        return this.getUserById(userId).getPassword().equals(password);
    }
}
