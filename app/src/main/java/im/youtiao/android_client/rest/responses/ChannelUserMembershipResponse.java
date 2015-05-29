package im.youtiao.android_client.rest.responses;


import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

import im.youtiao.android_client.core.User;

public class ChannelUserMembershipResponse {
    public String role;

    @JsonProperty("created_at")
    public Date createdAt;

    @JsonProperty("updated_at")
    public Date updatedAt;

    @JsonProperty("created_by")
    public User createdBy;
}
