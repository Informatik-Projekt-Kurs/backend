package com.MeetMate.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/test/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "get")
    @ResponseBody
    public User getUser(@RequestParam(name = "id") Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping(path = "getAll")
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping(path = "register")
    public void registerNewUser(@RequestParam String token) {
        userService.registerNewUser(token);
    }

    @PutMapping(path = "put")
    public void updateUser(@RequestParam MultiValueMap<String, String> formData) {
        System.out.println(formData);
        userService.updateUser(formData);
    }

    @PostMapping(path = "auth")
    public ResponseEntity<String> authenticateUser(@RequestParam String token){
        return ResponseEntity.ok(userService.authenticateUser(token));
    }

    @DeleteMapping(path = "delete")
    public void deleteUser(@RequestParam(name = "id") Long userId) {
        userService.deleteUser(userId);
    }

}
