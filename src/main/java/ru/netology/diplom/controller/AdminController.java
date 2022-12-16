package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
