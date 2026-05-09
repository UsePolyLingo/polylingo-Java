package com.usepolylingo.polylingo.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class BatchItemResult {

  private final String id;
  private final Map<String, String> translations;

  @JsonCreator
  public BatchItemResult(
      @JsonProperty("id") String id,
      @JsonProperty("translations") Map<String, String> translations) {
    this.id = id;
    this.translations =
        translations == null ? Collections.emptyMap() : Collections.unmodifiableMap(translations);
  }

  public String getId() {
    return id;
  }

  public Map<String, String> getTranslations() {
    return translations;
  }
}
