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

    public User getUserByEmail(String token) {
        String email = jwtService.extractClaimGeneric("email", token);
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        return userRepository.findUserByEmail(email).orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String registerNewUser(String token) {
        String email = jwtService.extractClaimGeneric("email", token);
        String name = jwtService.extractClaimGeneric("name", token);
        String password = jwtService.extractClaimGeneric("password", token);
        LocalDate birthday = jwtService.extractClaimGeneric("birthday", token);

        User user = new User(name, birthday, email, passwordEncoder.encode(password));

        if (email != null && !email.isEmpty()
                && password != null && !password.isEmpty()
                && name != null && !name.isEmpty()
                && birthday != null) {
            //check if user already exists
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            userRepository.findUserByEmail(email);
            if (userOptional.isPresent()) {
                throw new IllegalStateException("Email taken");
            }

            userRepository.save(user);
            return jwtService.generateToken(null, user);
        }
        throw new IllegalArgumentException("Required argument is missing");
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

    @Transactional
    public void updateUser(String token) {
        String email = jwtService.extractClaimGeneric("email", token);
        String name = jwtService.extractClaimGeneric("name", token);
        String password = jwtService.extractClaimGeneric("password", token);
        LocalDate birthday = jwtService.extractClaimGeneric("birthday", token);

        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new EntityNotFoundException("User does not exist."));

        if (userRepository.findUserByEmail(email).isEmpty()
                && email != null) {
            user.setEmail(email);
        } // throw error
        if (password != null) user.setPassword(password);
        if (name != null) user.setName(name);
        if (birthday != null) user.setBirthday(birthday);
    }

    public void deleteUser(String token) {
        String email = jwtService.extractClaimGeneric("email", token);
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist."));
        long userId = user.getId();
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new IllegalStateException("User does not exist");
        }
        userRepository.deleteById(userId);
    }

}