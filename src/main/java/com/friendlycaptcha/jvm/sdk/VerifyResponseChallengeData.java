package com.friendlycaptcha.jvm.sdk;

import java.time.Instant;

public class VerifyResponseChallengeData {
    private Instant timestamp;
    private String origin;

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getOrigin() {
        return origin;
    }
}
