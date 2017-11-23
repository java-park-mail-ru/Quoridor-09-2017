package application.users;

import application.dao.User;
import application.dao.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"InstanceMethodNamingConvention", "ConstantConditions"})
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
public class SessionControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        final User user = new User(1L, "test", "12345", "test@mail.ru");
        when(userService.emailExists("test@mail.ru")).thenReturn(true);
        when(userService.loginExists("test")).thenReturn(true);
        when(userService.getUserByEmailOrLogin("test")).thenReturn(user);
        when(userService.getUserByEmailOrLogin("test@mail.ru")).thenReturn(user);
        when(userService.getId(user)).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(userService.checkPassword(1L, "12345")).thenReturn(true);
        when(userService.addUser("test", "12345", "test@mail.ru")).thenReturn(1L);
    }

    @Test
    public void success_signin_with_login_or_email() throws Exception {
        mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test\", \"password\":\"12345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("login").value("test"))
                .andExpect(jsonPath("email").value("test@mail.ru"));

                mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test@mail.ru\", \"password\":\"12345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("login").value("test"))
                .andExpect(jsonPath("email").value("test@mail.ru"));
    }

    @Test
    public void unsuccess_signin() throws Exception {
        when(userService.emailExists("test1234567890@mail.ru")).thenReturn(false);

        mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test1234567890@mail.ru\", \"password\":\"12345\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Wrong login or email"));

        mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test@mail.ru\", \"password\":\"12345678\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Wrong password"));
    }

    @Test
    public void success_signup() throws Exception {
        when(userService.emailExists("daniil@mail.ru")).thenReturn(false);
        when(userService.loginExists("daniil")).thenReturn(false);
        when(userService.addUser("daniil", "12345", "daniil@mail.ru")).thenReturn(2L);
        when(userService.getUserById(2L)).thenReturn(new User(2L, "daniil", "12345", "daniil@mail.ru"));

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"daniil\", \"password\":\"12345\", \"email\":\"daniil@mail.ru\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("login").value("daniil"))
                .andExpect(jsonPath("email").value("daniil@mail.ru"));
    }

    @Test
    public void unsuccess_signup_login_or_email_allready_in_use() throws Exception {
        when(userService.emailExists("test12345@mail.ru")).thenReturn(true);
        when(userService.loginExists("test12345")).thenReturn(true);

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"daniil12345\", \"password\":\"12345\", \"email\":\"test12345@mail.ru\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Email already in use"));

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test12345\", \"password\":\"12345\", \"email\":\"daniil12345@mail.ru\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Login already in use"));
    }

    @Test
    public void unsuccess_signup_with_bad_data() throws Exception {
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"da\", \"password\":\"12\", \"email\":\"test_mail.ru\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Incorrect Email\n" +
                        "Login should be 3 to 30 characters.\n" +
                        "Password should be 4 to 30 characters.\n"));
    }

    @Test
    public void success_signout() throws Exception {
        mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test\", \"password\":\"12345\"}"))
                .andDo(mvcResult -> mockMvc.perform(delete("/signout")
                        .sessionAttr("userId", mvcResult.getRequest().getSession().getAttribute("userId")))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("info").value("Successful logout")));

        when(userService.emailExists("daniil@mail.ru")).thenReturn(false);
        when(userService.loginExists("daniil")).thenReturn(false);
        when(userService.addUser("daniil", "12345", "daniil@mail.ru")).thenReturn(2L);
        when(userService.getUserById(2L)).thenReturn(new User(2L, "daniil", "12345", "daniil@mail.ru"));

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"daniil\", \"password\":\"12345\", \"email\":\"daniil@mail.ru\"}"))
                .andDo(mvcResult -> mockMvc.perform(delete("/signout")
                        .sessionAttr("userId", mvcResult.getRequest().getSession().getAttribute("userId")))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("info").value("Successful logout")));
    }

    @Test
    public void unsuccess_signout() throws Exception {
        mockMvc.perform(delete("/signout"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("error").value("Unauthorized"));
    }

}