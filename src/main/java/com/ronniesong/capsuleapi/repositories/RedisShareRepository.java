package com.ronniesong.capsuleapi.repositories;

import com.ronniesong.capsuleapi.config.ShareProperties;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "capsule.share.store", havingValue = "redis", matchIfMissing = true)
public class RedisShareRepository implements ShareRepository {
  private final StringRedisTemplate redis;
  private final ShareProperties properties;

  @Override
  public boolean setIfAbsent(String code, String value, int ttlSeconds) {
    Boolean stored = redis.opsForValue().setIfAbsent(key(code), value, Duration.ofSeconds(ttlSeconds));
    return Boolean.TRUE.equals(stored);
  }

  @Override
  public Optional<String> get(String code) {
    return Optional.ofNullable(redis.opsForValue().get(key(code)));
  }

  @Override
  public boolean delete(String code) {
    return Boolean.TRUE.equals(redis.delete(key(code)));
  }

  private String key(String code) {
    return properties.getKeyPrefix() + code;
  }
}
