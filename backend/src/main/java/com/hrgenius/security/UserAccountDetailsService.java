package com.hrgenius.security;

import com.hrgenius.user.entity.UserAccount;
import com.hrgenius.user.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserAccountDetailsService implements UserDetailsService {
    private final UserRepository users;

    public UserAccountDetailsService(UserRepository users) { this.users = users; }

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserAccount account = users.findByEmail(UserAccount.normalizeEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
        return User.withUsername(account.getEmail())
                .password(account.getPasswordHash())
                .authorities(account.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList())
                .disabled(!account.isActive())
                .accountLocked(account.isLockedAt(Instant.now()))
                .build();
    }
}
