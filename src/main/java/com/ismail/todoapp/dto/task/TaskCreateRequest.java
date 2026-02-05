package com.ismail.todoapp.dto.task;

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

    @Schema(description = "Görevin yapılacağı konumun enlem bilgisi", example = "41.0082")
    private Double latitude;

    @Schema(description = "Görevin yapılacağı konumun boylam bilgisi", example = "28.9784")
    private Double longitude;

    @Schema(description = "Bu görev için tetikleme yarıçapı (metre cinsinden)", example = "500")
    private Double radiusInMeters;
}

