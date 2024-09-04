package com.erkutoguz.moviever_backend.controller;

import com.erkutoguz.moviever_backend.dto.request.*;
import com.erkutoguz.moviever_backend.dto.response.AuthResponse;
import com.erkutoguz.moviever_backend.model.UserDocument;
import com.erkutoguz.moviever_backend.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public AuthResponse registerUser(@Valid @RequestBody CreateUserRequest request) throws MessagingException, IOException {
        return authenticationService.registerUser(request);
    }

    @PostMapping("/login")
    public AuthResponse loginUser(@Valid @RequestBody AuthRequest request, HttpServletRequest requestIp) throws IOException {
        String clientIpAddress = requestIp.getHeader("X-Forwarded-For");
        if (clientIpAddress != null && clientIpAddress.contains(",")) {
            clientIpAddress = clientIpAddress.split(",")[0].trim();
        } else if (clientIpAddress == null) {
            clientIpAddress = requestIp.getRemoteAddr();
        }

        return authenticationService.loginUser(request, clientIpAddress);
    }

    @PostMapping("/userdoc")
    public String userDoc(@RequestBody UserDocument userDocument){
        return "created";
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

    @PostMapping("/reset-password-email")
    public ResponseEntity<Void> sendResetPasswordEmail(@Valid @RequestBody SendResetPasswordEmailRequest request)
            throws MessagingException, UnsupportedEncodingException {
        authenticationService.sendResetPasswordEmail(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetUserPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetUserPassword(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authenticationService.refreshToken(request.refreshToken());
    }

}
