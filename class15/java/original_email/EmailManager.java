package com.tdd.tddmock.email;

public class EmailManager {
    private EmailService emailService;

    public EmailManager(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendWelcomeEmail(String emailAddress) {
        String subject = "Welcome to our platform!";
        String body = "Dear user, welcome to our platform. We are excited to have you on board!";
        emailService.sendEmail(emailAddress, subject, body);
    }
}
