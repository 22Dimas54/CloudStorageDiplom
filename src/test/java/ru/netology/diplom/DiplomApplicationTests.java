package ru.netology.diplom;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.diplom.entity.StorageFile;
import ru.netology.diplom.repository.RoleRepository;
import ru.netology.diplom.repository.StorageFileRepository;
import ru.netology.diplom.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest()
@ExtendWith(MockitoExtension.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DiplomApplicationTests {

    @Autowired
    RoleRepository roleRepository;
    @InjectMocks
    UserService userService;
    @Mock
    StorageFileRepository storageFileRepository;
    private final StorageFile storageFile = Mockito.mock(StorageFile.class);
    private final Long ID = 1L;
//    @Value("${postgres.databaseName}")
//    private String POSTGRES_DATABASE_NAME;
//    @Value("${postgres.username}")
//    private String POSTGRES_USERNAME;
//    @Value("${postgres.password}")
//    private String POSTGRES_PASSWORD;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

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
    void testShowAllFiles() {
        List<StorageFile> storageFiles = new ArrayList<>();
        storageFiles.add(storageFile);
        Mockito.when(storageFileRepository.findBy()).thenReturn(storageFiles);
        int Expected = 1;
        int Actual = userService.showAllFiles().size();
        assertEquals(Expected, Actual);
    }

    @Test
    void testPutFile() {
        Mockito.when(storageFileRepository.findById(1L)).thenReturn(Optional.ofNullable(storageFile));
        StorageFile Actual = userService.putFile(ID, "");
        assertEquals(storageFile, Actual);
        Mockito.verify(storageFileRepository, Mockito.times(1)).findById(ID);
        Mockito.verify(storageFileRepository, Mockito.times(0)).save(storageFile);
    }

    @Test
    void testDeleteFile() {
        Mockito.when(storageFileRepository.existsById(1L)).thenReturn(false);
        var Actual = userService.deleteFile(ID);
        var Expected = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        assertEquals(Expected, Actual);
        Mockito.verify(storageFileRepository, Mockito.times(1)).existsById(ID);
        Mockito.verify(storageFileRepository, Mockito.times(0)).deleteById(ID);
    }

    @Test
    void testUploadFile() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        var Expected = "Вам не удалось загрузить null => null";
        var Actual = userService.uploadFile(file);
        assertEquals(Expected, Actual);
        Mockito.verify(storageFileRepository, Mockito.times(0)).save(storageFile);
    }
}
