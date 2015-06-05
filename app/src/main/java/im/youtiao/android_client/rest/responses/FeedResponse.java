package im.youtiao.android_client.rest.responses;


import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;


public class FeedResponse {
    public String id;

    public String text;

    @JsonProperty("checks_count")
    public int checksCount;

    @JsonProperty("crosses_count")
    public int crossedCount;

    @JsonProperty("questions_count")
    public int questionsCount;

    @JsonProperty("comments_count")
    public int commentsCount;

    @JsonProperty("created_at")
    public Date createdAt;

    @JsonProperty("updated_at")
    public Date updatedAt;

    @JsonProperty("mark")
    public MarkResponse markResponse;

    @JsonProperty("star")
    public StarResponse starResponse;

    @JsonProperty("created_by")
    public UserResponse createdBy;
}
