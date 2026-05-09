package com.usepolylingo.polylingo.types;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/** Parameters for {@code jobs.translate}: job body plus polling options (not JSON serialized). */
public final class JobsTranslateParams {

  private final String content;
  private final List<String> targets;
  private final String format;
  private final String source;
  private final String model;
  private final Duration pollInterval;
  private final Duration timeout;
  private final Consumer<Integer> onProgress;

  private JobsTranslateParams(Builder b) {
    this.content = Objects.requireNonNull(b.content, "content");
    this.targets = Collections.unmodifiableList(new ArrayList<>(b.targets));
    this.format = b.format;
    this.source = b.source;
    this.model = b.model;
    this.pollInterval = b.pollInterval;
    this.timeout = b.timeout;
    this.onProgress = b.onProgress;
  }

  public String getContent() {
    return content;
  }

  public List<String> getTargets() {
    return targets;
  }

  public String getFormat() {
    return format;
  }

  public String getSource() {
    return source;
  }

  public String getModel() {
    return model;
  }

  public Duration getPollInterval() {
    return pollInterval;
  }

  public Duration getTimeout() {
    return timeout;
  }

  public Consumer<Integer> getOnProgress() {
    return onProgress;
  }

  /** Builds the body for {@code POST /jobs}. */
  public CreateJobParams toCreateJobParams() {
    CreateJobParams.Builder b =
        CreateJobParams.builder().content(content).targets(new ArrayList<>(targets));
    if (format != null) {
      b.format(format);
    }
    if (source != null) {
      b.source(source);
    }
    if (model != null) {
      b.model(model);
    }
    return b.build();
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
    private Duration pollInterval;
    private Duration timeout;
    private Consumer<Integer> onProgress;

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

    public Builder pollInterval(Duration pollInterval) {
      this.pollInterval = pollInterval;
      return this;
    }

    public Builder timeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public Builder onProgress(Consumer<Integer> onProgress) {
      this.onProgress = onProgress;
      return this;
    }

    public JobsTranslateParams build() {
      if (targets.isEmpty()) {
        throw new IllegalStateException("targets required");
      }
      return new JobsTranslateParams(this);
    }
  }
}
