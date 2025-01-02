package com.friendlycaptcha.jvm.sdk;

/**
 * Configuration options when creating a new `FriendlyCaptchaClient`.
 */
public class FriendlyCaptchaClientOptions {
    private String sitekey = null;
    private String apiKey;
    private String apiEndpoint = "global";
    private boolean strict = false;

    private int timeout = 20_000;

    /**
     * Gets the sitekey.
     * 
     * @return the sitekey.
     */
    public String getSitekey() {
        return sitekey;
    }

    /**
     * Sets the sitekey.
     * 
     * @param sitekey the sitekey to set.
     */
    public FriendlyCaptchaClientOptions setSitekey(String sitekey) {
        this.sitekey = sitekey;
        return this;
    }

    /**
     * Gets the API key.
     * 
     * @return the API key.
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Sets the API key.
     * 
     * @param apiKey the API key to set.
     */
    public FriendlyCaptchaClientOptions setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Gets the API endpoint.
     * 
     * @return the endpoint.
     */
    public String getApiEndpoint() {
        return apiEndpoint;
    }

    /**
     * Sets the API endpoint.
     * 
     * @param apiEndpoint the API endpoint to set.
     */
    public FriendlyCaptchaClientOptions setApiEndpoint(String apiEndpoint)  {
        this.apiEndpoint = apiEndpoint;
        return this;
    }

    /**
     * Gets the strict mode setting.
     * 
     * @return true if strict mode is enabled, false otherwise.
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * Sets the strict mode setting.
     * 
     * @param strict true to enable strict mode, false to disable.
     */
    public FriendlyCaptchaClientOptions setStrict(boolean strict) {
        this.strict = strict;
        return this;
    }

    /**
     * Gets the timeout.
     * 
     * @return the timeout.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout for requests in milliseconds.
     * 
     * @param timeout the timeout to set.
     */
    public FriendlyCaptchaClientOptions setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Validate throws an exception if the options are invalid.
     */
    public void validate() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("api key is required");
        }

        if (timeout < 0) {
            throw new IllegalArgumentException("timeout must be a positive integer");
        }
    }

}