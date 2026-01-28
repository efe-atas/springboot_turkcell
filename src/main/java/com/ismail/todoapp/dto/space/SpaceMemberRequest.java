package com.ismail.todoapp.dto.space;

import com.ismail.todoapp.enums.SpaceRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Çalışma alanına üye ekleme/güncelleme isteği")
public class SpaceMemberRequest {

    @Schema(description = "Eklenecek kullanıcının ID'si", example = "5")
    private Long userId;

    @Schema(description = "Verilecek yetki seviyesi", example = "EDITOR", allowableValues = {"OWNER", "ADMIN", "EDITOR", "VIEWER"})
    private SpaceRole role;
}

