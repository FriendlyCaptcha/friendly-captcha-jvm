package com.friendlycaptcha.jvm.sdk;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FriendlyCaptchaClient {
    private static final String GLOBAL_ENDPOINT = "https://global.frcapi.com";
    private static final String EU_ENDPOINT = "https://eu.frcapi.com";
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private String sitekey;
    private String apiKey;
    private int timeout;
    private String apiEndpoint;
    private boolean strict;

    public FriendlyCaptchaClient(FriendlyCaptchaClientOptions opts) {
        // validate throws an exception if the options are invalid.
        opts.validate();

        this.sitekey = opts.getSitekey();
        this.timeout = opts.getTimeout();
        this.apiKey = opts.getApiKey();
        this.strict = opts.isStrict();

        String apiEndpoint = opts.getApiEndpoint();
        if ("global".equals(apiEndpoint)) {
            apiEndpoint = GLOBAL_ENDPOINT;
        } else if ("eu".equals(apiEndpoint)) {
            apiEndpoint = EU_ENDPOINT;
        }
        this.apiEndpoint = apiEndpoint;
    }

    public CompletableFuture<VerifyResult> verifyCaptchaResponse(String response) {
        SiteverifyRequest siteverifyRequest = new SiteverifyRequest();
        siteverifyRequest.setResponse(response);
        if (this.sitekey != null) {
            siteverifyRequest.setSitekey(this.sitekey);
        }

        VerifyResult result = new VerifyResult(this.strict);
        String body;
        try {
            ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
            body = objectMapper.writeValueAsString(siteverifyRequest);
        } catch (IOException e) {
            result.setException(e);
            result.setErrorCode(ErrorCode.FAILED_TO_ENCODE_REQUEST);
            return CompletableFuture.completedFuture(result);
        }

        CompletableFuture<VerifyResult> future = CompletableFuture.supplyAsync(() -> {
            try {
                URL url = URI.create(this.apiEndpoint).toURL();
                url = new URI(
                    url.getProtocol(),
                    null,
                    url.getHost(),
                    url.getPort(),
                    "/api/v2/captcha/siteverify", null, null).toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Frc-Sdk", "friendly-captcha-java@" + Version.SDK_VERSION);
                connection.setRequestProperty("X-Api-Key", this.apiKey);
                connection.setDoOutput(true);
                connection.getOutputStream().write(body.getBytes());

                connection.setConnectTimeout(timeout);
                connection.setReadTimeout(timeout);

                int status = connection.getResponseCode();
                result.setStatus(status);
                if (status >= 400 && status < 500) {
                    result.setErrorCode(ErrorCode.FAILED_DUE_TO_CLIENT_ERROR);
                }

                ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
                try {
                    result.setResponse(objectMapper.readValue(connection.getInputStream(), VerifyResponse.class));
                } catch (IOException e) {
                    result.setException(e);
                    result.setErrorCode(ErrorCode.FAILED_TO_DECODE_RESPONSE);
                }
            } catch (IOException e) {
                result.setException(e);
                result.setErrorCode(ErrorCode.REQUEST_FAILED);
            } catch (URISyntaxException e) {
                // The API endpoint URL is invalid.
                result.setException(e);
                result.setErrorCode(ErrorCode.FAILED_DUE_TO_CLIENT_ERROR);
            }
            return result;
        });

        CompletableFuture<VerifyResult> timeoutFuture = failAfter(timeout, TimeUnit.MILLISECONDS);

        return future.applyToEither(timeoutFuture, res -> {
            if (res == null) {
                result.setErrorCode(ErrorCode.RESPONSE_TIMEOUT);
            }
            return res;
        });
    }

    private static <T> CompletableFuture<T> failAfter(long timeout, TimeUnit unit) {
        CompletableFuture<T> promise = new CompletableFuture<>();
        scheduler.schedule(() -> {
            promise.completeExceptionally(new TimeoutException("Timeout after " + timeout + " " + unit));
        }, timeout, unit);
        return promise;
    }
}