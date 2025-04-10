package com.facsciences_planning_management.facsciences_planning_management.managers.services;

// import java.nio.charset.StandardCharsets;

// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailNotificationService {
    private final JavaMailSender javaMailSender;
    private static String ACCOUNT_ACTIVATION = "Activation de votre compte";

    private static final String BASE_URL = "https://extreme-ivonne-gedeontiga-3cf88bd1.koyeb.app";

    public void sendActivationEmail(String email, String token) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // For RESTful API, the frontend will handle the activation
            String activationLink = BASE_URL + "/activate?token=" + token;
            // + URLEncoder.encode(email, StandardCharsets.UTF_8.toString())

            helper.setFrom("gedeon.ambomo@facsciences-uy1.cm");
            helper.setTo(email);
            helper.setSubject(ACCOUNT_ACTIVATION);

            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 5px;'>"
                    +
                    "<h2>Activation de votre compte</h2>" +
                    "<p>Merci de vous être inscrit. Pour activer votre compte, veuillez cliquer sur le lien ci-dessous :</p>"
                    +
                    "<p><a href='" + activationLink
                    + "' style='display: inline-block; background-color: #4CAF50; color: white; " +
                    "padding: 10px 20px; text-decoration: none; border-radius: 4px;'>Activer mon compte</a></p>" +
                    "<p>Ce lien est valable pendant 24 heures.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send activation email", e);
        }
    }

    // In MailNotificationService.java
    public void sendPasswordResetEmail(String email, String token) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String resetLink = BASE_URL + "/reset-password?token=" + token;
            // + URLEncoder.encode(email, StandardCharsets.UTF_8.toString())

            helper.setFrom("gedeon.ambomo@facsciences-uy1.cm");
            helper.setTo(email);
            helper.setSubject("Réinitialisation de votre mot de passe");

            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 5px;'>"
                    +
                    "<h2>Réinitialisation de mot de passe</h2>" +
                    "<p>Vous avez demandé la réinitialisation de votre mot de passe. Cliquez sur le lien ci-dessous pour procéder :</p>"
                    +
                    "<p><a href='" + resetLink
                    + "' style='display: inline-block; background-color: #4CAF50; color: white; " +
                    "padding: 10px 20px; text-decoration: none; border-radius: 4px;'>Réinitialiser mon mot de passe</a></p>"
                    +
                    "<p>Ce lien est valable pendant 24 heures. Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.</p>"
                    +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
}
