package com.example.charlotte.myapplication;

/**
 * Created by charlotte on 08.05.16.
 */
public class AccessTokenRequest {


    /*grant_type
	Required. Set it to “refresh_token”.
refresh_token
	Required. The refresh token returned from the authorization code exchange.

The header of this POST request must contain the following parameter:
Header parameter
	Value
Authorization
	Required. Base 64 encoded string that contains the client ID and client secret key. The field must have the format: Authorization: Basic <base64 encoded client_id:client_secret>*/

    String grant_type;

    public AccessTokenRequest(String grant_type, String refresh_token, String client_id, String client_secret) {
        this.grant_type = grant_type;
        this.refresh_token = refresh_token;
        this.client_id = client_id;
        this.client_secret = client_secret;
    }

    String refresh_token;
    String client_id;
    String client_secret;


    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

}
