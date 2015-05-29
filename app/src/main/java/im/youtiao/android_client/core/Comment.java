package im.youtiao.android_client.core;

import org.codehaus.jackson.annotate.JsonProperty;

public class Comment {

    @JsonProperty("id")
    private String id;

    @JsonProperty("text")
    private String text;

    @JsonProperty("creator_id")
    private String createdId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedId() {
        return createdId;
    }

    public void setCreatedId(String createdId) {
        this.createdId = createdId;
    }
}
