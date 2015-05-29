package im.youtiao.android_client.core;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class Token {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Date expiresIn;

    @JsonProperty("created_at")
    private Date createdAt;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String access_token) {
        this.accessToken = access_token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String token_type) {
        this.tokenType = token_type;
    }

    public Date getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Date expires_in) {
        this.expiresIn = expires_in;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date created_at) {
        this.createdAt = created_at;
    }
}
