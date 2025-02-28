package com.oussama.FacultyPlanning.Email;

import com.oussama.FacultyPlanning.User.User;
import com.oussama.FacultyPlanning.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email-verification")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;
    private final UserService userService;

    @GetMapping("/{id}/{token}/{expiration}")
    public ResponseEntity<String> verifyEmail(@PathVariable Integer id, @PathVariable String token, @PathVariable Long expiration){
        Optional<EmailVerification> emailVerification = emailVerificationService.findEmailVerificationByToken(token);
        Calendar calendar = Calendar.getInstance();
        long exp = expiration - expiration % 1000;
        if ( (emailVerification.get().getExpirationDate().getTime() - calendar.getTime().getTime()) <= 0 ) {
            emailVerificationService.deleteEmailVerificationById(emailVerification.get().getId());
            return ResponseEntity.ok("Email Expired");
        }
        if ((id == emailVerification.get().getUser().getId()) && (exp == emailVerification.get().getExpirationDate().getTime())) {
            User user = emailVerification.get().getUser();
            user.setEnabled(true);
            userService.updateUser(user);
            return ResponseEntity.ok("Email confirmed");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token/"+ Long.toString(exp) +"/"+emailVerification.get().getExpirationDate().getTime());
        }
    }
}
