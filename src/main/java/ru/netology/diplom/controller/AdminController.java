package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.netology.diplom.service.UserService;

import javax.annotation.security.RolesAllowed;

@RestController
public class AdminController {
    @Autowired
    UserService userService;

    @GetMapping("/delete")
    @RolesAllowed({"ROLE_ADMIN"})
    public String deleteFile(@RequestParam("id") Long id) {
        return userService.deleteFile(id);
    }
}
