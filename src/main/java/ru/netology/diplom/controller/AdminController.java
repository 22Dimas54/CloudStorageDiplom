package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.entity.User;
import ru.netology.diplom.service.UserService;

import javax.annotation.security.RolesAllowed;


@RestController
public class AdminController {
    @Autowired
    UserService userService;

    @GetMapping("/admin")
    @RolesAllowed({"ROLE_ADMIN"})
    public User readUser() {
        return (User) userService.loadUserByUsername("admin");
    }

    @PostMapping("/uploadFile")
    @RolesAllowed({"ROLE_ADMIN"})
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return userService.uploadFile(file);
    }
}
