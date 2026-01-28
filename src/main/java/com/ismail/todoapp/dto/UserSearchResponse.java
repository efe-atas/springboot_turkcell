package com.ismail.todoapp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSearchResponse {
    private Long id;
    private String username;
}
