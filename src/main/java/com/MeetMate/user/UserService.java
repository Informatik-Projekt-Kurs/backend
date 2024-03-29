package com.MeetMate.user;

import com.MeetMate.response.AuthenticationResponse;
import com.MeetMate.response.GetResponse;
import com.MeetMate.response.RefreshResponse;
import com.MeetMate.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.naming.NameAlreadyBoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  public GetResponse getUserByEmail(String token) {
    String email = jwtService.extractUserEmail(token);

    Optional<User> userOptional = userRepository.findUserByEmail(email);

    User user =
        userRepository
            .findUserByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

    return GetResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .created_at(user.getCreatedAt())
        .email(user.getEmail())
        .role(user.getRole())
        .build();
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public void registerNewUser(MultiValueMap<String, String> data) throws NameAlreadyBoundException {
    String email = data.getFirst("email");
    String name = data.getFirst("name");
    String password = data.getFirst("password");

    User user = new User(name, email, passwordEncoder.encode(password));

    if (email != null
        && !email.isEmpty()
        && password != null
        && !password.isEmpty()
        && name != null
        && !name.isEmpty()) {
      Optional<User> userOptional = userRepository.findUserByEmail(email);
      if (userOptional.isPresent()) {
        throw new NameAlreadyBoundException("Email taken");
      }

      userRepository.save(user);
    } else {
      throw new IllegalArgumentException("Required argument is missing");
    }
  }

  @Transactional
  public void updateUser(String token, MultiValueMap<String, String> data) {
    String email = jwtService.extractUserEmail(token);
    String name = data.getFirst("name");
    String password = passwordEncoder.encode(data.getFirst("password"));

    User user =
        userRepository
            .findUserByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User does not exist."));

    if (password != null) user.setPassword(password);
    if (name != null) user.setName(name);
  }

  @Transactional
  public AuthenticationResponse authenticateUser(MultiValueMap<String, String> data) {
    String email = data.getFirst("email");
    String password = data.getFirst("password");

    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

    User user =
        userRepository
            .findUserByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

    HashMap<String, Object> body = new HashMap<>();
    String token = jwtService.generateAccessToken(user);
    String refresh = jwtService.generateRefreshToken(user);
    user.setRefreshToken(refresh);
    long exp =
        jwtService.extractClaim(token, Claims::getExpiration).getTime()
            / 1000; // expiration time in seconds
    return AuthenticationResponse.builder()
        .access_Token(token)
        .expires_at(exp)
        .refresh_token(refresh)
        .build();
  }

  public RefreshResponse refreshAccessToken(String refreshToken) {
    String email = jwtService.extractUserEmail(refreshToken);
    User user =
        userRepository
            .findUserByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

    if (refreshToken.equals(user.getRefreshToken())) {
      HashMap<String, Object> body = new HashMap<>();
      String token = jwtService.generateAccessToken(user);
      long exp =
          jwtService.extractClaim(token, Claims::getExpiration).getTime()
              / 1000; // expiration time in seconds
      return RefreshResponse.builder().access_Token(token).expires_at(exp).build();
    } else {
      throw new IllegalStateException("Refresh token is invalid");
    }
  }

  public void deleteUser(String token) {
    String email = jwtService.extractUserEmail(token);

    User user =
        userRepository
            .findUserByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User does not exist."));
    long userId = user.getId();
    boolean exists = userRepository.existsById(userId);
    if (!exists) {
      throw new EntityNotFoundException("User does not exist");
    }

    userRepository.deleteById(userId);
  }
}
