package com.ronniesong.capsuleapi.repositories;

import java.util.Optional;

public interface ShareRepository {
  boolean setIfAbsent(String code, String value, int ttlSeconds);

  Optional<String> get(String code);

  boolean delete(String code);
}
