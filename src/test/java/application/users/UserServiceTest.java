package application.users;

import application.dao.User;
import application.dao.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
@Transactional
public class UserServiceTest {

    @Autowired
    UserService userService;

    private long testId;
    private User testUser;

    @Before
    public void setup() {
        testId = userService.addUser("test12345", "12345", "test12345@mail.ru");
        testUser = userService.getUserById(testId);
    }

    @Test
    public void addUser() {
        final long id = userService.addUser("daniil12345", "12345", "daniil12345@mail.ru");
        assertTrue(id > 0);
    }

    @Test
    public void getUserByEmailOrLogin() {
        final User user1 = userService.getUserByEmailOrLogin("test12345");
        assertNotNull(user1);
        assertEquals(user1.getLogin(), testUser.getLogin());

        final User user2 = userService.getUserByEmailOrLogin("test12345@mail.ru");
        assertNotNull(user2);
        assertEquals(user2.getLogin(), testUser.getLogin());

        final User user3 = userService.getUserByEmailOrLogin("asdfg");
        assertNull(user3);
    }

    @Test
    public void getUserById() {
        final User user = userService.getUserById(testId);
        assertEquals(user.getId(), testUser.getId());
        assertEquals(user.getLogin(), testUser.getLogin());
        assertEquals(user.getEmail(), testUser.getEmail());
        assertEquals(user.getPassword(), testUser.getPassword());
    }

    @Test
    public void emailExists() {
        assertTrue(userService.emailExists("test12345@mail.ru"));
    }

    @Test
    public void loginExists() {
        assertTrue(userService.loginExists("test12345"));
    }

    @Test
    public void getId() {
        final long id = userService.getId(testUser);
        assertEquals(id, testId);
    }

    @Test
    public void changeEmail() {
        addUser();
        final User user1 = userService.getUserByEmailOrLogin("daniil12345");
        userService.changeEmail(user1.getId(), "newDaniil12345@mail.ru");
        final User user2 = userService.getUserByEmailOrLogin("daniil12345");
        assertNotEquals(user1.getEmail(), user2.getEmail());
        assertEquals(user2.getEmail(), "newDaniil12345@mail.ru");
    }

    @Test
    public void changeLogin() {
        addUser();
        final User user1 = userService.getUserByEmailOrLogin("daniil12345@mail.ru");
        userService.changeLogin(user1.getId(), "newDaniil12345");
        final User user2 = userService.getUserByEmailOrLogin("daniil12345@mail.ru");
        assertNotEquals(user1.getLogin(), user2.getLogin());
        assertEquals(user2.getLogin(), "newDaniil12345");
    }

    @Test
    public void changePassword() {
        addUser();
        final User user1 = userService.getUserByEmailOrLogin("daniil12345");
        userService.changePassword(user1.getId(), "67890");
        final User user2 = userService.getUserByEmailOrLogin("daniil12345");
        assertNotEquals(user1.getPassword(), user2.getPassword());
        assertEquals(user2.getPassword(), "67890");
    }

    @Test
    public void checkPassword() {
        assertTrue(userService.checkPassword(testId, "12345"));
    }
}
