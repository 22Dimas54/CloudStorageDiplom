package ru.netology.diplom.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.netology.diplom.DatabaseTest;
import ru.netology.diplom.entity.Role;
import ru.netology.diplom.entity.User;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserRepositoryTests extends DatabaseTest {
    @Autowired
    UserRepository userRepository;

    private final Set<Role> roles = new HashSet<>();
    private final User user = new User(1L, "userName", "password", "passwordConfirm", roles);

    @BeforeAll
    public static void started() {
        System.out.println("tests started");
    }

    @BeforeEach
    public void init() {
        System.out.println("test started");
        userRepository.save(user);
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
    void testFindBy() {
        var Actual = userRepository.findByUserName(user.getUsername());
        assertEquals(user.getUsername(), Actual.getUsername());
    }
}
