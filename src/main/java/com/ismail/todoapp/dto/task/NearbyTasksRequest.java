package com.ismail.todoapp.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Konuma göre yakın görevleri listeleme isteği")
public class NearbyTasksRequest {

    @Schema(description = "Kullanıcının anlık konumunun enlem bilgisi", example = "41.0082", required = true)
    private double latitude;

    @Schema(description = "Kullanıcının anlık konumunun boylam bilgisi", example = "28.9784", required = true)
    private double longitude;

    @Schema(description = "Arama yarıçapı (metre cinsinden). Gönderilmezse varsayılan 500m kullanılır.", example = "500")
    private Double radiusMeters;
}

