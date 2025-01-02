package com.friendlycaptcha.jvm.sdk;

public class VerifyResponseData {
    private VerifyResponseChallengeData challenge;

    public VerifyResponseChallengeData getChallenge() {
        return challenge;
    }

    public String toString() {
        return "VerifyResponseData{challenge=" + this.challenge + "}";
    }
}