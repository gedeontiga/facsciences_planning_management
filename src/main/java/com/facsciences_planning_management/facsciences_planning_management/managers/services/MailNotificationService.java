package com.facsciences_planning_management.facsciences_planning_management.managers.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailNotificationService {
    private final JavaMailSender javaMailSender;
    private static String ACCOUNT_ACTIVATION = "Activation de votre compte";

    private static final String BASE_URL = "https://extreme-ivonne-gedeontiga-3cf88bd1.koyeb.app/";

    public void sendActivationEmail(String email, String token) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String activationLink = BASE_URL + "/activate?email=" + email + "&token=" + token;
        String text = "Cliquez sur le lien suivant pour activer votre compte: " + activationLink;
        mailMessage.setTo(email);
        mailMessage.setSubject(ACCOUNT_ACTIVATION);
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }
}
