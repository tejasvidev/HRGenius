package com.hrgenius.auth.service;

import com.hrgenius.auth.dto.TokenResponse;
import com.hrgenius.auth.dto.UserProfileResponse;
import com.hrgenius.auth.entity.RefreshToken;
import com.hrgenius.auth.repository.RefreshTokenRepository;
import com.hrgenius.security.SecurityProperties;
import com.hrgenius.user.entity.UserAccount;
import com.hrgenius.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class AuthenticationService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final AuthenticationManager authenticationManager;
    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final SecurityProperties properties;

    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository users,
                                  RefreshTokenRepository refreshTokens, PasswordEncoder passwordEncoder,
                                  JwtEncoder jwtEncoder, SecurityProperties properties) {
        this.authenticationManager = authenticationManager;
        this.users = users;
        this.refreshTokens = refreshTokens;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
    }

    public IssuedSession login(String email, String password) {
        String normalizedEmail = UserAccount.normalizeEmail(email);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalizedEmail, password));
        } catch (BadCredentialsException exception) {
            recordFailedLogin(normalizedEmail);
            throw new BadCredentialsException("Invalid email or password");
        }
        UserAccount user = users.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        user.clearFailedLogins();
        users.save(user);
        return issueSession(user);
    }

    @Transactional
    public IssuedSession refresh(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) throw new BadCredentialsException("Refresh token is invalid");
        Instant now = Instant.now();
        RefreshToken token = refreshTokens.findByHashForUpdate(hash(rawToken))
                .orElseThrow(() -> new BadCredentialsException("Refresh token is invalid"));
        UserAccount user = token.getUser();
        if (!token.isUsableAt(now) || !user.isActive() || user.isLockedAt(now)) {
            token.revoke(now);
            throw new BadCredentialsException("Refresh token is invalid");
        }
        token.revoke(now);
        refreshTokens.save(token);
        return issueSession(user);
    }

    @Transactional
    public void logout(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return;
        refreshTokens.findByHashForUpdate(hash(rawToken)).ifPresent(token -> {
            token.revoke(Instant.now());
            refreshTokens.save(token);
        });
    }

    @Transactional(readOnly = true)
    public UserAccount getProfile(long userId) {
        return users.findById(userId).filter(UserAccount::isActive)
                .orElseThrow(() -> new BadCredentialsException("Account is unavailable"));
    }

    @Transactional
    public void changePassword(long userId, String currentPassword, String newPassword) {
        UserAccount user = getProfile(userId);
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        users.save(user);
        refreshTokens.revokeActiveByUserId(userId, Instant.now());
    }

    private void recordFailedLogin(String email) {
        users.findByEmail(email).ifPresent(user -> {
            int limit = Math.max(1, properties.failedLoginLimit());
            user.recordFailedLogin(limit, Instant.now().plus(Duration.ofMinutes(properties.failedLoginLockMinutes())));
            users.save(user);
        });
    }

    private IssuedSession issueSession(UserAccount user) {
        Instant now = Instant.now();
        Instant accessExpiry = now.plus(Duration.ofMinutes(properties.accessTokenMinutes()));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("hrgenius")
                .issuedAt(now)
                .expiresAt(accessExpiry)
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().stream().map(Enum::name).sorted().toList())
                .build();
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();

        byte[] random = new byte[64];
        RANDOM.nextBytes(random);
        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(random);
        Instant refreshExpiry = now.plus(Duration.ofDays(properties.refreshTokenDays()));
        refreshTokens.save(new RefreshToken(user, hash(refreshToken), refreshExpiry));
        TokenResponse response = new TokenResponse(accessToken, "Bearer",
                Duration.between(now, accessExpiry).toSeconds(), UserProfileResponse.from(user));
        return new IssuedSession(response, refreshToken, refreshExpiry);
    }

    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    public record IssuedSession(TokenResponse response, String refreshToken, Instant refreshExpiresAt) { }
}
