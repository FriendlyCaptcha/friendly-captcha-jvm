package com.friendlycaptcha.jvm.sdk;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerifyResponseError {

    @JsonProperty("error_code")
    private String errorCode;
    private String detail;

    public String getErrorCode() {
        return errorCode;
    }

    public String getDetail() {
        return detail;
    }
}

