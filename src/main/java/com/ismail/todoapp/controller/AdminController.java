package com.ismail.todoapp.controller;

import com.ismail.todoapp.dto.task.TaskResponse;
import com.ismail.todoapp.dto.user.UserResponse;
import com.ismail.todoapp.entity.Task;
import com.ismail.todoapp.entity.User;
import com.ismail.todoapp.repository.TaskRepository;
import com.ismail.todoapp.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Yönetici Paneli", description = "Sadece ADMIN rolüne sahip kullanıcılar için sistem yönetim işlemleri")
public class AdminController {

    private final UserRepository userRepository;
    private final TaskRepository todoRepository;

    @Operation(
            summary = "Tüm kullanıcıları listele",
            description = "Sistemdeki tüm kayıtlı kullanıcıları listeler. Sadece ADMIN yetkisi gerektirir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcılar başarıyla listelendi",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
            @ApiResponse(responseCode = "403", description = "Yönetici yetkisi gerekli", content = @Content)
    })
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Operation(
            summary = "Tüm görevleri listele",
            description = "Sistemdeki tüm görevleri (tüm çalışma alanlarından) listeler. Sadece ADMIN yetkisi gerektirir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Görevler başarıyla listelendi"),
            @ApiResponse(responseCode = "403", description = "Yönetici yetkisi gerekli", content = @Content)
    })
    @GetMapping("/all-todos")
    public List<TaskResponse> getAllTodosInSystem() {
        return todoRepository.findAll().stream()
                .map(this::toTaskResponse)
                .collect(Collectors.toList());
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(null) // Role bilgisi entity'de alan olarak yoksa veya disari acmak istemiyorsan null birakabilirsin
                .build();
    }

    private TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .spaceId(task.getSpace() != null ? task.getSpace().getId() : null)
                .createdById(task.getCreatedBy() != null ? task.getCreatedBy().getId() : null)
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .build();
    }
}