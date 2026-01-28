package com.ismail.todoapp.controller;


import com.ismail.todoapp.dto.AuthRequest;
import com.ismail.todoapp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Kimlik Doğrulama", description = "Kullanıcı kayıt ve giriş işlemleri")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Yeni kullanıcı kaydı",
            description = "Sisteme yeni bir kullanıcı kaydeder. Kullanıcı adı benzersiz olmalıdır."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Kayıt başarılı",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Kayit basarili"))),
            @ApiResponse(responseCode = "409", description = "Kullanıcı adı zaten mevcut",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request){

        return  ResponseEntity.status(201).body(authService.register(request));

    }

    @Operation(
            summary = "Kullanıcı girişi",
            description = "Kullanıcı adı ve şifre ile giriş yapar. Başarılı girişte JWT token döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Giriş başarılı - JWT token döner",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."))),
            @ApiResponse(responseCode = "401", description = "Geçersiz kullanıcı adı veya şifre",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request){
        return ResponseEntity.ok(authService.login(request));
    }




}
