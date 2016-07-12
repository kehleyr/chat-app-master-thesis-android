package com.app.charlotte.myapplication.spotify;

/**
 * Created by charlotte on 05.05.16.
 */
public class RefreshAndAccessToken {
	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	String access_token, token_type,  refresh_token;
    int expires_in;
    /*

    access_token
	string	An access token that can be provided in subsequent calls, for example to Spotify Web API services.
token_type
	string	How the access token may be used: always "Bearer".
expires_in
	int	The time period (in seconds) for which the access token is valid.
refresh_token
	string	A token that can be sent to the Spotify Accounts service in place of an authorization code. (When the access code expires, send a POST request to the Accounts service /api/token endpoint, but use this code in place of an authorization code. A new access token will be returned. A new refresh token might be returned too.)
     */
}
