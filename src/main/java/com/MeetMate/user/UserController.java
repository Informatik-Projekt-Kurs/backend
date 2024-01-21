package com.MeetMate.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.StringBufferInputStream;
import java.util.List;

@RestController
@RequestMapping(path = "api/test/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(path = "get")
    @ResponseBody
    public ResponseEntity<User> getUser(@RequestParam String token) {
        return ResponseEntity.ok(userService.getUserByEmail(token));
    }

    @GetMapping(path = "getAll")
    @ResponseBody
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping(path = "register")
    @ResponseBody
    public ResponseEntity<String> registerNewUser(@RequestParam String token) {
        return ResponseEntity.ok(userService.registerNewUser(token));
    }

    @PutMapping(path = "update")
    @ResponseBody
    public ResponseEntity<String> updateUser(@RequestParam String token) {
        userService.updateUser(token);
    }

    @PostMapping(path = "auth")
    @ResponseBody
    public ResponseEntity<String> authenticateUser(@RequestParam String token){
        return ResponseEntity.ok(userService.authenticateUser(token));
    }

    @DeleteMapping(path = "delete")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@RequestParam String token) {
        userService.deleteUser(token);
        return ResponseEntity.noContent().build();
    }

}