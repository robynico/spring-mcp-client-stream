package com.company.ai.mcp.client.controller;

import com.company.ai.mcp.client.agent.AgentService;
import com.company.ai.mcp.client.model.ChatRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Flux;

import javax.validation.Valid;

@RestController
@Validated
public class ChatController {

	public static final String HTTP_HEADER_TENANT = "Tenant";

	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	private final AgentService agentService;

	public ChatController(AgentService agentService) {
		this.agentService = agentService;
	}

	@PostMapping(value = "/chat")
	public String chat(@RequestBody @Valid ChatRequest request) {
		return agentService.chat(request.getPrompt());
	}

	@PostMapping(value = "/stream-chat")
	public Flux<String> streamChat(@RequestBody @Valid ChatRequest request, @Headers HttpHeaders headers,
			jakarta.servlet.http.HttpServletResponse response) {
		var requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		String tenant = requestAttributes.getRequest().getHeader(HTTP_HEADER_TENANT);
		logger.info("Tenant {}", tenant);
		response.setHeader(HTTP_HEADER_TENANT, tenant);
		return agentService.streamChat(request.getPrompt());
	}

}