package com.noyon.system.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Kullanıcı adı boş bırakılamaz")
    @Size(min = 2, max = 50, message = "Kullanıcı adı 2-50 karakter arasında olmalı")
    private String username;

    @Email(message = "Geçerli bir e-posta adresi giriniz")
    @NotBlank(message = "E-posta boş bırakılamaz")
    private String email;

    @NotBlank(message = "Şifre boş bırakılamaz")
    @Size(min = 8, message = "Şifre en az 8 karakter olmalıdır")
    private String password;
}