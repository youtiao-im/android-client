package im.youtiao.android_client.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class Bulletin implements java.io.Serializable {
    public String type;

    public String id;

    @JsonProperty("group_id")
    public String groupId;

    public String text;

    @JsonProperty("created_by_id")
    public String createdById;

    @JsonProperty("created_by_type")
    public String createdByType;

    @JsonProperty("checks_count")
    public String checksCount;

    @JsonProperty("crosses_count")
    public String crossesCount;

    @JsonProperty("comments_count")
    public String comments_count;

    @JsonProperty("created_at")
    public String createdAt;

    @JsonProperty("updated_at")
    public String updatedAt;

    public Group group;

    @JsonProperty("created_by")
    public User createdBy;

    @JsonProperty("current_stamp")
    public Stamp stamp;
}
