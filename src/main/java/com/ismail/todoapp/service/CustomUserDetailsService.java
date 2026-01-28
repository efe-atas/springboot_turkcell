package com.ismail.todoapp.service;


import com.ismail.todoapp.entity.User;
import com.ismail.todoapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // En kritik fonksiyon
    // Kullanici giris yapmak istiyor gidip veritabininda bulup getiriyoruz
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Giriş yapmaya çalışan kullanıcı: " + username);

        // Veritabanından senin Entity'ni buluyoruz
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        System.out.println("Veritabanından bulunan şifre hash'i: " + user.getPassword());

        // Doğrudan user nesnesini dönüyoruz
        return user;
    }
}
