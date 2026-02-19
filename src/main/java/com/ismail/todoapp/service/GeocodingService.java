package com.ismail.todoapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class GeocodingService {

    private static final String NOMINATIM_REVERSE_URL = "https://nominatim.openstreetmap.org/reverse?format=json&lat={lat}&lon={lon}&addressdetails=1";
    private final RestTemplate restTemplate;

    public GeocodingService() {
        this.restTemplate = new RestTemplate();
        // OpenStreetMap'in rate limiting'i için User-Agent header'ı ekle
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("User-Agent", "TodoApp/1.0");
            return execution.execute(request, body);
        });
    }

    public Optional<String> reverseGeocode(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return Optional.empty();
        }

        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.getForEntity(
                    NOMINATIM_REVERSE_URL,
                    (Class<Map<String, Object>>) (Class<?>) Map.class,
                    latitude,
                    longitude
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                
                // Adres bilgisini formatla
                String displayName = (String) body.get("display_name");
                if (displayName != null && !displayName.isEmpty()) {
                    return Optional.of(displayName);
                }
            }
        } catch (Exception e) {
            log.warn("Reverse geocoding hatası: lat={}, lon={}, error={}", latitude, longitude, e.getMessage());
        }

        return Optional.empty();
    }
}
