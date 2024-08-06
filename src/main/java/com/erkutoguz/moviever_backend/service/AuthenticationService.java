package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.AuthRequest;
import com.erkutoguz.moviever_backend.dto.request.CreateUserRequest;
import com.erkutoguz.moviever_backend.dto.response.AuthResponse;
import com.erkutoguz.moviever_backend.exception.DuplicateResourceException;
import com.erkutoguz.moviever_backend.exception.InvalidOtpException;
import com.erkutoguz.moviever_backend.exception.InvalidTokenException;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.model.Role;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;
    private final FirebaseStorageService firebaseStorageService;
    public AuthenticationService(UserRepository userRepository,
                                 JwtService jwtService,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager, EmailVerificationService emailVerificationService, FirebaseStorageService firebaseStorageService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailVerificationService = emailVerificationService;
        this.firebaseStorageService = firebaseStorageService;
    }

    public AuthResponse loginUser(AuthRequest request) throws IOException {
        User user = (User) userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + request.username() + " not found"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        String pictureUrl = firebaseStorageService.getImageUrl(user);
        return new AuthResponse(user.getUsername(), accessToken, refreshToken,  pictureUrl, user.isEnabled());
    }

    public AuthResponse registerUser(CreateUserRequest request) throws MessagingException, IOException {
        User newUser = createUser(request);
        emailVerificationService.sendVerificationMail(newUser.getEmail(), newUser.getFirstname(),newUser.getOtp());
        String accessToken = jwtService.generateAccessToken(newUser);
        String refreshToken = jwtService.generateRefreshToken(newUser);
        String pictureUrl = firebaseStorageService.getImageUrl(newUser);
        return new AuthResponse(newUser.getUsername(), accessToken, refreshToken, pictureUrl, newUser.isEnabled());
    }

    private User createUser(CreateUserRequest request) {
        if(userRepository.findByUsername(request.username()).isPresent()) {
            throw new DuplicateResourceException("Username exists");
        }
        User newUser = new User();
        newUser.setUsername(request.username());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setFirstname(request.firstname());
        newUser.setLastname(request.lastname());
        newUser.setAccountNonExpired(true);
        newUser.setAccountNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        Set<Role> authorities = new HashSet<>();
        authorities.add(Role.ROLE_USER);
        newUser.setRoles(authorities);
        return userRepository.save(newUser);
    }

    public boolean verifyRegistration(String otp) {
        User user = userRepository.findByOtp(otp).orElseThrow(() -> new InvalidOtpException("OTP is invalid"));
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        if (!jwtService.validateToken(refreshToken,username)) {
            throw new InvalidTokenException("Invalid or expired token");
        }
        User user = (User) userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new AuthResponse(username,jwtService.generateAccessToken(user),refreshToken, user.getPictureUrl(), user.isEnabled());
    }
}
