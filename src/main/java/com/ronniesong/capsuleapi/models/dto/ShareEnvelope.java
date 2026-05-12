package com.ronniesong.capsuleapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareEnvelope {
  private int v;
  private String kind;
  private String encoding;
  private String payload;
  private long createdAt;
}
