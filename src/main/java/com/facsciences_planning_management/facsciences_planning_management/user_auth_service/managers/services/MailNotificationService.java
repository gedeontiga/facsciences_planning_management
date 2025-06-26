package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services;

import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailNotificationService {

    private static final String ACCOUNT_ACTIVATION = "Activation de votre compte";
    private static final String ACCOUNT_CREATION = "Création de votre compte";
    private static final String PASSWORD_RESET = "Réinitialisation de votre mot de passe";

    private final JavaMailSender javaMailSender;
    private final Environment environment;

    public void sendAccountCreationEmail(String email, String firstName, String defaultPassword, String role) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String loginLink = getFrontendUrl() + "/signin";
            String displayName = firstName != null ? firstName : "Utilisateur";

            // Anti-spam headers
            helper.setFrom(getFromEmail(), "Faculté des Sciences UY1");
            helper.setTo(email);
            helper.setSubject(ACCOUNT_CREATION);
            helper.setReplyTo(getFromEmail());

            // Add anti-spam headers
            mimeMessage.setHeader("X-Mailer", "FacSciences Planning System");
            mimeMessage.setHeader("X-Priority", "3");
            mimeMessage.setHeader("X-MSMail-Priority", "Normal");
            mimeMessage.setHeader("List-Unsubscribe", "<mailto:" + getFromEmail() + "?subject=unsubscribe>");

            String htmlContent = buildAccountCreationEmailContent(displayName, email, defaultPassword, role, loginLink);
            String textContent = buildAccountCreationTextContent(displayName, email, defaultPassword, role, loginLink);

            helper.setText(textContent, htmlContent);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send account creation email", e);
        }
    }

    public void sendActivationEmail(String email, String token) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String activationLink = getFrontendUrl() + "/activate?token=" + token;

            // Anti-spam headers
            helper.setFrom(getFromEmail(), "Planning FacSciences UY1");
            helper.setTo(email);
            helper.setSubject(ACCOUNT_ACTIVATION);
            helper.setReplyTo(getFromEmail());

            // Add anti-spam headers
            mimeMessage.setHeader("X-Mailer", "FacSciences Planning System");
            mimeMessage.setHeader("X-Priority", "3");
            mimeMessage.setHeader("X-MSMail-Priority", "Normal");
            mimeMessage.setHeader("List-Unsubscribe", "<mailto:" + getFromEmail() + "?subject=unsubscribe>");

            String htmlContent = buildActivationEmailContent(activationLink);
            String textContent = buildActivationTextContent(activationLink);

            helper.setText(textContent, htmlContent);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send activation email", e);
        }
    }

    public void sendPasswordResetEmail(String email, String token) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String resetLink = getFrontendUrl() + "/reset-password?token=" + token;

            // Anti-spam headers
            helper.setFrom(getFromEmail(), "Planning FacSciences UY1");
            helper.setTo(email);
            helper.setSubject(PASSWORD_RESET);
            helper.setReplyTo(getFromEmail());

            // Add anti-spam headers
            mimeMessage.setHeader("X-Mailer", "FacSciences Planning System");
            mimeMessage.setHeader("X-Priority", "3");
            mimeMessage.setHeader("X-MSMail-Priority", "Normal");
            mimeMessage.setHeader("List-Unsubscribe", "<mailto:" + getFromEmail() + "?subject=unsubscribe>");

            String htmlContent = buildPasswordResetEmailContent(resetLink);
            String textContent = buildPasswordResetTextContent(resetLink);

            helper.setText(textContent, htmlContent);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String getFrontendUrl() {
        return environment.getProperty("app.frontend-url", "https://app-planning-uy1-web.vercel.app");
    }

    private String getFromEmail() {
        return environment.getProperty("mail.from", "gedeon.ambomo@facsciences-uy1.cm");
    }

    private String buildAccountCreationEmailContent(String firstName, String email, String defaultPassword, String role,
            String loginLink) {
        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Création de votre compte</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4;'>"
                +
                "<div style='max-width: 600px; margin: 0 auto; padding: 30px; background-color: white; border: 1px solid #eee; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>"
                +
                "<div style='text-align: center; margin-bottom: 30px;'>" +
                "<h1 style='color: #9444ff; margin: 0; font-size: 24px;'>Faculté des Sciences UY1</h1>" +
                "<h2 style='color: #333; margin: 10px 0 0 0; font-size: 20px;'>Création de votre compte</h2>" +
                "</div>" +
                "<p style='margin-bottom: 20px;'>Bonjour " + firstName + ",</p>" +
                "<p style='margin-bottom: 20px;'>Un compte a été créé pour vous sur la plateforme de gestion de planning de la Faculté des Sciences de l'Université de Yaoundé 1.</p>"
                +
                "<div style='background-color: #f9f9f9; padding: 20px; border-radius: 6px; margin: 25px 0; border-left: 8px solid #9444ff;'>"
                +
                "<h3 style='margin-top: 0; color: ;'>Vos informations de connexion :</h3>" +
                "<p style='margin: 8px 0;'><strong>Email :</strong> " + email + "</p>" +
                "<p style='margin: 8px 0;'><strong>Mot de passe temporaire :</strong> <code style='background-color: #e8e8e8; padding: 2px 4px; border-radius: 3px;'>"
                + defaultPassword + "</code></p>" +
                "<p style='margin: 8px 0;'><strong>Rôle :</strong> " + role + "</p>" +
                "</div>" +
                "<div style='background-color: #fff3cd; padding: 15px; border-radius: 6px; margin: 25px 0; border: 1px solid #ffeaa7;'>"
                +
                "<p style='margin: 0; color: #856404;'><strong>⚠️ Important :</strong> Vous devez changer ce mot de passe lors de votre première connexion pour des raisons de sécurité.</p>"
                +
                "</div>" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + loginLink + "' style='display: inline-block; background-color: #9444ff; color: white; " +
                "padding: 12px 30px; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 16px;'>Se connecter</a>"
                +
                "</div>" +
                "<hr style='border: none; border-top: 1px solid #eee; margin: 30px 0;'>" +
                "<p style='font-size: 12px; color: #666; margin-top: 20px; text-align: center;'>" +
                "Si vous n'arrivez pas à cliquer sur le bouton, copiez et collez ce lien dans votre navigateur :<br>" +
                "<a href='" + loginLink + "' style='color: #9444ff; word-break: break-all;'>" + loginLink + "</a>" +
                "</p>" +
                "<p style='font-size: 11px; color: #888; margin-top: 20px; text-align: center;'>" +
                "Cet email a été envoyé automatiquement par le système de gestion de planning de la Faculté des Sciences UY1."
                +
                "</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildActivationEmailContent(String activationLink) {
        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Activation de votre compte</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4;'>"
                +
                "<div style='max-width: 600px; margin: 0 auto; padding: 30px; background-color: white; border: 1px solid #eee; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>"
                +
                "<div style='text-align: center; margin-bottom: 30px;'>" +
                "<h1 style='color: #4CAF50; margin: 0; font-size: 24px;'>Faculté des Sciences UY1</h1>" +
                "<h2 style='color: #333; margin: 10px 0 0 0; font-size: 20px;'>Activation de votre compte</h2>" +
                "</div>" +
                "<p style='margin-bottom: 20px;'>Merci de vous être inscrit sur la plateforme de gestion de planning de la Faculté des Sciences.</p>"
                +
                "<p style='margin-bottom: 25px;'>Pour activer votre compte, veuillez cliquer sur le bouton ci-dessous :</p>"
                +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + activationLink
                + "' style='display: inline-block; background-color: #4CAF50; color: white; " +
                "padding: 12px 30px; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 16px;'>Activer mon compte</a>"
                +
                "</div>" +
                "<div style='background-color: #fff3cd; padding: 15px; border-radius: 6px; margin: 25px 0; border: 1px solid #ffeaa7;'>"
                +
                "<p style='margin: 0; color: #856404;'><strong>⏰ Attention :</strong> Ce lien est valable pendant 24 heures seulement.</p>"
                +
                "</div>" +
                "<hr style='border: none; border-top: 1px solid #eee; margin: 30px 0;'>" +
                "<p style='font-size: 12px; color: #666; margin-top: 20px; text-align: center;'>" +
                "Si vous n'arrivez pas à cliquer sur le bouton, copiez et collez ce lien dans votre navigateur :<br>" +
                "<a href='" + activationLink + "' style='color: #4CAF50; word-break: break-all;'>" + activationLink
                + "</a>" +
                "</p>" +
                "<p style='font-size: 11px; color: #888; margin-top: 20px; text-align: center;'>" +
                "Cet email a été envoyé automatiquement par le système de gestion de planning de la Faculté des Sciences UY1."
                +
                "</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildPasswordResetEmailContent(String resetLink) {
        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Réinitialisation de mot de passe</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4;'>"
                +
                "<div style='max-width: 600px; margin: 0 auto; padding: 30px; background-color: white; border: 1px solid #eee; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>"
                +
                "<div style='text-align: center; margin-bottom: 30px;'>" +
                "<h1 style='color: #ff6b6b; margin: 0; font-size: 24px;'>Faculté des Sciences UY1</h1>" +
                "<h2 style='color: #333; margin: 10px 0 0 0; font-size: 20px;'>Réinitialisation de mot de passe</h2>" +
                "</div>" +
                "<p style='margin-bottom: 20px;'>Vous avez demandé la réinitialisation de votre mot de passe pour votre compte sur la plateforme de gestion de planning.</p>"
                +
                "<p style='margin-bottom: 25px;'>Cliquez sur le bouton ci-dessous pour procéder à la réinitialisation :</p>"
                +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + resetLink + "' style='display: inline-block; background-color: #ff6b6b; color: white; " +
                "padding: 12px 30px; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 16px;'>Réinitialiser mon mot de passe</a>"
                +
                "</div>" +
                "<div style='background-color: #fff3cd; padding: 15px; border-radius: 6px; margin: 25px 0; border: 1px solid #ffeaa7;'>"
                +
                "<p style='margin: 0; color: #856404;'><strong>⏰ Attention :</strong> Ce lien est valable pendant 24 heures seulement.</p>"
                +
                "</div>" +
                "<div style='background-color: #f8d7da; padding: 15px; border-radius: 6px; margin: 25px 0; border: 1px solid #f5c6cb;'>"
                +
                "<p style='margin: 0; color: #721c24;'><strong>🔒 Sécurité :</strong> Si vous n'avez pas demandé cette réinitialisation, vous pouvez ignorer cet email en toute sécurité.</p>"
                +
                "</div>" +
                "<hr style='border: none; border-top: 1px solid #eee; margin: 30px 0;'>" +
                "<p style='font-size: 12px; color: #666; margin-top: 20px; text-align: center;'>" +
                "Si vous n'arrivez pas à cliquer sur le bouton, copiez et collez ce lien dans votre navigateur :<br>" +
                "<a href='" + resetLink + "' style='color: #ff6b6b; word-break: break-all;'>" + resetLink + "</a>" +
                "</p>" +
                "<p style='font-size: 11px; color: #888; margin-top: 20px; text-align: center;'>" +
                "Cet email a été envoyé automatiquement par le système de gestion de planning de la Faculté des Sciences UY1."
                +
                "</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    // Plain text versions for better deliverability
    private String buildAccountCreationTextContent(String firstName, String email, String defaultPassword, String role,
            String loginLink) {
        return "FACULTÉ DES SCIENCES UY1\n" +
                "Création de votre compte\n\n" +
                "Bonjour " + firstName + ",\n\n" +
                "Un compte a été créé pour vous sur la plateforme de gestion de planning de la Faculté des Sciences de l'Université de Yaoundé 1.\n\n"
                +
                "VOS INFORMATIONS DE CONNEXION :\n" +
                "Email : " + email + "\n" +
                "Mot de passe temporaire : " + defaultPassword + "\n" +
                "Rôle : " + role + "\n\n" +
                "IMPORTANT : Vous devez changer ce mot de passe lors de votre première connexion pour des raisons de sécurité.\n\n"
                +
                "Pour vous connecter, visitez : " + loginLink + "\n\n" +
                "Cet email a été envoyé automatiquement par le système de gestion de planning de la Faculté des Sciences UY1.";
    }

    private String buildActivationTextContent(String activationLink) {
        return "FACULTÉ DES SCIENCES UY1\n" +
                "Activation de votre compte\n\n" +
                "Merci de vous être inscrit sur la plateforme de gestion de planning de la Faculté des Sciences.\n\n" +
                "Pour activer votre compte, visitez le lien suivant :\n" +
                activationLink + "\n\n" +
                "ATTENTION : Ce lien est valable pendant 24 heures seulement.\n\n" +
                "Cet email a été envoyé automatiquement par le système de gestion de planning de la Faculté des Sciences UY1.";
    }

    private String buildPasswordResetTextContent(String resetLink) {
        return "FACULTÉ DES SCIENCES UY1\n" +
                "Réinitialisation de mot de passe\n\n" +
                "Vous avez demandé la réinitialisation de votre mot de passe pour votre compte sur la plateforme de gestion de planning.\n\n"
                +
                "Pour procéder à la réinitialisation, visitez le lien suivant :\n" +
                resetLink + "\n\n" +
                "ATTENTION : Ce lien est valable pendant 24 heures seulement.\n\n" +
                "SÉCURITÉ : Si vous n'avez pas demandé cette réinitialisation, vous pouvez ignorer cet email en toute sécurité.\n\n"
                +
                "Cet email a été envoyé automatiquement par le système de gestion de planning de la Faculté des Sciences UY1.";
    }
}