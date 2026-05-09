package com.usepolylingo.polylingo.errors;

import java.util.Optional;

/** Rate limited (HTTP 429). */
public final class RateLimitException extends PolyLingoException {

  private final Integer retryAfter;

  public RateLimitException(int status, String error, String message, Integer retryAfter) {
    super(status, error, message);
    this.retryAfter = retryAfter;
  }

  public Optional<Integer> getRetryAfter() {
    return Optional.ofNullable(retryAfter);
  }
}
