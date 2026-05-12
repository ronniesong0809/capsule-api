package com.ronniesong.capsuleapi.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response returned after a share code is created.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareCreateResponse {
  @Schema(description = "8-character short code.", example = "A8K3P9Q2")
  private String code;

  @Schema(description = "Seconds until the code expires.", example = "600")
  private int expiresIn;
}
