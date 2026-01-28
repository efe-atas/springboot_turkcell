package com.ismail.todoapp.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Kimlik doğrulama isteği")
public class AuthRequest {

    @Schema(description = "Kullanıcı adı", example = "username123", required = true)
    private String username;

    @Schema(description = "Kullanıcı şifresi", example = "password123", required = true)
    private String password;
}

