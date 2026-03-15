package com.friendlycaptcha.jvm.sdk;

import java.util.Objects;

/**
 * RiskIntelligenceRetrieveResult is a wrapper around the response of a
 * /api/v2/riskIntelligence/retrieve request.
 */
public class RiskIntelligenceRetrieveResult {
    /** The HTTP status code of the response. */
    public int status;

    /** The response body. */
    public RiskIntelligenceRetrieveResponse response;

    /** The exception that was thrown, if any. */
    public Exception exception;

    /**
     * `null` if retrieval could be completed, in other words we got a 200 response.
     *
     * Otherwise this will be set to one of the internal error codes in `ErrorCode`.
     */
    public String errorCode = null;

    public boolean isRequestError() {
        return Objects.equals(errorCode, ErrorCode.REQUEST_FAILED);
    }

    public boolean isEncodeError() {
        return Objects.equals(errorCode, ErrorCode.FAILED_TO_ENCODE_REQUEST);
    }

    public boolean isDecodeError() {
        return Objects.equals(errorCode, ErrorCode.FAILED_TO_DECODE_RESPONSE);
    }

    public boolean isClientError() {
        return Objects.equals(errorCode, ErrorCode.FAILED_DUE_TO_CLIENT_ERROR);
    }

    public RiskIntelligenceRetrieveResponse getResponse() {
        return response;
    }

    public RiskIntelligenceRetrieveResponseError getResponseError() {
        if (response == null) {
            return null;
        }
        return response.getError();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setResponse(RiskIntelligenceRetrieveResponse response) {
        this.response = response;
    }

    public boolean wasAbleToRetrieve() {
        if (isEncodeError()) {
            // If the request could not be encoded we can still treat this as resolved:
            // the provided token is unusable and retrieval cannot succeed with it.
            return true;
        }
        return status == 200 && !isRequestError() && !isDecodeError();
    }

    /**
     * Returns true if the retrieval request was successful and the token is valid.
     *
     * A token can be invalid even when retrieval itself succeeded (for example, token expired).
     */
    public boolean isValid() {
        if (!wasAbleToRetrieve()) {
            return false;
        }
        return response != null && response.isSuccess();
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
