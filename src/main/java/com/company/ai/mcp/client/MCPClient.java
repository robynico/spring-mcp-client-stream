package com.company.ai.mcp.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.company.ai.mcp.client" })
public class MCPClient {

	public static void main(String[] args) {
		SpringApplication.run(MCPClient.class, args);
	}

}