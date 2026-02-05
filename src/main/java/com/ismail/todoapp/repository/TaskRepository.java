package com.ismail.todoapp.repository;

import com.ismail.todoapp.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findBySpaceId(Long spaceId);

    List<Task> findBySpaceIdAndLatitudeIsNotNullAndLongitudeIsNotNull(Long spaceId);
}