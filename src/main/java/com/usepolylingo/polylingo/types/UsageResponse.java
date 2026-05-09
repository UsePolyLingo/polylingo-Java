package com.usepolylingo.polylingo.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class UsageResponse {

  private final UsageInner usage;

  @JsonCreator
  public UsageResponse(@JsonProperty("usage") UsageInner usage) {
    this.usage = usage;
  }

  public UsageInner getUsage() {
    return usage;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static final class UsageInner {

    private final String periodStart;
    private final String periodEnd;
    private final int translationsUsed;
    private final Integer translationsLimit;
    private final int tokensUsed;
    private final Integer tokensLimit;

    @JsonCreator
    public UsageInner(
        @JsonProperty("period_start") String periodStart,
        @JsonProperty("period_end") String periodEnd,
        @JsonProperty("translations_used") int translationsUsed,
        @JsonProperty("translations_limit") Integer translationsLimit,
        @JsonProperty("tokens_used") int tokensUsed,
        @JsonProperty("tokens_limit") Integer tokensLimit) {
      this.periodStart = periodStart;
      this.periodEnd = periodEnd;
      this.translationsUsed = translationsUsed;
      this.translationsLimit = translationsLimit;
      this.tokensUsed = tokensUsed;
      this.tokensLimit = tokensLimit;
    }

    public String getPeriodStart() {
      return periodStart;
    }

    public String getPeriodEnd() {
      return periodEnd;
    }

    public int getTranslationsUsed() {
      return translationsUsed;
    }

    public Integer getTranslationsLimit() {
      return translationsLimit;
    }

    public int getTokensUsed() {
      return tokensUsed;
    }

    public Integer getTokensLimit() {
      return tokensLimit;
    }
  }
}
