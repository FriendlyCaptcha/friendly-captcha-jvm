package com.friendlycaptcha.jvm.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.Test;

public class TruncateCaptchaResponseTest {
    @Test
    public void tooLongResponseIsTruncatedAndSent() throws Exception {
        AtomicReference<JsonNode> sent = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            sent.set(new ObjectMapper().readTree(exchange.getRequestBody()));
            byte[] body = "{\"success\":false,\"error\":{\"error_code\":\"response_invalid\"}}".getBytes();
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.start();

        try {
            FriendlyCaptchaClientOptions opts = new FriendlyCaptchaClientOptions()
                    .setApiKey("some-key")
                    .setApiEndpoint("http://localhost:" + server.getAddress().getPort());
            FriendlyCaptchaClient client = new FriendlyCaptchaClient(opts);

            StringBuilder response = new StringBuilder();
            for (int i = 0; i < 3 * 1024 * 1024; i++) {
                response.append('A');
            }
            VerifyResult result = client.verifyCaptchaResponse(response.toString()).get();

            assertEquals(2 * 1024 * 1024, sent.get().get("response").asText().length());
            assertFalse(result.shouldAccept());
        } finally {
            server.stop(0);
        }
    }
}
