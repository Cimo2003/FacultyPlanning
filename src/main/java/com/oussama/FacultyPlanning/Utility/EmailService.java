package com.oussama.FacultyPlanning.Utility;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async("emailTaskExecutor")
    public void sendTeacherCredentials(String email, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Faculty Portal Credentials");
        message.setText("Your temporary password is: " + password + "\n\nPlease change it after first login.");
        mailSender.send(message);
    }

    @Async("emailTaskExecutor")
    public void sendBatchTeacherCredentials(List<String> emails, List<String> passwords) {
        if (emails.size() != passwords.size()) {
            throw new IllegalArgumentException("Emails and passwords lists must be of equal size");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("boubaayaoussama@gmail.com");
        message.setSubject("Your Faculty Portal Credentials");
        message.setBcc(emails.toArray(new String[0]));

        StringBuilder textBuilder = new StringBuilder();
        for (int i = 0; i < emails.size(); i++) {
            textBuilder.append("Email: ").append(emails.get(i))
                    .append("\nTemporary password: ").append(passwords.get(i))
                    .append("\n\nPlease change it after first login.\n\n---\n\n");
        }

        message.setText(textBuilder.toString());
        mailSender.send(message);
    }

    public void sendEmailVerification(String toEmail, String url) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("boubaayaoussama@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Email Activation");
        message.setText("Your activation url is: " + url);
        mailSender.send(message);
    }

    public void sendResetCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("boubaayaoussama@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Reset Password Code");
        message.setText("Your reset password code is: " + code);
        mailSender.send(message);
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
