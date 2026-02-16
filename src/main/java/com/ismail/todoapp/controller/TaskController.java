package com.ismail.todoapp.controller;

import com.ismail.todoapp.config.StandardAccessErrorResponses;
import com.ismail.todoapp.dto.task.NearbyTasksRequest;
import com.ismail.todoapp.dto.task.TaskCreateRequest;
import com.ismail.todoapp.dto.task.TaskResponse;
import com.ismail.todoapp.dto.task.TaskUpdateRequest;
import com.ismail.todoapp.service.RateLimitingService;
import com.ismail.todoapp.service.TaskService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/spaces/{spaceId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Görev Yönetimi", description = "Çalışma alanı içindeki görevlerin CRUD işlemleri")
public class TaskController {

    private final TaskService taskService;
    private final RateLimitingService rateLimitingService;
    private final UserService userService;

    @Operation(summary = "Görevleri listele", description = "Belirtilen çalışma alanındaki tüm görevleri listeler. VIEWER ve üstü yetki gerektirir.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Görevler başarıyla listelendi", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class))))})
    @StandardAccessErrorResponses
    @GetMapping
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'VIEWER')")
    public ResponseEntity<List<TaskResponse>> getTasksBySpace(@Parameter(description = "Çalışma alanı ID'si", required = true) @PathVariable Long spaceId) {
        return ResponseEntity.ok(taskService.getTasksBySpaceId(spaceId));
    }

    @Operation(summary = "Görev detayı getir", description = "Belirtilen görevin detaylarını getirir. VIEWER ve üstü yetki gerektirir.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Görev başarıyla getirildi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class)))})
    @StandardAccessErrorResponses
    @GetMapping("/{taskId}")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'VIEWER')")
    public ResponseEntity<TaskResponse> getTaskById(@Parameter(description = "Çalışma alanı ID'si", required = true) @PathVariable Long spaceId, @Parameter(description = "Görev ID'si", required = true) @PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(spaceId, taskId));
    }

    @Operation(summary = "Yeni görev oluştur", description = "Belirtilen çalışma alanında yeni bir görev oluşturur. EDITOR ve üstü yetki gerektirir.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Görev başarıyla oluşturuldu", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))), @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content),})
    @StandardAccessErrorResponses
    @PostMapping
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'EDITOR')")
    public ResponseEntity<TaskResponse> createTask(@Parameter(description = "Çalışma alanı ID'si", required = true) @PathVariable Long spaceId, @RequestBody TaskCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(spaceId, request));
    }

    @Operation(summary = "Görevi güncelle", description = "Görevin belirli alanlarını günceller (kısmi güncelleme). EDITOR ve üstü yetki gerektirir.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Görev başarıyla güncellendi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))), @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content),})
    @StandardAccessErrorResponses
    @PatchMapping("/{taskId}")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'EDITOR')")
    public ResponseEntity<TaskResponse> patchTask(@Parameter(description = "Çalışma alanı ID'si", required = true) @PathVariable Long spaceId, @Parameter(description = "Görev ID'si", required = true) @PathVariable Long taskId, @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(taskService.patchTask(spaceId, taskId, request));
    }

    @Operation(summary = "Konuma göre yakın görevleri listele", description = "Kullanıcının anlık konumuna ve verilen yarıçapa göre, belirtilen çalışma alanındaki yakın görevleri listeler. VIEWER ve üstü yetki gerektirir.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Yakın görevler başarıyla listelendi", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))), @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content)})
    @StandardAccessErrorResponses
    @PostMapping("/nearby")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'VIEWER')")
    public ResponseEntity<List<TaskResponse>> getNearbyTasks(@Parameter(description = "Çalışma alanı ID'si", required = true) @PathVariable Long spaceId, @RequestBody NearbyTasksRequest request) {

        double requestedRadius = request.getRadiusMeters() != null ? request.getRadiusMeters() : 500.0;
        List<TaskResponse> responses = taskService.findNearbyTasks(spaceId, request.getLatitude(), request.getLongitude(), requestedRadius);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Görevi sil", description = "Belirtilen görevi kalıcı olarak siler. ADMIN ve üstü yetki gerektirir.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Görev başarıyla silindi")})
    @StandardAccessErrorResponses
    @DeleteMapping("/{taskId}")
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'ADMIN')")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "Çalışma alanı ID'si", required = true) @PathVariable Long spaceId, @Parameter(description = "Görev ID'si", required = true) @PathVariable Long taskId) {
        taskService.deleteTask(spaceId, taskId);
        return ResponseEntity.noContent().build();
    }



    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Resim basariyla yuklendi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))), @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi", content = @Content),@ApiResponse(responseCode = "429", description = "Çok fazla istek", content = @Content)})
    @PreAuthorize("@permissionService.hasSpaceAccess(#spaceId, 'EDITOR')")
    @StandardAccessErrorResponses
    @PostMapping(value = "/{taskId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity <TaskResponse> uploadImage(
            @PathVariable Long taskId,
            @Parameter MultipartFile file, @PathVariable Long spaceId) throws IOException {
            String key = userService.getCurrentUser().getUsername();
            if(rateLimitingService.tryConsume(key,1)){
                return ResponseEntity.ok(taskService.uploadTaskImage(spaceId,taskId,file));
            }
            else{
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
            }


    }


}