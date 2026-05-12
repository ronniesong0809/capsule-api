package com.ronniesong.capsuleapi.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Error response.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  @Schema(description = "Stable error code.", example = "invalid_request")
  private String error;

  @Schema(description = "Human-readable error message.")
  private String message;
}
