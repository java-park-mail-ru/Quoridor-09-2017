package application;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserService {
    private HashMap<Long, User> db;
//    private static long id = 1;
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    public UserService() {
        db = new HashMap<>();
    }

//    public long addUser(SignUpRequest request) {
//        final User user = new User(request.getLogin(), request.getPassword(), request.getEmail());
//        final long id = ID_GENERATOR.getAndIncrement();
//        db.put(id, user);
//        return id;
//    }

    public long addUser(String login, String password, String email) {
        final User user = new User(login, password, email);
        final long id = ID_GENERATOR.getAndIncrement();
        db.put(id, user);
        return id;
    }

//    public long addUser(User user) {
//        final long id = ID_GENERATOR.getAndIncrement();
//        db.put(id, user);
//        return id;
//    }

    public User getUserById(long userId) {
        return db.get(userId);
    }

    public User getUserByEmail(String email) {
        for (long i = 1; i < ID_GENERATOR.get(); i++) {
            final User curUser = db.get(i);
            if (curUser.getEmail().equals(email)) {
                return curUser;
            }
        }
        return null;
    }

    public User getUserByLogin(String login) {
        for (long i = 1; i < ID_GENERATOR.get(); i++) {
            final User curUser = db.get(i);
            if (curUser.getLogin().equals(login)) {
                return curUser;
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

    public long getId(String loginOrEmail) {
        for (long i = 1; i < ID_GENERATOR.get(); i++) {
            if (db.get(i).getLogin().equals(loginOrEmail) || db.get(i).getEmail().equals(loginOrEmail)) {
                return i;
            }
        }
        return -1;
    }

//    public boolean hasLogin(String login) {
//        for (long i = 1; i < ID_GENERATOR.get(); i++) {
//            if (db.get(i).getLogin().equals(login)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean hasEmail(String email) {
//        for (long i = 1; i < ID_GENERATOR.get(); i++) {
//            if (db.get(i).getEmail().equals(email)) {
//                return true;
//            }
//        }
//        return false;
//    }

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
