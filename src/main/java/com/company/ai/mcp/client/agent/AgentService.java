package com.company.ai.mcp.client.agent;

import com.company.ai.mcp.client.config.AuthenticationMcpTransportContextProvider;
import org.springframework.ai.bedrock.converse.BedrockChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class AgentService {

	private final ChatClient chatClient;

	public AgentService(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpToolProvider) {
		var options = BedrockChatOptions.builder()
			.model("anthropic.claude-3-5-sonnet-20240620-v1:0")
			.temperature(0.6)
			.maxTokens(3000)
			.build();
		chatClient = chatClientBuilder.defaultOptions(options).defaultToolCallbacks(mcpToolProvider).build();
	}

	public Flux<String> streamChat(String prompt) {
		return chatClient.prompt()
			.user(userMessage -> userMessage.text(prompt))
			.stream()
			.content()
			.contextWrite(AuthenticationMcpTransportContextProvider.writeToReactorContext());
	}

	public String chat(String prompt) {
		return chatClient.prompt().user(userMessage -> userMessage.text(prompt)).call().content();
	}

}