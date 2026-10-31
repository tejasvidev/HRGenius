package com.hrgenius.auth.controller;

import com.hrgenius.auth.dto.ChangePasswordRequest;
import com.hrgenius.auth.dto.LoginRequest;
import com.hrgenius.auth.dto.TokenResponse;
import com.hrgenius.auth.dto.UserProfileResponse;
import com.hrgenius.auth.service.AuthenticationService;
import com.hrgenius.security.SecurityProperties;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private static final String REFRESH_COOKIE = "hrgenius_refresh";
    private final AuthenticationService authentication;
    private final SecurityProperties properties;

    public AuthenticationController(AuthenticationService authentication, SecurityProperties properties) {
        this.authentication = authentication;
        this.properties = properties;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        var session = authentication.login(request.email(), request.password());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie(session.refreshToken(),
                Duration.between(java.time.Instant.now(), session.refreshExpiresAt())).toString()).body(session.response());
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        var session = authentication.refresh(refreshToken);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie(session.refreshToken(),
                Duration.between(java.time.Instant.now(), session.refreshExpiresAt())).toString()).body(session.response());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        authentication.logout(refreshToken);
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, refreshCookie("", Duration.ZERO).toString()).build();
    }

    @GetMapping("/me")
    public UserProfileResponse me(@AuthenticationPrincipal Jwt principal) {
        return UserProfileResponse.from(authentication.getProfile(Long.parseLong(principal.getSubject())));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal Jwt principal,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        authentication.changePassword(Long.parseLong(principal.getSubject()), request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, refreshCookie("", Duration.ZERO).toString()).build();
    }

    private ResponseCookie refreshCookie(String value, Duration maxAge) {
        return ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true)
                .secure(properties.cookieSecure())
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(maxAge.isNegative() ? Duration.ZERO : maxAge)
                .build();
    }
}
