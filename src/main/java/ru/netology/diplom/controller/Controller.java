package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

import ru.netology.diplom.service.UserService;

@RestController
public class Controller {
    @Autowired
    UserService userService;

    @GetMapping("/")
    public String greeting() {
        return "Welcome to Cloud Storage";
    }

    @GetMapping("/cloud")
    @RolesAllowed({"ROLE_USER","ROLE_ADMIN"})
    public String read() {
        return "cloud";
    }
}

