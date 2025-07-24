package com.oussama.FacultyPlanning.Configuration;

import com.oussama.FacultyPlanning.Enum.Role;
import com.oussama.FacultyPlanning.Model.User;
import com.oussama.FacultyPlanning.Service.UserService;
import com.oussama.FacultyPlanning.Utility.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserService userService;
    private final EmailService emailService;

    public OAuth2SuccessHandler(JwtService jwtService, UserService userService, EmailService emailService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.emailService = emailService;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Find or create user
        User user = userService.findUserByEmail(email)
                .orElseGet(() -> createOAuthUser(email, name));

        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Set tokens in HTTP-only cookies
        addTokenCookies(response, accessToken, refreshToken);

        response.setHeader("Location", "http://localhost:3000/dashboard");
        response.setStatus(HttpStatus.FOUND.value());
    }

    private User createOAuthUser(String email, String name) {
        User newUser = new User();
        String[] names = StringUtils.split(name, " ");
        String firstName = names != null && names.length > 0 ? names[0] : "";
        String lastName = names != null && names.length > 1 ? names[1] : "";
        String password = UUID.randomUUID().toString();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(Role.ADMIN);
        newUser.setEnabled(true);
        emailService.sendCredentials(email, password);
        return userService.addNewUser(newUser);
    }

    private void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        // Access Token Cookie (short-lived)
        ResponseCookie accessCookie = ResponseCookie.from("access-token", accessToken)
                .httpOnly(true)
                .secure(false) // True in production
                .path("/")
                .build();

        // Refresh Token Cookie (long-lived)
        ResponseCookie refreshCookie = ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }
}
