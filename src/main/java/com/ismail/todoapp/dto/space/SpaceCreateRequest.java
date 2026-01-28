package com.ismail.todoapp.dto.space;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Yeni çalışma alanı oluşturma isteği")
public class SpaceCreateRequest {

    @Schema(description = "Çalışma alanı adı", example = "Proje Alpha", required = true)
    private String name;

    @Schema(description = "Çalışma alanı açıklaması", example = "2026 yılı ana proje çalışma alanı")
    private String description;
}

