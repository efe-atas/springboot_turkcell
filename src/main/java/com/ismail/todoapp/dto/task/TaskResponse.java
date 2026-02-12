package com.ismail.todoapp.dto.task;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private boolean completed;

    private Double latitude;
    private Double longitude;
    private Double radiusInMeters;
    private String imageUrl;

    private Long spaceId;
    private Long createdById;
    private Long assigneeId;
}

