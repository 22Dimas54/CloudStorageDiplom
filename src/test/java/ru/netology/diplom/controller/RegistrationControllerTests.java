package ru.netology.diplom.controller;

import org.junit.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.diplom.service.UserService;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTests {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;


    @BeforeAll
    public static void started() {
        System.out.println("tests started");
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        System.out.println("test started");
    }

    @AfterEach
    public void finished() {
        System.out.println("\ntest compiled");
    }

    @AfterAll
    public static void finishedAll() {
        System.out.println("tests finished");
    }


    @Test
    public void testAddUser() throws Exception {
        when(userService.saveUser("username", "password")).thenReturn(true);
        this.mvc.perform(post("/registration")
                .param("userName", "username")
                .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(content().string("The user has been successfully registered"));

        when(userService.saveUser("username", "password")).thenReturn(false);
        this.mvc.perform(post("/registration")
                .param("userName", "username")
                .param("password", "password"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error"));
    }
}
