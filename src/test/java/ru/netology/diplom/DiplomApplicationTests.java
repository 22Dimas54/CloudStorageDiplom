package ru.netology.diplom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.diplom.repository.RoleRepository;
import ru.netology.diplom.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest()
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DiplomApplicationTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

//    @Value("${postgres.databaseName}")
//    private String POSTGRES_DATABASE_NAME;
//    @Value("${postgres.username}")
//    private String POSTGRES_USERNAME;
//    @Value("${postgres.password}")
//    private String POSTGRES_PASSWORD;

    @Container
    public static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgrespw");

    public static class DataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            );
        }
    }

    @Test
    void testFindIdUser() {
        Long Expected = 1L;
        Long Actual = userRepository.findByUserName("admin").getId();
        assertEquals(Expected, Actual);
    }
    @Test
    void testFindIdRole() {
        Long Expected = 2L;
        Long Actual = roleRepository.findByName("ROLE_ADMIN").getId();
        assertEquals(Expected, Actual);
    }
}
