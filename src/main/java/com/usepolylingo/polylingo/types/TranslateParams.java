package com.usepolylingo.polylingo.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class TranslateParams {

  private final String content;
  private final List<String> targets;
  private final String format;
  private final String source;
  private final String model;

  private TranslateParams(Builder b) {
    this.content = Objects.requireNonNull(b.content, "content");
    this.targets = Collections.unmodifiableList(new ArrayList<>(b.targets));
    this.format = b.format;
    this.source = b.source;
    this.model = b.model;
  }

  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  @JsonProperty("targets")
  public List<String> getTargets() {
    return targets;
  }

  @JsonProperty("format")
  public String getFormat() {
    return format;
  }

  @JsonProperty("source")
  public String getSource() {
    return source;
  }

  @JsonProperty("model")
  public String getModel() {
    return model;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String content;
    private final List<String> targets = new ArrayList<>();
    private String format;
    private String source;
    private String model;

    public Builder content(String content) {
      this.content = content;
      return this;
    }

    public Builder targets(List<String> targets) {
      this.targets.clear();
      if (targets != null) {
        this.targets.addAll(targets);
      }
      return this;
    }

    public Builder format(String format) {
      this.format = format;
      return this;
    }

    public Builder source(String source) {
      this.source = source;
      return this;
    }

    public Builder model(String model) {
      this.model = model;
      return this;
    }

    public TranslateParams build() {
      if (targets.isEmpty()) {
        throw new IllegalStateException("targets required");
      }
      return new TranslateParams(this);
    }
  }
}
