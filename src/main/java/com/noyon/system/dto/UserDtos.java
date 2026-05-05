package com.noyon.system.dto;

import lombok.Data;

public class UserDtos {

    @Data
    public static class ProfileUpdateRequest {
        private String displayName;
        private String email;
        private String bio;
        private String website;
    }

    @Data
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String displayName;
        private String bio;
        private String website;
    }

    @Data
    public static class PasswordChangeRequest {
        private String currentPassword;
        private String newPassword;
    }
}