package com.friendlycaptcha.jvm.sdk;

/**
 * VerifyResponse represents the response body for the /api/v2/captcha/verify endpoint.
 * See the documentation at https://developer.friendlycaptcha.com/docs/v2/api/siteverify.
 */
public class VerifyResponse {
    private boolean success;
    private VerifyResponseData data;
    private VerifyResponseError error;

    /**
     * Returns the value of the `success` field.
     * @return
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the value of the `data` field. This is only present if the `success` field is true.
     * @return
     */
    public VerifyResponseData getData() {
        return data;
    }

    /**
     * Returns the value of the `error` field. This is only present if the `success` field is false.
     * @return
     */
    public VerifyResponseError getError() {
        return error;
    }

    public String toString() {
        return "VerifyResponse{success=" + this.success + ", data=" + this.data + ", error=" + this.error + "}";
    }
}