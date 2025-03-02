/*
 * Copyright 2023-2025 the original author or authors.
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

package org.springframework.ai.moonshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallingOptions;
import org.springframework.ai.moonshot.api.MoonshotApi;
import org.springframework.util.Assert;

/**
 * Options for Moonshot chat completions.
 *
 * @author Geng Rong
 * @author Thomas Vitale
 * @author Alexandros Pappas
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoonshotChatOptions implements FunctionCallingOptions {

	/**
	 * ID of the model to use
	 */
	private @JsonProperty("model") String model;

	/**
	 * The maximum number of tokens to generate in the chat completion. The total length
	 * of input tokens and generated tokens is limited by the model's context length.
	 */
	private @JsonProperty("max_tokens") Integer maxTokens;

	/**
	 * What sampling temperature to use, between 0.0 and 1.0. Higher values like 0.8 will
	 * make the output more random, while lower values like 0.2 will make it more focused
	 * and deterministic. We generally recommend altering this or top_p but not both.
	 */
	private @JsonProperty("temperature") Double temperature;

	/**
	 * An alternative to sampling with temperature, called nucleus sampling, where the
	 * model considers the results of the tokens with top_p probability mass. So 0.1 means
	 * only the tokens comprising the top 10% probability mass are considered. We
	 * generally recommend altering this or temperature but not both.
	 */
	private @JsonProperty("top_p") Double topP;

	/**
	 * How many chat completion choices to generate for each input message. Note that you
	 * will be charged based on the number of generated tokens across all the choices.
	 * Keep n as 1 to minimize costs.
	 */
	private @JsonProperty("n") Integer n;

	/**
	 * Number between -2.0 and 2.0. Positive values penalize new tokens based on whether
	 * they appear in the text so far, increasing the model's likelihood to talk about new
	 * topics.
	 */
	private @JsonProperty("presence_penalty") Double presencePenalty;

	/**
	 * Number between -2.0 and 2.0. Positive values penalize new tokens based on their
	 * existing frequency in the text so far, decreasing the model's likelihood to repeat
	 * the same line verbatim.
	 */
	private @JsonProperty("frequency_penalty") Double frequencyPenalty;

	/**
	 * Up to 5 sequences where the API will stop generating further tokens.
	 */
	private @JsonProperty("stop") List<String> stop;

	private @JsonProperty("tools") List<MoonshotApi.FunctionTool> tools;

	/**
	 * Controls which (if any) function is called by the model. none means the model will
	 * not call a function and instead generates a message. auto means the model can pick
	 * between generating a message or calling a function. Specifying a particular
	 * function via {"type: "function", "function": {"name": "my_function"}} forces the
	 * model to call that function. none is the default when no functions are present.
	 * auto is the default if functions are present. Use the
	 * {@link MoonshotApi.ChatCompletionRequest.ToolChoiceBuilder} to create a tool choice
	 * object.
	 */
	private @JsonProperty("tool_choice") String toolChoice;

	/**
	 * Moonshot Tool Function Callbacks to register with the ChatModel. For Prompt Options
	 * the functionCallbacks are automatically enabled for the duration of the prompt
	 * execution. For Default Options the functionCallbacks are registered but disabled by
	 * default. Use the enableFunctions to set the functions from the registry to be used
	 * by the ChatModel chat completion requests.
	 */
	@JsonIgnore
	private List<FunctionCallback> functionCallbacks = new ArrayList<>();

	/**
	 * List of functions, identified by their names, to configure for function calling in
	 * the chat completion requests. Functions with those names must exist in the
	 * functionCallbacks registry. The {@link #functionCallbacks} from the PromptOptions
	 * are automatically enabled for the duration of the prompt execution.
	 *
	 * Note that function enabled with the default options are enabled for all chat
	 * completion requests. This could impact the token count and the billing. If the
	 * functions is set in a prompt options, then the enabled functions are only active
	 * for the duration of this prompt execution.
	 */
	@JsonIgnore
	private Set<String> functions = new HashSet<>();

	/**
	 * A unique identifier representing your end-user, which can help Moonshot to monitor
	 * and detect abuse.
	 */
	private @JsonProperty("user") String user;

	@JsonIgnore
	private Boolean proxyToolCalls;

	@JsonIgnore
	private Map<String, Object> toolContext = new HashMap<>();

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public List<FunctionCallback> getFunctionCallbacks() {
		return this.functionCallbacks;
	}

	@Override
	public void setFunctionCallbacks(List<FunctionCallback> functionCallbacks) {
		this.functionCallbacks = functionCallbacks;
	}

	@Override
	public Set<String> getFunctions() {
		return this.functions;
	}

	public void setFunctions(Set<String> functionNames) {
		this.functions = functionNames;
	}

	@Override
	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Override
	public Double getFrequencyPenalty() {
		return this.frequencyPenalty;
	}

	public void setFrequencyPenalty(Double frequencyPenalty) {
		this.frequencyPenalty = frequencyPenalty;
	}

	@Override
	public Integer getMaxTokens() {
		return this.maxTokens;
	}

	public void setMaxTokens(Integer maxTokens) {
		this.maxTokens = maxTokens;
	}

	public Integer getN() {
		return this.n;
	}

	public void setN(Integer n) {
		this.n = n;
	}

	@Override
	public Double getPresencePenalty() {
		return this.presencePenalty;
	}

	public void setPresencePenalty(Double presencePenalty) {
		this.presencePenalty = presencePenalty;
	}

	@Override
	@JsonIgnore
	public List<String> getStopSequences() {
		return getStop();
	}

	@JsonIgnore
	public void setStopSequences(List<String> stopSequences) {
		setStop(stopSequences);
	}

	public List<String> getStop() {
		return this.stop;
	}

	public void setStop(List<String> stop) {
		this.stop = stop;
	}

	@Override
	public Double getTemperature() {
		return this.temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	@Override
	public Double getTopP() {
		return this.topP;
	}

	public void setTopP(Double topP) {
		this.topP = topP;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	@JsonIgnore
	public Integer getTopK() {
		return null;
	}

	@Override
	public Boolean getProxyToolCalls() {
		return this.proxyToolCalls;
	}

	public void setProxyToolCalls(Boolean proxyToolCalls) {
		this.proxyToolCalls = proxyToolCalls;
	}

	@Override
	public Map<String, Object> getToolContext() {
		return this.toolContext;
	}

	@Override
	public void setToolContext(Map<String, Object> toolContext) {
		this.toolContext = toolContext;
	}

	@Override
	@SuppressWarnings("unchecked")
	public MoonshotChatOptions copy() {
		return builder().model(this.model)
			.maxTokens(this.maxTokens)
			.temperature(this.temperature)
			.topP(this.topP)
			.N(this.n)
			.presencePenalty(this.presencePenalty)
			.frequencyPenalty(this.frequencyPenalty)
			.stop(this.stop != null ? new ArrayList<>(this.stop) : null)
			.user(this.user)
			.tools(this.tools != null ? new ArrayList<>(this.tools) : null)
			.toolChoice(this.toolChoice)
			.functionCallbacks(this.functionCallbacks != null ? new ArrayList<>(this.functionCallbacks) : null)
			.functions(this.functions != null ? new HashSet<>(this.functions) : null)
			.proxyToolCalls(this.proxyToolCalls)
			.toolContext(this.toolContext != null ? new HashMap<>(this.toolContext) : null)
			.build();
	}

	@Override
	public int hashCode() {
		return Objects.hash(model, maxTokens, temperature, topP, n, presencePenalty, frequencyPenalty, stop, tools,
				toolChoice, functionCallbacks, functions, user, proxyToolCalls, toolContext);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof MoonshotChatOptions other)) {
			return false;
		}
		return Objects.equals(this.model, other.model) && Objects.equals(this.maxTokens, other.maxTokens)
				&& Objects.equals(this.temperature, other.temperature) && Objects.equals(this.topP, other.topP)
				&& Objects.equals(this.n, other.n) && Objects.equals(this.presencePenalty, other.presencePenalty)
				&& Objects.equals(this.frequencyPenalty, other.frequencyPenalty)
				&& Objects.equals(this.stop, other.stop) && Objects.equals(this.tools, other.tools)
				&& Objects.equals(this.toolChoice, other.toolChoice)
				&& Objects.equals(this.functionCallbacks, other.functionCallbacks)
				&& Objects.equals(this.functions, other.functions) && Objects.equals(this.user, other.user)
				&& Objects.equals(this.proxyToolCalls, other.proxyToolCalls)
				&& Objects.equals(this.toolContext, other.toolContext);
	}

	public static class Builder {

		private final MoonshotChatOptions options = new MoonshotChatOptions();

		public Builder model(String model) {
			this.options.model = model;
			return this;
		}

		public Builder maxTokens(Integer maxTokens) {
			this.options.maxTokens = maxTokens;
			return this;
		}

		public Builder temperature(Double temperature) {
			this.options.temperature = temperature;
			return this;
		}

		public Builder topP(Double topP) {
			this.options.topP = topP;
			return this;
		}

		public Builder N(Integer n) {
			this.options.n = n;
			return this;
		}

		public Builder presencePenalty(Double presencePenalty) {
			this.options.presencePenalty = presencePenalty;
			return this;
		}

		public Builder frequencyPenalty(Double frequencyPenalty) {
			this.options.frequencyPenalty = frequencyPenalty;
			return this;
		}

		public Builder stop(List<String> stop) {
			this.options.stop = stop;
			return this;
		}

		public Builder user(String user) {
			this.options.user = user;
			return this;
		}

		public Builder tools(List<MoonshotApi.FunctionTool> tools) {
			this.options.tools = tools;
			return this;
		}

		public Builder toolChoice(String toolChoice) {
			this.options.toolChoice = toolChoice;
			return this;
		}

		public Builder functionCallbacks(List<FunctionCallback> functionCallbacks) {
			this.options.functionCallbacks = functionCallbacks;
			return this;
		}

		public Builder functions(Set<String> functionNames) {
			Assert.notNull(functionNames, "Function names must not be null");
			this.options.functions = functionNames;
			return this;
		}

		public Builder function(String functionName) {
			Assert.hasText(functionName, "Function name must not be empty");
			if (this.options.functions == null) {
				this.options.functions = new HashSet<>();
			}
			this.options.functions.add(functionName);
			return this;
		}

		public Builder proxyToolCalls(Boolean proxyToolCalls) {
			this.options.proxyToolCalls = proxyToolCalls;
			return this;
		}

		public Builder toolContext(Map<String, Object> toolContext) {
			if (this.options.toolContext == null) {
				this.options.toolContext = toolContext;
			}
			else {
				this.options.toolContext.putAll(toolContext);
			}
			return this;
		}

		public MoonshotChatOptions build() {
			return this.options;
		}

	}

}
