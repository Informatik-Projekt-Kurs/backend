package com.MeetMate.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void addNewUser(MultiValueMap<String, String> data) {
        String email = data.getFirst("email");
        String password = data.getFirst("password");

        User user = new User(email, password);

        if (email != null && password != null && !email.isEmpty() && !password.isEmpty()) {
            System.out.println(email);
            System.out.println(password);

            //check if user already exists
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            userRepository.findUserByEmail(email);
            if (userOptional.isPresent()) {
                throw new IllegalStateException("Email taken");
            }

            userRepository.save(user);
        }

    }

    public void deleteUser(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new IllegalStateException("User does not exist");
        }
        userRepository.deleteById(userId);
    }
}
