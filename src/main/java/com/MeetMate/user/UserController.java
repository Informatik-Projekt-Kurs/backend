package com.MeetMate.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@RestController
@RequestMapping(path = "api/test/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "get")
    public User getUser(@RequestParam(name = "id") Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping(path = "getAll")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping(path = "post")
    public void registerNewUser(@RequestBody MultiValueMap<String, String> formData) {
        userService.addNewUser(formData);
    }

    @PutMapping(path = "put")
    public void updateUser(@RequestParam MultiValueMap<String, String> formData) {
        System.out.println(formData);
        userService.updateUser(formData);
    }

    @DeleteMapping(path = "delete")
    public void deleteUser(@RequestParam(name = "id") Long userId) {
        userService.deleteUser(userId);
    }

}
