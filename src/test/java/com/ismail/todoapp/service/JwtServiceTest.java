package com.ismail.todoapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // private String secret alanini reflection ile doldur
        Field secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        // application.properties'dekine benzer bir base64 string koyabilirsin
        secretField.set(jwtService, "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
    }

    @Test
    @DisplayName("generateToken: Bos olmayan bir token uretmeli ve username'i icermeli")
    void generateToken_calismali() {
        // when
        String token = jwtService.generateToken("ali");

        // then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        String username = jwtService.extractUsername(token);
        assertEquals("ali", username);
    }

    @Test
    @DisplayName("validateToken: Dogru username ile token gecerli olmali")
    void validateToken_gecerli() {
        // given
        String token = jwtService.generateToken("ali");

        org.springframework.security.core.userdetails.UserDetails userDetails =
                org.springframework.security.core.userdetails.User.withUsername("ali")
                        .password("pw")
                        .roles("USER")
                        .build();

        // when
        Boolean valid = jwtService.validateToken(token, userDetails);

        // then
        assertTrue(valid);
    }

    @Test
    @DisplayName("validateToken: Farkli username ile token gecersiz olmali")
    void validateToken_farkliUser_icinGecersiz() {
        // given
        String token = jwtService.generateToken("ali");

        org.springframework.security.core.userdetails.UserDetails userDetails =
                org.springframework.security.core.userdetails.User.withUsername("veli")
                        .password("pw")
                        .roles("USER")
                        .build();

        // when
        Boolean valid = jwtService.validateToken(token, userDetails);

        // then
        assertFalse(valid);
    }
}