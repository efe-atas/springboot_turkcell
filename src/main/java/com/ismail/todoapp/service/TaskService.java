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
import com.ismail.todoapp.util.DistanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final SpaceRepository spaceRepository;
    private final UserService userService;
    private final StorageService storageService;
    private final GeocodingService geocodingService;

    // 1. Bir Space icindeki tum gorevleri getir
    public List<TaskResponse> getTasksBySpaceId(Long spaceId) {
        return taskRepository.findBySpaceId(spaceId).stream().map(this::toTaskResponse).collect(Collectors.toList());
    }

    // 1.5. Tek bir gorevi getir
    public TaskResponse getTaskById(Long spaceId, Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Gorev", taskId));

        if (!task.getSpace().getId().equals(spaceId)) {
            throw new BadRequestException("Bu gorev bu calisma alanina ait degil");
        }

        task.setImageUrl(getUrl(task)); // Frontend bu URL'i <img src="..."> içine koyacak


        return toTaskResponse(task);
    }

    // 2. Yeni Task olustur ve Space'e bagla
    public TaskResponse createTask(Long spaceId, TaskCreateRequest request) {
        Space space = spaceRepository.findById(spaceId).orElseThrow(() -> new ResourceNotFoundException("Space", spaceId));

        User user = userService.getCurrentUser();

        Task task = Task.builder().title(request.getTitle()).description(request.getDescription()).latitude(request.getLatitude()).longitude(request.getLongitude()).radiusInMeters(request.getRadiusInMeters()).space(space).createdBy(user).completed(false).build();


        Task savedTask = taskRepository.save(task);
        return toTaskResponse(savedTask);
    }

    // 3. PATCH Mantigi: Sadece gelen (null olmayan) alanlari guncelle
    public TaskResponse patchTask(Long spaceId, Long taskId, TaskUpdateRequest request) {
        Task existingTask = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Gorev", taskId));

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
        if (request.getLatitude() != null) {
            existingTask.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            existingTask.setLongitude(request.getLongitude());
        }
        if (request.getRadiusInMeters() != null) {
            existingTask.setRadiusInMeters(request.getRadiusInMeters());
        }

        Task updatedTask = taskRepository.save(existingTask);
        return toTaskResponse(updatedTask);
    }

    // 4. Silme Islemi
    public void deleteTask(Long spaceId, Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Gorev", taskId));

        if (!task.getSpace().getId().equals(spaceId)) {
            throw new BadRequestException("Bu gorev bu calisma alanina ait degil");
        }

        taskRepository.delete(task);
        storageService.deleteFile(task.getImageKey());
    }

    // 5. Kullanici konumuna gore yakin gorevleri bul
    public List<TaskResponse> findNearbyTasks(Long spaceId, double userLat, double userLon, double requestedRadiusMeters) {
        // Ilgili space altindaki, konum bilgisi olan gorevleri getir
        List<Task> tasksWithLocation = taskRepository.findBySpaceIdAndLatitudeIsNotNullAndLongitudeIsNotNull(spaceId);

        // taskwithlocation
        return tasksWithLocation.stream().filter(task -> {
            Double taskLat = task.getLatitude();
            Double taskLon = task.getLongitude();
            task.setImageUrl(getUrl(task));
            if (taskLat == null || taskLon == null) {
                return false;
            }

            double distance = DistanceUtil.distanceInMeters(userLat, userLon, taskLat, taskLon);

            double effectiveRadius = task.getRadiusInMeters() != null ? task.getRadiusInMeters() : requestedRadiusMeters;

            return distance <= effectiveRadius;
        }).map(this::toTaskResponse).collect(Collectors.toList());
    }


    @Transactional
    public TaskResponse uploadTaskImage(Long spaceId, Long taskId, MultipartFile file) throws IOException {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task bulunamadi"));

        // Guvenlik: Bu gorev gercekten bu Space'e mi ait?
        if (!existingTask.getSpace().getId().equals(spaceId)) {
            throw new BadRequestException("Bu gorev bu calisma alanina ait degil");
        }

        if (existingTask.getImageKey() != null) {
            storageService.deleteFile(existingTask.getImageKey());
        }

        String key = storageService.uploadFile(file, "tasks");

        existingTask.setImageKey(key);

        Task savedTask = taskRepository.save(existingTask);
        return toTaskResponse(savedTask);
    }


    private TaskResponse toTaskResponse(Task task) {
        String address = null;
        if (task.getLatitude() != null && task.getLongitude() != null) {
            address = geocodingService.reverseGeocode(task.getLatitude(), task.getLongitude()).orElse(null);
        }
        
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .latitude(task.getLatitude())
                .longitude(task.getLongitude())
                .radiusInMeters(task.getRadiusInMeters())
                .spaceId(task.getSpace() != null ? task.getSpace().getId() : null)
                .createdById(task.getCreatedBy() != null ? task.getCreatedBy().getId() : null)
                .imageUrl(getUrl(task))
                .address(address)
                .build();
    }

    private String getUrl(Task task) {
        if (task.getImageKey() != null) {
            String url = storageService.getPresignedUrl(task.getImageKey());
            task.setImageUrl(url); // Frontend bu URL'i <img src="..."> içine koyacak
            return url;

        }
        return "";
    }
}
