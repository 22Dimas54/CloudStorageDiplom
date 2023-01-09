package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
}
