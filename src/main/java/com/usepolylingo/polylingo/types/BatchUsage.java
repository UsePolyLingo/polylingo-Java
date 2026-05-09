package com.usepolylingo.polylingo.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class BatchUsage {

  private final int totalTokens;
  private final int inputTokens;
  private final int outputTokens;
  private final String model;

  @JsonCreator
  public BatchUsage(
      @JsonProperty("total_tokens") int totalTokens,
      @JsonProperty("input_tokens") int inputTokens,
      @JsonProperty("output_tokens") int outputTokens,
      @JsonProperty("model") String model) {
    this.totalTokens = totalTokens;
    this.inputTokens = inputTokens;
    this.outputTokens = outputTokens;
    this.model = model;
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
}
