package application;

import java.util.HashMap;

public class UserDB {
    private HashMap<Long, User> db;
    private static long id = 1;

    public UserDB() {
        db = new HashMap<>();
    }

    public long addUser(User user) {
        db.put(id, user);
        return id++;
    }

    public User getUser(long id) {
        return db.get(id);
    }

    public long getId(String login) {
        for (long i = 1; i < id; i++) {
            if (db.get(i).getLogin().equals(login)) {
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

    public boolean checkPassword(long id, String password) {
        return this.getUser(id).getPassword().equals(password);
    }
}
