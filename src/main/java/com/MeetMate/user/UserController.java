package com.MeetMate.user;

import com.MeetMate.response.AuthenticationResponse;
import com.MeetMate.response.GetResponse;
import com.MeetMate.response.RefreshResponse;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Map;
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

        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.notFound().header(enfe.getMessage()).build();
        }
    }

    @GetMapping(path = "getAll")
    @ResponseBody
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping(path = "signup")
    @ResponseBody
    public ResponseEntity<?> registerNewUser(@RequestParam MultiValueMap<String, String> data) {
        try {
            userService.registerNewUser(data);
            return ResponseEntity.ok().build();

        } catch (NameAlreadyBoundException nabe) {
            return ResponseEntity.status(HttpStatus.CONFLICT).header(nabe.getMessage()).build();

        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).header(iae.getMessage()).build();
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

        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.notFound().header(enfe.getMessage()).build();
        }
    }

    @PostMapping(path = "login")
    @ResponseBody
    public ResponseEntity<AuthenticationResponse> authenticateUser(
            @RequestParam MultiValueMap<String, String> data) {
        try {
            return ResponseEntity.ok(userService.authenticateUser(data));

        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.notFound().header(enfe.getMessage()).build();

        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).header(iae.getMessage()).build();
        }
    }

    @PostMapping(path = "refresh")
    @ResponseBody
    public ResponseEntity<RefreshResponse> refreshAccessToken(
            @RequestHeader(name = "Authorization") String refreshToken) {
        refreshToken = refreshToken.substring(7);
        try {
            return ResponseEntity.ok(userService.refreshAccessToken(refreshToken));

        } catch (IllegalStateException ise) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(ise.getMessage()).build();
        }
    }

    @DeleteMapping(path = "delete")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@RequestHeader(name = "Authorization") String token) {
        token = token.substring(7);
        try {
            userService.deleteUser(token);
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.notFound().header(enfe.getMessage()).build();
        }
    }
}
