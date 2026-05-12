package com.ronniesong.capsuleapi.codecs;

import static org.assertj.core.api.Assertions.assertThat;

import com.ronniesong.capsuleapi.models.response.ShareReadResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class ShareCodecTests {
  private final ShareCodec codec = new ShareCodec(new ObjectMapper());

  @Test
  void roundTripsJsonDataThroughGzipAndBase64UrlEnvelope() {
    Map<String, Object> data = Map.of(
        "cards", List.of(
            Map.of("id", "intro", "title", "Intro"),
            Map.of("id", "details", "title", "Details")
        )
    );

    String storedValue = codec.encode("deck", data);
    ShareReadResponse decoded = codec.decode(storedValue);

    assertThat(storedValue).contains("\"encoding\":\"base64url+gzip\"");
    assertThat(decoded.getKind()).isEqualTo("deck");
    assertThat(decoded.getData()).isEqualTo(data);
  }
}
