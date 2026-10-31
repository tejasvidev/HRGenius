package com.hrgenius.notification;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailNotificationListenerTest {
    @Test
    void sendsPlainTextNotificationWhenEnabled() {
        JavaMailSender sender = mock(JavaMailSender.class);
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(sender.createMimeMessage()).thenReturn(message);
        ObjectProvider<JavaMailSender> provider = provider(sender);

        new EmailNotificationListener(provider, true, "hr@example.test")
                .send(new OutboundEmail("person@example.test", "Subject", "Message body"));

        verify(sender).send(message);
    }

    @Test
    void doesNotSendWhenEmailNotificationsAreDisabled() {
        JavaMailSender sender = mock(JavaMailSender.class);
        new EmailNotificationListener(provider(sender), false, "")
                .send(new OutboundEmail("person@example.test", "Subject", "Message body"));
        org.mockito.Mockito.verifyNoInteractions(sender);
    }

    private ObjectProvider<JavaMailSender> provider(JavaMailSender sender) {
        StaticListableBeanFactory factory = new StaticListableBeanFactory();
        factory.addBean("mailSender", sender);
        return factory.getBeanProvider(JavaMailSender.class);
    }
}
