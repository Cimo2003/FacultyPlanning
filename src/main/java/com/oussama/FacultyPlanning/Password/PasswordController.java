package com.oussama.FacultyPlanning.Password;

import com.oussama.FacultyPlanning.Configuration.JwtService;
import com.oussama.FacultyPlanning.User.User;
import com.oussama.FacultyPlanning.User.UserService;
import com.oussama.FacultyPlanning.Utility.EmailService;
import com.oussama.FacultyPlanning.Utility.RandomStringGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;
    private final ResetCodeService resetCodeService;
    private final EmailService emailService;

    @PostMapping("/update-password")
    public ResponseEntity<String> changePassword(HttpServletRequest request,
                                                 @RequestParam String prevPassword,
                                                 @RequestParam String newPassword) {
        String jwt = request.getHeader("Authorization");
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
            String username = jwtService.extractUsername(jwt);

            Optional<User> userOptional = userService.findUserByEmail(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                if (passwordEncoder.matches(prevPassword, user.getPassword())) {
                    // Encode the new password
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userService.updateUser(user);
                    return ResponseEntity.ok("Password updated successfully.");
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token.");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestParam String email) {
        if(userService.findUserByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This Email Doesn't Exist");
        }
        Optional<ResetCode> resetCode = resetCodeService.findResetCodeByEmail(email);
        String code = RandomStringGenerator.generateRandomString(8);
        if(resetCode.isPresent()){
            resetCode.get().setNumTries(0);
            resetCode.get().setCode(passwordEncoder.encode(code));
            resetCode.get().setExpirationDate(LocalDateTime.now().plusMinutes(5));
            resetCodeService.updateResetCode(resetCode.get());
        }else {
            resetCodeService.newResetCode(new ResetCode(email, passwordEncoder.encode(code)));
        }
        emailService.sendResetCode(email, code);
        return ResponseEntity.ok("Reset password code has been sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestParam String email, @RequestParam String code, @RequestParam String newPassword) {
        boolean isCodeValid = resetCodeService.isCodeValid(email, code);
        if (isCodeValid) {
            User user = userService.findUserByEmail(email).get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.updateUser(user);
            return ResponseEntity.ok("Password has been reset successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired reset code.");
    }
}
