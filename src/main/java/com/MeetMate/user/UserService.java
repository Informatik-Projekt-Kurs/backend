package com.MeetMate.user;

import com.MeetMate.enums.UserRole;
import com.MeetMate.response.AuthenticationResponse;
import com.MeetMate.response.GetResponse;
import com.MeetMate.response.RefreshResponse;
import com.MeetMate.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.naming.NameAlreadyBoundException;
import java.util.List;
import java.util.Optional;

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

  @Transactional
  public void registerNewUser(MultiValueMap<String, String> data) throws NameAlreadyBoundException {
    String email = data.getFirst("email");
    String name = data.getFirst("name");
    String password = data.getFirst("password");
    String role = data.getFirst("role");
    long associatedCompany = Long.parseLong(data.getFirst("associatedCompany"));
    UserRole userRole;

    if (role == null
        || role.isEmpty())
      userRole = UserRole.CLIENT;
    else
      userRole = UserRole.valueOf(role);

    if (email == null
        || email.isEmpty()
        || password == null
        || password.isEmpty()
        || name == null
        || name.isEmpty()) throw new IllegalArgumentException("Required argument is missing");

    if (userRepository.findUserByEmail(email).isPresent())
      throw new NameAlreadyBoundException("Email already taken");

    User user = new User(name, email, passwordEncoder.encode(password), userRole);

    switch (userRole) {
      case COMPANY_OWNER, COMPANY_MEMBER -> user.setAssociatedCompany(associatedCompany);
      case CLIENT -> user.setAssociatedCompany(-1);
      default -> throw new IllegalStateException(role + "is invalid!");
    }

    userRepository.save(user);
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

    String token = jwtService.generateAccessToken(user);
    String refresh = jwtService.generateRefreshToken(user);
    user.setRefreshToken(refresh);
    long exp =
        jwtService.extractClaim(token, Claims::getExpiration).getTime()
            / 1000; // expiration time in seconds

    return AuthenticationResponse.builder()
        .access_Token(token)
        .expires_at(exp)
        .refresh_Token(refresh)
        .build();
  }

  @Transactional
  public RefreshResponse refreshAccessToken(String refreshToken) {
    String email = jwtService.extractUserEmail(refreshToken);
    User user =
        userRepository
            .findUserByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

    if (!refreshToken.equals(user.getRefreshToken()))
      throw new IllegalStateException("Refresh token is invalid");

    String token = jwtService.generateAccessToken(user);
    long exp =
        jwtService.extractClaim(token, Claims::getExpiration).getTime()
            / 1000; // expiration time in seconds

    return RefreshResponse.builder().access_Token(token).expires_at(exp).build();
  }

  @Transactional
  public void deleteUser(String token) {
    String email = jwtService.extractUserEmail(token);
    User user =
        userRepository
            .findUserByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User does not exist."));

    userRepository.deleteByEmail(email);
  }
}
