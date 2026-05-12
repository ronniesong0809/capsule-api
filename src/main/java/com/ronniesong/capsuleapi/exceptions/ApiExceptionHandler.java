package com.ronniesong.capsuleapi.exceptions;

import com.ronniesong.capsuleapi.models.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {
    return ResponseEntity
        .status(exception.getStatus())
        .body(new ErrorResponse(exception.getError(), exception.getMessage()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleInvalidJson() {
    return ResponseEntity
        .badRequest()
        .body(new ErrorResponse("invalid_json", "Request body must be valid JSON"));
  }
}
