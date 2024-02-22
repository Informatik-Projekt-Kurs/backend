package com.MeetMate.user;

import com.MeetMate.response.AuthResponse;
import com.MeetMate.response.GetResponse;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import javax.naming.NameAlreadyBoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping(path = "get")
  @ResponseBody
  public ResponseEntity<GetResponse> getUser(@RequestHeader(name = "Authorization") String token) {
    token = token.substring(7);
    try {
      return ResponseEntity.ok(userService.getUserByEmail(token));

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();

      if (tc == EntityNotFoundException.class)
        return ResponseEntity.notFound().header(t.getMessage()).build();

      return ResponseEntity.internalServerError().header(t.getMessage()).build();
    }
  }

  @GetMapping(path = "getAll")
  @ResponseBody
  public ResponseEntity<List<User>> getAllUsers() {
    try {
      return ResponseEntity.ok(userService.getAllUsers());

    } catch (Throwable t) {
      return ResponseEntity.internalServerError().header(t.getMessage()).build();
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
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).header(t.getMessage()).build();

      if (tc == NameAlreadyBoundException.class)
        return ResponseEntity.status(HttpStatus.CONFLICT).header(t.getMessage()).build();

      return ResponseEntity.internalServerError().header(t.getMessage()).build();
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
        return ResponseEntity.notFound().header(t.getMessage()).build();

      return ResponseEntity.internalServerError().header(t.getMessage()).build();
    }
  }

  @PostMapping(path = "login")
  @ResponseBody
  public ResponseEntity<AuthResponse> authenticateUser(
      @RequestParam MultiValueMap<String, String> data) {
    try {
      return ResponseEntity.ok(userService.authenticateUser(data));

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();
      System.out.println(tc.getName());
      if (tc == EntityNotFoundException.class)
        return ResponseEntity.notFound().header(t.getMessage()).build();

      return ResponseEntity.internalServerError().header(t.getMessage()).build();
    }
  }

  @PostMapping(path = "refresh")
  @ResponseBody
  public ResponseEntity<AuthResponse> refreshAccessToken(
      @RequestHeader(name = "Authorization") String refreshToken) {
    refreshToken = refreshToken.substring(7);
    try {
      return ResponseEntity.ok(userService.refreshAccessToken(refreshToken));

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();

      if (tc == EntityNotFoundException.class)
        return ResponseEntity.notFound().header(t.getMessage()).build();

      if (tc == IllegalStateException.class)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(t.getMessage()).build();

      return ResponseEntity.internalServerError().header(t.getMessage()).build();
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
        return ResponseEntity.notFound().header(t.getMessage()).build();

      return ResponseEntity.internalServerError().header(t.getMessage()).build();
    }
  }
}
