package com.ronniesong.capsuleapi.utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SensitiveFieldUtils {
  private static final Set<String> SENSITIVE_KEYS = Set.of(
      "apikey",
      "authorization",
      "authtoken",
      "credential",
      "credentials",
      "password",
      "passwd",
      "privatekey",
      "pwd",
      "refreshtoken",
      "secret",
      "token"
  );

  public static Optional<String> findSensitivePath(Object value) {
    return findSensitivePath(value, "$", 0);
  }

  private static Optional<String> findSensitivePath(Object value, String path, int depth) {
    if (depth > 30 || value == null) {
      return Optional.empty();
    }

    if (value instanceof Map<?, ?> map) {
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        String key = String.valueOf(entry.getKey());
        String childPath = path + "." + key;

        if (SENSITIVE_KEYS.contains(normalizeKey(key))) {
          return Optional.of(childPath);
        }

        Optional<String> nestedPath = findSensitivePath(entry.getValue(), childPath, depth + 1);
        if (nestedPath.isPresent()) {
          return nestedPath;
        }
      }
    }

    if (value instanceof List<?> list) {
      for (int index = 0; index < list.size(); index += 1) {
        Optional<String> nestedPath = findSensitivePath(list.get(index), path + "[" + index + "]", depth + 1);
        if (nestedPath.isPresent()) {
          return nestedPath;
        }
      }
    }

    return Optional.empty();
  }

  private static String normalizeKey(String key) {
    return key.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
  }
}
