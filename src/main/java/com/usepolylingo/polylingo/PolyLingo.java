package com.usepolylingo.polylingo;

import com.usepolylingo.polylingo.internal.ApiClient;
import com.usepolylingo.polylingo.resources.JobsResource;
import com.usepolylingo.polylingo.types.BatchParams;
import com.usepolylingo.polylingo.types.BatchResult;
import com.usepolylingo.polylingo.types.HealthResponse;
import com.usepolylingo.polylingo.types.LanguagesResponse;
import com.usepolylingo.polylingo.types.TranslateParams;
import com.usepolylingo.polylingo.types.TranslateResult;
import com.usepolylingo.polylingo.types.UsageResponse;
import java.time.Duration;

/** PolyLingo API client (Java 11+). */
public final class PolyLingo {

  private final ApiClient apiClient;
  private final JobsResource jobs;

  private PolyLingo(ApiClient apiClient) {
    this.apiClient = apiClient;
    this.jobs = new JobsResource(apiClient);
  }

  public static Builder builder() {
    return new Builder();
  }

  public JobsResource jobs() {
    return jobs;
  }

  public HealthResponse health() {
    return apiClient.get("/health", HealthResponse.class, 200);
  }

  public LanguagesResponse languages() {
    return apiClient.get("/languages", LanguagesResponse.class, 200);
  }

  public UsageResponse usage() {
    return apiClient.get("/usage", UsageResponse.class, 200);
  }

  public TranslateResult translate(TranslateParams params) {
    return apiClient.postJson("/translate", params, TranslateResult.class, 200);
  }

  public BatchResult batch(BatchParams params) {
    return apiClient.postJson("/translate/batch", params, BatchResult.class, 200);
  }

  public static final class Builder {
    private String apiKey;
    private String baseUrl;
    private Duration timeout = Duration.ofSeconds(120);

    public Builder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    /** Base URL without trailing slash (default production v1). */
    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    /** HTTP timeout per request (connect + response); default 120 seconds. */
    public Builder timeout(Duration timeout) {
      if (timeout != null) {
        this.timeout = timeout;
      }
      return this;
    }

    public PolyLingo build() {
      return new PolyLingo(new ApiClient(apiKey, baseUrl, timeout));
    }
  }
}
