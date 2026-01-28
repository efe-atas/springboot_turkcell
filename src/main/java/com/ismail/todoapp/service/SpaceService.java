package com.ismail.todoapp.service;

import com.ismail.todoapp.dto.space.SpaceCreateRequest;
import com.ismail.todoapp.dto.space.SpaceMemberRequest;
import com.ismail.todoapp.dto.space.SpaceMemberResponse;
import com.ismail.todoapp.dto.space.SpaceResponse;
import com.ismail.todoapp.dto.space.SpaceUpdateRequest;
import com.ismail.todoapp.entity.Space;
import com.ismail.todoapp.entity.SpaceMember;
import com.ismail.todoapp.entity.User;
import com.ismail.todoapp.enums.SpaceRole;
import com.ismail.todoapp.exception.BadRequestException;
import com.ismail.todoapp.exception.ConflictException;
import com.ismail.todoapp.exception.ResourceNotFoundException;
import com.ismail.todoapp.exception.UnauthorizedException;
import com.ismail.todoapp.repository.SpaceMemberRepository;
import com.ismail.todoapp.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceMemberRepository spaceMemberRepository;
    private final UserService userService;

    /**
     * Yeni space olustur - olusturan kisi otomatik OWNER olur
     */
    @Transactional
    public Space createSpace(SpaceCreateRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Space ismi zorunludur");
        }

        User currentUser = userService.getCurrentUser();

        // Space olustur
        Space space = new Space();
        space.setName(request.getName().trim());
        space.setDescription(request.getDescription());
        Space savedSpace = spaceRepository.save(space);

        // Olusturan kisiyi OWNER olarak ekle
        SpaceMember ownerMember = new SpaceMember();
        ownerMember.setSpace(savedSpace);
        ownerMember.setUser(currentUser);
        ownerMember.setRole(SpaceRole.OWNER);
        spaceMemberRepository.save(ownerMember);

        return savedSpace;
    }

    /**
     * Kullanicinin uye oldugu space'leri getir
     */
    public List<SpaceResponse> getUserSpaces() {
        User currentUser = userService.getCurrentUser();
        List<SpaceMember> memberships = spaceMemberRepository.findByUserId(currentUser.getId());

        return memberships.stream()
                .map(membership -> toSpaceResponse(membership.getSpace()))
                .collect(Collectors.toList());
    }

    /**
     * Tek bir space'in detaylarini getir
     */
    public SpaceResponse getSpaceById(Long spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", spaceId));
        return toSpaceResponse(space);
    }

    /**
     * Space guncelle (isim, aciklama)
     */
    @Transactional
    public Space updateSpace(Long spaceId, SpaceUpdateRequest request) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", spaceId));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            space.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            space.setDescription(request.getDescription());
        }

        return spaceRepository.save(space);
    }

    /**
     * Space sil - sadece OWNER yapabilir (cascade ile task ve member'lar da silinir)
     */
    @Transactional
    public void deleteSpace(Long spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", spaceId));
        spaceRepository.delete(space);
    }

    /**
     * Space uyeliklerini listele
     */
    public List<SpaceMemberResponse> getSpaceMembers(Long spaceId) {
        if (!spaceRepository.existsById(spaceId)) {
            throw new ResourceNotFoundException("Space", spaceId);
        }

        List<SpaceMember> members = spaceMemberRepository.findBySpaceId(spaceId);
        return members.stream()
                .map(this::toMemberResponse)
                .collect(Collectors.toList());
    }

    /**
     * Space'e yeni uye ekle
     */
    @Transactional
    public SpaceMemberResponse addMember(Long spaceId, SpaceMemberRequest request) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", spaceId));

        User userToAdd = userService.getUserById(request.getUserId());

        // Zaten uye mi kontrol et
        if (spaceMemberRepository.findBySpaceIdAndUserId(spaceId, request.getUserId()).isPresent()) {
            throw new ConflictException("Bu kullanici zaten bu space'in uyesi");
        }

        // OWNER rolu verilemez - sadece bir OWNER olabilir
        if (request.getRole() == SpaceRole.OWNER) {
            throw new BadRequestException("OWNER rolu atanamaz. Her space'in sadece bir OWNER'i olabilir");
        }

        SpaceMember member = new SpaceMember();
        member.setSpace(space);
        member.setUser(userToAdd);
        member.setRole(request.getRole() != null ? request.getRole() : SpaceRole.VIEWER);

        SpaceMember savedMember = spaceMemberRepository.save(member);
        return toMemberResponse(savedMember);
    }

    /**
     * Uye rolunu guncelle
     */
    @Transactional
    public SpaceMemberResponse updateMemberRole(Long spaceId, Long memberId, SpaceRole newRole) {
        SpaceMember member = spaceMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Uyelik", memberId));

        // Uyelik bu space'e ait mi?
        if (!member.getSpace().getId().equals(spaceId)) {
            throw new BadRequestException("Bu uyelik bu space'e ait degil");
        }

        // OWNER rolu degistirilemez
        if (member.getRole() == SpaceRole.OWNER) {
            throw new BadRequestException("OWNER'in rolu degistirilemez");
        }

        // OWNER rolu verilemez
        if (newRole == SpaceRole.OWNER) {
            throw new BadRequestException("OWNER rolu atanamaz");
        }

        member.setRole(newRole);
        SpaceMember updatedMember = spaceMemberRepository.save(member);
        return toMemberResponse(updatedMember);
    }

    /**
     * Uyeyi space'ten cikar
     */
    @Transactional
    public void removeMember(Long spaceId, Long memberId) {
        SpaceMember member = spaceMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Uyelik", memberId));

        // Uyelik bu space'e ait mi?
        if (!member.getSpace().getId().equals(spaceId)) {
            throw new BadRequestException("Bu uyelik bu space'e ait degil");
        }

        // OWNER cikarilaamaz
        if (member.getRole() == SpaceRole.OWNER) {
            throw new BadRequestException("OWNER space'ten cikarilmaz. Space'i silmeniz gerekir");
        }

        spaceMemberRepository.delete(member);
    }

    // Helper: Space -> SpaceResponse
    private SpaceResponse toSpaceResponse(Space space) {
        List<SpaceMemberResponse> members = space.getMembers().stream()
                .map(this::toMemberResponse)
                .collect(Collectors.toList());

        return SpaceResponse.builder()
                .id(space.getId())
                .name(space.getName())
                .description(space.getDescription())
                .members(members)
                .build();
    }

    // Helper: SpaceMember -> SpaceMemberResponse
    private SpaceMemberResponse toMemberResponse(SpaceMember member) {
        return SpaceMemberResponse.builder()
                .memberId(member.getId())
                .userId(member.getUser().getId())
                .username(member.getUser().getUsername())
                .role(member.getRole())
                .build();
    }
}
