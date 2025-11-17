package com.company.ai.mcp.client.config;

import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import io.modelcontextprotocol.common.McpTransportContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

import static com.company.ai.mcp.client.controller.ChatController.HTTP_HEADER_TENANT;

class CustomMcpSyncRequestCustomizer implements McpSyncHttpClientRequestCustomizer {

	private static final Logger logger = LoggerFactory.getLogger(CustomMcpSyncRequestCustomizer.class);

	@Override
	public void customize(HttpRequest.Builder builder, String method, URI endpoint, String body,
			McpTransportContext context) {
		var headers = context.get(AuthenticationMcpTransportContextProvider.HTTP_HEADERS_KEY);
		logger.info("Headers found in McpTransportContext: {}", headers);

		if (headers instanceof Map<?, ?> headerMap) {
			List.of(HttpHeaders.AUTHORIZATION, HTTP_HEADER_TENANT).forEach(headerName -> {
				var value = headerMap.get(headerName);
				if (value instanceof String stringValue) {
					builder.header(headerName, stringValue);
				}
			});
		}
	}

}