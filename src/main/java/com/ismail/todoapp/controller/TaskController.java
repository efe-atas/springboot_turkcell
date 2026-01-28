package com.ismail.todoapp.controller;

import com.ismail.todoapp.dto.TaskCreateRequest;
import com.ismail.todoapp.dto.TaskUpdateRequest;
import com.ismail.todoapp.entity.Task;
import com.ismail.todoapp.service.TaskService;
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

@RestController
@RequestMapping("/api/spaces/{spaceId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Görev Yönetimi", description = "Çalışma alanı içindeki görevlerin CRUD işlemleri")
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Görevleri listele",
            description = "Belirtilen çalışma alanındaki tüm görevleri listeler. VIEWER ve üstü yetki gerektirir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Görevler başarıyla listelendi",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Task.class)))),
            @ApiResponse(responseCode = "403", description = "Bu alana erişim yetkiniz yok", content = @Content),
            @ApiResponse(responseCode = "404", description = "Çalışma alanı bulunamadı", content = @Content)
    })
    @GetMapping
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'VIEWER')")
    public ResponseEntity<List<Task>> getTasksBySpace(
            @Parameter(description = "Çalışma alanı ID'si", required = true)
            @PathVariable Long spaceId) {
        return ResponseEntity.ok(taskService.getTasksBySpaceId(spaceId));
    }

    @Operation(
            summary = "Görev detayı getir",
            description = "Belirtilen görevin detaylarını getirir. VIEWER ve üstü yetki gerektirir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Görev başarıyla getirildi",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "403", description = "Bu alana erişim yetkiniz yok", content = @Content),
            @ApiResponse(responseCode = "404", description = "Görev bulunamadı", content = @Content)
    })
    @GetMapping("/{taskId}")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'VIEWER')")
    public ResponseEntity<Task> getTaskById(
            @Parameter(description = "Çalışma alanı ID'si", required = true)
            @PathVariable Long spaceId,
            @Parameter(description = "Görev ID'si", required = true)
            @PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(spaceId, taskId));
    }

    @Operation(
            summary = "Yeni görev oluştur",
            description = "Belirtilen çalışma alanında yeni bir görev oluşturur. EDITOR ve üstü yetki gerektirir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Görev başarıyla oluşturuldu",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content),
            @ApiResponse(responseCode = "403", description = "Bu alanda görev oluşturma yetkiniz yok", content = @Content),
            @ApiResponse(responseCode = "404", description = "Çalışma alanı bulunamadı", content = @Content)
    })
    @PostMapping
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'EDITOR')")
    public ResponseEntity<Task> createTask(
            @Parameter(description = "Çalışma alanı ID'si", required = true)
            @PathVariable Long spaceId,
            @RequestBody TaskCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(spaceId, request));
    }

    @Operation(
            summary = "Görevi güncelle",
            description = "Görevin belirli alanlarını günceller (kısmi güncelleme). EDITOR ve üstü yetki gerektirir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Görev başarıyla güncellendi",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content),
            @ApiResponse(responseCode = "403", description = "Bu görevi güncelleme yetkiniz yok", content = @Content),
            @ApiResponse(responseCode = "404", description = "Görev bulunamadı", content = @Content)
    })
    @PatchMapping("/{taskId}")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'EDITOR')")
    public ResponseEntity<Task> patchTask(
            @Parameter(description = "Çalışma alanı ID'si", required = true)
            @PathVariable Long spaceId,
            @Parameter(description = "Görev ID'si", required = true)
            @PathVariable Long taskId,
            @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(taskService.patchTask(spaceId, taskId, request));
    }

    @Operation(
            summary = "Görevi sil",
            description = "Belirtilen görevi kalıcı olarak siler. ADMIN ve üstü yetki gerektirir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Görev başarıyla silindi"),
            @ApiResponse(responseCode = "403", description = "Bu görevi silme yetkiniz yok", content = @Content),
            @ApiResponse(responseCode = "404", description = "Görev bulunamadı", content = @Content)
    })
    @DeleteMapping("/{taskId}")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'ADMIN')")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Çalışma alanı ID'si", required = true)
            @PathVariable Long spaceId,
            @Parameter(description = "Görev ID'si", required = true)
            @PathVariable Long taskId) {
        taskService.deleteTask(spaceId, taskId);
        return ResponseEntity.noContent().build();
    }
}