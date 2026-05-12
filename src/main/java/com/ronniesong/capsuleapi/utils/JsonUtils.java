package com.ronniesong.capsuleapi.utils;

import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@UtilityClass
public class JsonUtils {
  public static String stringify(ObjectMapper objectMapper, Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JacksonException exception) {
      throw new IllegalArgumentException("Payload must be JSON serializable", exception);
    }
  }

  public static int utf8Size(String value) {
    return value.getBytes(StandardCharsets.UTF_8).length;
  }
}
