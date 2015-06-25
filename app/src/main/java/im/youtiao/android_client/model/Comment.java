package im.youtiao.android_client.model;


import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class Comment implements java.io.Serializable {
    public String type;

    public String id;

    @JsonProperty("bulletin_id")
    public String bulletinId;

    public String text;

    @JsonProperty("created_by_type")
    public String createdByType;

    @JsonProperty("created_by_id")
    public String createdById;

    @JsonProperty("created_at")
    public String createdAt;

    @JsonProperty("updated_at")
    public String updatedAt;

    @JsonProperty("created_by")
    public User createdBy;
}
