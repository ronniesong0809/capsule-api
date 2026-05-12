package com.ronniesong.capsuleapi.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareReadResponse {
  private String kind;
  private Object data;
}
