package com.oussama.FacultyPlanning.Event;

import com.oussama.FacultyPlanning.Email.EmailVerification;
import com.oussama.FacultyPlanning.Email.EmailVerificationService;
import com.oussama.FacultyPlanning.User.User;
import com.oussama.FacultyPlanning.Utility.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;
    private final EmailVerification emailVerification = new EmailVerification();

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        User user = event.getUser();
        String verificationToken = UUID.randomUUID().toString();
        Date verificationDate = emailVerification.getNewExpirationDate();
        String url = event.getApplicationURL()+"/email-verification/"+user.getId()+"/"+verificationToken+"/"+verificationDate.getTime();
        emailVerification.setUser(user);
        emailVerification.setToken(verificationToken);
        emailVerification.setExpirationDate(verificationDate);
        emailVerificationService.addNewEmailVerification(emailVerification);
        emailService.sendEmailVerification(user.getEmail(), url);
    }
}
