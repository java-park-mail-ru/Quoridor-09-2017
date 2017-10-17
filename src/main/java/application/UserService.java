package application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
@Service
@Transactional
public class UserService {
    private final JdbcTemplate template;

    private static final Logger LOGGER = LoggerFactory.getLogger("application");

    @Autowired
    public UserService(JdbcTemplate template) {
        this.template = template;
    }

    private static final RowMapper<User> USER_MAP = (res, num) -> new User(res.getLong("id"),
            res.getString("login"), res.getString("password"), res.getString("email"));

    @SuppressWarnings("ConstantConditions")
    public long addUser(String login, String password, String email) {
        final String query = "INSERT INTO users (login, email, password) VALUES (?, ?, ?) RETURNING *";
        return template.queryForObject(query, USER_MAP, login, email, password).getId();
    }

    public User getUserById(long userId) {
        try {
            final String query = "SELECT * FROM users u WHERE u.id = ?";
            return template.queryForObject(query, USER_MAP, userId);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.error("Can't find in DB user with id = " + userId);
            return null;
        }
    }

    private User getUserByEmail(String email) {
        try {
            final String query = "SELECT * FROM users u WHERE lower(u.email) = lower(?)";
            return template.queryForObject(query, USER_MAP, email);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.error("Can't find in DB user with email = " + email);
            return null;
        }
    }

    private User getUserByLogin(String login) {
        try {
            final String query = "SELECT * FROM users u WHERE lower(u.login) = lower(?)";
            return template.queryForObject(query, USER_MAP, login);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.error("Can't find in DB user with login = " + login);
            return null;
        }
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
        final String query = "UPDATE users SET email = ? WHERE id = ?";
        template.update(query, newEmail, userId);
    }

    public void changeLogin(long userId, String newLogin) {
        final String query = "UPDATE users SET login = ? WHERE id = ?";
        template.update(query, newLogin, userId);
    }

    public void changePassword(long userId, String newPassword) {
        final String query = "UPDATE users SET password = ? WHERE id = ?";
        template.update(query, newPassword, userId);
    }

    public boolean checkPassword(long userId, String password) {
        return this.getUserById(userId).getPassword().equals(password);
    }
}
