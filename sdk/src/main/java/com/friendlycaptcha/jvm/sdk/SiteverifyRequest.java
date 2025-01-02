package com.friendlycaptcha.jvm.sdk;

/**
 * SiteverifyRequest represents the request body for the /api/v2/captcha/siteverify endpoint.
 */
public class SiteverifyRequest {
    private String response;
    private String sitekey;

    /**
     * Gets the captcha response token.
     * 
     * @return the captcha response token.
     */
    public String getResponse() {
        return response;
    }

    /**
     * Sets the captcha response token.
     * 
     * @param response the captcha response token.
     */
    public void setResponse(String response) {
        this.response = response;
    }

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
     * @param sitekey the sitekey.
     */
    public void setSitekey(String sitekey) {
        this.sitekey = sitekey;
    }
}