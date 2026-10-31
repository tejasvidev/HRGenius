package com.hrgenius.auth.dto;

import com.hrgenius.security.Role;
import com.hrgenius.user.entity.UserAccount;

import java.util.Set;

public record UserProfileResponse(Long id, String email, String firstName, String lastName, Set<Role> roles) {
    public static UserProfileResponse from(UserAccount user) {
        return new UserProfileResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRoles());
    }
}
