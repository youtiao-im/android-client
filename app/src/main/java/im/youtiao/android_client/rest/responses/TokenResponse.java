package im.youtiao.android_client.rest.responses;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class TokenResponse {
    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("token_type")
    public String tokenType;

    @JsonProperty("expires_in")
    public Date expiresIn;

    @JsonProperty("created_at")
    public Date createdAt;
}
