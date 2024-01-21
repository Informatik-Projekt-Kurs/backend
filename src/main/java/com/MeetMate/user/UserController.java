package com.MeetMate.user;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import javax.naming.NameAlreadyBoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/test/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping(path = "get")
  @ResponseBody
  public ResponseEntity<User> getUser(@RequestParam String token) {
    try {
      return ResponseEntity.ok(userService.getUserByEmail(token));

    } catch (EntityNotFoundException enfe) {
      return ResponseEntity.notFound().header(enfe.getMessage()).build();
    }
  }

  @GetMapping(path = "getAll")
  @ResponseBody
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @PostMapping(path = "register")
  @ResponseBody
  public ResponseEntity<String> registerNewUser(@RequestParam String token) {
    try {
      return ResponseEntity.ok(userService.registerNewUser(token));

    } catch (NameAlreadyBoundException nabe) {
      return ResponseEntity.status(HttpStatus.CONFLICT).header(nabe.getMessage()).build();

    } catch (IllegalArgumentException iae) {
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).header(iae.getMessage()).build();
    }
  }

  @PutMapping(path = "update")
  @ResponseBody
  public ResponseEntity<String> updateUser(@RequestParam String token) {
    try {
      return ResponseEntity.ok(userService.updateUser(token));

    } catch (EntityNotFoundException enfe) {
      return ResponseEntity.notFound().header(enfe.getMessage()).build();
    }
  }

  @PostMapping(path = "auth")
  @ResponseBody
  public ResponseEntity<String> authenticateUser(@RequestParam String token) {
    try {
      return ResponseEntity.ok(userService.authenticateUser(token));

    } catch (EntityNotFoundException enfe) {
      return ResponseEntity.notFound().header(enfe.getMessage()).build();
    }
  }

  @DeleteMapping(path = "delete")
  @ResponseBody
  public ResponseEntity<?> deleteUser(@RequestParam String token) {
    try {
      userService.deleteUser(token);
      return ResponseEntity.noContent().build();

    } catch (EntityNotFoundException enfe) {
      return ResponseEntity.notFound().header(enfe.getMessage()).build();
    }
  }
}
