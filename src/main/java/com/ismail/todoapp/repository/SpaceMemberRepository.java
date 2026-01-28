package com.ismail.todoapp.repository;

import com.ismail.todoapp.entity.SpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceMemberRepository extends JpaRepository<SpaceMember, Long> {

    // Kullanici bu space'in uyesi mi? Varsa rolunu de getirir.
    Optional<SpaceMember> findBySpaceIdAndUserId(Long spaceId, Long userId);

    // Bir space'in tum uyelerini getir
    List<SpaceMember> findBySpaceId(Long spaceId);

    // Bir kullanicinin tum uyeliklerini getir (hangi space'lere uye)
    List<SpaceMember> findByUserId(Long userId);
}