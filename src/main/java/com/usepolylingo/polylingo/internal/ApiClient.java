package com.usepolylingo.polylingo.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usepolylingo.polylingo.errors.AuthException;
import com.usepolylingo.polylingo.errors.PolyLingoException;
import com.usepolylingo.polylingo.errors.RateLimitException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Internal HTTP client; use {@link com.usepolylingo.polylingo.PolyLingo} from application code. */
public final class ApiClient {

  static final String DEFAULT_BASE_URL = "https://api.usepolylingo.com/v1";

  private static final ObjectMapper MAPPER =
      new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private final HttpClient httpClient;
  private final String baseUrl;
  private final String apiKey;
  private final Duration requestTimeout;

  public ApiClient(String apiKey, String baseUrl, Duration requestTimeout) {
    if (apiKey == null || apiKey.isEmpty()) {
      throw new IllegalArgumentException("PolyLingo: apiKey is required");
    }
    String resolved = baseUrl == null || baseUrl.isEmpty() ? DEFAULT_BASE_URL : baseUrl;
    while (resolved.endsWith("/")) {
      resolved = resolved.substring(0, resolved.length() - 1);
    }
    this.baseUrl = resolved;
    this.apiKey = apiKey;
    this.requestTimeout = requestTimeout == null ? Duration.ofSeconds(120) : requestTimeout;
    this.httpClient =
        HttpClient.newBuilder().connectTimeout(this.requestTimeout).followRedirects(HttpClient.Redirect.NORMAL).build();
  }

  public static ObjectMapper mapper() {
    return MAPPER;
  }

  public <T> T get(String path, Class<T> responseType, int expectStatus) {
    return send("GET", path, null, responseType, expectStatus);
  }

  public <T> T postJson(String path, Object body, Class<T> responseType, int expectStatus) {
    return send("POST", path, body, responseType, expectStatus);
  }

  private <T> T send(
      String method, String path, Object body, Class<T> responseType, int expectStatus) {
    try {
      HttpRequest.Builder rb =
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + path))
              .timeout(requestTimeout)
              .header("Authorization", "Bearer " + apiKey)
              .header("Accept", "application/json");

      if ("POST".equals(method)) {
        String json = body == null ? "{}" : MAPPER.writeValueAsString(body);
        rb.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json));
      } else {
        rb.GET();
      }

      HttpResponse<String> response = httpClient.send(rb.build(), HttpResponse.BodyHandlers.ofString());
      int status = response.statusCode();
      String text = response.body() == null ? "" : response.body();

      if (status != expectStatus) {
        throw errorFromResponse(status, text, response.headers());
      }

      if (text.isEmpty()) {
        throw new IOException("Empty response body");
      }

      return MAPPER.readValue(text, responseType);
    } catch (HttpTimeoutException e) {
      throw new PolyLingoException(
          408, "timeout", "Request timed out after " + requestTimeout);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new PolyLingoException(408, "timeout", "Request interrupted");
    }
  }

  static PolyLingoException errorFromResponse(int status, String rawBody, HttpHeaders headers) {
    String code = "unknown_error";
    String message = "Request failed with status " + status;
    JsonNode retryAfterNode = null;

    if (rawBody != null && !rawBody.isEmpty()) {
      try {
        JsonNode root = MAPPER.readTree(rawBody);
        if (root.hasNonNull("error")) {
          code = root.get("error").asText(code);
        }
        if (root.hasNonNull("message")) {
          message = root.get("message").asText(message);
        }
        if (root.has("retry_after")) {
          retryAfterNode = root.get("retry_after");
        }
      } catch (IOException ignored) {
        code = "unknown_error";
        message = rawBody;
      }
    }

    Integer retryAfter = parseRetryAfter(retryAfterNode, headers.firstValue("retry-after").orElse(null));

    if (status == 401) {
      return new AuthException(status, code, message);
    }
    if (status == 429) {
      return new RateLimitException(status, code, message, retryAfter);
    }
    return new PolyLingoException(status, code, message);
  }

  private static Integer parseRetryAfter(JsonNode node, String header) {
    if (node != null && !node.isNull()) {
      if (node.isInt() || node.isLong()) {
        int v = node.intValue();
        return v >= 0 ? v : null;
      }
      if (node.isTextual()) {
        try {
          int v = Integer.parseInt(node.asText().trim());
          return v >= 0 ? v : null;
        } catch (NumberFormatException ignored) {
          /* fall through */
        }
      }
    }
    if (header != null && !header.isEmpty()) {
      try {
        int v = Integer.parseInt(header.trim());
        return v >= 0 ? v : null;
      } catch (NumberFormatException ignored) {
        /* ignore */
      }
    }
    return null;
  }

  /** Encode a single path segment for {@code /jobs/{id}}. */
  public static String encodeJobId(String jobId) {
    return URLEncoder.encode(jobId, StandardCharsets.UTF_8).replace("+", "%20");
  }
}
