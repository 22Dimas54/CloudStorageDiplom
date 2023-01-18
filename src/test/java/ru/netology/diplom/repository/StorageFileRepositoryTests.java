package ru.netology.diplom.repository;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.netology.diplom.DatabaseTest;
import ru.netology.diplom.entity.StorageFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class StorageFileRepositoryTests extends DatabaseTest {
    @Autowired
    StorageFileRepository storageFileRepository;
    private final StorageFile storageFile = new StorageFile("test", 1L, new Date());

    @BeforeAll
    public static void started() {
        System.out.println("tests started");
    }

    @BeforeEach
    public void init() {
        System.out.println("test started");
        storageFileRepository.save(storageFile);
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
        List<StorageFile> storageFiles = new ArrayList<>();
        storageFiles.add(storageFile);
        int Expected = storageFiles.size();
        int Actual = storageFileRepository.findBy().size();
        assertEquals(Expected, Actual);
    }

    @Test
    void testFindByName() {
        var Actual = storageFileRepository.findByName(storageFile.getName());
        assertEquals(storageFile, Actual);
    }
}
