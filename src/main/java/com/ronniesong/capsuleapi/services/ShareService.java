package com.ronniesong.capsuleapi.services;

import com.ronniesong.capsuleapi.codecs.ShareCodec;
import com.ronniesong.capsuleapi.config.ShareProperties;
import com.ronniesong.capsuleapi.exceptions.ApiException;
import com.ronniesong.capsuleapi.models.request.ShareCreateRequest;
import com.ronniesong.capsuleapi.models.response.ShareCreateResponse;
import com.ronniesong.capsuleapi.models.response.ShareReadResponse;
import com.ronniesong.capsuleapi.repositories.ShareRepository;
import com.ronniesong.capsuleapi.utils.CodeGenerator;
import com.ronniesong.capsuleapi.utils.JsonUtils;
import com.ronniesong.capsuleapi.utils.SensitiveFieldUtils;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ShareService {
  private final ShareRepository repository;
  private final ShareCodec codec;
  private final CodeGenerator codeGenerator;
  private final ObjectMapper objectMapper;
  private final ShareProperties properties;

  public ShareCreateResponse create(ShareCreateRequest request) {
    validateRequest(request);

    String value = codec.encode(request.getKind(), request.getData());

    for (int attempt = 0; attempt < properties.getMaxCodeAttempts(); attempt += 1) {
      String code = codeGenerator.generate(properties.getCodeLength());

      if (repository.setIfAbsent(code, value, properties.getTtlSeconds())) {
        return new ShareCreateResponse(code, properties.getTtlSeconds());
      }
    }

    throw new ApiException(HttpStatus.CONFLICT, "code_collision", "Could not allocate a unique share code");
  }

  public ShareReadResponse get(String rawCode) {
    String code = normalizeAndValidateCode(rawCode);
    String value = repository.get(code).orElseThrow(
        () -> new ApiException(HttpStatus.NOT_FOUND, "not_found", "Share code expired or does not exist")
    );

    return codec.decode(value);
  }

  public void delete(String rawCode) {
    String code = normalizeAndValidateCode(rawCode);

    if (!repository.delete(code)) {
      throw new ApiException(HttpStatus.NOT_FOUND, "not_found", "Share code expired or does not exist");
    }
  }

  private void validateRequest(ShareCreateRequest request) {
    if (request == null || isBlank(request.getKind()) || request.getData() == null) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "invalid_request", "Expected JSON body with kind and data");
    }

    Set<String> allowedKinds = properties.getAllowedKinds();
    if (!allowedKinds.contains(request.getKind())) {
      throw new ApiException(
          HttpStatus.BAD_REQUEST,
          "unsupported_kind",
          "kind must be one of: " + String.join(", ", allowedKinds)
      );
    }

    SensitiveFieldUtils.findSensitivePath(request.getData()).ifPresent(path -> {
      throw new ApiException(HttpStatus.UNPROCESSABLE_CONTENT, "sensitive_payload", "Refusing to store sensitive field at " + path);
    });

    int payloadBytes = JsonUtils.utf8Size(JsonUtils.stringify(objectMapper, request.getData()));
    if (payloadBytes > properties.getMaxPayloadBytes()) {
      throw new ApiException(HttpStatus.CONTENT_TOO_LARGE, "payload_too_large", "data must be at most " + properties.getMaxPayloadBytes() + " bytes");
    }
  }

  private String normalizeAndValidateCode(String rawCode) {
    String code = codeGenerator.normalize(rawCode);

    if (!codeGenerator.isValid(code, properties.getCodeLength())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "invalid_code", "Invalid share code");
    }

    return code;
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
