package com.oussama.FacultyPlanning.Utility;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailVerification(String toEmail, String url) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("aoma1931@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Email Activation");
        message.setText("Your activation url is: " + url);
        mailSender.send(message);
    }

    public void sendResetCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bagh.omar@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Reset Password Code");
        message.setText("Your reset password code is: " + code);
        mailSender.send(message);
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
