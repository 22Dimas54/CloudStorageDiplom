package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

import ru.netology.diplom.service.UserService;


@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/")
    public String greeting() {
        return "Welcome to Cloud Storage";
    }

    @GetMapping("/cloud")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public String read() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Welcome to Cloud Storage " + auth.getName();
    }

}

