package com.ronniesong.capsuleapi.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareCreateRequest {
  private String kind;
  private Object data;
}
