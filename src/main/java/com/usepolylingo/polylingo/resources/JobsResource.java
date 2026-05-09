package com.usepolylingo.polylingo.resources;

import com.usepolylingo.polylingo.errors.JobFailedException;
import com.usepolylingo.polylingo.internal.ApiClient;
import com.usepolylingo.polylingo.types.CreateJobParams;
import com.usepolylingo.polylingo.types.Job;
import com.usepolylingo.polylingo.types.JobsTranslateParams;
import com.usepolylingo.polylingo.types.TranslateResult;
import com.usepolylingo.polylingo.types.TranslateUsage;
import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

public final class JobsResource {

  private static final Duration DEFAULT_POLL = Duration.ofSeconds(5);
  private static final Duration DEFAULT_JOB_TIMEOUT = Duration.ofMinutes(20);

  private final ApiClient client;

  public JobsResource(ApiClient client) {
    this.client = client;
  }

  public Job create(CreateJobParams params) {
    return client.postJson("/jobs", params, Job.class, 202);
  }

  public Job get(String jobId) {
    return client.get("/jobs/" + ApiClient.encodeJobId(jobId), Job.class, 200);
  }

  /** Submit a translation job and poll until it completes or fails. */
  public TranslateResult translate(JobsTranslateParams params) {
    Job submitted = create(params.toCreateJobParams());
    String jobId = submitted.getJobId();
    if (jobId == null || jobId.isEmpty()) {
      throw new JobFailedException("", 500, "invalid_response", "create job returned no job_id");
    }

    Duration pollInterval =
        params.getPollInterval() != null ? params.getPollInterval() : DEFAULT_POLL;
    Duration budget = params.getTimeout() != null ? params.getTimeout() : DEFAULT_JOB_TIMEOUT;
    long deadlineNanos = System.nanoTime() + budget.toNanos();
    Consumer<Integer> onProgress = params.getOnProgress();

    while (System.nanoTime() < deadlineNanos) {
      Job st = get(jobId);
      String status = st.getStatus();
      if (("pending".equals(status) || "processing".equals(status)) && onProgress != null) {
        onProgress.accept(st.getQueuePosition());
      }
      if ("completed".equals(status)) {
        Map<String, String> translations = st.getTranslations();
        TranslateUsage usage = st.getUsage();
        if (translations == null || usage == null) {
          throw new JobFailedException(
              jobId,
              500,
              "invalid_response",
              "Job completed but translations or usage was missing");
        }
        return new TranslateResult(translations, usage);
      }
      if ("failed".equals(status)) {
        String err = st.getError() != null ? st.getError() : "job_failed";
        String msg =
            st.getMessage() != null
                ? st.getMessage()
                : (st.getError() != null ? st.getError() : "Translation job failed");
        throw new JobFailedException(jobId, 200, err, msg);
      }
      sleep(jobId, pollInterval);
    }

    throw new JobFailedException(
        jobId, 408, "timeout", "Job polling exceeded the configured timeout");
  }

  private static void sleep(String jobId, Duration d) {
    try {
      Thread.sleep(d.toMillis());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new JobFailedException(jobId, 408, "timeout", "Polling interrupted");
    }
  }
}
