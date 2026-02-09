package com.ismail.todoapp.service;

import com.ismail.todoapp.entity.User;
import com.ismail.todoapp.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("loadUserByUsername: Kullanici yoksa UsernameNotFoundException firlatmali")
    void loadUserByUsername_yoksa_exception() {
        // given
        when(userRepository.findByUsername("ali")).thenReturn(Optional.empty());

        CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

        // when & then
        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("ali"));
    }

    @Test
    @DisplayName("loadUserByUsername: Kullanici varsa UserDetails olarak donmeli")
    void loadUserByUsername_varsa_donmeli() {
        // given
        User user = new User();
        user.setUsername("ali");
        user.setPassword("pw");

        when(userRepository.findByUsername("ali")).thenReturn(Optional.of(user));

        CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

        // when
        UserDetails details = service.loadUserByUsername("ali");

        // then
        assertEquals("ali", details.getUsername());
        assertEquals("pw", details.getPassword());
    }
}