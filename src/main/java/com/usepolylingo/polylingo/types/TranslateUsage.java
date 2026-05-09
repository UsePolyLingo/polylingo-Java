package com.usepolylingo.polylingo.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class TranslateUsage {

  private final int totalTokens;
  private final int inputTokens;
  private final int outputTokens;
  private final String model;
  private final String detectedFormat;
  private final Double detectionConfidence;

  @JsonCreator
  public TranslateUsage(
      @JsonProperty("total_tokens") int totalTokens,
      @JsonProperty("input_tokens") int inputTokens,
      @JsonProperty("output_tokens") int outputTokens,
      @JsonProperty("model") String model,
      @JsonProperty("detected_format") String detectedFormat,
      @JsonProperty("detection_confidence") Double detectionConfidence) {
    this.totalTokens = totalTokens;
    this.inputTokens = inputTokens;
    this.outputTokens = outputTokens;
    this.model = model;
    this.detectedFormat = detectedFormat;
    this.detectionConfidence = detectionConfidence;
  }

  public int getTotalTokens() {
    return totalTokens;
  }

  public int getInputTokens() {
    return inputTokens;
  }

  public int getOutputTokens() {
    return outputTokens;
  }

  public String getModel() {
    return model;
  }

  public String getDetectedFormat() {
    return detectedFormat;
  }

  public Double getDetectionConfidence() {
    return detectionConfidence;
  }
}
