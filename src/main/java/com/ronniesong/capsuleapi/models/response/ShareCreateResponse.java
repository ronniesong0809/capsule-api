package com.ronniesong.capsuleapi.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareCreateResponse {
  private String code;
  private int expiresIn;
}
