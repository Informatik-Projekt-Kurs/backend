package com.MeetMate.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.naming.NameAlreadyBoundException;

//.body("message: " + t.getMessage() + "\nStack trace: " + Arrays.toString(t.getStackTrace()));

@RestController
@RequestMapping(path = "api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping(path = "get")
  @ResponseBody
  public ResponseEntity<?> getUser(@RequestHeader(name = "Authorization") String token) {
    token = token.substring(7);
    try {
      return ResponseEntity.ok(userService.getUserByEmail(token));

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();

      if (tc == EntityNotFoundException.class)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("message: " + t.getMessage());
    }
  }

  @GetMapping(path = "getAll")
  @ResponseBody
  public ResponseEntity<?> getAllUsers() {
    try {
      return ResponseEntity.ok(userService.getAllUsers());

    } catch (Throwable t) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("message: " + t.getMessage());
    }
  }

  @PostMapping(path = "signup")
  @ResponseBody
  public ResponseEntity<?> registerNewUser(@RequestParam MultiValueMap<String, String> data) {
    try {
      userService.registerNewUser(data);
      return ResponseEntity.ok().build();

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();

      if (tc == IllegalArgumentException.class)
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("message: " + t.getMessage());

      if (tc == NameAlreadyBoundException.class)
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("message: " + t.getMessage());
    }
  }

  @PutMapping(path = "update")
  @ResponseBody
  public ResponseEntity<?> updateUser(
      @RequestHeader(name = "Authorization") String token,
      @RequestParam MultiValueMap<String, String> data) {
    token = token.substring(7);
    try {
      userService.updateUser(token, data);
      return ResponseEntity.ok().build();

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();

      if (tc == EntityNotFoundException.class)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("message: " + t.getMessage());
    }
  }

  @PostMapping(path = "login")
  @ResponseBody
  public ResponseEntity<?> authenticateUser(@RequestParam MultiValueMap<String, String> data) {
    try {
      return ResponseEntity.ok(userService.authenticateUser(data));

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();

      if (tc == InternalAuthenticationServiceException.class || tc == BadCredentialsException.class)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("message: " + t.getMessage());

      if (tc == EntityNotFoundException.class)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("message: " + t.getMessage());
    }
  }

  @PostMapping(path = "refresh")
  @ResponseBody
  public ResponseEntity<?> refreshAccessToken(@RequestHeader(name = "Authorization") String refreshToken) {
    refreshToken = refreshToken.substring(7);
    try {
      return ResponseEntity.ok(userService.refreshAccessToken(refreshToken));

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();

      if (tc == EntityNotFoundException.class)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

      if (tc == IllegalStateException.class)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("message: " + t.getMessage());
    }
  }

  @DeleteMapping(path = "delete")
  @ResponseBody
  public ResponseEntity<?> deleteUser(@RequestHeader(name = "Authorization") String token) {
    token = token.substring(7);
    try {
      userService.deleteUser(token);
      return ResponseEntity.noContent().build();

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();

      if (tc == EntityNotFoundException.class)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("type: " + tc + "\nmessage: " + t.getMessage());
    }
  }
}
