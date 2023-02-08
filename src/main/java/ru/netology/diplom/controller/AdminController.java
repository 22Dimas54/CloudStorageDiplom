package ru.netology.diplom.controller;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class AdminController {
    @Autowired
    UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    @DeleteMapping(value = "/delete/{id}")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return userService.deleteFile(id);
    }

    @PutMapping("/put/{id}")
    @RolesAllowed({"ROLE_ADMIN"})
    public Object putFile(@PathVariable Long id, @RequestBody String name) {
        return userService.putFile(id, name);
    }

    @PostMapping("/file")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return userService.uploadFile(file);
    }

    @GetMapping("/list")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public List<StorageFile> showAllFiles(@RequestParam("limit") Integer limit) {
        return userService.showAllFiles(limit);
    }

    @GetMapping(path = "/get/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<InputStreamResource> download(@PathVariable("id") Long id) {
        try {
            Optional<StorageFile> foundFile = userService.findById(id);
            InputStreamResource resource = null;
            if (!foundFile.isEmpty()) {
                resource = userService.download(foundFile.get().getName());
            }
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + foundFile.get().getName())
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login")
    public JSONObject login(@RequestBody @Valid JSONObject requestBody) throws JsonProcessingException {
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
            return jsonObj;
    }
}
