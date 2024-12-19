package com.friendlycaptcha.jvm.sdk;

import java.time.Instant;

public class VerifyResponseChallengeData {
    /**
     * Timestamp when the captcha challenge was completed.
     */
    private Instant timestamp;

    /**
     * Origin where the challenge happened. This can be empty if unknown.
     */
    private String origin;

    /**
     * Gets the timestamp when the captcha challenge was completed.
     * @return
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the origin where the challenge happened.
     * @return
     */
    public String getOrigin() {
        return origin;
    }
}
