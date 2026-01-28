package com.ismail.todoapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Görev Yönetim Uygulaması API")
                        .description("""
                                ## Görev Yönetim Sistemi REST API Dokümantasyonu
                                
                                Bu API, kullanıcıların görevlerini ve çalışma alanlarını (Space) yönetmelerini sağlar.
                                
                                ### Özellikler:
                                - **Kimlik Doğrulama:** JWT tabanlı güvenli giriş sistemi
                                - **Çalışma Alanları (Spaces):** Görevleri organize etmek için alanlar oluşturma
                                - **Görev Yönetimi:** Görev ekleme, düzenleme, silme işlemleri
                                - **Üye Yönetimi:** Çalışma alanlarına üye ekleme ve yetki atama
                                
                                ### Yetki Seviyeleri:
                                | Rol | Açıklama |
                                |-----|----------|
                                | **OWNER** | Tam yetki - Space'i silebilir |
                                | **ADMIN** | Üye yönetimi ve görev silme |
                                | **EDITOR** | Görev oluşturma ve düzenleme |
                                | **VIEWER** | Sadece görüntüleme |
                                
                                ### Kimlik Doğrulama:
                                1. `/api/auth/register` ile kayıt olun
                                2. `/api/auth/login` ile giriş yapın
                                3. Dönen JWT token'ı "Authorize" butonuna yapıştırın
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("İsmail")
                                .email("ismail@example.com"))
                        .license(new License()
                                .name("MIT Lisansı")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Yerel Geliştirme Sunucusu")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token'ınızı buraya girin. Önce /api/auth/login endpoint'inden token alın.")));
    }
}
