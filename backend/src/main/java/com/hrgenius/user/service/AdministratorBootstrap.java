package com.hrgenius.user.service;

import com.hrgenius.security.Role;
import com.hrgenius.user.entity.UserAccount;
import com.hrgenius.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AdministratorBootstrap implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdministratorBootstrap.class);
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;

    public AdministratorBootstrap(UserRepository users, PasswordEncoder passwordEncoder,
                                  @Value("${app.bootstrap-admin.email:}") String email,
                                  @Value("${app.bootstrap-admin.password:}") String password,
                                  @Value("${app.bootstrap-admin.first-name:HR}") String firstName,
                                  @Value("${app.bootstrap-admin.last-name:Administrator}") String lastName) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() && password.isBlank()) return;
        if (email.isBlank() || password.length() < 12) {
            throw new IllegalStateException("Set both BOOTSTRAP_ADMIN_EMAIL and a BOOTSTRAP_ADMIN_PASSWORD of at least 12 characters");
        }
        String normalized = UserAccount.normalizeEmail(email);
        if (users.existsByEmail(normalized)) {
            log.info("Bootstrap administrator already exists; existing account was left unchanged");
            return;
        }
        users.save(new UserAccount(normalized, passwordEncoder.encode(password), firstName, lastName, Set.of(Role.SUPER_ADMIN)));
        log.info("Created the one-time bootstrap administrator account");
    }
}
