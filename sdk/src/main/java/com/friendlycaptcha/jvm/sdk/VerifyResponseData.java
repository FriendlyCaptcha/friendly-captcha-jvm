package com.friendlycaptcha.jvm.sdk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyResponseData {
    private VerifyResponseChallengeData challenge;

    @JsonProperty("event_id")
    private String eventId;

    public VerifyResponseChallengeData getChallenge() {
        return challenge;
    }

    public String getEventId() {
        return eventId;
    }

    public String toString() {
        return "VerifyResponseData{challenge=" + this.challenge + ", eventId=" + this.eventId + "}";
    }
}