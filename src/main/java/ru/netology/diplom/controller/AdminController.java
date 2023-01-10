package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.diplom.entity.StorageFile;
import ru.netology.diplom.repository.StorageFileRepository;
import ru.netology.diplom.service.UserService;

import javax.annotation.security.RolesAllowed;


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
    public StorageFile updateUser(@PathVariable Long id, @RequestBody String name) {
        StorageFile storageFile = userService.putFile(id,name);
        return storageFile;
    }
}
