import application.Application;
import application.User;
import application.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    UserService userService;

    private static long testId;
    private static User testUser;

    @Before
    public void setup() {
        UserServiceTest.testId = userService.addUser("test", "12345", "test@mail.ru");
        UserServiceTest.testUser = userService.getUserById(UserServiceTest.testId);
    }

    @Test
    public void addUser() {
        final long id = userService.addUser("daniil", "12345", "daniil@mail.ru");
        assertTrue(id > 0);
    }

//    @Test
//    public void getUserByEmailOrLogin() {
//        final User user1 = userService.getUserByEmailOrLogin("test");
//        assertNotNull(user1);
//        assertEquals(user1.getLogin(), UserServiceTest.testUser.getLogin());
//
//        final User user2 = userService.getUserByEmailOrLogin("test@mail.ru");
//        assertNotNull(user2);
//        assertEquals(user2.getLogin(), UserServiceTest.testUser.getLogin());
//    }

    @Test
    public void getUserById() {
        final User user = userService.getUserById(UserServiceTest.testId);
        assertEquals(user, UserServiceTest.testUser);
    }

    @Test
    public void emailExists() {
        assertTrue(userService.emailExists("test@mail.ru"));
    }

    @Test
    public void loginExists() {
        assertTrue(userService.loginExists("test"));
    }

    @Test
    public void getId() {
        final long id = userService.getId(UserServiceTest.testUser);
        assertEquals(id, UserServiceTest.testId);
    }

    @Test
    public void changeEmail() {
        final User user1 = userService.getUserById(UserServiceTest.testId);
        userService.changeEmail(UserServiceTest.testId, "newTest@mail.ru");
        final User user2 = userService.getUserById(UserServiceTest.testId);
        assertEquals(user1.getEmail(), user2.getEmail());
        assertEquals(user2.getEmail(), "newTest@mail.ru");
    }

    @Test
    public void changeLogin() {
        final User user1 = userService.getUserById(UserServiceTest.testId);
        userService.changeLogin(UserServiceTest.testId, "newTest");
        final User user2 = userService.getUserById(UserServiceTest.testId);
        assertEquals(user1.getLogin(), user2.getLogin());
        assertEquals(user2.getLogin(), "newTest");
    }

    @Test
    public void changePassword() {
        final User user1 = userService.getUserById(UserServiceTest.testId);
        userService.changePassword(UserServiceTest.testId, "67890");
        final User user2 = userService.getUserById(UserServiceTest.testId);
        assertEquals(user1.getPassword(), user2.getPassword());
        assertEquals(user2.getPassword(), "67890");
    }

    @Test
    public void checkPassword() {
        assertTrue(userService.checkPassword(UserServiceTest.testId, "12345"));
    }
}
