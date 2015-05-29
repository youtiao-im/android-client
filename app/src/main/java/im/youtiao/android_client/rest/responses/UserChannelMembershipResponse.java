package im.youtiao.android_client.rest.responses;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class UserChannelMembershipResponse {
    @JsonProperty("role")
    public String role;

    @JsonProperty("created_at")
    public Date createdAt;

    @JsonProperty("updated_at")
    public Date updatedAt;

    @JsonProperty("channel")
    public ChannelResponse channelResponse;
}
