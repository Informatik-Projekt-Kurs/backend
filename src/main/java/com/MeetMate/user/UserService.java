package com.MeetMate.user;

import com.MeetMate.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

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

    public User getUserByEmail(String token) {
        String email = jwtService.extractClaimGeneric("email", token);

        Optional<User> userOptional = userRepository.findUserByEmail(email);

        return userRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void registerNewUser(MultiValueMap<String, String> data) throws NameAlreadyBoundException {
        String email = data.getFirst("email");
        String name = data.getFirst("name");
        String password = data.getFirst("password");

        User user = new User(name, email, passwordEncoder.encode(password));

        if (email != null && !email.isEmpty()
                && password != null && !password.isEmpty()
                && name != null && !name.isEmpty()) {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if (userOptional.isPresent()) {
                throw new NameAlreadyBoundException("Email taken");
            }

            userRepository.save(user);
        }
        throw new IllegalArgumentException("Required argument is missing");
    }

    @Transactional
    public String updateUser(String token, MultiValueMap<String, String> data) {
        String email = jwtService.extractClaimGeneric("email", token);
        String name = data.getFirst("name");
        String password = passwordEncoder.encode(data.getFirst("password"));

        User user =
                userRepository
                        .findUserByEmail(email)
                        .orElseThrow(() -> new EntityNotFoundException("User does not exist."));

        if (password != null) user.setPassword(password);
        if (name != null) user.setName(name);

        return jwtService.generateToken(null, user);
    }

    public String authenticateUser(String token) {
        String email = jwtService.extractUserEmail(token);
        String password =
                jwtService.extractClaim(token, Claims -> Claims.get("password", String.class));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        User user =
                userRepository
                        .findUserByEmail(email)
                        .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        return jwtService.generateToken(null, user);
    }

    public void deleteUser(String token) {
        String email = jwtService.extractClaimGeneric("email", token);

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
