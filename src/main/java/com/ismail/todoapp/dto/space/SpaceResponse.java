package com.ismail.todoapp.dto.space;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SpaceResponse {
    private Long id;
    private String name;
    private String description;
    private List<SpaceMemberResponse> members;
}

