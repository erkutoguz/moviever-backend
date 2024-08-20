package com.erkutoguz.moviever_backend.controller;


import com.erkutoguz.moviever_backend.dto.request.AuthRequest;
import com.erkutoguz.moviever_backend.dto.request.CreateUserRequest;
import com.erkutoguz.moviever_backend.dto.request.RefreshTokenRequest;
import com.erkutoguz.moviever_backend.dto.response.AuthResponse;
import com.erkutoguz.moviever_backend.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    Logger log = LoggerFactory.getLogger(getClass());

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public AuthResponse registerUser(@RequestBody CreateUserRequest request) throws MessagingException, IOException {
        return authenticationService.registerUser(request);
    }

    @PostMapping("/login")
    public AuthResponse loginUser(@RequestBody AuthRequest request, HttpServletRequest request1) throws IOException {
        String clientIpAddress = request1.getHeader("X-Forwarded-For");
        if (clientIpAddress != null && clientIpAddress.contains(",")) {
            clientIpAddress = clientIpAddress.split(",")[0].trim();
        } else if (clientIpAddress == null) {
            clientIpAddress = request1.getRemoteAddr();
        }
        log.info(clientIpAddress);
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

    @GetMapping("/logout")
    public ResponseEntity<Void> logoutUser() {
        authenticationService.logoutUser();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        return authenticationService.refreshToken(request.refreshToken());
    }

}
