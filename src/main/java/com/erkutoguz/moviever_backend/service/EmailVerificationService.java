package com.erkutoguz.moviever_backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;

@Service
public class EmailVerificationService {

    @Value("${spring.mail.username}")
    private String mailFrom;

    private final JavaMailSender mailSender;
    public EmailVerificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationMail(String mailTo,
                                     String firstname,
                                     String otp) throws MessagingException, UnsupportedEncodingException {
        String subject = "Please verify your registration";
        String senderName = "Moviever";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Moviever.<br>Erkut Oğuz.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(mailFrom, senderName);
        helper.setTo(mailTo);
        helper.setSubject(subject);

        content = content.replace("[[name]]", firstname);

        String verifyURL = getCurrentDomain() + "/api/v1/auth/verify?otp=" + otp;
        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    private String getCurrentDomain() {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath();
        return builder.build().toUriString();
    }

}
