package com.erkutoguz.moviever_backend.controller;


import com.erkutoguz.moviever_backend.dto.request.AuthRequest;
import com.erkutoguz.moviever_backend.dto.request.RefreshTokenRequest;
import com.erkutoguz.moviever_backend.dto.response.AuthResponse;
import com.erkutoguz.moviever_backend.dto.request.CreateUserRequest;
import com.erkutoguz.moviever_backend.security.SecurityConfig;
import com.erkutoguz.moviever_backend.service.AuthenticationService;
import com.erkutoguz.moviever_backend.service.JwtService;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public AuthResponse registerUser(@RequestBody CreateUserRequest request) throws MessagingException, UnsupportedEncodingException {
        return authenticationService.registerUser(request);
    }

    @PostMapping("/login")
    public AuthResponse loginUser(@RequestBody AuthRequest request) {
        return authenticationService.loginUser(request);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyRegistration(@RequestParam String otp) {
       boolean isValid =  authenticationService.verifyRegistration(otp);
        if(!isValid) {
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "http://localhost:5173/verification-failed").build();
        }
        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "http://localhost:5173/verification-success").build();
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        return authenticationService.refreshToken(request.refreshToken());
    }

}
