package com.usepolylingo.polylingo.errors;

/** Base unchecked exception for all PolyLingo API failures. */
public class PolyLingoException extends RuntimeException {

  private final int status;
  private final String error;

  public PolyLingoException(int status, String error, String message) {
    super(message);
    this.status = status;
    this.error = error == null ? "unknown_error" : error;
  }

  public int getStatus() {
    return status;
  }

  /** API {@code error} code field (e.g. {@code invalid_request}). */
  public String getError() {
    return error;
  }
}
