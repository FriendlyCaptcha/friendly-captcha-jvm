package com.friendlycaptcha.jvm.sdk;

import java.util.Objects;

/**
 * VerifyResult is a wrapper around the response of an /api/v2/captcha/siteverify request.
 * 
 * The main methods are `shouldAccept` and `wasAbleToVerify`, the first one you should use to determine if the user's
 * request should be accepted, the second one to determine if the request was able to be verified. If that returns
 * false, you should alert yourself.
 */
public class VerifyResult {
    /** Whether the request was made in strict mode. */
    private boolean strict;

    /** The HTTP status code of the response. */
    public int status;

    /** The response body. */
    public VerifyResponse response;

    /** The exception that was thrown, if any. */
    public Exception exception;

    /**
     * `null` if the puzzle could be verified, in other words we got a 200 response.
     * 
     * Otherwise this will be set to one of the error codes in `ErrorCodes`:
     * * `ErrorCodes.REQUEST_FAILED`
     * * `ErrorCodes.FAILED_DUE_TO_CLIENT_ERROR` (see response.error for more details, your API key might be wrong).
     * * `ErrorCodes.FAILED_TO_ENCODE_REQUEST`
     * * `ErrorCodes.FAILED_TO_DECODE_RESPONSE`
     */
    public String errorCode = null;

    public VerifyResult(boolean strict) {
        this.strict = strict;
    }

    /**
     * @return whether the `strict` option was set to true on the client. In `strict` mode this will only return `true`
     * if the request was successful (e.g. verification could happen), and the challenge was solved successfully.
     * 
     * By default, `strict` is set to `false`, which means that the request will be accepted if the challenge could
     * not be verified (also called *fail open*).
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * Determines if the user's request should be accepted.
     * 
     * @return true if the request should be accepted, false otherwise.
     */
    public boolean shouldAccept() {
        if (wasAbleToVerify()) {
            if (isEncodeError()) {
                return false;
            }
            return response.isSuccess();
        }
        if (errorCode != null) {
            if (strict) {
                return false;
            }
            if (errorCode.equals(ErrorCode.REQUEST_FAILED) || errorCode.equals(ErrorCode.FAILED_DUE_TO_CLIENT_ERROR) || errorCode.equals(ErrorCode.FAILED_TO_DECODE_RESPONSE)) {
                return true;
            }
            return false;
        }

        throw new RuntimeException("Implementation error in friendly-captcha-java-sdk shouldAccept: error should never be null if success is false. " + this);
    }

    /**
     * Determines if the user's request should be rejected.
     * 
     * @return true if the request should be rejected, false otherwise.
     */
    public boolean shouldReject() {
        return !shouldAccept();
    }

    /**
     * Was unable to encode the captcha response. This means the captcha response was invalid and should never be accepted.
     * 
     * @return true if there was an encoding error, false otherwise.
     */
    public boolean isEncodeError() {
        return Objects.equals(errorCode, ErrorCode.FAILED_TO_ENCODE_REQUEST);
    }

    /**
     * Something went wrong making the request to the Friendly Captcha API, perhaps there is a network connection issue?
     * 
     * @return true if there was a request error, false otherwise.
     */
    public boolean isRequestError() {
        return Objects.equals(errorCode, ErrorCode.REQUEST_FAILED);
    }

    /**
     * Something went wrong decoding the response from the Friendly Captcha API.
     * 
     * @return true if there was a decoding error, false otherwise.
     */
    public boolean isDecodeError() {
        return Objects.equals(errorCode, ErrorCode.FAILED_TO_DECODE_RESPONSE);
    }

    /**
     * Something went wrong on the client side, this generally means your configuration is wrong.
     * Check your secrets (API key) and sitekey.
     * 
     * See `response.error` for more details.
     * 
     * @return true if there was a client error, false otherwise.
     */
    public boolean isClientError() {
        return Objects.equals(errorCode, ErrorCode.FAILED_DUE_TO_CLIENT_ERROR);
    }

    /**
     * Get the response as was sent from the server.
     * This can be null if the request to the API could not be made successfully.
     * 
     * @return the response from the server, or null if the request failed.
     */
    public VerifyResponse getResponse() {
        return response;
    }

    /**
     * Get the error field from the response as was returned by the API, or null if the field is not present.
     * 
     * @return the error field from the response, or null if not present.
     */
    public VerifyResponseError getResponseError() {
        if (response == null) {
            return null;
        }
        return response.getError();
    }

    /**
     * Get the error code.
     * 
     * @return the error code, or null if not present.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Set the error code.
     * 
     * @param errorCode the error code to set.
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Set the HTTP status code of the response.
     * 
     * @param status the status code to set.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Set the response body.
     * 
     * @param response the response to set.
     */
    public void setResponse(VerifyResponse response) {
        this.response = response;
    }

    /**
     * Whether the request to verify the captcha was completed. In other words: the API responded with status 200.
     * If this is false, you should notify yourself and check `errorCode` to see what is wrong.
     * 
     * @return true if the request was able to be verified, false otherwise.
     */
    public boolean wasAbleToVerify() {
        if (isEncodeError()) {
            // Despite not being able to make the request, if we are not even able to encode the captcha response
            // we can be certain it's invalid and were thus able to verify it without even making a request.
            return true;
        }
        return status == 200 && !isRequestError() && !isDecodeError();
    }

    /**
     * Get the exception that was thrown, if any.
     * 
     * @return the exception, or null if none was thrown.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Set the exception that was thrown, if any.
     * 
     * @param exception the exception to set.
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }

}