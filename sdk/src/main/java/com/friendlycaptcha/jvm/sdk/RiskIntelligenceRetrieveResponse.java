package com.friendlycaptcha.jvm.sdk;

/**
 * RiskIntelligenceRetrieveResponse represents the response body for the
 * /api/v2/riskIntelligence/retrieve endpoint.
 */
public class RiskIntelligenceRetrieveResponse {
    private boolean success;
    private RiskIntelligenceRetrieveResponseData data;
    private RiskIntelligenceRetrieveResponseError error;

    public boolean isSuccess() {
        return success;
    }

    public RiskIntelligenceRetrieveResponseData getData() {
        return data;
    }

    public RiskIntelligenceRetrieveResponseError getError() {
        return error;
    }

    @Override
    public String toString() {
        return "RiskIntelligenceRetrieveResponse{success=" + success + ", data=" + data + ", error=" + error + "}";
    }
}
