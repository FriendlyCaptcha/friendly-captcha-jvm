package com.friendlycaptcha.jvm.sdk;

public class VerifyResponse {
    private boolean success;
    private VerifyResponseData data;
    private VerifyResponseError error;

    public boolean isSuccess() {
        return success;
    }

    public VerifyResponseData getData() {
        return data;
    }

    public VerifyResponseError getError() {
        return error;
    }
}