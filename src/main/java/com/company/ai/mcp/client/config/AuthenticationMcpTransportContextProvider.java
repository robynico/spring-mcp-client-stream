package com.company.ai.mcp.client.config;

import io.modelcontextprotocol.common.McpTransportContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.company.ai.mcp.client.controller.ChatController.HTTP_HEADER_TENANT;

public class AuthenticationMcpTransportContextProvider implements Supplier<McpTransportContext> {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationMcpTransportContextProvider.class);

	public static final String HTTP_HEADERS_KEY = "httpHeaders";

	@Override
	public McpTransportContext get() {
		var data = new HashMap<String, Object>();
		RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
		if (previousAttributes instanceof ServletRequestAttributes) {
			HttpServletRequest request = ((ServletRequestAttributes) previousAttributes).getRequest();
			Map<String, String> headers = new HashMap<>();
			List.of(HttpHeaders.AUTHORIZATION, HTTP_HEADER_TENANT).forEach(headerName -> {
				String value = request.getHeader(headerName);
				if (value != null) {
					headers.put(headerName, value);
				}
			});
			data.put(HTTP_HEADERS_KEY, headers);
		}
		logger.info("Headers found in RequestContextHolder: {}", data);
		return McpTransportContext.create(data);
	}

}