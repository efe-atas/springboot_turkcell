package com.ismail.todoapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Çalışma alanı güncelleme isteği (kısmi güncelleme destekler)")
public class SpaceUpdateRequest {
    
    @Schema(description = "Yeni çalışma alanı adı", example = "Güncellenmiş Proje Adı")
    private String name;
    
    @Schema(description = "Yeni çalışma alanı açıklaması", example = "Güncellenmiş proje açıklaması")
    private String description;
}
