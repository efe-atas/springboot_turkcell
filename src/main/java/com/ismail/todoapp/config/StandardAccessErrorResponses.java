package com.ismail.todoapp.config;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ortak 403 / 404 hata cevaplari icin meta-annotation.
 * Bir endpoint hem yetki hatasi (403) hem de kaynak bulunamama (404)
 * durumlarini donuyorsa bu anotasyon eklenebilir.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = "403",
                description = "Bu alana erisim yetkiniz yok",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Kaynak bulunamadi",
                content = @Content
        )
})
public @interface StandardAccessErrorResponses {
}

