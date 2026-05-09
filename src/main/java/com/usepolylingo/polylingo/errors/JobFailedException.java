package com.usepolylingo.polylingo.errors;

/** Async job finished as {@code failed} or polling exceeded timeout. */
public final class JobFailedException extends PolyLingoException {

  private final String jobId;

  public JobFailedException(String jobId, int status, String error, String message) {
    super(status, error, message);
    this.jobId = jobId;
  }

  public String getJobId() {
    return jobId;
  }
}
