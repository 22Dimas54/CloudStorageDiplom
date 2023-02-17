package ru.netology.diplom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.config.JwtTokenUtil;
import ru.netology.diplom.entity.StorageFile;
import ru.netology.diplom.entity.User;
import ru.netology.diplom.service.UserService;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class AdminController {
    @Autowired
    UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @DeleteMapping(value = "/file")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity delete(@RequestParam("filename") String fileName) {
        return userService.deleteFile(fileName);
    }

    @PutMapping("/file")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<StorageFile> putFile(@RequestParam("filename") String fileName, @RequestBody @Valid JSONObject requestBody) {
        try {
            return userService.putFile(fileName, (String) requestBody.get("filename"));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/file")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.findByUserName(auth.getName()).get();
        return userService.uploadFile(file, user);
    }

    @GetMapping("/list")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<JSONArray> showAllFiles(@RequestParam("limit") Integer limit) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.findByUserName(auth.getName()).get();
        return userService.showAllFiles(limit, user);
    }

    @GetMapping(path = "/file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<InputStreamResource> download(@RequestParam("filename") String filename) {
        try {
            var foundFile = userService.findByName(filename);
            InputStreamResource resource = null;
            if (foundFile != null) {
                resource = userService.download(foundFile.getName());
            }
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + foundFile.getName())
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid JSONObject requestBody) throws JsonProcessingException {
        try {
            var authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(requestBody.get("login"), requestBody.get("password")));
            var user = (User) authentication.getPrincipal();

            var token = this.jwtTokenUtil.generateAccessToken(user);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("auth-token", token);
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(jsonObj);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
////admin
////write