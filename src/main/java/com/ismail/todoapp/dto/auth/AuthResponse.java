package com.ismail.todoapp.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Kimlik doğrulama cevabı")
public class AuthResponse {

    @Schema(description = "JWT erişim tokeni", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Token tipi", example = "Bearer")
    private String tokenType = "Bearer";
}

