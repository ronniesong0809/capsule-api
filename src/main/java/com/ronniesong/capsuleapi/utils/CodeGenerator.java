package com.ronniesong.capsuleapi.utils;

import com.ronniesong.capsuleapi.constants.ShareConstants;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class CodeGenerator {
  private final SecureRandom random = new SecureRandom();

  public String generate(int length) {
    StringBuilder code = new StringBuilder(length);

    for (int index = 0; index < length; index += 1) {
      int characterIndex = random.nextInt(ShareConstants.CODE_ALPHABET.length());
      code.append(ShareConstants.CODE_ALPHABET.charAt(characterIndex));
    }

    return code.toString();
  }

  public String normalize(String code) {
    return code == null ? "" : code.trim().toUpperCase();
  }

  public boolean isValid(String code, int length) {
    if (code == null || code.length() != length) {
      return false;
    }

    for (int index = 0; index < code.length(); index += 1) {
      if (ShareConstants.CODE_ALPHABET.indexOf(code.charAt(index)) < 0) {
        return false;
      }
    }

    return true;
  }
}
