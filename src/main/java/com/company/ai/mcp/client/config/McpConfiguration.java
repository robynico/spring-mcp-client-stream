package com.company.ai.mcp.client.config;

import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class McpConfiguration implements WebMvcConfigurer {

	@Bean
	McpSyncClientCustomizer syncClientCustomizer() {
		return (name, syncSpec) -> syncSpec.transportContextProvider(new AuthenticationMcpTransportContextProvider());
	}

	@Bean
	McpSyncHttpClientRequestCustomizer requestCustomizer() {
		return new CustomMcpSyncRequestCustomizer();
	}

}
