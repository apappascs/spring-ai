package org.springframework.ai.openai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ReactiveRetryTemplate {

	private static final Logger logger = LoggerFactory.getLogger(ReactiveRetryTemplate.class);

	private final RetryBackoffSpec retrySpec;

	private ReactiveRetryTemplate(RetryBackoffSpec retrySpec) {
		this.retrySpec = retrySpec;
	}

	public <T> Flux<T> executeFlux(Supplier<Flux<T>> operation) {
		return Flux.defer(operation)
			.retryWhen(retrySpec)
			.doOnError(error -> logger.error("Final failure after retries: {}", error.getMessage()))
			.doFinally(signal -> logger.info("Retryable operation completed with signal: {}", signal));
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private long maxAttempts = 3;

		private Duration minBackoff = Duration.ofMillis(500);

		private Duration maxBackoff = Duration.ofSeconds(5);

		private Predicate<Throwable> retryPredicate = throwable -> true;

		public Builder maxAttempts(long maxAttempts) {
			this.maxAttempts = maxAttempts;
			return this;
		}

		public Builder backoff(Duration minBackoff, Duration maxBackoff) {
			this.minBackoff = minBackoff;
			this.maxBackoff = maxBackoff;
			return this;
		}

		public Builder retryOn(Predicate<Throwable> retryPredicate) {
			this.retryPredicate = retryPredicate;
			return this;
		}

		public ReactiveRetryTemplate build() {
			RetryBackoffSpec retrySpec = Retry.backoff(maxAttempts, minBackoff)
				.maxBackoff(maxBackoff)
				.jitter(0.5) // Adds 50% jitter to the backoff
				.filter(throwable -> {
					boolean shouldRetry = retryPredicate.test(throwable);
					logger.info("Evaluating retry predicate for exception: {}. Should retry: {}",
							throwable.getClass().getName(), shouldRetry);
					return shouldRetry;
				})
				.onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new RuntimeException("Retries exhausted",
						retrySignal.failure()));
			return new ReactiveRetryTemplate(retrySpec);
		}

	}

}
