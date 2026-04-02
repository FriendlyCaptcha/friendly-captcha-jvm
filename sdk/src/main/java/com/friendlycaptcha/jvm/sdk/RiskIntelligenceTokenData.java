package com.friendlycaptcha.jvm.sdk;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RiskIntelligenceTokenData {
    private Instant timestamp;

    @JsonProperty("expires_at")
    private Instant expiresAt;

    @JsonProperty("num_uses")
    private long numUses;

    private String origin;

    public Instant getTimestamp() {
        return timestamp;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public long getNumUses() {
        return numUses;
    }

    public String getOrigin() {
        return origin;
    }

    @Override
    public String toString() {
        return "RiskIntelligenceTokenData{timestamp=" + timestamp
                + ", expiresAt=" + expiresAt
                + ", numUses=" + numUses
                + ", origin='" + origin + "'}";
    }
}
