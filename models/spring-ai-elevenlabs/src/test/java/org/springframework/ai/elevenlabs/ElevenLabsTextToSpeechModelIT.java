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

package org.springframework.ai.elevenlabs;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import reactor.core.publisher.Flux;

import org.springframework.ai.elevenlabs.tts.Speech;
import org.springframework.ai.elevenlabs.tts.TextToSpeechPrompt;
import org.springframework.ai.elevenlabs.tts.TextToSpeechResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration tests for the {@link ElevenLabsTextToSpeechModel}.
 *
 * <p>
 * These tests require a valid ElevenLabs API key to be set as an environment variable
 * named {@code ELEVEN_LABS_API_KEY}.
 *
 * @author Alexandros Pappas
 */
@SpringBootTest(classes = ElevenLabsTestConfiguration.class)
@EnabledIfEnvironmentVariable(named = "ELEVEN_LABS_API_KEY", matches = ".+")
public class ElevenLabsTextToSpeechModelIT {

	private static final String VOICE_ID = "9BWtsMINqrJLrRacOk9x";

	@Autowired
	private ElevenLabsTextToSpeechModel textToSpeechModel;

	@Test
	void textToSpeechWithVoiceTest() {
		ElevenLabsTextToSpeechOptions options = ElevenLabsTextToSpeechOptions.builder().voice(VOICE_ID).build();
		TextToSpeechPrompt prompt = new TextToSpeechPrompt("Hello, world!", options);
		TextToSpeechResponse response = textToSpeechModel.call(prompt);

		assertThat(response).isNotNull();
		List<Speech> results = response.getResults();
		assertThat(results).hasSize(1);
		Speech speech = results.get(0);
		assertThat(speech.getOutput()).isNotEmpty();
	}

	@Test
	void textToSpeechStreamWithVoiceTest() {
		ElevenLabsTextToSpeechOptions options = ElevenLabsTextToSpeechOptions.builder().voice(VOICE_ID).build();
		TextToSpeechPrompt prompt = new TextToSpeechPrompt(
				"Hello, world! This is a test of streaming speech synthesis.", options);
		Flux<TextToSpeechResponse> responseFlux = textToSpeechModel.stream(prompt);

		List<TextToSpeechResponse> responses = responseFlux.collectList().block();
		assertThat(responses).isNotNull().isNotEmpty();

		responses.forEach(response -> {
			assertThat(response).isNotNull();
			assertThat(response.getResults()).hasSize(1);
			assertThat(response.getResults().get(0).getOutput()).isNotEmpty();
		});
	}

}
