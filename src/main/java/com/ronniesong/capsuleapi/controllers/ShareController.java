package com.ronniesong.capsuleapi.controllers;

import com.ronniesong.capsuleapi.models.request.ShareCreateRequest;
import com.ronniesong.capsuleapi.models.response.ShareCreateResponse;
import com.ronniesong.capsuleapi.models.response.ShareReadResponse;
import com.ronniesong.capsuleapi.services.ShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Shares", description = "Temporary short-code exports")
public class ShareController {
  private final ShareService shareService;

  @Operation(summary = "Create a temporary share code", description = "Stores JSON data for a short time and returns an 8-character code.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Share code created",
          content = @Content(schema = @Schema(implementation = ShareCreateResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request or unsupported kind"),
      @ApiResponse(responseCode = "413", description = "Payload is too large"),
      @ApiResponse(responseCode = "422", description = "Payload contains sensitive fields"),
      @ApiResponse(responseCode = "409", description = "Could not allocate a unique code")
  })
  @PostMapping({"/share", "/exports"})
  public ResponseEntity<ShareCreateResponse> create(@RequestBody ShareCreateRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .header(HttpHeaders.CACHE_CONTROL, "no-store")
        .body(shareService.create(request));
  }

  @Operation(summary = "Read a share by code", description = "Returns the stored JSON payload if the code exists and has not expired.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Share payload found",
          content = @Content(schema = @Schema(implementation = ShareReadResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid share code"),
      @ApiResponse(responseCode = "404", description = "Share code expired or does not exist")
  })
  @GetMapping({"/share/{code}", "/exports/{code}"})
  public ResponseEntity<ShareReadResponse> get(@PathVariable String code) {
    return ResponseEntity
        .ok()
        .header(HttpHeaders.CACHE_CONTROL, "no-store")
        .body(shareService.get(code));
  }

  @Operation(summary = "Delete a share by code", description = "Deletes a share before its TTL expires.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Share deleted"),
      @ApiResponse(responseCode = "400", description = "Invalid share code"),
      @ApiResponse(responseCode = "404", description = "Share code expired or does not exist")
  })
  @DeleteMapping({"/share/{code}", "/exports/{code}"})
  public ResponseEntity<Void> delete(@PathVariable String code) {
    shareService.delete(code);
    return ResponseEntity.noContent().build();
  }
}
