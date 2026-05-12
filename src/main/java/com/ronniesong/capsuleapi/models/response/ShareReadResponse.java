package com.ronniesong.capsuleapi.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Stored share payload.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareReadResponse {
  @Schema(description = "Payload type.", example = "deck")
  private String kind;

  @Schema(description = "Decoded JSON payload.")
  private Object data;
}
