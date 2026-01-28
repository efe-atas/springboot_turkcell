package com.ismail.todoapp.service;

import com.ismail.todoapp.entity.SpaceMember;
import com.ismail.todoapp.enums.SpaceRole;
import com.ismail.todoapp.repository.SpaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("permissionService")
@RequiredArgsConstructor
public class PermissionService {

    private final SpaceMemberRepository spaceMemberRepository;
    private final UserService userService; // Mevcut kullaniciyi almak icin

    public boolean hasSpaceAccess(Long spaceId, String requiredRole) {
        // 1. O anki giris yapmis kullaniciyi bul
        Long userId = userService.getCurrentUser().getId();

        // 2. Bu kullanicinin bu space'te bir uyeligi var mi bak
        SpaceMember member = spaceMemberRepository.findBySpaceIdAndUserId(spaceId, userId)
                .orElse(null);

        if (member == null) return false; // Uye degilse direkt reddet

        // 3. Yetki Seviyesi Kontrolu (Hiyerarsi)
        SpaceRole userRole = member.getRole();

        return switch (requiredRole) {
            case "OWNER" -> userRole == SpaceRole.OWNER;
            case "ADMIN" -> userRole == SpaceRole.OWNER || userRole == SpaceRole.ADMIN;
            case "EDITOR" -> userRole != SpaceRole.VIEWER; // Viewer degilse editor veya ustudur
            case "VIEWER" -> true; // Zaten uye oldugu icin gorebilir
            default -> false;
        };
    }
}