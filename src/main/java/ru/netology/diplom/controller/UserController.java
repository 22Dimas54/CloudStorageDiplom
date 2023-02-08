package ru.netology.diplom.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
public class UserController {
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

