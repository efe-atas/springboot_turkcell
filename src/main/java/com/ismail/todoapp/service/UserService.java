package com.ismail.todoapp.service;

import com.ismail.todoapp.entity.User;
import com.ismail.todoapp.exception.ResourceNotFoundException;
import com.ismail.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * JWT token'dan o anki giris yapmis kullaniciyi dondurur
     */
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanici bulunamadi: " + username));
    }

    /**
     * ID ile kullanici bul
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanici", id));
    }

    /**
     * Kullanici arama - Space'e davet icin kullanilir
     * Username veya email icinde aranan kelime geciyorsa dondurur
     */
    public List<User> searchUsers(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        return userRepository.searchByUsername(query.trim());
    }

    /**
     * Tum kullanicilari getir (Admin icin)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
