package com.MeetMate.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/test/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(path = "get")
    @ResponseBody
    public User getUser(@RequestParam String token) {
        return userService.getUserByEmail(token);
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

    @PutMapping(path = "update")
    public void updateUser(@RequestParam String token) {
        userService.updateUser(token);
    }

    @PostMapping(path = "auth")
    public ResponseEntity<String> authenticateUser(@RequestParam String token){
        return ResponseEntity.ok(userService.authenticateUser(token));
    }

    @DeleteMapping(path = "delete")
    public void deleteUser(@RequestParam String token) {
        userService.deleteUser(token);
    }

}
