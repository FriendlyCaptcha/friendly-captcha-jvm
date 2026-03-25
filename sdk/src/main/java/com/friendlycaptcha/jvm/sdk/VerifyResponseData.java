package com.friendlycaptcha.jvm.sdk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyResponseData {
    private VerifyResponseChallengeData challenge;

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("risk_intelligence")
    private RiskIntelligenceData riskIntelligence;

    public VerifyResponseChallengeData getChallenge() {
        return challenge;
    }

    public String getEventId() {
        return eventId;
    }

    public RiskIntelligenceData getRiskIntelligence() {
        return riskIntelligence;
    }

    public String toString() {
        return "VerifyResponseData{challenge=" + this.challenge + ", eventId=" + this.eventId
                + ", riskIntelligence=" + this.riskIntelligence + "}";
    }
}
