package ru.netology.diplom.controller;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.entity.StorageFile;
import ru.netology.diplom.entity.User;
import ru.netology.diplom.service.UserService;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.io.IOException;
import java.time.Instant;

@RequiredArgsConstructor
@RestController
public class AdminController {
    @Autowired
    UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    @DeleteMapping(value = "/delete/{id}")
//    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return userService.deleteFile(id);
    }

    @PutMapping("/file")
//    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<StorageFile> putFile(@RequestParam("filename") String filename, @RequestBody @Valid JSONObject requestBody) {
        try {
            return userService.putFile(filename, (String) requestBody.get("filename"));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/file")
//    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return userService.uploadFile(file);
    }

    @GetMapping("/list")
//    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<JSONArray> showAllFiles(@RequestParam("limit") Integer limit) {
        try {
            var storageFiles = userService.showAllFiles(limit);

            JSONArray files = new JSONArray();
            for (StorageFile storageFile : storageFiles) {
                JSONObject file = new JSONObject();
                file.put("filename", storageFile.getName());
                file.put("data", storageFile.getChangeDate());
                file.put("size", storageFile.getSize());
                files.add(file);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("files", files);
            return ResponseEntity.ok(files);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping(path = "/file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<InputStreamResource> download(@RequestParam("filename") String filename) {
        try {
            var foundFile = userService.findById(filename);
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
            var now = Instant.now();
            var expiry = 36000L;
            var scope =
                    authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(joining(" "));
            var claims =
                    JwtClaimsSet.builder()
                            .issuer("example.io")
                            .issuedAt(now)
                            .expiresAt(now.plusSeconds(expiry))
                            .subject(format("%s,%s", user.getId(), user.getUsername()))
                            .claim("roles", scope)
                            .build();
            var token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
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
//admin
//write