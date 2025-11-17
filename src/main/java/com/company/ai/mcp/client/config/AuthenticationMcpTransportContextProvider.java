package com.company.ai.mcp.client.config;

import io.modelcontextprotocol.common.McpTransportContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.model.tool.internal.ToolCallReactiveContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.util.context.Context;

import static com.company.ai.mcp.client.controller.ChatController.HTTP_HEADER_TENANT;

public class AuthenticationMcpTransportContextProvider implements Supplier<McpTransportContext> {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationMcpTransportContextProvider.class);

	public static final String HTTP_HEADERS_KEY = "httpHeaders";

	public static Function<Context, Context> writeToReactorContext() {
		return context -> {
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			if (requestAttributes instanceof ServletRequestAttributes) {
				HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
				Context updatedContext = context;

				for (String headerName : List.of(HttpHeaders.AUTHORIZATION, HTTP_HEADER_TENANT)) {
					String headerValue = request.getHeader(headerName);
					if (headerValue != null) {
						updatedContext = updatedContext.put(headerName, headerValue);
					}
				}
				return updatedContext;
			}
			return context;
		};
	}

	@Override
	public McpTransportContext get() {
		var data = new HashMap<String, Object>();
		var ctx = ToolCallReactiveContextHolder.getContext();
		Map<String, String> headers = new HashMap<>();
		List.of(HttpHeaders.AUTHORIZATION, HTTP_HEADER_TENANT).forEach(headerName -> {
			if (ctx.hasKey(headerName)) {
				String value = ctx.get(headerName);
				headers.put(headerName, value);
			}
		});
		data.put(HTTP_HEADERS_KEY, headers);
		logger.info("Headers found in ToolCallReactiveContextHolder: {}", data);
		return McpTransportContext.create(data);
	}

}