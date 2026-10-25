package com.userapp.controller;


import com.userapp.dto.SignupRequest;
import com.userapp.entity.User;
import com.userapp.repository.UserRepository;
import com.userapp.security.JwtTokenProvider;
import com.userapp.service.GeolocationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")

public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final GeolocationService geoService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, GeolocationService geoService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.geoService = geoService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest request, HttpServletRequest servletRequest) {
        String clientIp = servletRequest.getRemoteAddr();
        String country = geoService.getCountryFromIp(clientIp);

        if (geoService.isCountryBanned(country)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sign-up not allowed from your region.");
        }

        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Username or Email already taken.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));// Cryptographically secure hash
        user.setUserType(request.getUserType());
        //user.setRole(request.getUserType());
        //user.setCountry(country);
        userRepository.save(user);

        return ResponseEntity.ok("Registration Successful. Please verify OTP.");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String>  request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.get("username"), request.get("password"))
            );
            String token = jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/api/error").build();
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard(Authentication authentication) {
        return ResponseEntity.ok("Welcome " + authentication.getName());
    }

    @GetMapping("/error")
    public ResponseEntity<String> getErrorPage() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed");
    }

    @GetMapping("/settings")
    public ResponseEntity<String> getSettings() {
        return ResponseEntity.ok("Welcome Admin! Access granted to Settings Configuration.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        String username = authentication.getName();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(username + " Logged Out");
    }

}
