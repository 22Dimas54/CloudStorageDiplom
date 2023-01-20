package ru.netology.diplom.controller;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest()
public class UserControllerTests {
    @InjectMocks
    UserController userController;

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
    void testGreeting() {
        var Expected = "Welcome to Cloud Storage";
        var Actual = userController.greeting();
        assertEquals(Expected, Actual);
    }
}
