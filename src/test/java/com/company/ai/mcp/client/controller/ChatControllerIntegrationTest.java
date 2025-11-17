package com.company.ai.mcp.client.controller;

import com.company.ai.mcp.client.model.ChatRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatControllerIntegrationTest {

	@LocalServerPort
	private int port;

	private final HttpClient httpClient = HttpClient.newHttpClient();

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void testSingleTenantStreamChat() throws Exception {
		ChatRequest request = new ChatRequest("add 2 + 3");
		String json = objectMapper.writeValueAsString(request);

		HttpRequest httpRequest = HttpRequest.newBuilder()
			.uri(URI.create("http://localhost:" + port + "/stream-chat"))
			.header("Content-Type", "application/json")
			.header("Tenant", "A")
			.POST(HttpRequest.BodyPublishers.ofString(json))
			.build();

		HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

		assertEquals(200, response.statusCode());
		assertEquals("A", response.headers().firstValue("Tenant").orElse(null));
		assertNotNull(response.body());
	}

	@Test
	void testMultiTenantConcurrentRequests() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(3);

		CompletableFuture<Void> tenantA = CompletableFuture.runAsync(() -> testTenantRequest("A", "add 2 + 3"),
				executor);

		CompletableFuture<Void> tenantB = CompletableFuture.runAsync(() -> testTenantRequest("B", "multiply 4 * 5"),
				executor);

		CompletableFuture<Void> tenantC = CompletableFuture.runAsync(() -> testTenantRequest("C", "subtract 10 - 3"),
				executor);

		CompletableFuture.allOf(tenantA, tenantB, tenantC).get();
		executor.shutdown();
	}

	@Test
	void testTenantContextPropagation() throws Exception {
		ChatRequest request = new ChatRequest("do an echo toto");
		String json = objectMapper.writeValueAsString(request);

		HttpRequest httpRequest = HttpRequest.newBuilder()
			.uri(URI.create("http://localhost:" + port + "/stream-chat"))
			.header("Content-Type", "application/json")
			.header("Tenant", "TestTenant")
			.POST(HttpRequest.BodyPublishers.ofString(json))
			.build();

		HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

		assertEquals(200, response.statusCode());
		assertEquals("TestTenant", response.headers().firstValue("Tenant").orElse(null));
		assertNotNull(response.body());
	}

	@Test
	void testMultipleTenantIterations() throws Exception {
		String[] tenants = {"A", "B", "C", "D", "E", "F"};
		String[] prompts = {"add 2 + 3", "multiply 4 * 5", "subtract 10 - 3"};
		int iterations = 3;
		ExecutorService executor = Executors.newFixedThreadPool(10);

		int i = 0;
		while (i < iterations) {
			CompletableFuture<Void>[] futures = new CompletableFuture[tenants.length];
			for (int j = 0; j < tenants.length; j++) {
				final int index = j;
				futures[j] = CompletableFuture.runAsync(() -> 
					testTenantRequest(tenants[index], prompts[index % prompts.length]), executor);
			}
			CompletableFuture.allOf(futures).get();
			i++;
		}
		executor.shutdown();
	}

	private void testTenantRequest(String tenant, String prompt) {
		try {
			ChatRequest request = new ChatRequest(prompt);
			String json = objectMapper.writeValueAsString(request);

			HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + port + "/stream-chat"))
				.header("Content-Type", "application/json")
				.header("Tenant", tenant)
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.build();

			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			assertEquals(200, response.statusCode());
			assertEquals(tenant, response.headers().firstValue("Tenant").orElse(null));
			assertNotNull(response.body());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}