package com.MeetMate.user;

import com.MeetMate.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findUserById(userId);
        return userRepository.findUserById(userId).orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }

    public User getUserByEmail(String userEmail) {
        Optional<User> userOptional = userRepository.findUserByEmail(userEmail);
        return userRepository.findUserByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String registerNewUser(String token) {
        String email = jwtService.extractClaimGeneric("email", token);
        String name = jwtService.extractClaimGeneric("name", token);
        String password = jwtService.extractClaimGeneric("password", token);

        User user = new User(name, LocalDate.EPOCH, email, passwordEncoder.encode(password));

        if (email != null && password != null && !email.isEmpty() && !password.isEmpty()) {
            //check if user already exists
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            userRepository.findUserByEmail(email);
            if (userOptional.isPresent()) {
                throw new IllegalStateException("Email taken");
            }

            userRepository.save(user);
            return jwtService.generateToken(null, user);
        }
        throw new EntityNotFoundException("User not found");
    }

    public String authenticateUser(String token) {
        String email = jwtService.extractUserEmail(token);
        String password = jwtService.extractClaim(token, Claims -> Claims.get("password", String.class));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return jwtService.generateToken(null, user);
    }

    //doesn't need repository methods
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
        User user = userRepository.findUserById(id).orElseThrow(() -> new IllegalStateException("User does not exist."));

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