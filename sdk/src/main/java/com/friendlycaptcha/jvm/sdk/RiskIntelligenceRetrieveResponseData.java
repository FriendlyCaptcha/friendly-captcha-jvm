package com.friendlycaptcha.jvm.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RiskIntelligenceRetrieveResponseData {

    @JsonProperty("event_id")
    private String eventId;

    @JsonIgnore
    private RiskIntelligenceData riskIntelligence;

    @JsonIgnore
    private JsonNode riskIntelligenceRaw;

    private RiskIntelligenceTokenData token;

    @JsonSetter("risk_intelligence")
    public void setRiskIntelligenceRaw(JsonNode riskIntelligenceRaw) {
        this.riskIntelligenceRaw = riskIntelligenceRaw;
        if (riskIntelligenceRaw == null || riskIntelligenceRaw.isNull()) {
            this.riskIntelligence = null;
            return;
        }

        this.riskIntelligence = ObjectMapperSingleton.getInstance()
                .convertValue(riskIntelligenceRaw, RiskIntelligenceData.class);
    }

    public String getEventId() {
        return eventId;
    }

    @JsonProperty("risk_intelligence")
    public JsonNode getRiskIntelligenceRaw() {
        return riskIntelligenceRaw;
    }

    public RiskIntelligenceData getRiskIntelligence() {
        return riskIntelligence;
    }

    public RiskIntelligenceTokenData getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "RiskIntelligenceRetrieveResponseData{eventId='" + eventId + "', riskIntelligence="
                + riskIntelligence + ", token=" + token + ", riskIntelligenceRaw=" + riskIntelligenceRaw + "}";
    }
}
