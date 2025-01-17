package com.friendlycaptcha.jvm.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Stream;

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
    private static final String API_KEY = "some-api-key";
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private static class TestCase {
        public String name;
        public String response;
        public boolean strict;
        public JsonNode siteverify_response;
        public int siteverify_status_code;
        public Expectation expectation;
    }

    private static class Expectation {
        public boolean should_accept;
        public boolean was_able_to_verify;
        public boolean is_client_error;
    }

    private static class TestCasesFile {
        public int version;
        public List<TestCase> tests;
    }

    private TestCasesFile casesFile;

    @BeforeAll
    public void setup() throws IOException, URISyntaxException {
        URL url = new URI(MOCK_SERVER_URL + "/api/v1/tests").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            casesFile = objectMapper.readValue(content.toString(), TestCasesFile.class);
        } else {
            throw new IOException("Failed to fetch test cases from mock server");
        }
    }

    private Stream<Arguments> provideTestCases() {
        return casesFile.tests.stream().map(testCase -> Arguments.of(testCase));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestCases")
    public void runTestCase(TestCase testCase) throws Exception {
        FriendlyCaptchaClient client = new FriendlyCaptchaClient(
                new FriendlyCaptchaClientOptions()
                        .setApiKey(API_KEY)
                        .setApiEndpoint(MOCK_SERVER_URL + "/api/v2/captcha/siteverify")
                        .setStrict(testCase.strict)
        );
        

        VerifyResult result = client.verifyCaptchaResponse(testCase.response).get();

        assertEquals(testCase.expectation.should_accept, result.shouldAccept(), testCase.name);
        assertEquals(testCase.expectation.was_able_to_verify, result.wasAbleToVerify(), testCase.name);
        assertEquals(testCase.expectation.is_client_error, result.isClientError(), testCase.name);
    }
}