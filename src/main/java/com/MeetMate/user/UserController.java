package com.MeetMate.user;

import org.springframework.beans.factory.annotation.Autowired;
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
    public User getUser(@RequestParam(name = "id") Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping(path = "getAll")
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @PostMapping(path = "post")
    @ResponseBody
    public void registerNewUser(@RequestBody MultiValueMap<String, String> formData) {
        userService.addNewUser(formData);
    }

    @DeleteMapping(path = "delete")
    @ResponseBody
    public void deleteUser(@RequestParam(name = "id") Long userId) {
        userService.deleteUser(userId);
    }

}
