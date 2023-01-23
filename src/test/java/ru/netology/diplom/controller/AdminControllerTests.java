package ru.netology.diplom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.entity.StorageFile;
import ru.netology.diplom.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AdminController.class)
public class AdminControllerTests {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    private final Long ID = 1L;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void testShowAllFiles() throws Exception {
        List<StorageFile> storageFiles = new ArrayList<>();
        var storageFile = StorageFile.builder()
                .id(ID)
                .name("test")
                .size(ID)
                .changeDate(new Date(2023, 1, 1, 1, 1, 1))
                .build();
        storageFiles.add(storageFile);
        given(this.userService.showAllFiles()).willReturn(storageFiles);
        this.mvc.perform(get("/list")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content()
                .string("[{\"id\":1,\"name\":\"test\",\"size\":1,\"changeDate\":61633332061000}]"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void testDeleteFile() throws Exception {
        given(this.userService.deleteFile(1L)).willReturn(new ResponseEntity<>(HttpStatus.OK));
        this.mvc.perform(delete("/delete/1")
                .accept(MediaType.ALL))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void testPutFile() throws Exception {
        var storageFile = StorageFile.builder()
                .id(ID)
                .name("test")
                .size(ID)
                .changeDate(new Date(2023, 1, 1, 1, 1, 1))
                .build();
        mvc.perform(
                put("/put/{id}", ID)
                        .content(objectMapper.writeValueAsString(storageFile))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
    public void testUploadFile() throws Exception{
        given(this.userService.uploadFile(Mockito.mock(MultipartFile.class))).willReturn("Вам не удалось загрузить null => null");
        this.mvc.perform(post("/file")
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content()
                .string("Вам не удалось загрузить null => null"));
    }
}
