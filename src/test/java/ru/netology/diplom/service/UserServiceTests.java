package ru.netology.diplom.service;

import org.junit.Before;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.entity.StorageFile;
import ru.netology.diplom.repository.StorageFileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest()
public class UserServiceTests {

    @InjectMocks
    UserService userService;
    @Mock
    StorageFileRepository storageFileRepository;
    private final Long ID = 1L;
    private final StorageFile storageFile = Mockito.mock(StorageFile.class);

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
        var Actual =  userService.putFile(ID, "");
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
        var Expected =  new ResponseEntity<String>("Error", HttpStatus.NOT_FOUND);
        var Actual = userService.uploadFile(file);
        assertEquals(Expected, Actual);
        Mockito.verify(storageFileRepository, Mockito.times(0)).save(storageFile);
    }
}
