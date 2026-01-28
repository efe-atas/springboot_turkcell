package com.ismail.todoapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Yeni görev oluşturma isteği")
public class TaskCreateRequest {
    
    @Schema(description = "Görev başlığı", example = "Rapor hazırla", required = true)
    private String title;
    
    @Schema(description = "Görev açıklaması", example = "Aylık satış raporunu hazırla ve yöneticiye gönder")
    private String description;
}
