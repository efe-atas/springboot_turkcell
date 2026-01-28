package com.ismail.todoapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Görev güncelleme isteği (kısmi güncelleme destekler)")
public class TaskUpdateRequest {
    
    @Schema(description = "Yeni görev başlığı", example = "Güncellenmiş başlık")
    private String title;
    
    @Schema(description = "Yeni görev açıklaması", example = "Güncellenmiş açıklama metni")
    private String description;
    
    @Schema(description = "Görev tamamlanma durumu", example = "true")
    private Boolean completed;
}
