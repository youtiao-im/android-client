package im.youtiao.android_client.rest.responses;


import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class ChannelResponse {
    @JsonProperty("id")
    public String id;

    @JsonProperty("name")
    public String name;

    @JsonProperty("memberships_count")
    public Integer membershipsCount;

    @JsonProperty("created_at")
    public Date createdAt;

    @JsonProperty("updated_at")
    public Date updatedAt;

    @JsonProperty("created_by")
    public UserResponse createdBy;
}
