package application.users;

import application.dao.UserService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("InstanceMethodNamingConvention")
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
public class UserControllerTest {

    @Autowired
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    private Long id;

    @Before
    public void setup() {
        id = userService.addUser("test12345", "12345", "test12345@mail.ru");
    }

    @Test
    public void success_get_current_user() throws Exception {
        mockMvc.perform(get("/currentUser")
                .sessionAttr("userId", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("login").value("test12345"))
                .andExpect(jsonPath("email").value("test12345@mail.ru"));
    }

    @Test
    public void unsuccess_get_current_user() throws Exception {
        mockMvc.perform(get("/currentUser"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Invalid session"));
    }

    @Test
    public void success_change_login() throws Exception {
        mockMvc.perform(patch("/currentUser/changeLogin")
                .sessionAttr("userId", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test1234567890\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("info").value("Login changed"));
    }

    @Test
    public void unsuccess_change_login() throws Exception {
        mockMvc.perform(patch("/currentUser/changeLogin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test1234567890\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Invalid session"));

        mockMvc.perform(patch("/currentUser/changeLogin")
                .sessionAttr("userId", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test12345\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Login already in use"));

        mockMvc.perform(patch("/currentUser/changeLogin")
                .sessionAttr("userId", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"te\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Login should be 3 to 30 characters.\n"));
    }

    @Test
    public void success_change_email() throws Exception {
        mockMvc.perform(patch("/currentUser/changeEmail")
                .sessionAttr("userId", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test1234567890@mail.ru\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("info").value("Email changed"));
    }

    @Test
    public void unsuccess_change_email() throws Exception {
        mockMvc.perform(patch("/currentUser/changeEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test1234567890@mail.ru\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Invalid session"));

        mockMvc.perform(patch("/currentUser/changeEmail")
                .sessionAttr("userId", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test12345@mail.ru\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Email already in use"));

        mockMvc.perform(patch("/currentUser/changeEmail")
                .sessionAttr("userId", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"te\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Email should be 4 to 30 characters.\n"));
    }

    @Test
    public void success_change_password() throws Exception {
        mockMvc.perform(patch("/currentUser/changePass")
                .sessionAttr("userId", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newPassword\":\"67890\", \"oldPassword\":\"12345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("info").value("Password changed"));
    }

    @Test
    public void unsuccess_change_password() throws Exception {
        mockMvc.perform(patch("/currentUser/changePass")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newPassword\":\"67890\", \"oldPassword\":\"12345\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Invalid session"));

        mockMvc.perform(patch("/currentUser/changePass")
                .sessionAttr("userId", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newPassword\":\"67890\", \"oldPassword\":\"123454321\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Wrong old password"));

        mockMvc.perform(patch("/currentUser/changePass")
                .sessionAttr("userId", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newPassword\":\"67\", \"oldPassword\":\"12345\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Password should be 4 to 30 characters.\n"));
    }

    @Test
    public void success_get_scoreboard() throws Exception {
        final long testId1 = userService.addUser("user1", "12345", "user1@mail.ru");
        final long testId2 = userService.addUser("user2", "12345", "user2@mail.ru");
        final long testId3 = userService.addUser("user3", "12345", "user3@mail.ru");

        userService.increaseScore(testId3);

        userService.increaseScore(testId1);
        userService.increaseScore(testId1);

        userService.increaseScore(testId2);
        userService.increaseScore(testId2);
        userService.increaseScore(testId2);

        mockMvc.perform(get("/scoreBoard")
                .param("offset", "0")
                .param("limit", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scoreboard", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.scoreboard[0].userName").value("user2"))
                .andExpect(jsonPath("$.scoreboard[0].score").value("3"))
                .andExpect(jsonPath("$.scoreboard[1].userName").value("user1"))
                .andExpect(jsonPath("$.scoreboard[1].score").value("2"))
                .andExpect(jsonPath("$.scoreboard[2].userName").value("user3"))
                .andExpect(jsonPath("$.scoreboard[2].score").value("1"));
    }

    @Test
    public void unsuccess_get_scoreboard() throws Exception {
        mockMvc.perform(get("/scoreBoard")
                .param("offset", "2")
                .param("limit", "asdf"))
                .andExpect(status().isBadRequest());
    }
}
