package com.friendlycaptcha.jvm.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * RiskIntelligenceRetrieveRequest represents the request body for the
 * /api/v2/riskIntelligence/retrieve endpoint.
 */
public class RiskIntelligenceRetrieveRequest {
    private String token;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sitekey;

    /**
     * Gets the risk intelligence token.
     *
     * @return the risk intelligence token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the risk intelligence token.
     *
     * @param token the risk intelligence token.
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets the optional sitekey.
     *
     * @return the sitekey.
     */
    public String getSitekey() {
        return sitekey;
    }

    /**
     * Sets the optional sitekey.
     *
     * @param sitekey the sitekey.
     */
    public void setSitekey(String sitekey) {
        this.sitekey = sitekey;
    }
}
