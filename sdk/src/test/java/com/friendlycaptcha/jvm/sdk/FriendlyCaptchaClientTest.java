package com.friendlycaptcha.jvm.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FriendlyCaptchaClientTest {
    private static final String MOCK_SERVER_URL = "http://localhost:1090";
    private static final String CAPTCHA_SITEVERIFY_CASES_ENDPOINT = "/api/v1/captcha/siteverifyTests";
    private static final String RISK_INTELLIGENCE_RETRIEVE_CASES_ENDPOINT = "/api/v1/riskIntelligence/retrieveTests";
    private static final String API_KEY = "some-api-key";
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CaptchaSiteverifyTestCase {
        public String name;
        public String response;
        public boolean strict;
        public JsonNode siteverify_response;
        public CaptchaSiteverifyExpectation expectation;

        @Override
        public String toString() {
            return name;
        }
    }

    private static class CaptchaSiteverifyExpectation {
        public boolean should_accept;
        public boolean was_able_to_verify;
        public boolean is_client_error;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CaptchaSiteverifyTestCasesFile {
        public List<CaptchaSiteverifyTestCase> tests;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RiskIntelligenceRetrieveTestCase {
        public String name;
        public String token;
        public JsonNode retrieve_response;
        public RiskIntelligenceRetrieveExpectation expectation;

        @Override
        public String toString() {
            return name;
        }
    }

    private static class RiskIntelligenceRetrieveExpectation {
        public boolean was_able_to_retrieve;
        public boolean is_valid;
        public boolean is_client_error;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RiskIntelligenceRetrieveTestCasesFile {
        public List<RiskIntelligenceRetrieveTestCase> tests;
    }

    private CaptchaSiteverifyTestCasesFile captchaSiteverifyCasesFile;
    private RiskIntelligenceRetrieveTestCasesFile riskIntelligenceRetrieveCasesFile;

    @BeforeAll
    public void setup() throws IOException, URISyntaxException {
        captchaSiteverifyCasesFile = fetchTestCases(CAPTCHA_SITEVERIFY_CASES_ENDPOINT,
                CaptchaSiteverifyTestCasesFile.class);
        riskIntelligenceRetrieveCasesFile = fetchTestCases(
                RISK_INTELLIGENCE_RETRIEVE_CASES_ENDPOINT,
                RiskIntelligenceRetrieveTestCasesFile.class);
    }

    private <T> T fetchTestCases(String endpoint, Class<T> clazz) throws IOException, URISyntaxException {
        URL url = new URI(MOCK_SERVER_URL + endpoint).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to fetch test cases from mock server endpoint: " + endpoint);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();

        return objectMapper.readValue(content.toString(), clazz);
    }

    private Stream<Arguments> provideCaptchaSiteverifyTestCases() {
        return captchaSiteverifyCasesFile.tests.stream().map(testCase -> Arguments.of(testCase));
    }

    private Stream<Arguments> provideRiskIntelligenceRetrieveTestCases() {
        return riskIntelligenceRetrieveCasesFile.tests.stream().map(testCase -> Arguments.of(testCase));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideCaptchaSiteverifyTestCases")
    public void runCaptchaSiteverifyTestCase(CaptchaSiteverifyTestCase testCase) throws Exception {
        FriendlyCaptchaClient client = new FriendlyCaptchaClient(
                new FriendlyCaptchaClientOptions()
                        .setApiKey(API_KEY)
                        .setApiEndpoint(MOCK_SERVER_URL)
                        .setStrict(testCase.strict));

        VerifyResult result = client.verifyCaptchaResponse(testCase.response).get();

        assertEquals(testCase.expectation.should_accept, result.shouldAccept(), testCase.name);
        assertEquals(testCase.expectation.was_able_to_verify, result.wasAbleToVerify(), testCase.name);
        assertEquals(testCase.expectation.is_client_error, result.isClientError(), testCase.name);

        if (result.getResponse() != null && result.getResponse().isSuccess()) {
            VerifyResponse expectedResponse = objectMapper.treeToValue(testCase.siteverify_response,
                    VerifyResponse.class);

            VerifyResponseData exp = expectedResponse.getData();
            VerifyResponseData res = result.getResponse().getData();

            assertNotNull(res, "Siteverify response data should not be null on success");
            assertNotNull(exp, "Expected siteverify response data should not be null on success");
            if (res == null || exp == null) {
                return;
            }

            assertEquals(exp.getEventId(), res.getEventId(), "Event ID does not match expected value");

            VerifyResponseChallengeData expChallenge = exp.getChallenge();
            VerifyResponseChallengeData resChallenge = res.getChallenge();
            assertNotNull(resChallenge, "Challenge data should not be null on success");
            assertNotNull(expChallenge, "Expected challenge data should not be null on success");
            if (expChallenge != null && resChallenge != null) {
                assertEquals(expChallenge.getTimestamp(), resChallenge.getTimestamp(), "Challenge timestamp mismatch");
                assertEquals(expChallenge.getOrigin(), resChallenge.getOrigin(), "Challenge origin mismatch");
            }

            assertEquals(
                    exp.getRiskIntelligence(),
                    res.getRiskIntelligence(),
                    "Risk intelligence data does not match expected value");

            JsonNode expRiskIntelligence = exp.getRiskIntelligence();
            JsonNode resRiskIntelligence = res.getRiskIntelligence();
            if (expRiskIntelligence != null && !expRiskIntelligence.isNull() && resRiskIntelligence != null
                    && !resRiskIntelligence.isNull()) {
                assertTrue(
                        resRiskIntelligence.toString().contains("header_user_agent"),
                        "Risk intelligence should include header_user_agent");

                JsonNode expHeaderUserAgent = expRiskIntelligence.path("client").path("header_user_agent");
                JsonNode resHeaderUserAgent = resRiskIntelligence.path("client").path("header_user_agent");
                if (!expHeaderUserAgent.isMissingNode() && !expHeaderUserAgent.isNull()) {
                    assertEquals(
                            expHeaderUserAgent.asText(),
                            resHeaderUserAgent.asText(),
                            "Client header user agent does not match");
                }

                JsonNode expBrowserId = expRiskIntelligence.path("client").path("browser").path("id");
                JsonNode resBrowserId = resRiskIntelligence.path("client").path("browser").path("id");
                if (!expBrowserId.isMissingNode() && !expBrowserId.isNull()
                        && !resBrowserId.isMissingNode() && !resBrowserId.isNull()) {
                    assertEquals(expBrowserId.asText(), resBrowserId.asText(), "Client browser ID does not match");
                }
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideRiskIntelligenceRetrieveTestCases")
    public void runRiskIntelligenceRetrieveTestCase(RiskIntelligenceRetrieveTestCase testCase) throws Exception {
        FriendlyCaptchaClient client = new FriendlyCaptchaClient(
                new FriendlyCaptchaClientOptions()
                        .setApiKey(API_KEY)
                        .setApiEndpoint(MOCK_SERVER_URL));

        RiskIntelligenceRetrieveResult result = client.retrieveRiskIntelligence(testCase.token).get();

        assertEquals(
                testCase.expectation.was_able_to_retrieve,
                result.wasAbleToRetrieve(),
                testCase.name);
        assertEquals(
                testCase.expectation.is_valid,
                result.isValid(),
                testCase.name);
        assertEquals(
                testCase.expectation.is_client_error,
                result.isClientError(),
                testCase.name);

        if (result.getResponse() != null && result.getResponse().isSuccess()) {
            assertNotNull(result.getResponse().getRawJson(), "Raw retrieve response should not be null on success");
            assertTrue(
                    result.getResponse().getRawJson().toString().contains("header_user_agent"),
                    "Raw retrieve response should include header_user_agent");

            RiskIntelligenceRetrieveResponse expectedResponse = objectMapper.treeToValue(
                    testCase.retrieve_response,
                    RiskIntelligenceRetrieveResponse.class);

            RiskIntelligenceRetrieveResponseData exp = expectedResponse.getData();
            RiskIntelligenceRetrieveResponseData res = result.getResponse().getData();

            assertNotNull(res, "Retrieve response data should not be null on success");
            assertNotNull(exp, "Expected retrieve response data should not be null on success");
            if (res == null || exp == null) {
                return;
            }

            assertEquals(exp.getEventId(), res.getEventId(), "Event ID does not match expected value");
            assertEquals(
                    exp.getRiskIntelligence(),
                    res.getRiskIntelligence(),
                    "Risk intelligence data does not match expected value");

            RiskIntelligenceTokenData expToken = exp.getToken();
            RiskIntelligenceTokenData resToken = res.getToken();
            if (expToken != null || resToken != null) {
                assertNotNull(resToken, "Retrieve token should not be null when expected token is present");
                assertNotNull(expToken, "Expected retrieve token should not be null when response token is present");
                if (expToken != null && resToken != null) {
                    assertEquals(expToken.getTimestamp(), resToken.getTimestamp(), "Token timestamp does not match");
                    assertEquals(expToken.getExpiresAt(), resToken.getExpiresAt(), "Token expiry does not match");
                    assertEquals(expToken.getNumUses(), resToken.getNumUses(), "Token num uses does not match");
                    assertEquals(expToken.getOrigin(), resToken.getOrigin(), "Token origin does not match");
                }
            }

            JsonNode expRiskIntelligence = exp.getRiskIntelligence();
            JsonNode resRiskIntelligence = res.getRiskIntelligence();
            if (expRiskIntelligence != null && !expRiskIntelligence.isNull() && resRiskIntelligence != null
                    && !resRiskIntelligence.isNull()) {
                JsonNode expHeaderUserAgent = expRiskIntelligence.path("client").path("header_user_agent");
                JsonNode resHeaderUserAgent = resRiskIntelligence.path("client").path("header_user_agent");
                if (!expHeaderUserAgent.isMissingNode() && !expHeaderUserAgent.isNull()) {
                    assertEquals(
                            expHeaderUserAgent.asText(),
                            resHeaderUserAgent.asText(),
                            "Client header user agent does not match");
                }

                JsonNode expBrowserId = expRiskIntelligence.path("client").path("browser").path("id");
                JsonNode resBrowserId = resRiskIntelligence.path("client").path("browser").path("id");
                if (!expBrowserId.isMissingNode() && !expBrowserId.isNull()
                        && !resBrowserId.isMissingNode() && !resBrowserId.isNull()) {
                    assertEquals(expBrowserId.asText(), resBrowserId.asText(), "Client browser ID does not match");
                }
            }
        }
    }
}
