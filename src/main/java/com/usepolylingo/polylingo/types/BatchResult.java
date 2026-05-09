package com.usepolylingo.polylingo.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class BatchResult {

  private final List<BatchItemResult> results;
  private final BatchUsage usage;

  @JsonCreator
  public BatchResult(
      @JsonProperty("results") List<BatchItemResult> results,
      @JsonProperty("usage") BatchUsage usage) {
    this.results = results == null ? Collections.emptyList() : Collections.unmodifiableList(results);
    this.usage = usage == null ? new BatchUsage(0, 0, 0, null) : usage;
  }

  public List<BatchItemResult> getResults() {
    return results;
  }

  public BatchUsage getUsage() {
    return usage;
  }
}
