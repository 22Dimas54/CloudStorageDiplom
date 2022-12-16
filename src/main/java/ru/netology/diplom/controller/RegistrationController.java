package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.diplom.service.UserService;

@RestController
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String addUser(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        if (userService.saveUser(userName, password)) {
            return "The user has been successfully registered";
        } else {
            return "Failed to register a user, maybe a user with that name exists!";
        }
    }
}
