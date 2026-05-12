package com.ronniesong.capsuleapi.codecs;

import com.ronniesong.capsuleapi.constants.ShareConstants;
import com.ronniesong.capsuleapi.models.dto.ShareEnvelope;
import com.ronniesong.capsuleapi.models.response.ShareReadResponse;
import com.ronniesong.capsuleapi.utils.CompressionUtils;
import com.ronniesong.capsuleapi.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ShareCodec {
  private final ObjectMapper objectMapper;

  public String encode(String kind, Object data) {
    String json = JsonUtils.stringify(objectMapper, data);
    ShareEnvelope envelope = new ShareEnvelope(
        ShareConstants.ENVELOPE_VERSION,
        kind,
        ShareConstants.ENCODING,
        CompressionUtils.gzipToBase64Url(json),
        System.currentTimeMillis()
    );

    return JsonUtils.stringify(objectMapper, envelope);
  }

  public ShareReadResponse decode(String value) {
    try {
      ShareEnvelope envelope = objectMapper.readValue(value, ShareEnvelope.class);
      if (envelope.getV() != ShareConstants.ENVELOPE_VERSION) {
        throw new IllegalArgumentException("Unsupported share envelope version");
      }

      if (!ShareConstants.ENCODING.equals(envelope.getEncoding())) {
        throw new IllegalArgumentException("Unsupported share envelope encoding");
      }

      Object data = objectMapper.readValue(CompressionUtils.gunzipFromBase64Url(envelope.getPayload()), Object.class);
      return new ShareReadResponse(envelope.getKind(), data);
    } catch (JacksonException exception) {
      throw new IllegalArgumentException("Stored share payload is invalid", exception);
    }
  }
}
