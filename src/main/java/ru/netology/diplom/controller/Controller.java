package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.diplom.entity.User;

import javax.annotation.security.RolesAllowed;

import ru.netology.diplom.service.UserService;

@RestController
public class Controller {
    @Autowired
    UserService userService;

    @GetMapping("/test")
    public String read() {
        return "test";
    }

    @GetMapping("/admin")
    @RolesAllowed({"ROLE_ADMIN"})
    public User readUser() {
        return (User) userService.loadUserByUsername("admin");
    }
}

