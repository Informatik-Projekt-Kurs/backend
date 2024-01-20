package com.MeetMate.user;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getUser(Long userId) {
    Optional<User> userOptional = userRepository.findUserById(userId);
    // ???
    userRepository.findUserById(userId);
    if (userOptional.isPresent()) {
      return userOptional.get();
    }
    throw new IllegalStateException("User does not exist");
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public void addNewUser(MultiValueMap<String, String> data) {
    String email = data.getFirst("email");
    String password = data.getFirst("password");

    User user = new User(email, password);

    if (email != null && password != null && !email.isEmpty() && !password.isEmpty()) {
      System.out.println(email);
      System.out.println(password);

      // check if user already exists
      Optional<User> userOptional = userRepository.findUserByEmail(email);
      userRepository.findUserByEmail(email);
      if (userOptional.isPresent()) {
        throw new IllegalStateException("Email taken");
      }

      userRepository.save(user);
    }
  }

  // doesn't need repository methods
  @Transactional
  public void updateUser(MultiValueMap<String, String> data) {
    long id;
    try {
      id = Long.parseLong(data.getFirst("id"));
    } catch (NumberFormatException nfe) {
      throw new IllegalStateException("Invalid id");
    }
    String email = data.getFirst("email");
    String password = data.getFirst("password");

    // is converted from optional to user bc it always exists
    User user =
        userRepository
            .findUserById(id)
            .orElseThrow(() -> new IllegalStateException("User does not exist."));

    if (userRepository.findUserByEmail(email).isEmpty()) {
      user.setEmail(email);
    } // throw error
    user.setPassword(password);
  }

  public void deleteUser(Long userId) {
    boolean exists = userRepository.existsById(userId);
    if (!exists) {
      throw new IllegalStateException("User does not exist");
    }
    userRepository.deleteById(userId);
  }
}
