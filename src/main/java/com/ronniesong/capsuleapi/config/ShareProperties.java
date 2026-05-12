package com.ronniesong.capsuleapi.config;

import com.ronniesong.capsuleapi.constants.ShareConstants;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "capsule.share")
public class ShareProperties {
  private String store = "redis";
  private String keyPrefix = "capsule:share:";
  private int ttlSeconds = ShareConstants.DEFAULT_TTL_SECONDS;
  private int codeLength = ShareConstants.DEFAULT_CODE_LENGTH;
  private int maxPayloadBytes = ShareConstants.DEFAULT_MAX_PAYLOAD_BYTES;
  private int maxCodeAttempts = ShareConstants.DEFAULT_MAX_CODE_ATTEMPTS;
  private Set<String> allowedKinds = new LinkedHashSet<>(ShareConstants.DEFAULT_ALLOWED_KINDS);
}
