package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.*;
import com.erkutoguz.moviever_backend.dto.response.AuthResponse;
import com.erkutoguz.moviever_backend.dto.response.IpAddressResponse;
import com.erkutoguz.moviever_backend.exception.*;
import com.erkutoguz.moviever_backend.kafka.producer.ESProducer;
import com.erkutoguz.moviever_backend.model.IpAddress;
import com.erkutoguz.moviever_backend.model.Role;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.repository.IpAddressRepository;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import com.erkutoguz.moviever_backend.util.IpAddressMapper;
import com.erkutoguz.moviever_backend.util.UserDocumentMapper;
import jakarta.mail.MessagingException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final IpAddressRepository ipAddressRepository;
    private final IpAddressService ipAddressService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;
    private final ESProducer esProducer;
    public AuthenticationService(UserRepository userRepository,
                                 IpAddressRepository ipAddressRepository, IpAddressService ipAddressService,
                                 JwtService jwtService,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 EmailVerificationService emailVerificationService,
                                 ESProducer esProducer) {
        this.userRepository = userRepository;
        this.ipAddressRepository = ipAddressRepository;
        this.ipAddressService = ipAddressService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailVerificationService = emailVerificationService;
        this.esProducer = esProducer;
    }

    public AuthResponse loginUser(AuthRequest request, String clientIpAddress) throws IOException {
        User user = (User) userRepository.findByUsername(request.username())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
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

        try{
            IpAddress ipAddress = ipAddressRepository.findByIp(clientIpAddress)
                    .orElseGet(() -> {
                        IpAddressResponse ipRes = ipAddressService.extractIpAddressInformation(clientIpAddress);
                        if(ipRes == null) {
                            return ipAddressRepository.save(new IpAddress());
                        }
                        IpAddress newIp = IpAddressMapper.map(ipRes);
                        return ipAddressRepository.save(newIp);
                    });

            if(user.getIpAddresses() != null && !user.getIpAddresses().contains(ipAddress)){
                user.getIpAddresses().add(ipAddress);
                userRepository.save(user);
            }

            if(ipAddress.getUsers() != null && !ipAddress.getUsers().contains(user)) {
                ipAddress.getUsers().add(user);
                ipAddressRepository.save(ipAddress);
            }

        } catch (Exception exception) {
            throw new InternalServerException("Something went wrong");
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

    public void sendResetPasswordEmail(SendResetPasswordEmailRequest request) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String resetPassToken = jwtService.generateResetPasswordToken(user);
        emailVerificationService.sendResetPasswordEmail(user.getEmail(), resetPassToken);
    }

    public void resetUserPassword(ResetPasswordRequest request) {
        String username = jwtService.extractUsername(request.token());

        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!jwtService.validateToken(request.token(),username)) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
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

    @CacheEvict(value = "retrieveAllUsers", allEntries = true)
    public ResponseEntity<String> updateUserWithPassword(String username,
                                                         UpdateUserRequestWithPassword request) {
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(request.firstname() != null && !request.firstname().isEmpty()) {
            user.setFirstname(request.firstname());
        }
        if(request.lastname() != null && !request.lastname().isEmpty()) {
            user.setLastname(request.lastname());
        }
        user.setAbout(request.about());
        if(request.password() != null && !request.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        userRepository.save(user);
        esProducer.sendUpdateUserDocument(new UpdateUserDocumentRequest(user.getId(), user.getFirstname(), user.getLastname()));
        return new ResponseEntity<String>("User successfully updated", HttpStatus.OK );
    }

    @CacheEvict(value = "retrieveAllUsers", allEntries = true)
    public ResponseEntity<String> updateUser(String username,
                                                         UpdateUserRequest request) {
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(request.firstname() != null && !request.firstname().isEmpty()) {
            user.setFirstname(request.firstname());
        }
        if(request.lastname() != null && !request.lastname().isEmpty()) {
            user.setLastname(request.lastname());
        }
        user.setAbout(request.about());

        userRepository.save(user);
        esProducer.sendUpdateUserDocument(new UpdateUserDocumentRequest(user.getId(), user.getFirstname(), user.getLastname()));
        return new ResponseEntity<String>("User successfully updated", HttpStatus.OK );
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
