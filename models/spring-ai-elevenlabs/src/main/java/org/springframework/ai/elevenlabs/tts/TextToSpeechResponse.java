/*
 * Copyright 2025-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.elevenlabs.tts;

import java.util.List;
import java.util.Objects;

import org.springframework.ai.model.ModelResponse;
import org.springframework.ai.model.ResponseMetadata;

/**
 * Implementation of the {@link ModelResponse} interface for the text to speech response.
 *
 * @author Alexandros Pappas
 */
public class TextToSpeechResponse implements ModelResponse<Speech> {

	private final List<Speech> results;

	private final ResponseMetadata metadata;

	public TextToSpeechResponse(List<Speech> results) {
		this(results, null);
	}

	public TextToSpeechResponse(List<Speech> results, ResponseMetadata metadata) {
		this.results = results;
		this.metadata = metadata;
	}

	@Override
	public List<Speech> getResults() {
		return this.results;
	}

	public Speech getResult() {
		return this.results.get(0);
	}

	@Override
	public ResponseMetadata getMetadata() {
		return this.metadata;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TextToSpeechResponse that))
			return false;
		return Objects.equals(results, that.results);
	}

	@Override
	public int hashCode() {
		return Objects.hash(results);
	}

	@Override
	public String toString() {
		return "TextToSpeechResponse{" + "results=" + results + '}';
	}

}
