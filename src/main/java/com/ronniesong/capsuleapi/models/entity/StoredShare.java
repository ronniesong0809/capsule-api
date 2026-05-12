package com.ronniesong.capsuleapi.models.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoredShare {
  private final String value;
  private final Instant expiresAt;

  public boolean isExpired(Instant now) {
    return !expiresAt.isAfter(now);
  }
}
