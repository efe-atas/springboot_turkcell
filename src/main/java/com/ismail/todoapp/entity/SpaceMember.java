package com.ismail.todoapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ismail.todoapp.enums.SpaceRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Ayni kullanici ayni Space'e iki kere eklenmesin diye unique constraint
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "space_id"})})
public class SpaceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    @JsonIgnore
    private Space space;

    @Enumerated(EnumType.STRING) // Veritabanina "ADMIN", "EDITOR" diye yazar
    @Column(nullable = false)
    private SpaceRole role;
}