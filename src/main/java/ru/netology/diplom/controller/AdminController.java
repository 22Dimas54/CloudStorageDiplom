package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.entity.StorageFile;
import ru.netology.diplom.service.UserService;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
public class AdminController {
    @Autowired
    UserService userService;

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
    public List<StorageFile> showAllFiles() {
        return userService.showAllFiles();
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


}
