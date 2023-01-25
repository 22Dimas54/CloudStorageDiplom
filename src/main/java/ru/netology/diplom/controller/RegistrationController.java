package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.diplom.service.UserService;

@RestController
public class RegistrationController {
    @Autowired
    private UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<String> addUser(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        if (userService.saveUser(userName, password)) {
            return new ResponseEntity("The user has been successfully registered", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Error", HttpStatus.NOT_FOUND);
        }
    }
}
