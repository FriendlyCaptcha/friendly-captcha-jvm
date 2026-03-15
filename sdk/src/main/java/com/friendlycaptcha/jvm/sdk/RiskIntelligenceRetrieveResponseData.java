package com.friendlycaptcha.jvm.sdk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RiskIntelligenceRetrieveResponseData {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("risk_intelligence")
    private JsonNode riskIntelligence;

    private RiskIntelligenceTokenData token;

    public String getEventId() {
        return eventId;
    }

    public JsonNode getRiskIntelligence() {
        return riskIntelligence;
    }

    public RiskIntelligenceTokenData getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "RiskIntelligenceRetrieveResponseData{eventId='" + eventId + "', riskIntelligence="
                + riskIntelligence + ", token=" + token + "}";
    }
}
