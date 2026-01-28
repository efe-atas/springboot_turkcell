package com.ismail.todoapp.repository;

import com.ismail.todoapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    // Sen sadece metodun ismini yazarsin, o arkada SQL'i yazar.
    // "select * from users where username = ?" islemini yapar.
    Optional<User> findByUsername(String username);

    // Kullanici arama - Space'e davet icin
    // Username icinde aranan kelime geciyorsa dondurur (case-insensitive)
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchByUsername(@Param("query") String query);
}
