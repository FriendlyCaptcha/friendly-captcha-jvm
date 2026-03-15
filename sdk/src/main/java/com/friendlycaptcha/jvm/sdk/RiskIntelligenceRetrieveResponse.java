package com.friendlycaptcha.jvm.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * RiskIntelligenceRetrieveResponse represents the response body for the
 * /api/v2/riskIntelligence/retrieve endpoint.
 */
public class RiskIntelligenceRetrieveResponse {
    private boolean success;
    private RiskIntelligenceRetrieveResponseData data;
    private RiskIntelligenceRetrieveResponseError error;

    @JsonIgnore
    private JsonNode rawJson;

    public boolean isSuccess() {
        return success;
    }

    public RiskIntelligenceRetrieveResponseData getData() {
        return data;
    }

    public RiskIntelligenceRetrieveResponseError getError() {
        return error;
    }

    /**
     * Returns the raw JSON response body exactly as received from the API.
     * This can be used to access newly added fields before the SDK models them.
     */
    public JsonNode getRawJson() {
        return rawJson;
    }

    public void setRawJson(JsonNode rawJson) {
        this.rawJson = rawJson;
    }

    @Override
    public String toString() {
        return "RiskIntelligenceRetrieveResponse{success=" + success + ", data=" + data + ", error=" + error + "}";
    }
}
