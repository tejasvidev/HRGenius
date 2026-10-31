package com.hrgenius.notification;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailNotificationListener {
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationListener.class);
    private final ObjectProvider<JavaMailSender> sender;
    private final boolean enabled;
    private final String fromAddress;

    public EmailNotificationListener(ObjectProvider<JavaMailSender> sender,
                                     @Value("${app.notifications.email-enabled:false}") boolean enabled,
                                     @Value("${app.notifications.from-address:}") String fromAddress) {
        this.sender = sender;
        this.enabled = enabled;
        this.fromAddress = fromAddress;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(OutboundEmail email) {
        if (!enabled) {
            log.debug("Email notification skipped because app.notifications.email-enabled is false");
            return;
        }
        JavaMailSender mailSender = sender.getIfAvailable();
        if (mailSender == null) {
            log.warn("Email notification was not sent because no mail sender is configured");
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            if (!fromAddress.isBlank()) helper.setFrom(fromAddress);
            helper.setTo(email.recipient());
            helper.setSubject(email.subject());
            helper.setText(email.body(), false);
            mailSender.send(message);
        } catch (MailException | jakarta.mail.MessagingException exception) {
            log.error("Email notification delivery failed for recipient {}", email.recipient(), exception);
        }
    }
}
