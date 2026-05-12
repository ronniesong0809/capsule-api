package com.ronniesong.capsuleapi.repositories;

import com.ronniesong.capsuleapi.models.entity.StoredShare;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "capsule.share.store", havingValue = "memory")
public class InMemoryShareRepository implements ShareRepository {
  private final ConcurrentMap<String, StoredShare> records = new ConcurrentHashMap<>();

  @Override
  public boolean setIfAbsent(String code, String value, int ttlSeconds) {
    pruneExpired(code);

    StoredShare record = new StoredShare(value, Instant.now().plusSeconds(ttlSeconds));
    return records.putIfAbsent(code, record) == null;
  }

  @Override
  public Optional<String> get(String code) {
    pruneExpired(code);
    return Optional.ofNullable(records.get(code)).map(StoredShare::getValue);
  }

  @Override
  public boolean delete(String code) {
    pruneExpired(code);
    return records.remove(code) != null;
  }

  private void pruneExpired(String code) {
    StoredShare record = records.get(code);

    if (record != null && record.isExpired(Instant.now())) {
      records.remove(code, record);
    }
  }
}
