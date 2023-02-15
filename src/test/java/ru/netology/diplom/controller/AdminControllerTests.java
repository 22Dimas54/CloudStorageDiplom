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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.netology.diplom.entity.StorageFile;
import ru.netology.diplom.entity.User;
import ru.netology.diplom.service.UserService;

import java.util.Date;

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
        userService.showAllFiles(Integer.valueOf(1),Mockito.mock(User.class));
        Mockito.verify(userService).showAllFiles(Integer.valueOf(1),Mockito.mock(User.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void testDeleteFile() throws Exception {
        given(this.userService.deleteFile("")).willReturn(new ResponseEntity<>(HttpStatus.OK));
        this.mvc.perform(delete("/file")
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
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void testUploadFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "test content".getBytes());

        this.mvc.perform(MockMvcRequestBuilders.fileUpload("/file")
                .file(multipartFile))
                .andExpect(status().isOk());
    }
}
