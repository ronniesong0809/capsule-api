package com.ronniesong.capsuleapi.controllers;

import com.ronniesong.capsuleapi.models.request.ShareCreateRequest;
import com.ronniesong.capsuleapi.models.response.ShareCreateResponse;
import com.ronniesong.capsuleapi.models.response.ShareReadResponse;
import com.ronniesong.capsuleapi.services.ShareService;
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
public class ShareController {
  private final ShareService shareService;

  @PostMapping({"/share", "/exports"})
  public ResponseEntity<ShareCreateResponse> create(@RequestBody ShareCreateRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .header(HttpHeaders.CACHE_CONTROL, "no-store")
        .body(shareService.create(request));
  }

  @GetMapping({"/share/{code}", "/exports/{code}"})
  public ResponseEntity<ShareReadResponse> get(@PathVariable String code) {
    return ResponseEntity
        .ok()
        .header(HttpHeaders.CACHE_CONTROL, "no-store")
        .body(shareService.get(code));
  }

  @DeleteMapping({"/share/{code}", "/exports/{code}"})
  public ResponseEntity<Void> delete(@PathVariable String code) {
    shareService.delete(code);
    return ResponseEntity.noContent().build();
  }
}
