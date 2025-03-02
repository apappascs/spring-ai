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

package org.springframework.ai.autoconfigure.elevenlabs;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import org.springframework.ai.elevenlabs.ElevenLabsTextToSpeechModel;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Integration tests for the {@link ElevenLabsAutoConfiguration}.
 *
 * @author Alexandros Pappas
 */
@EnabledIfEnvironmentVariable(named = "ELEVEN_LABS_API_KEY", matches = ".*")
public class ElevenLabsAutoConfigurationIT {

	private static final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory
		.getLog(ElevenLabsAutoConfigurationIT.class);

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withPropertyValues("spring.ai.elevenlabs.api-key=" + System.getenv("ELEVEN_LABS_API_KEY"))
		.withConfiguration(AutoConfigurations.of(ElevenLabsAutoConfiguration.class));

	@Test
	void speech() {
		this.contextRunner.run(context -> {
			ElevenLabsTextToSpeechModel speechModel = context.getBean(ElevenLabsTextToSpeechModel.class);
			byte[] response = speechModel.call("H");
			assertThat(response).isNotNull();
			assertThat(verifyMp3FrameHeader(response))
				.withFailMessage("Expected MP3 frame header to be present in the response, but it was not found.")
				.isTrue();
			assertThat(response).isNotEmpty();

			logger.debug("Response: " + Arrays.toString(response));
		});
	}

	@Test
	void speechStream() {
		this.contextRunner.run(context -> {
			ElevenLabsTextToSpeechModel speechModel = context.getBean(ElevenLabsTextToSpeechModel.class);
			byte[] response = speechModel.call("Hello");
			assertThat(response).isNotNull();
			assertThat(verifyMp3FrameHeader(response))
				.withFailMessage("Expected MP3 frame header to be present in the response, but it was not found.")
				.isTrue();
			assertThat(response).isNotEmpty();

			logger.debug("Response: " + Arrays.toString(response));
		});
	}

	public boolean verifyMp3FrameHeader(byte[] audioResponse) {
		// Check if the response is null or too short to contain a frame header
		if (audioResponse == null || audioResponse.length < 2) {
			return false;
		}
		// Check for the MP3 frame header
		// 0xFFE0 is the sync word for an MP3 frame (11 bits set to 1 followed by 3 bits
		// set to 0)
		return (audioResponse[0] & 0xFF) == 0xFF && (audioResponse[1] & 0xE0) == 0xE0;
	}

}
