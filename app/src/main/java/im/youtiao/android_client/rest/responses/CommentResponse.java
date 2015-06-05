package im.youtiao.android_client.rest.responses;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class CommentResponse {
    public String id;
    public String text;

    @JsonProperty("created_at")
    public Date createdAt;

    @JsonProperty("updated_at")
    public Date updatedAt;

    @JsonProperty("created_by")
    public UserResponse createdBy;
}
