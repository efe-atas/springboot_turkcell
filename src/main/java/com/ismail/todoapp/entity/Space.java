package com.ismail.todoapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    // Bir Space silinirse icindeki Task'lar da silinsin (Cascade)
    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();

    // Uyeler (SpaceMember tablosu uzerinden baglanacak)
    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<SpaceMember> members = new ArrayList<>();
}