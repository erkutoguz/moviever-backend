package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.AuthRequest;
import com.erkutoguz.moviever_backend.dto.request.CreateUserRequest;
import com.erkutoguz.moviever_backend.dto.response.AuthResponse;
import com.erkutoguz.moviever_backend.exception.*;
import com.erkutoguz.moviever_backend.kafka.producer.ESProducer;
import com.erkutoguz.moviever_backend.model.Role;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import com.erkutoguz.moviever_backend.util.UserDocumentMapper;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ESProducer esProducer;
    public AuthenticationService(UserRepository userRepository,
                                 JwtService jwtService,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 EmailVerificationService emailVerificationService,
                                 ESProducer esProducer) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailVerificationService = emailVerificationService;
        this.esProducer = esProducer;
    }

    public AuthResponse loginUser(AuthRequest request) throws IOException {
        User user = (User) userRepository.findByUsername(request.username())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with username: " + request.username() + " not found"));
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (DisabledException e) {
            throw new UnverifiedEmailException("You have to verify your mail. If you verified before, please contact us.");
        } catch (BadCredentialsException e) {
           throw new AccessDeniedException("Invalid Credentials");
        } catch (Exception e) {
            throw new AccessDeniedException("Something went wrong");
        }
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(user.getUsername(), accessToken, refreshToken, user.getPictureUrl(), user.isEnabled());
    }

    @CacheEvict(value = "retrieveAllUsers", allEntries = true)
    public AuthResponse registerUser(CreateUserRequest request) throws MessagingException, IOException {
        User newUser = createUser(request);

        esProducer.sendUserDocument(UserDocumentMapper.map(newUser));

        emailVerificationService.sendVerificationMail(newUser.getEmail(), newUser.getFirstname(),newUser.getOtp());

        String accessToken = jwtService.generateAccessToken(newUser);
        String refreshToken = jwtService.generateRefreshToken(newUser);
        return new AuthResponse(newUser.getUsername(),
                accessToken, refreshToken, newUser.getPictureUrl(), newUser.isEnabled());
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
        User user = userRepository.findByOtp(otp)
                .orElseThrow(() -> new InvalidOtpException("OTP is invalid"));
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        if (!jwtService.validateToken(refreshToken,username)) {
            throw new InvalidTokenException("Invalid or expired token");
        }
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new AuthResponse(username,
                jwtService.generateAccessToken(user),
                refreshToken, user.getPictureUrl(), user.isEnabled());
    }

    public void logoutUser() {
        SecurityContextHolder.clearContext();
    }
}
