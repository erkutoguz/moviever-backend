package com.erkutoguz.moviever_backend.controller;


import com.erkutoguz.moviever_backend.dto.request.AuthRequest;
import com.erkutoguz.moviever_backend.dto.request.RefreshTokenRequest;
import com.erkutoguz.moviever_backend.dto.response.AuthResponse;
import com.erkutoguz.moviever_backend.dto.request.CreateUserRequest;
import com.erkutoguz.moviever_backend.security.SecurityConfig;
import com.erkutoguz.moviever_backend.service.AuthenticationService;
import com.erkutoguz.moviever_backend.service.JwtService;
import jakarta.mail.MessagingException;
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
    public String verifyRegistration(@RequestParam String otp) {
        // TODO şu an için resend yok tek seferlik otp gönderiyor
        return authenticationService.verifyRegistration(otp);
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        return authenticationService.refreshToken(request.refreshToken());
    }

}
