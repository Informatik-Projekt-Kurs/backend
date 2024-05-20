package com.MeetMate._experiments;

import com.MeetMate.user.User;
import com.MeetMate.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


  @RequiredArgsConstructor
  @Service
  public class TestService {

    private final UserRepository userRepository;

  public User getUsers(long id) {
    return userRepository.findUserById(id)
        .orElseThrow(() -> new IllegalStateException("User not found"));
  }

  @Transactional
  public void updateUser(String name, String email, String password) {
    User user =
        userRepository
            .findUserByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User does not exist."));

    if (password != null) user.setPassword(password);
    if (name != null) user.setName(name);
  }

  public void createUser(String name, String email, String password) {
    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPassword(password);
    userRepository.save(user);
  }
}
