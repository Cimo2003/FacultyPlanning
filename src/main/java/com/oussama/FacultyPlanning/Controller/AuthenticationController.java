package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Dto.AuthenticationRequest;
import com.oussama.FacultyPlanning.Dto.RefreshTokenRequest;
import com.oussama.FacultyPlanning.Configuration.JwtService;
import com.oussama.FacultyPlanning.Service.TokenBlacklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;


    @PostMapping("/login")
    public ResponseEntity<HashMap<String,String>> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (Exception e) {
            HashMap<String, String> errorMap = new HashMap<>();
            errorMap.put("error message", "Incorrect username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }

        final UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String accessToken = jwtService.generateToken(userDetails);
        final String refreshToken = jwtService.generateRefreshToken(userDetails);
        HashMap<String,String> jwt = new HashMap<>();
        jwt.put("access-token", accessToken);
        jwt.put("refresh-token", refreshToken);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<HashMap<String,String>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) throws Exception {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        String username = jwtService.extractUsername(refreshToken);

        if (jwtService.validateToken(refreshToken, userDetailsService.loadUserByUsername(username)) && !tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
            final UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(username);
            final String newToken = jwtService.generateToken(userDetails);
            HashMap<String,String> jwt = new HashMap<>();
            jwt.put("access-token", newToken);
            return ResponseEntity.ok(jwt);
        } else {
            HashMap<String, String> errorMap1 = new HashMap<>();
            errorMap1.put("error message", "Invalid refresh token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap1);
        }
    }
    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String accessToken, @RequestParam String refreshToken) {
        Date expiryDateAccess = jwtService.extractExpiration(accessToken);
        Date expiryDateRefresh = jwtService.extractExpiration(refreshToken);
        tokenBlacklistService.blacklistToken(accessToken, expiryDateAccess);
        tokenBlacklistService.blacklistToken(refreshToken, expiryDateRefresh);
        return ResponseEntity.ok("Logged out successfully");
    }
}
