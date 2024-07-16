package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.AuthRequest;
import com.erkutoguz.moviever_backend.dto.response.AuthResponse;
import com.erkutoguz.moviever_backend.dto.request.CreateUserRequest;
import com.erkutoguz.moviever_backend.exception.DuplicateResourceException;
import com.erkutoguz.moviever_backend.exception.InvalidOtpException;
import com.erkutoguz.moviever_backend.model.Role;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;

    public AuthenticationService(UserRepository userRepository,
                                 JwtService jwtService,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager, EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailVerificationService = emailVerificationService;
    }

    public AuthResponse loginUser(AuthRequest request) {
        UserDetails user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + request.username() + " not found"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(user.getUsername(), accessToken, refreshToken);
    }

    public AuthResponse registerUser(CreateUserRequest request) throws MessagingException, UnsupportedEncodingException {
        User newUser = createUser(request);
        emailVerificationService.sendVerificationMail(newUser.getEmail(), newUser.getFirstname(),newUser.getOtp());
        String accessToken = jwtService.generateAccessToken(newUser);
        String refreshToken = jwtService.generateRefreshToken(newUser);
        return new AuthResponse(newUser.getUsername(), accessToken, refreshToken);
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

    public String verifyRegistration(String otp) {
        User user = userRepository.findByOtp(otp).orElseThrow(() -> new InvalidOtpException("OTP is invalid"));
        user.setEnabled(true);
        userRepository.save(user);
        return "Successfully Registered";
    }
}
