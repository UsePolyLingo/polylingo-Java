package com.usepolylingo.polylingo.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class BatchItem {

  private final String id;
  private final String content;
  private final String format;
  private final String source;

  private BatchItem(Builder b) {
    this.id = Objects.requireNonNull(b.id, "id");
    this.content = Objects.requireNonNull(b.content, "content");
    this.format = b.format;
    this.source = b.source;
  }

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  @JsonProperty("format")
  public String getFormat() {
    return format;
  }

  @JsonProperty("source")
  public String getSource() {
    return source;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String id;
    private String content;
    private String format;
    private String source;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder content(String content) {
      this.content = content;
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

    public BatchItem build() {
      return new BatchItem(this);
    }
  }
}
