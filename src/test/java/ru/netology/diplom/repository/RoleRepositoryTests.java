package ru.netology.diplom.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.netology.diplom.DatabaseTest;
import ru.netology.diplom.entity.Role;
import ru.netology.diplom.entity.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class RoleRepositoryTests extends DatabaseTest {
    @Autowired
    RoleRepository roleRepository;

    private final Set<User> users = new HashSet<>();
    private final Role role = new Role(1L, "test", users);

    @BeforeAll
    public static void started() {
        System.out.println("tests started");
    }

    @BeforeEach
    public void init() {
        System.out.println("test started");
        roleRepository.save(role);
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
        var Actual = roleRepository.findByName(role.getName());
        assertEquals(role.getName(), Actual.getName());
    }
}
