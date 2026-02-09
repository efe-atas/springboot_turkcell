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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TaskService'in tum temel davranislarini test eden unit test sinifi.
 * Burada amac, junior bir gelistiricinin:
 *  - service katmanini nasil test edecegini
 *  - repository ve diger servisleri nasil mock'layacagini
 *  - hangi senaryolari dusunmesi gerektigini gormesi.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    // region getTasksBySpaceId

    @Test
    @DisplayName("getTasksBySpaceId: Space altindaki tum task'leri TaskResponse listesine map etmeli")
    void getTasksBySpaceId_tumTaskleriDonmeli() {
        // given
        Long spaceId = 1L;

        Space space = new Space();
        space.setId(spaceId);

        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("T1");
        task1.setDescription("D1");
        task1.setCompleted(false);
        task1.setSpace(space);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("T2");
        task2.setDescription("D2");
        task2.setCompleted(true);
        task2.setSpace(space);

        when(taskRepository.findBySpaceId(spaceId)).thenReturn(List.of(task1, task2));

        // when
        List<TaskResponse> responses = taskService.getTasksBySpaceId(spaceId);

        // then
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());
        assertEquals(spaceId, responses.get(0).getSpaceId());
        assertEquals(spaceId, responses.get(1).getSpaceId());
    }

    // endregion

    // region getTaskById

    @Test
    @DisplayName("getTaskById: Task bulunamadiginda ResourceNotFoundException firlatmali")
    void getTaskById_taskYoksa_exceptionFirlatmali() {
        // given
        Long spaceId = 1L;
        Long taskId = 10L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.getTaskById(spaceId, taskId));
    }

    @Test
    @DisplayName("getTaskById: Task farkli space'e aitse BadRequestException firlatmali")
    void getTaskById_spaceUyusmazsa_exceptionFirlatmali() {
        // given
        Long beklenenSpaceId = 1L;
        Long taskId = 10L;

        Space farkliSpace = new Space();
        farkliSpace.setId(2L);

        Task task = new Task();
        task.setId(taskId);
        task.setSpace(farkliSpace);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // when & then
        assertThrows(BadRequestException.class,
                () -> taskService.getTaskById(beklenenSpaceId, taskId));
    }

    @Test
    @DisplayName("getTaskById: Task ve space dogruysa dogru TaskResponse donmeli")
    void getTaskById_gecerliDurumda_taskResponseDonmeli() {
        // given
        Long spaceId = 1L;
        Long taskId = 10L;

        Space space = new Space();
        space.setId(spaceId);

        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Baslik");
        task.setDescription("Aciklama");
        task.setCompleted(false);
        task.setSpace(space);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // when
        TaskResponse response = taskService.getTaskById(spaceId, taskId);

        // then
        assertEquals(taskId, response.getId());
        assertEquals("Baslik", response.getTitle());
        assertEquals("Aciklama", response.getDescription());
        assertFalse(response.isCompleted());
        assertEquals(spaceId, response.getSpaceId());
    }

    // endregion

    // region createTask

    @Test
    @DisplayName("createTask: Space yoksa ResourceNotFoundException firlatmali")
    void createTask_spaceYoksa_exceptionFirlatmali() {
        // given
        Long spaceId = 1L;
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("T");

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.createTask(spaceId, request));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask: Gecerli istek ile task olusturup TaskResponse donmeli")
    void createTask_gecerliIstek_taskOlusturmali() {
        // given
        Long spaceId = 1L;

        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Market Alisverisi");
        request.setDescription("Sut al");
        request.setLatitude(41.0);
        request.setLongitude(29.0);
        request.setRadiusInMeters(100.0);

        Space space = new Space();
        space.setId(spaceId);

        User user = new User();

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(userService.getCurrentUser()).thenReturn(user);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);

        Task savedTask = new Task();
        savedTask.setId(10L);
        savedTask.setTitle(request.getTitle());
        savedTask.setDescription(request.getDescription());
        savedTask.setLatitude(request.getLatitude());
        savedTask.setLongitude(request.getLongitude());
        savedTask.setRadiusInMeters(request.getRadiusInMeters());
        savedTask.setSpace(space);
        savedTask.setCreatedBy(user);
        savedTask.setCompleted(false);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // when
        TaskResponse response = taskService.createTask(spaceId, request);

        // then - response kontrolu
        assertEquals(10L, response.getId());
        assertEquals("Market Alisverisi", response.getTitle());
        assertEquals("Sut al", response.getDescription());
        assertEquals(spaceId, response.getSpaceId());

        // then - kaydedilen Task degerleri kontrolu
        verify(taskRepository).save(taskCaptor.capture());
        Task kaydedilenTask = taskCaptor.getValue();
        assertEquals("Market Alisverisi", kaydedilenTask.getTitle());
        assertEquals("Sut al", kaydedilenTask.getDescription());
        assertEquals(space, kaydedilenTask.getSpace());
        assertEquals(user, kaydedilenTask.getCreatedBy());
        assertFalse(kaydedilenTask.isCompleted());
    }

    // endregion

    // region patchTask

    @Test
    @DisplayName("patchTask: Task yoksa ResourceNotFoundException firlatmali")
    void patchTask_taskYoksa_exceptionFirlatmali() {
        // given
        Long spaceId = 1L;
        Long taskId = 5L;

        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Yeni Baslik");

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.patchTask(spaceId, taskId, request));
    }

    @Test
    @DisplayName("patchTask: Sadece null olmayan alanlari guncellemeli")
    void patchTask_sadeceNullOlmayanAlanlariGuncellemeli() {
        // given
        Long spaceId = 1L;
        Long taskId = 5L;

        Space space = new Space();
        space.setId(spaceId);

        Task mevcutTask = new Task();
        mevcutTask.setId(taskId);
        mevcutTask.setTitle("Eski Baslik");
        mevcutTask.setDescription("Eski Aciklama");
        mevcutTask.setCompleted(false);
        mevcutTask.setLatitude(40.0);
        mevcutTask.setLongitude(28.0);
        mevcutTask.setRadiusInMeters(50.0);
        mevcutTask.setSpace(space);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mevcutTask));

        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Yeni Baslik");
        request.setDescription(null);
        request.setCompleted(true);
        request.setLatitude(null);
        request.setLongitude(29.0);
        request.setRadiusInMeters(100.0);

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        TaskResponse response = taskService.patchTask(spaceId, taskId, request);

        // then
        assertEquals("Yeni Baslik", response.getTitle());
        assertEquals("Eski Aciklama", response.getDescription());
        assertTrue(response.isCompleted());
        assertEquals(40.0, response.getLatitude());
        assertEquals(29.0, response.getLongitude());
        assertEquals(100.0, response.getRadiusInMeters());
    }

    // endregion

    // region deleteTask

    @Test
    @DisplayName("deleteTask: Task bulunamazsa ResourceNotFoundException firlatmali")
    void deleteTask_taskYoksa_exceptionFirlatmali() {
        // given
        Long spaceId = 1L;
        Long taskId = 5L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(spaceId, taskId));

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    @DisplayName("deleteTask: Task farkli space'e aitse BadRequestException firlatmali")
    void deleteTask_spaceUyusmazsa_exceptionFirlatmali() {
        // given
        Long spaceId = 1L;
        Long taskId = 5L;

        Space baskaSpace = new Space();
        baskaSpace.setId(2L);

        Task task = new Task();
        task.setId(taskId);
        task.setSpace(baskaSpace);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // when & then
        assertThrows(BadRequestException.class,
                () -> taskService.deleteTask(spaceId, taskId));

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    @DisplayName("deleteTask: Task dogru space'e aitse silinmeli")
    void deleteTask_gecerliDurumda_silmeli() {
        // given
        Long spaceId = 1L;
        Long taskId = 5L;

        Space space = new Space();
        space.setId(spaceId);

        Task task = new Task();
        task.setId(taskId);
        task.setSpace(space);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // when
        taskService.deleteTask(spaceId, taskId);

        // then
        verify(taskRepository, times(1)).delete(task);
    }

    // endregion

    // region findNearbyTasks

    @Test
    @DisplayName("findNearbyTasks: Konumu olmayan task'leri elemelidir (repo zaten filtreliyor)")
    void findNearbyTasks_konumuOlmayanlariEleMeli() {
        // given
        Long spaceId = 1L;
        double userLat = 41.0;
        double userLon = 29.0;
        double requestedRadius = 100.0;

        when(taskRepository.findBySpaceIdAndLatitudeIsNotNullAndLongitudeIsNotNull(spaceId))
                .thenReturn(Collections.emptyList());

        // when
        List<TaskResponse> result = taskService.findNearbyTasks(spaceId, userLat, userLon, requestedRadius);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findNearbyTasks: Yakin olan task'leri donmeli, uzak olanlari elemelidir")
    void findNearbyTasks_yakinOlanlariDonmeli() {
        // given
        Long spaceId = 1L;
        double userLat = 41.0;
        double userLon = 29.0;
        double requestedRadius = 100.0;

        Task taskA = new Task();
        taskA.setId(1L);
        taskA.setLatitude(41.0);
        taskA.setLongitude(29.0);
        taskA.setRadiusInMeters(null);
        taskA.setSpace(new Space());

        Task taskB = new Task();
        taskB.setId(2L);
        taskB.setLatitude(41.0);
        taskB.setLongitude(29.0);
        taskB.setRadiusInMeters(5.0);
        taskB.setSpace(new Space());

        Task taskC = new Task();
        taskC.setId(3L);
        taskC.setLatitude(40.0);
        taskC.setLongitude(28.0);
        taskC.setRadiusInMeters(10.0);
        taskC.setSpace(new Space());

        when(taskRepository.findBySpaceIdAndLatitudeIsNotNullAndLongitudeIsNotNull(spaceId))
                .thenReturn(List.of(taskA, taskB, taskC));

        // when
        List<TaskResponse> result = taskService.findNearbyTasks(spaceId, userLat, userLon, requestedRadius);

        // then
        List<Long> ids = result.stream()
                .map(TaskResponse::getId)
                .toList();

        assertTrue(ids.contains(1L));
        assertTrue(ids.contains(2L));
        assertFalse(ids.contains(3L));
    }

    // endregion
}


