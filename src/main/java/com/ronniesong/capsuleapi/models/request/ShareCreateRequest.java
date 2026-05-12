package com.ronniesong.capsuleapi.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request body for creating a temporary share.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareCreateRequest {
  @Schema(description = "Payload type. Must be one of the configured allowed kinds.", example = "deck")
  private String kind;

  @Schema(description = "JSON-serializable payload to store temporarily.")
  private Object data;
}
