package com.hrgenius.notification;

public record OutboundEmail(String recipient, String subject, String body) { }
