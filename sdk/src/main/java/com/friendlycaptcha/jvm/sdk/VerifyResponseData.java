package com.friendlycaptcha.jvm.sdk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyResponseData {
    private VerifyResponseChallengeData challenge;

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("risk_intelligence")
    private JsonNode riskIntelligence;

    public VerifyResponseChallengeData getChallenge() {
        return challenge;
    }

    public String getEventId() {
        return eventId;
    }

    public JsonNode getRiskIntelligence() {
        return riskIntelligence;
    }

    public String toString() {
        return "VerifyResponseData{challenge=" + this.challenge + ", eventId=" + this.eventId
                + ", riskIntelligence=" + this.riskIntelligence + "}";
    }
}
