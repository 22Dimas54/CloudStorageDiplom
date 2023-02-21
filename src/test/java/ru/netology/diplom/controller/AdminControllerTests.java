package ru.netology.diplom.controller;

import com.nimbusds.jose.shaded.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.config.JwtTokenUtil;
import ru.netology.diplom.entity.StorageFile;
import ru.netology.diplom.entity.User;
import ru.netology.diplom.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminControllerTests {
    @MockBean
    private UserService userService;

    @Autowired
    private AdminController adminController;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @BeforeAll
    public static void started() {
        System.out.println("tests started");
    }

    @BeforeEach
    public void init() {
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
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void deleteTest() {
        when(userService.deleteFile(anyString())).thenReturn(ResponseEntity.ok().build());
        ResponseEntity response = adminController.delete("test.txt");
        verify(userService).deleteFile("test.txt");
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void putFileTest() {
        when(userService.putFile(anyString(), anyString())).thenReturn(ResponseEntity.ok().build());
        JSONObject requestBody = new JSONObject();
        requestBody.put("filename", "test.txt");
        ResponseEntity response = adminController.putFile("test.txt", requestBody);
        verify(userService).putFile("test.txt", "test.txt");
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void testUploadFile() {
        var mockUser = Mockito.mock(User.class);
        var file = Mockito.mock(MultipartFile.class);
        var mockResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(userService.uploadFile(file, mockUser)).thenReturn(mockResponse);
        adminController.uploadFile(file);
        var userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userService).uploadFile(Mockito.eq(file), userCaptor.capture());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void testShowAllFiles() {
        var mockUser = Mockito.mock(User.class);
        var mockResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(userService.showAllFiles(10, mockUser)).thenReturn(mockResponse);
        adminController.showAllFiles(10);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userService).showAllFiles(Mockito.eq(10), userCaptor.capture());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void testDownload() {
        when(userService.findByName("test.txt")).thenReturn(Mockito.mock(StorageFile.class));
        ResponseEntity<InputStreamResource> response = adminController.download("test.txt");
        verify(userService).findByName("test.txt");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testLogin() {
        User user = Mockito.mock(User.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        String token = "test-token";
        when(jwtTokenUtil.generateAccessToken(user)).thenReturn(token);
        JSONObject requestBody = new JSONObject();
        requestBody.put("login", "test");
        requestBody.put("password", "test");
        ResponseEntity<Object> response = adminController.login(requestBody);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil).generateAccessToken(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
        JSONObject jsonObj = (JSONObject) response.getBody();
        assertEquals(token, jsonObj.get("auth-token"));
    }
}
