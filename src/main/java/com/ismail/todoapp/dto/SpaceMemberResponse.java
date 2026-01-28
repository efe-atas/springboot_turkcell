package com.ismail.todoapp.dto;

import com.ismail.todoapp.enums.SpaceRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SpaceMemberResponse {
    private Long memberId;
    private Long userId;
    private String username;
    private SpaceRole role;
}
