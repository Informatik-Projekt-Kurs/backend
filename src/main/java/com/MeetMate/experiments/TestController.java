package com.MeetMate.experiments;

import com.MeetMate.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class TestController {

  private final TestService testService;

  @QueryMapping
  public User getUsers(@Argument Long id) {
    return testService.getUsers(id);
  }

  @MutationMapping
  public ResponseEntity<?> UpdateUser(@Argument String name, @Argument String email, @Argument String password) {
    testService.updateUser(name, email, password);
    return ResponseEntity.ok().build();
  }

  @MutationMapping
  public ResponseEntity<?> CreateUser(@Argument String name, @Argument String email, @Argument String password) {
    testService.createUser(name, email, password);
    return ResponseEntity.status(201).build();
  }
}
