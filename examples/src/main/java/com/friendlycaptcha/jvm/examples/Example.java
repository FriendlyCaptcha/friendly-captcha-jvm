package com.friendlycaptcha.jvm.examples;

import com.friendlycaptcha.jvm.sdk.FriendlyCaptchaClient;
import com.friendlycaptcha.jvm.sdk.FriendlyCaptchaClientOptions;
import com.friendlycaptcha.jvm.sdk.RiskIntelligenceRetrieveResponseData;
import com.friendlycaptcha.jvm.sdk.RiskIntelligenceRetrieveResult;
import com.friendlycaptcha.jvm.sdk.VerifyResult;
import com.friendlycaptcha.jvm.sdk.VerifyResponseError;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        if (formData == null || formData.isEmpty()) {
            return map;
        }

        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name()) : "";
            map.put(key, value);
        }
        return map;
    }

    private static void processTemplate(HttpExchange exchange, Configuration cfg, Map<String, Object> model)
            throws IOException {
        Template template = cfg.getTemplate("demo.html");
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        try (Writer out = new OutputStreamWriter(exchange.getResponseBody())) {
            template.process(model, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readRequestBody(InputStream body) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = body.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }

    private static void retrieveRiskIntelligenceIfAvailable(FriendlyCaptchaClient client,
            Map<String, String> formData) {
        String riskIntelligenceToken = formData.get("frc-risk-intelligence-token");
        if (riskIntelligenceToken == null || riskIntelligenceToken.trim().isEmpty()) {
            System.out.println("No risk intelligence token found in form data, skipping risk intelligence retrieval.");
            return;
        }

        try {
            RiskIntelligenceRetrieveResult retrieveResult = client.retrieveRiskIntelligence(riskIntelligenceToken)
                    .get();
            if (!retrieveResult.wasAbleToRetrieve() || !retrieveResult.isValid()) {
                System.out.println("Failed to retrieve risk intelligence: " + retrieveResult.getErrorCode()
                        + " / " + retrieveResult.getResponseError());
                return;
            }

            RiskIntelligenceRetrieveResponseData data = retrieveResult.getResponse().getData();
            if (data == null) {
                System.out.println("Risk intelligence response had no data.");
                return;
            }

            System.out.println("Risk intelligence response data:");
            System.out.println(data);
            System.out.println("Token data:");
            System.out.println(data.getToken());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Risk intelligence retrieval interrupted: " + e.getMessage());
        } catch (ExecutionException e) {
            System.out.println("Risk intelligence retrieval failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {

        // Load sitekey and apiKey from environment variables
        String sitekey = System.getenv("FRC_SITEKEY");
        String apiKey = System.getenv("FRC_APIKEY");
        String apiEndpoint = System.getenv("FRC_API_ENDPOINT");
        String widgetEndpoint = System.getenv("FRC_WIDGET_ENDPOINT");

        if (sitekey == null || apiKey == null) {
            System.err.println("Environment variables FRC_SITEKEY and FRC_APIKEY must be set.");
            System.exit(1);
        }

        // Create a FriendlyCaptcha client
        FriendlyCaptchaClientOptions options = new FriendlyCaptchaClientOptions()
                .setApiKey(apiKey)
                .setSitekey(sitekey);
        if (apiEndpoint != null && !apiEndpoint.trim().isEmpty()) {
            options.setApiEndpoint(apiEndpoint);
        }
        FriendlyCaptchaClient client = new FriendlyCaptchaClient(options);

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
                model.put("widgetEndpoint", widgetEndpoint == null ? "" : widgetEndpoint);

                if ("GET".equals(exchange.getRequestMethod())) {
                    model.put("message", "");
                    exchange.sendResponseHeaders(200, 0);
                    processTemplate(exchange, cfg, model);
                } else if ("POST".equals(exchange.getRequestMethod())) {
                    String requestBody = readRequestBody(exchange.getRequestBody());
                    Map<String, String> formData = parseFormData(requestBody);

                    // Retrieve risk intelligence data regardless of captcha verification result.
                    retrieveRiskIntelligenceIfAvailable(client, formData);

                    String message = formData.get("message");
                    if (message != null) {
                        System.out.println("Received form message. message=" + message);
                    }

                    String responseToken = formData.get("frc-captcha-response");
                    try {
                        // Verify the response token
                        VerifyResult result = client.verifyCaptchaResponse(responseToken).get();
                        System.out.println("Captcha: Should accept: " + result.shouldAccept());
                        System.out.println("Captcha: Was able to verify: " + result.wasAbleToVerify());
                        System.out.println("Captcha: Is client error: " + result.isClientError());

                        if (!result.wasAbleToVerify()) {
                            VerifyResponseError responseError = result.getResponseError();

                            // Alert yourself: something went wrong, we weren't able to verify the captcha.
                            // Maybe the API is down, or your credentials are incorrect.
                            System.out.println("COULD NOT VERIFY FRIENDLY CAPTCHA RESPONSE!\n"
                                    + "> Error Code: " + result.getErrorCode() + "\n"
                                    + "> Response Error: " + responseError);
                        }

                        if (result.shouldAccept()) {
                            model.put("message", "✅ Your message has been submitted successfully.");
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            model.put("message", "❌ Anti-robot check failed, please try again.");
                            exchange.sendResponseHeaders(400, 0);
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
