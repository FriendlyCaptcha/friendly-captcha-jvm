package com.friendlycaptcha.jvm.examples;

import com.friendlycaptcha.jvm.sdk.FriendlyCaptchaClient;
import com.friendlycaptcha.jvm.sdk.FriendlyCaptchaClientOptions;
import com.friendlycaptcha.jvm.sdk.VerifyResult;
import com.friendlycaptcha.jvm.sdk.VerifyResponseError;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Example {
    private static Map<String, String> parseFormData(String formData) throws IOException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name()) : "";
            map.put(key, value);
        }
        return map;
    }

    private static void processTemplate(HttpExchange exchange, Configuration cfg, Map<String, Object> model) throws IOException {
        Template template = cfg.getTemplate("demo.html");
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        try (Writer out = new OutputStreamWriter(exchange.getResponseBody())) {
            template.process(model, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        // Load sitekey and apiKey from environment variables
        String sitekey = System.getenv("FRC_SITEKEY");
        String apiKey = System.getenv("FRC_APIKEY");
        String endpoint = "global";

        if (sitekey == null || apiKey == null) {
            System.err.println("Environment variables FRC_SITEKEY and FRC_APIKEY must be set.");
            System.exit(1);
        }

        // Create a FriendlyCaptcha client
        FriendlyCaptchaClient client = new FriendlyCaptchaClient(
                new FriendlyCaptchaClientOptions()
                        .setApiKey(apiKey)
                        .setSitekey(sitekey)
        );

        // Configure FreeMarker, the template engine
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassLoaderForTemplateLoading(Example.class.getClassLoader(), "/");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);

        // Start the HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Prepare the data model
                Map<String, Object> model = new HashMap<>();
                model.put("sitekey", sitekey);
                model.put("widgetEndpoint", endpoint);

                if ("GET".equals(exchange.getRequestMethod())) {
                    model.put("message", "");
                    exchange.sendResponseHeaders(200, 0);
                    processTemplate(exchange, cfg, model);
                } else if ("POST".equals(exchange.getRequestMethod())) {
                    // Parse the request body (in a way that supports Java 8)
                    byte[] bytes = new byte[exchange.getRequestBody().available()];
                    try (DataInputStream dis = new DataInputStream(exchange.getRequestBody())) {
                        dis.readFully(bytes);
                    }
                    String requestBody = new String(bytes, StandardCharsets.UTF_8);
                
                    Map<String, String> formData = parseFormData(requestBody);
                    String responseToken = formData.get("frc-captcha-response");
                    try {
                        // Verify the response token
                        VerifyResult result = client.verifyCaptchaResponse(responseToken).get();
                        System.out.println("Should accept: " + result.shouldAccept());
                        System.out.println("Was able to verify: " + result.wasAbleToVerify());
                        System.out.println("Is client error: " + result.isClientError());

                        if (!result.wasAbleToVerify()) {
                            VerifyResponseError responseError = result.getResponseError();

                            // Alert yourself: something went wrong, we weren't able to verify the captcha.
                            // Maybe the API is down, or your credentials are incorrect.
                            System.out.println("COULD NOT VERIFY FRIENDLY CAPTCHA RESPONSE!\n" + 
                            "> Error Code: " + result.getErrorCode() + "\n" +
                            "> Response Error: " + responseError);
                        }

                        if (result.shouldAccept()) {
                            model.put("message", "✅ Message submitted successfully!");
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            model.put("message", "❌ Captcha verification failed! Please try again.");
                            exchange.sendResponseHeaders(400, 0); // Bad Request
                        }

                        processTemplate(exchange, cfg, model);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, -1); // Internal Server Error
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                }
            }
        });
        server.setExecutor(null);
        server.start();

        System.out.println("Server started at http://localhost:8080");
    }
}