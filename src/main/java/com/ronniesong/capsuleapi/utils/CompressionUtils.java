package com.ronniesong.capsuleapi.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CompressionUtils {
  public static String gzipToBase64Url(String value) {
    try {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();

      try (GZIPOutputStream gzip = new GZIPOutputStream(bytes)) {
        gzip.write(value.getBytes(StandardCharsets.UTF_8));
      }

      return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes.toByteArray());
    } catch (IOException exception) {
      throw new IllegalStateException("Could not gzip payload", exception);
    }
  }

  public static String gunzipFromBase64Url(String value) {
    byte[] compressed = Base64.getUrlDecoder().decode(value);

    try (
        GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream()
    ) {
      gzip.transferTo(bytes);
      return bytes.toString(StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new IllegalStateException("Could not gunzip payload", exception);
    }
  }
}
