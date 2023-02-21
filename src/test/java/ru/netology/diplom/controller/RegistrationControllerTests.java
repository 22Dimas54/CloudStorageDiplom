package ru.netology.diplom.controller;

import org.junit.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.test.context.junit4.SpringRunner;
import ru.netology.diplom.service.UserService;

@RunWith(SpringRunner.class)
public class RegistrationControllerTests {

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
    public void testAddUserVerifyParameters() {
        UserService userService = Mockito.mock(UserService.class);
        var userName = "username";
        var password = "password";
        userService.saveUser(userName, password);
        Mockito.verify(userService).saveUser(userName, password);
    }
}