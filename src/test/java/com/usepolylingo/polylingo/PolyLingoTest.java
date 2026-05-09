package com.usepolylingo.polylingo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.usepolylingo.polylingo.errors.AuthException;
import com.usepolylingo.polylingo.errors.JobFailedException;
import com.usepolylingo.polylingo.errors.RateLimitException;
import com.usepolylingo.polylingo.types.BatchItem;
import com.usepolylingo.polylingo.types.BatchParams;
import com.usepolylingo.polylingo.types.CreateJobParams;
import com.usepolylingo.polylingo.types.HealthResponse;
import com.usepolylingo.polylingo.types.JobsTranslateParams;
import com.usepolylingo.polylingo.types.LanguagesResponse;
import com.usepolylingo.polylingo.types.TranslateParams;
import com.usepolylingo.polylingo.types.TranslateResult;
import com.usepolylingo.polylingo.types.UsageResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PolyLingoTest {

  private MockWebServer server;

  @BeforeEach
  void setUp() throws IOException {
    server = new MockWebServer();
    server.start();
  }

  @AfterEach
  void tearDown() throws IOException {
    server.shutdown();
  }

  private PolyLingo client() {
    String base = server.url("/v1").toString().replaceAll("/$", "");
    return PolyLingo.builder().apiKey("pl_test").baseUrl(base).timeout(Duration.ofSeconds(5)).build();
  }

  @Test
  void health() throws InterruptedException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "application/json")
            .setBody("{\"status\":\"ok\",\"timestamp\":\"2025-01-01T00:00:00Z\"}"));

    HealthResponse h = client().health();
    assertEquals("ok", h.getStatus());
    assertEquals("/v1/health", server.takeRequest().getPath());
  }

  @Test
  void languages() throws InterruptedException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"languages\":[{\"code\":\"en\",\"name\":\"English\",\"rtl\":false}]}"));

    LanguagesResponse r = client().languages();
    assertEquals(1, r.getLanguages().size());
    assertEquals("en", r.getLanguages().get(0).getCode());
    assertEquals("/v1/languages", server.takeRequest().getPath());
  }

  @Test
  void translateSuccess() throws InterruptedException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(
                "{\"translations\":{\"es\":\"hola\"},\"usage\":{\"total_tokens\":10,\"input_tokens\":4,\"output_tokens\":6,\"model\":\"standard\"}}"));

    TranslateResult r =
        client()
            .translate(
                TranslateParams.builder()
                    .content("hello")
                    .targets(List.of("es"))
                    .format("plain")
                    .build());

    assertEquals("hola", r.getTranslations().get("es"));
    assertEquals(10, r.getUsage().getTotalTokens());
    assertEquals("/v1/translate", server.takeRequest().getPath());
  }

  @Test
  void batchSuccess() throws InterruptedException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(
                "{\"results\":[{\"id\":\"a\",\"translations\":{\"fr\":\"salut\"}}],\"usage\":{\"total_tokens\":20,\"input_tokens\":8,\"output_tokens\":12,\"model\":\"standard\"}}"));

    var result =
        client()
            .batch(
                BatchParams.builder()
                    .addItem(BatchItem.builder().id("a").content("hi").format("plain").build())
                    .targets(List.of("fr"))
                    .build());

    assertEquals("salut", result.getResults().get(0).getTranslations().get("fr"));
    assertEquals("/v1/translate/batch", server.takeRequest().getPath());
  }

  @Test
  void usageEndpoint() throws InterruptedException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(
                "{\"usage\":{\"period_start\":\"2025-03-01\",\"period_end\":\"2025-03-31\",\"translations_used\":0,\"translations_limit\":null,\"tokens_used\":100,\"tokens_limit\":100000}}}"));

    UsageResponse u = client().usage();
    assertEquals(100, u.getUsage().getTokensUsed());
    assertEquals("/v1/usage", server.takeRequest().getPath());
  }

  @Test
  void authErrorThrowsAuthException() {
    server.enqueue(
        new MockResponse()
            .setResponseCode(401)
            .setBody("{\"error\":\"invalid_api_key\",\"message\":\"bad\"}"));

    AuthException ex =
        assertThrows(
            AuthException.class,
            () ->
                client()
                    .translate(
                        TranslateParams.builder().content("x").targets(List.of("es")).build()));
    assertEquals("invalid_api_key", ex.getError());
    assertEquals(401, ex.getStatus());
  }

  @Test
  void rateLimitIncludesRetryAfterHeader() {
    server.enqueue(
        new MockResponse()
            .setResponseCode(429)
            .setHeader("Retry-After", "60")
            .setBody("{\"error\":\"rate_limit_reached\",\"message\":\"slow down\"}"));

    RateLimitException ex =
        assertThrows(
            RateLimitException.class,
            () ->
                client()
                    .translate(
                        TranslateParams.builder().content("x").targets(List.of("es")).build()));
    assertEquals(60, ex.getRetryAfter().orElseThrow().intValue());
  }

  @Test
  void jobsTranslatePollsUntilCompleted() throws InterruptedException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(202)
            .setBody(
                "{\"job_id\":\"j1\",\"status\":\"pending\",\"created_at\":\"2025-01-01T00:00:00Z\"}"));
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(
                "{\"job_id\":\"j1\",\"status\":\"processing\",\"queue_position\":2,\"updated_at\":\"2025-01-01T00:00:01Z\"}"));
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(
                "{\"job_id\":\"j1\",\"status\":\"completed\",\"translations\":{\"de\":\"hallo\"},\"usage\":{\"total_tokens\":5,\"input_tokens\":2,\"output_tokens\":3,\"model\":\"standard\"},\"completed_at\":\"2025-01-01T00:00:02Z\"}"));

    AtomicInteger progressCalls = new AtomicInteger();
    TranslateResult r =
        client()
            .jobs()
            .translate(
                JobsTranslateParams.builder()
                    .content("hello")
                    .targets(List.of("de"))
                    .pollInterval(Duration.ofMillis(1))
                    .timeout(Duration.ofSeconds(10))
                    .onProgress(pos -> progressCalls.incrementAndGet())
                    .build());

    assertEquals("hallo", r.getTranslations().get("de"));
    assertTrue(progressCalls.get() >= 1);
    assertEquals("/v1/jobs", server.takeRequest().getPath());
    assertEquals("/v1/jobs/j1", server.takeRequest().getPath());
    assertEquals("/v1/jobs/j1", server.takeRequest().getPath());
    assertEquals("/v1/jobs/j1", server.takeRequest().getPath());
  }

  @Test
  void jobsTranslateFailsWhenJobFailed() {
    server.enqueue(
        new MockResponse().setResponseCode(202).setBody("{\"job_id\":\"j2\",\"status\":\"pending\"}"));
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(
                "{\"job_id\":\"j2\",\"status\":\"failed\",\"error\":\"Model returned invalid JSON\"}"));

    JobFailedException ex =
        assertThrows(
            JobFailedException.class,
            () ->
                client()
                    .jobs()
                    .translate(
                        JobsTranslateParams.builder()
                            .content("x")
                            .targets(List.of("es"))
                            .pollInterval(Duration.ofMillis(1))
                            .timeout(Duration.ofSeconds(5))
                            .build()));

    assertEquals("j2", ex.getJobId());
    assertEquals(200, ex.getStatus());
  }

  @Test
  void jobsTranslateTimesOut() {
    server.enqueue(
        new MockResponse().setResponseCode(202).setBody("{\"job_id\":\"j3\",\"status\":\"pending\"}"));
    for (int i = 0; i < 100; i++) {
      server.enqueue(
          new MockResponse()
              .setResponseCode(200)
              .setBody("{\"job_id\":\"j3\",\"status\":\"pending\",\"queue_position\":99}"));
    }

    JobFailedException ex =
        assertThrows(
            JobFailedException.class,
            () ->
                client()
                    .jobs()
                    .translate(
                        JobsTranslateParams.builder()
                            .content("x")
                            .targets(List.of("es"))
                            .pollInterval(Duration.ofMillis(5))
                            .timeout(Duration.ofMillis(80))
                            .build()));

    assertEquals("j3", ex.getJobId());
    assertEquals("timeout", ex.getError());
    assertEquals(408, ex.getStatus());
  }

  @Test
  void jobCreateAndGet() throws InterruptedException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(202)
            .setBody("{\"job_id\":\"abc\",\"status\":\"pending\"}"));

    var job =
        client()
            .jobs()
            .create(
                CreateJobParams.builder().content("hello").targets(List.of("fr")).format("plain").build());
    assertEquals("abc", job.getJobId());

    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"job_id\":\"abc\",\"status\":\"pending\"}"));

    var got = client().jobs().get("abc");
    assertEquals("pending", got.getStatus());
    assertEquals("/v1/jobs", server.takeRequest().getPath());
    assertEquals("/v1/jobs/abc", server.takeRequest().getPath());
  }
}
