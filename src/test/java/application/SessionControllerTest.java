package application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"InstanceMethodNamingConvention", "ConstantConditions"})
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
public class SessionControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        userService.clearDB();
        userService.addUser("test12345", "12345", "test12345@mail.ru");
    }

    @Test
    public void success_signin_with_login_or_email() throws Exception {
        mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test12345\", \"password\":\"12345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("login").value("test12345"))
                .andExpect(jsonPath("email").value("test12345@mail.ru"));

        mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test12345@mail.ru\", \"password\":\"12345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("login").value("test12345"))
                .andExpect(jsonPath("email").value("test12345@mail.ru"));
    }

    @Test
    public void unsuccess_signin() throws Exception {
        mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test1234567890@mail.ru\", \"password\":\"12345\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Wrong login or email"));

        mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"test12345@mail.ru\", \"password\":\"12345678\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Wrong password"));
    }

    @Test
    public void success_signup() throws Exception {
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"daniil12345\", \"password\":\"12345\", \"email\":\"daniil12345@mail.ru\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("login").value("daniil12345"))
                .andExpect(jsonPath("email").value("daniil12345@mail.ru"));
    }

    @Test
    public void unsuccess_signun_login_or_email_allready_in_use() throws Exception {
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
    public void unsuccess_signun_with_bad_data() throws Exception {
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
                .content("{\"login\":\"test12345\", \"password\":\"12345\"}"))
                .andDo(mvcResult -> mockMvc.perform(delete("/signout")
                        .sessionAttr("userId", mvcResult.getRequest().getSession().getAttribute("userId")))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("info").value("Successful logout")));

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"daniil12345\", \"password\":\"12345\", \"email\":\"daniil12345@mail.ru\"}"))
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