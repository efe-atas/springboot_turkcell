package com.ismail.todoapp.service;

import com.ismail.todoapp.dto.task.TaskCreateRequest;
import com.ismail.todoapp.dto.task.TaskResponse;
import com.ismail.todoapp.dto.task.TaskUpdateRequest;
import com.ismail.todoapp.entity.Space;
import com.ismail.todoapp.entity.Task;
import com.ismail.todoapp.entity.User;
import com.ismail.todoapp.exception.BadRequestException;
import com.ismail.todoapp.exception.ResourceNotFoundException;
import com.ismail.todoapp.repository.SpaceRepository;
import com.ismail.todoapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final SpaceRepository spaceRepository;
    private final UserService userService;

    // 1. Bir Space icindeki tum gorevleri getir
    public List<TaskResponse> getTasksBySpaceId(Long spaceId) {
        return taskRepository.findBySpaceId(spaceId).stream()
                .map(this::toTaskResponse)
                .collect(Collectors.toList());
    }

    // 1.5. Tek bir gorevi getir
    public TaskResponse getTaskById(Long spaceId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Gorev", taskId));

        if (!task.getSpace().getId().equals(spaceId)) {
            throw new BadRequestException("Bu gorev bu calisma alanina ait degil");
        }

        return toTaskResponse(task);
    }

    // 2. Yeni Task olustur ve Space'e bagla
    public TaskResponse createTask(Long spaceId, TaskCreateRequest request) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", spaceId));

        User user = userService.getCurrentUser();

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setSpace(space);
        task.setCreatedBy(user);
        task.setCompleted(false);

        Task savedTask = taskRepository.save(task);
        return toTaskResponse(savedTask);
    }

    // 3. PATCH Mantigi: Sadece gelen (null olmayan) alanlari guncelle
    public TaskResponse patchTask(Long spaceId, Long taskId, TaskUpdateRequest request) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Gorev", taskId));

        // Guvenlik: Bu gorev gercekten bu Space'e mi ait?
        if (!existingTask.getSpace().getId().equals(spaceId)) {
            throw new BadRequestException("Bu gorev bu calisma alanina ait degil");
        }

        // Kismi Guncelleme (Partial Update)
        if (request.getTitle() != null) {
            existingTask.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            existingTask.setDescription(request.getDescription());
        }
        if (request.getCompleted() != null) {
            existingTask.setCompleted(request.getCompleted());
        }

        Task updatedTask = taskRepository.save(existingTask);
        return toTaskResponse(updatedTask);
    }

    // 4. Silme Islemi
    public void deleteTask(Long spaceId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Gorev", taskId));

        if (!task.getSpace().getId().equals(spaceId)) {
            throw new BadRequestException("Bu gorev bu calisma alanina ait degil");
        }

        taskRepository.delete(task);
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
