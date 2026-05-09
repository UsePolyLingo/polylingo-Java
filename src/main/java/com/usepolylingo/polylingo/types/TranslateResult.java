package com.usepolylingo.polylingo.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TranslateResult {

  private final Map<String, String> translations;
  private final TranslateUsage usage;

  @JsonCreator
  public TranslateResult(
      @JsonProperty("translations") Map<String, String> translations,
      @JsonProperty("usage") TranslateUsage usage) {
    this.translations =
        translations == null ? Collections.emptyMap() : Collections.unmodifiableMap(translations);
    this.usage = usage == null ? new TranslateUsage(0, 0, 0, null, null, null) : usage;
  }

  public Map<String, String> getTranslations() {
    return translations;
  }

  public TranslateUsage getUsage() {
    return usage;
  }
}
