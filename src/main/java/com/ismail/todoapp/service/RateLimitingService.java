package com.ismail.todoapp.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key) {

        return buckets.computeIfAbsent(key, k -> newBucket());
    }

    private Bucket newBucket() {

        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillGreedy(5, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public boolean tryConsume(String key, int tokens) {

        Bucket bucket = resolveBucket(key);
        return bucket.tryConsume(tokens);
    }
}