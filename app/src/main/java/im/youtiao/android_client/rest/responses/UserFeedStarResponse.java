package im.youtiao.android_client.rest.responses;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class UserFeedStarResponse {
    @JsonProperty("created_at")
    public Date createdAt;

    @JsonProperty("updated_at")
    public Date updatedAt;

    @JsonProperty("feed")
    public FeedResponse feedResponse;
}
