package org.gluu.credmgr.service;

import org.apache.commons.lang.CharEncoding;
import org.gluu.credmgr.config.CredmgrProperties;
import org.gluu.credmgr.config.JHipsterProperties;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.Email;
import org.gluu.oxtrust.model.scim2.ExtensionFieldType;
import org.gluu.oxtrust.model.scim2.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Properties;

/**
 * Service for sending e-mails.
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Service
public class MailService {

    private static final String OP_CONFIG = "opConfig";
    private static final String BASE_URL = "baseUrl";
    private final Logger log = LoggerFactory.getLogger(MailService.class);
    @Inject
    private JHipsterProperties jHipsterProperties;

    @Inject
    private CredmgrProperties credmgrProperties;

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Inject
    private MessageSource messageSource;

    @Inject
    private SpringTemplateEngine templateEngine;

    @Async
    public void sendEmailWithCustomSMTP(JavaMailSenderImpl mailSender, String from, String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);
            mailSender.send(mimeMessage);
            log.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetMail(User user, String baseUrl, OPConfig opConfig) {
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        context.setVariable("resetKey", user.getExtensions().get(Constants.USER_EXT_SCHEMA_ID).getField("resetKey", ExtensionFieldType.STRING));
        context.setVariable("username", user.getUserName());

        context.setVariable(BASE_URL, baseUrl);
        String content = templateEngine.process("passwordResetEmail", context);
        String subject = messageSource.getMessage("email.reset.title", null, locale);
        Email email = user.getEmails().get(0);

        JavaMailSenderImpl javaMailSenderImpl = createJavaMailSender(opConfig.getSmtpHost(), opConfig.getSmtpPort(), opConfig.getSmtpUsername(), opConfig.getSmtpPassword(), opConfig.isSmtpUseSSL());
        sendEmailWithCustomSMTP(javaMailSenderImpl, opConfig.getSmtpUsername(), email.getValue(), subject, content, false, true);
    }

    private JavaMailSenderImpl createJavaMailSender(String smtpHost, String smtpPort, String smtpUsername, String smtpPassword, Boolean smtpUseSSL) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(smtpHost);
        javaMailSender.setPort(Integer.parseInt(smtpPort));
        javaMailSender.setUsername(smtpUsername);
        javaMailSender.setPassword(smtpPassword);
        javaMailSender.setProtocol("smtp");
        if (smtpUseSSL) {
            Properties javaMailProperties = new Properties();
            javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
            javaMailSender.setJavaMailProperties(javaMailProperties);
        }
        return javaMailSender;
    }
}
