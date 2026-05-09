package com.usepolylingo.polylingo.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Job {

  private final String jobId;
  private final String status;
  private final Integer queuePosition;
  private final Map<String, String> translations;
  private final TranslateUsage usage;
  private final String error;
  private final String message;
  private final String createdAt;
  private final String updatedAt;
  private final String completedAt;

  @JsonCreator
  public Job(
      @JsonProperty("job_id") String jobId,
      @JsonProperty("status") String status,
      @JsonProperty("queue_position") Integer queuePosition,
      @JsonProperty("translations") Map<String, String> translations,
      @JsonProperty("usage") TranslateUsage usage,
      @JsonProperty("error") String error,
      @JsonProperty("message") String message,
      @JsonProperty("created_at") String createdAt,
      @JsonProperty("updated_at") String updatedAt,
      @JsonProperty("completed_at") String completedAt) {
    this.jobId = jobId;
    this.status = status;
    this.queuePosition = queuePosition;
    this.translations =
        translations == null ? null : Collections.unmodifiableMap(translations);
    this.usage = usage;
    this.error = error;
    this.message = message;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.completedAt = completedAt;
  }

  public String getJobId() {
    return jobId;
  }

  public String getStatus() {
    return status;
  }

  public Integer getQueuePosition() {
    return queuePosition;
  }

  public Map<String, String> getTranslations() {
    return translations;
  }

  public TranslateUsage getUsage() {
    return usage;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public String getCompletedAt() {
    return completedAt;
  }
}
