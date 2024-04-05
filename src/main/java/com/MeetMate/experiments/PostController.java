package com.MeetMate.experiments;

import com.MeetMate.user.User;
import com.MeetMate.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PostController {
  private final UserRepository userRepository;

  @QueryMapping
  public User getUsers(@Argument Long id) {
    System.out.println("Interesting");
    return userRepository.findUserById(id)
        .orElseThrow(() -> new IllegalStateException("User not found"));
  }
}
