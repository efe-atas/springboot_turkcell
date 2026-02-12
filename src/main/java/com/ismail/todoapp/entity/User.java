package com.ismail.todoapp.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Setter
@Entity
@Table(name = "users") // user postresql de ozel kelimeymis her gun yeni bir bilgi
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // ayni username iki tane olamaz
    private String username;

    @Column(name = "password")
    private String password; // bu yaklasim dogru mu degilse neden yanlis ne yapabiliriz riskleri nelerdir

    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public Long getId(){
        return id;
    }

   // Bu sorulara override atmaliyiz ki kullanabilelim
    @Override
    public boolean isAccountNonExpired() {
        return true; // Hesap süresi dolmadı
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Hesap kilitli değil
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Şifre süresi dolmadı
    }

    @Override
    public boolean isEnabled() {
        return true; // Hesap aktif
    }


}
