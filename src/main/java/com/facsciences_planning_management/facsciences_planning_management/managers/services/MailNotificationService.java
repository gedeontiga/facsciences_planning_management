package com.facsciences_planning_management.facsciences_planning_management.managers.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailNotificationService {

    private final JavaMailSender javaMailSender;
    private static String CODE_ACTIVATION = "Code d'activation";

    public void sendActivationEmail(String email, String code) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String text = "Votre code d'activation est: " + code;
        mailMessage.setTo(email);
        mailMessage.setSubject(CODE_ACTIVATION);
        mailMessage.setFrom("gedeon.ambomo@facsciences-uy1.cm"); // Optionnel si Gmail gère "From"
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }
}
