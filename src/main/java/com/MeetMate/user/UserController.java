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

    @GetMapping
    public List<User> getUsers(){
        return userService.getUsers();
    }

//    @PostMapping
////    public void registerNewUser(@RequestBody StreamingHttpOutputMessage.Body hallo){
////                                    //User user) {
////        userService.printBody(hallo);
////        //userService.addNewUser(user);
////    }

    @PostMapping("/signup")
    @ResponseBody
    public String registerNewUser(@RequestBody MultiValueMap<String, String> formData) {
        return userService.addNewUser(formData);
    }

}
