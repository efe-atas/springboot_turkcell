package com.ismail.todoapp.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // @PreAuthorize annotation'larinin calismasi icin
@AllArgsConstructor
public class SecurityConfig {

    private final JwtFiter jwtFiter;
    // Springe BCrypt ile sifreledigizi soyleyip onunla encode yapmasini soyledik
    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder(); // DETAYINA INELIM
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

      return http
                //CSRF korumasini kapatiyoruz REST API lerde boyle yapiliyor
                .csrf(AbstractHttpConfigurer :: disable)

                //YETKI AYARLARI
                .authorizeHttpRequests(auth -> auth
                        // Bu adreslere herkes girebilsin
                         .requestMatchers("/api/auth/**").permitAll()
                        // Swagger UI icin izin ver
                         .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                        //Admin endpoint'lerini kilitleyelim
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Geri kalan hepsi icin giris sart
                         .anyRequest().authenticated()
                )
                // session yonetimi stateless olmali
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // filtreyi ekliyoruz
                .addFilterBefore(jwtFiter, UsernamePasswordAuthenticationFilter.class)
                .build();






    }

}
