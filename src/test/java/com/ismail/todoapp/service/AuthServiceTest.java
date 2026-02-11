package com.ismail.todoapp.service;

import com.ismail.todoapp.dto.auth.AuthRequest;
import com.ismail.todoapp.dto.auth.AuthResponse;
import com.ismail.todoapp.entity.User;
import com.ismail.todoapp.exception.BadRequestException;
import com.ismail.todoapp.exception.ConflictException;
import com.ismail.todoapp.exception.ResourceNotFoundException;
import com.ismail.todoapp.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.CharBuffer;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("register: Username zaten varsa ConflictException firlatmali")
    void register_usernameDoluysa_conflict() {
        // given
        AuthRequest request = new AuthRequest();
        request.setUsername("ali");
        request.setPassword(new char[] {'1', '2', '3'});

        when(userRepository.findByUsername("ali")).thenReturn(Optional.of(new User()));

        // when & then
        assertThrows(ConflictException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("register: Yeni user olusturup kaydetmeli")
    void register_yeniUserKaydedilmeli() {
        // given
        AuthRequest request = new AuthRequest();
        request.setUsername("ali");
        request.setPassword(new char[] {'1', '2', '3'});

        when(userRepository.findByUsername("ali")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(CharBuffer.wrap(new char[] {'1', '2', '3'}))).thenReturn("encoded");

        // when
        String result = authService.register(request);

        // then
        assertEquals("finito", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("login: Kullanici yoksa ResourceNotFoundException firlatmali")
    void login_kullaniciYoksa_exception() {
        // given
        AuthRequest request = new AuthRequest();
        request.setUsername("ali");
        request.setPassword(new char[] {'1', '2', '3'});

        when(userRepository.findByUsername("ali")).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("login: Sifre dogruysa token uretip AuthResponse donmeli")
    void login_sifreDogruysa_tokenDonmeli() {
        // given
        AuthRequest request = new AuthRequest();
        request.setUsername("ali");
        request.setPassword(new char[] {'1', '2', '3'});

        User user = new User();
        user.setUsername("ali");
        user.setPassword("encoded");

        when(userRepository.findByUsername("ali")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(CharBuffer.wrap(new char[] {'1', '2', '3'}), "encoded")).thenReturn(true);
        when(jwtService.generateToken("ali")).thenReturn("token123");

        // when
        AuthResponse response = authService.login(request);

        // then
        assertEquals("token123", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    @DisplayName("login: Sifre yanlissa BadRequestException firlatmali")
    void login_sifreYanlissa_exception() {
        // given
        AuthRequest request = new AuthRequest();
        request.setUsername("ali");
        request.setPassword(new char[] {'1', '2', '3'});

        User user = new User();
        user.setUsername("ali");
        user.setPassword("encoded");

        when(userRepository.findByUsername("ali")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(CharBuffer.wrap(new char[] {'1', '2', '3'}), "encoded")).thenReturn(false);

        // when & then
        assertThrows(BadRequestException.class, () -> authService.login(request));
    }
}