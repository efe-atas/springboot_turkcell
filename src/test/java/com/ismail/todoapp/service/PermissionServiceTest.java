package com.ismail.todoapp.service;

import com.ismail.todoapp.entity.Space;
import com.ismail.todoapp.entity.SpaceMember;
import com.ismail.todoapp.entity.User;
import com.ismail.todoapp.enums.SpaceRole;
import com.ismail.todoapp.repository.SpaceMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private SpaceMemberRepository spaceMemberRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PermissionService permissionService;

    private User mockUser(Long id) {
        User u = new User();
        u.setUsername("u" + id);
        // getId methodu var, alan private, ama setId yok; bu durumda ya reflection
        // kullanirsin ya da sadece repo sonucuna bakarsin.
        // Burada sadece id lazim oldugu icin SpaceMember icinde user set ederken
        // id ayari onemli degil; SpaceMemberRepository mock'undan donecek veriyle calisiyoruz.
        return u;
    }

    @Test
    @DisplayName("hasSpaceAccess: Kullanici uyelik yoksa false donmeli")
    void hasSpaceAccess_uyelikYoksa_false() {
        // given
        Long spaceId = 1L;

        User user = mockUser(10L);
        when(userService.getCurrentUser()).thenReturn(user);
        when(spaceMemberRepository.findBySpaceIdAndUserId(spaceId, user.getId()))
                .thenReturn(Optional.empty());

        // when
        boolean result = permissionService.hasSpaceAccess(spaceId, "VIEWER");

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("hasSpaceAccess: OWNER sadece OWNER'a erisebilmeli")
    void hasSpaceAccess_ownerIcin() {
        // given
        Long spaceId = 1L;
        User user = mockUser(10L);

        SpaceMember member = new SpaceMember();
        member.setSpace(new Space());
        member.setUser(user);
        member.setRole(SpaceRole.OWNER);

        when(userService.getCurrentUser()).thenReturn(user);
        when(spaceMemberRepository.findBySpaceIdAndUserId(spaceId, user.getId()))
                .thenReturn(Optional.of(member));

        // when & then
        assertTrue(permissionService.hasSpaceAccess(spaceId, "OWNER"));
        assertTrue(permissionService.hasSpaceAccess(spaceId, "ADMIN"));
        assertTrue(permissionService.hasSpaceAccess(spaceId, "EDITOR"));
        assertTrue(permissionService.hasSpaceAccess(spaceId, "VIEWER"));
    }

    @Test
    @DisplayName("hasSpaceAccess: VIEWER sadece VIEWER seviye erisebilmeli")
    void hasSpaceAccess_viewerIcin() {
        // given
        Long spaceId = 1L;
        User user = mockUser(10L);

        SpaceMember member = new SpaceMember();
        member.setSpace(new Space());
        member.setUser(user);
        member.setRole(SpaceRole.VIEWER);

        when(userService.getCurrentUser()).thenReturn(user);
        when(spaceMemberRepository.findBySpaceIdAndUserId(spaceId, user.getId()))
                .thenReturn(Optional.of(member));

        // when & then
        assertFalse(permissionService.hasSpaceAccess(spaceId, "OWNER"));
        assertFalse(permissionService.hasSpaceAccess(spaceId, "ADMIN"));
        assertFalse(permissionService.hasSpaceAccess(spaceId, "EDITOR"));
        assertTrue(permissionService.hasSpaceAccess(spaceId, "VIEWER"));
    }
}