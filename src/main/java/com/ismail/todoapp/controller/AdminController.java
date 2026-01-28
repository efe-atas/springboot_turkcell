package com.ismail.todoapp.controller;

import com.ismail.todoapp.entity.User;
import com.ismail.todoapp.repository.UserRepository;
import com.ismail.todoapp.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = "403", description = "Yönetici yetkisi gerekli", content = @Content)
    })
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
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
    public List<?> getAllTodosInSystem() {
        return todoRepository.findAll();
    }
}