# MCP Client & McpTransportContext 

## 1 Start MCP SERVER and MCP CLIENT

```bash
npx @modelcontextprotocol/server-everything streamableHttp
```

```bash
aws sso login
aws configure
# https://docs.aws.amazon.com/cli/latest/userguide/welcome-examples.html
```

```bash
./gradlew bootRun
```

## 2 Run stream chat without tool call

```bash
curl -X POST http://localhost:8080/stream-chat \
-H "Content-Type: application/json" \
-H "Tenant: A " \
-d '{"prompt": "list the tools"}'
```

## 3 Run stream chat with tool call

> the following query do not access to servlet headers when tool is called ;(

```bash
curl -X POST http://localhost:8080/stream-chat \
-H "Content-Type: application/json" \
-H "Tenant: A " \
-d '{"prompt": "do an echo toto"}'
```

