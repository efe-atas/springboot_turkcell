package com.ismail.todoapp.repository;

import com.ismail.todoapp.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    // JpaRepository zaten findById, findAll, save, delete metodlarini sagliyor
}
