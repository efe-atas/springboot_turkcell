package com.ismail.todoapp.dto.task;

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

    @Schema(description = "Görevin yapılacağı konumun enlem bilgisi", example = "41.0082")
    private Double latitude;

    @Schema(description = "Görevin yapılacağı konumun boylam bilgisi", example = "28.9784")
    private Double longitude;

    @Schema(description = "Bu görev için tetikleme yarıçapı (metre cinsinden)", example = "500")
    private Double radiusInMeters;
}

