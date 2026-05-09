package com.usepolylingo.polylingo.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class LanguagesResponse {

  private final List<LanguageEntry> languages;

  @JsonCreator
  public LanguagesResponse(@JsonProperty("languages") List<LanguageEntry> languages) {
    this.languages =
        languages == null ? Collections.emptyList() : Collections.unmodifiableList(languages);
  }

  public List<LanguageEntry> getLanguages() {
    return languages;
  }
}
