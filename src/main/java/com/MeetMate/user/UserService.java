package com.MeetMate.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

//    public void addNewUser(User user) {
//        System.out.println(user);
//        //userRepository.save(user);
//    }

    public String addNewUser(MultiValueMap<String, String> data) {
        String email = data.getFirst("email");
        String password = data.getFirst("password");

        User user = new User(email, password);

        if (email != null && password != null && !email.isEmpty() && !password.isEmpty()){
        System.out.println(email);
        System.out.println(password);

            userRepository.save(user);
            return "Successfully created User";
        } else return "Error";

    }

    public void printBody(StreamingHttpOutputMessage.Body test){
        System.out.println(test.toString());
    }
}
