package com.ismail.todoapp.controller;

import com.ismail.todoapp.dto.space.SpaceCreateRequest;
import com.ismail.todoapp.dto.space.SpaceMemberRequest;
import com.ismail.todoapp.dto.space.SpaceMemberResponse;
import com.ismail.todoapp.dto.space.SpaceResponse;
import com.ismail.todoapp.dto.space.SpaceUpdateRequest;
import com.ismail.todoapp.dto.user.UserSearchResponse;
import com.ismail.todoapp.entity.Space;
import com.ismail.todoapp.enums.SpaceRole;
import com.ismail.todoapp.service.SpaceService;
import com.ismail.todoapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
@Tag(name = "Çalışma Alanı Yönetimi", description = "Çalışma alanları (Space) ve üye yönetimi işlemleri")
public class SpaceController {

    private final SpaceService spaceService;
    private final UserService userService;

    // ==================== SPACE CRUD ====================

    @Operation(
            summary = "Yeni çalışma alanı oluştur",
            description = "Yeni bir çalışma alanı oluşturur. Oluşturan kullanıcı otomatik olarak OWNER rolü alır."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Çalışma alanı başarıyla oluşturuldu",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpaceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli", content = @Content)
    })
    @PostMapping
    public ResponseEntity<SpaceResponse> createSpace(@RequestBody SpaceCreateRequest request) {
        Space space = spaceService.createSpace(request);
        SpaceResponse response = spaceService.getSpaceById(space.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Çalışma alanlarımı listele",
            description = "Giriş yapan kullanıcının üyesi olduğu tüm çalışma alanlarını listeler."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Çalışma alanları başarıyla listelendi",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SpaceResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<SpaceResponse>> getUserSpaces() {
        return ResponseEntity.ok(spaceService.getUserSpaces());
    }

    @Operation(
            summary = "Çalışma alanı detayı",
            description = "Belirtilen çalışma alanının detaylarını getirir. VIEWER ve üstü yetki gerektirir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Çalışma alanı detayları getirildi",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpaceResponse.class))),
            @ApiResponse(responseCode = "403", description = "Bu alana erişim yetkiniz yok", content = @Content),
            @ApiResponse(responseCode = "404", description = "Çalışma alanı bulunamadı", content = @Content)
    })
    @GetMapping("/{spaceId}")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'VIEWER')")
    public ResponseEntity<SpaceResponse> getSpace(
            @Parameter(description = "Çalışma alanı ID'si", required = true)
            @PathVariable Long spaceId) {
        return ResponseEntity.ok(spaceService.getSpaceById(spaceId));
    }

    @Operation(
            summary = "Çalışma alanını güncelle",
            description = "Çalışma alanının adını ve açıklamasını günceller. ADMIN ve üstü yetki gerektirir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Çalışma alanı başarıyla güncellendi",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpaceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content),
            @ApiResponse(responseCode = "403", description = "Bu alanı güncelleme yetkiniz yok", content = @Content),
            @ApiResponse(responseCode = "404", description = "Çalışma alanı bulunamadı", content = @Content)
    })
    @PatchMapping("/{spaceId}")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'ADMIN')")
    public ResponseEntity<SpaceResponse> updateSpace(
            @Parameter(description = "Çalışma alanı ID'si", required = true)
            @PathVariable Long spaceId,
            @RequestBody SpaceUpdateRequest request) {
        spaceService.updateSpace(spaceId, request);
        return ResponseEntity.ok(spaceService.getSpaceById(spaceId));
    }

    @Operation(
            summary = "Çalışma alanını sil",
            description = "Çalışma alanını ve içindeki tüm görevleri kalıcı olarak siler. Sadece OWNER yetkisi ile yapılabilir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Çalışma alanı başarıyla silindi"),
            @ApiResponse(responseCode = "403", description = "Bu alanı silme yetkiniz yok (OWNER gerekli)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Çalışma alanı bulunamadı", content = @Content)
    })
    @DeleteMapping("/{spaceId}")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'OWNER')")
    public ResponseEntity<Void> deleteSpace(
            @Parameter(description = "Çalışma alanı ID'si", required = true)
            @PathVariable Long spaceId) {
        spaceService.deleteSpace(spaceId);
        return ResponseEntity.noContent().build();
    }

    // ==================== MEMBER MANAGEMENT ====================

    @Operation(
            summary = "Üyeleri listele",
            description = "Çalışma alanındaki tüm üyeleri ve rollerini listeler. VIEWER ve üstü yetki gerektirir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Üyeler başarıyla listelendi",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SpaceMemberResponse.class)))),
            @ApiResponse(responseCode = "403", description = "Bu alana erişim yetkiniz yok", content = @Content),
            @ApiResponse(responseCode = "404", description = "Çalışma alanı bulunamadı", content = @Content)
    })
    @GetMapping("/{spaceId}/members")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'VIEWER')")
    public ResponseEntity<List<SpaceMemberResponse>> getMembers(
            @Parameter(description = "Çalışma alanı ID'si", required = true)
            @PathVariable Long spaceId) {
        return ResponseEntity.ok(spaceService.getSpaceMembers(spaceId));
    }

    @Operation(
            summary = "Üye ekle",
            description = "Çalışma alanına yeni bir üye ekler ve rol atar. ADMIN ve üstü yetki gerektirir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Üye başarıyla eklendi",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpaceMemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content),
            @ApiResponse(responseCode = "403", description = "Üye ekleme yetkiniz yok", content = @Content),
            @ApiResponse(responseCode = "404", description = "Kullanıcı veya çalışma alanı bulunamadı", content = @Content),
            @ApiResponse(responseCode = "409", description = "Kullanıcı zaten üye", content = @Content)
    })
    @PostMapping("/{spaceId}/members")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'ADMIN')")
    public ResponseEntity<SpaceMemberResponse> addMember(
            @Parameter(description = "Çalışma alanı ID'si", required = true)
            @PathVariable Long spaceId,
            @RequestBody SpaceMemberRequest request) {
        SpaceMemberResponse member = spaceService.addMember(spaceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @Operation(
            summary = "Üye rolünü güncelle",
            description = "Bir üyenin rolünü değiştirir. ADMIN ve üstü yetki gerektirir. OWNER rolü değiştirilemez."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Üye rolü başarıyla güncellendi",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpaceMemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz rol veya OWNER değiştirilemez", content = @Content),
            @ApiResponse(responseCode = "403", description = "Rol güncelleme yetkiniz yok", content = @Content),
            @ApiResponse(responseCode = "404", description = "Üye bulunamadı", content = @Content)
    })
    @PatchMapping("/{spaceId}/members/{memberId}")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'ADMIN')")
    public ResponseEntity<SpaceMemberResponse> updateMemberRole(
            @Parameter(description = "Çalışma alanı ID'si", required = true)
            @PathVariable Long spaceId,
            @Parameter(description = "Üyelik ID'si", required = true)
            @PathVariable Long memberId,
            @RequestBody SpaceMemberRequest request) {
        SpaceMemberResponse member = spaceService.updateMemberRole(spaceId, memberId, request.getRole());
        return ResponseEntity.ok(member);
    }

    @Operation(
            summary = "Üyeyi çıkar",
            description = "Bir üyeyi çalışma alanından çıkarır. ADMIN ve üstü yetki gerektirir. OWNER çıkarılamaz."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Üye başarıyla çıkarıldı"),
            @ApiResponse(responseCode = "400", description = "OWNER çıkarılamaz", content = @Content),
            @ApiResponse(responseCode = "403", description = "Üye çıkarma yetkiniz yok", content = @Content),
            @ApiResponse(responseCode = "404", description = "Üye bulunamadı", content = @Content)
    })
    @DeleteMapping("/{spaceId}/members/{memberId}")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'ADMIN')")
    public ResponseEntity<Void> removeMember(
            @Parameter(description = "Çalışma alanı ID'si", required = true)
            @PathVariable Long spaceId,
            @Parameter(description = "Üyelik ID'si", required = true)
            @PathVariable Long memberId) {
        spaceService.removeMember(spaceId, memberId);
        return ResponseEntity.noContent().build();
    }

    // ==================== USER SEARCH (Davet icin) ====================

    @Operation(
            summary = "Kullanıcı ara",
            description = "Çalışma alanına davet etmek için kullanıcı arar. Kullanıcı adına göre arama yapar."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arama sonuçları döndü",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserSearchResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli", content = @Content)
    })
    @GetMapping("/users/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(
            @Parameter(description = "Aranacak kullanıcı adı (en az 2 karakter)", required = true)
            @RequestParam("q") String query) {
        List<UserSearchResponse> users = userService.searchUsers(query).stream()
                .map(user -> UserSearchResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
}
