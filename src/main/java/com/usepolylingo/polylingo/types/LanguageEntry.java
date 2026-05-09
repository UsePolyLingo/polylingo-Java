package com.usepolylingo.polylingo.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class LanguageEntry {

  private final String code;
  private final String name;
  private final Boolean rtl;

  @JsonCreator
  public LanguageEntry(
      @JsonProperty("code") String code,
      @JsonProperty("name") String name,
      @JsonProperty("rtl") Boolean rtl) {
    this.code = code;
    this.name = name;
    this.rtl = rtl;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public Boolean getRtl() {
    return rtl;
  }
}
