package com.usepolylingo.polylingo.errors;

/** Invalid or missing API key (HTTP 401). */
public final class AuthException extends PolyLingoException {

  public AuthException(int status, String error, String message) {
    super(status, error, message);
  }
}
