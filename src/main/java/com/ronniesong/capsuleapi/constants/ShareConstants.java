package com.ronniesong.capsuleapi.constants;

import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ShareConstants {
  public static final int ENVELOPE_VERSION = 1;
  public static final String ENCODING = "base64url+gzip";
  public static final String CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
  public static final int DEFAULT_CODE_LENGTH = 8;
  public static final int DEFAULT_TTL_SECONDS = 600;
  public static final int DEFAULT_MAX_PAYLOAD_BYTES = 262_144;
  public static final int DEFAULT_MAX_CODE_ATTEMPTS = 5;
  public static final Set<String> DEFAULT_ALLOWED_KINDS = Set.of("deck", "config", "form", "settings");
}
