package com.ismail.todoapp.service;

import com.ismail.todoapp.entity.User;
import com.ismail.todoapp.exception.ResourceNotFoundException;
import com.ismail.todoapp.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("getCurrentUser: SecurityContext'teki username ile user bulunamazsa ResourceNotFoundException firlatmali")
    void getCurrentUser_userYoksa_exceptionFirlatir() {
        // given
        String username = "ali";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, "pw")
        );

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUser());
    }

    @Test
    @DisplayName("getCurrentUser: SecurityContext'teki username ile user bulunursa donmeli")
    void getCurrentUser_userVarsa_donmeli() {
        // given
        String username = "ali";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, "pw")
        );

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when
        User result = userService.getCurrentUser();

        // then
        assertEquals(username, result.getUsername());
    }

    @Test
    @DisplayName("getUserById: Kullanici yoksa ResourceNotFoundException firlatmali")
    void getUserById_yoksa_exceptionFirlatir() {
        // given
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    @DisplayName("getUserById: Kullanici varsa dondurmeli")
    void getUserById_varsa_donmeli() {
        // given
        Long id = 1L;
        User user = new User();
        user.setUsername("ali");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // when
        User result = userService.getUserById(id);

        // then
        assertEquals("ali", result.getUsername());
    }

    @Test
    @DisplayName("searchUsers: Query null veya 2 karakterden kucukse bos liste donmeli")
    void searchUsers_queryKisaIse_bosListe() {
        assertTrue(userService.searchUsers(null).isEmpty());
        assertTrue(userService.searchUsers(" ").isEmpty());
        assertTrue(userService.searchUsers("a").isEmpty());

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("searchUsers: Query uygun ise repository.searchByUsername cagrilmali")
    void searchUsers_gecerliQuery_ileAramaYapmali() {
        // given
        String query = "al";
        User user = new User();
        user.setUsername("ali");

        when(userRepository.searchByUsername("al")).thenReturn(List.of(user));

        // when
        List<User> result = userService.searchUsers(query);

        // then
        assertEquals(1, result.size());
        assertEquals("ali", result.get(0).getUsername());
        verify(userRepository).searchByUsername("al");
    }

    @Test
    @DisplayName("getAllUsers: Tüm kullanicilari dondurmeli")
    void getAllUsers_tumunuDonmeli() {
        // given
        User u1 = new User();
        User u2 = new User();
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        // when
        List<User> all = userService.getAllUsers();

        // then
        assertEquals(2, all.size());
        verify(userRepository).findAll();
    }
}