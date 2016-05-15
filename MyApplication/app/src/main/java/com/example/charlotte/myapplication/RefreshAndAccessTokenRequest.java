package com.example.charlotte.myapplication;

/**
 * Created by charlotte on 05.05.16.
 */
public class RefreshAndAccessTokenRequest {

    public String getGrant_type() {
        return grant_type;
    }

    public String getCode() {
        return code;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    String grant_type="authorization_code";
    String code;

    public RefreshAndAccessTokenRequest(String code, String client_id, String client_secret, String redirect_uri) {
        this.code = code;
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.redirect_uri = redirect_uri;
    }

    String client_id;

    public String getClient_secret() {
        return client_secret;
    }

    public String getClient_id() {
        return client_id;
    }

    String client_secret;



    String redirect_uri;


}
