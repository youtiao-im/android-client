package im.youtiao.android_client.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
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
    public int checksCount;

    @JsonProperty("crosses_count")
    public int crossesCount;

    @JsonProperty("eyes_count")
    public int eyesCount;

    @JsonProperty("comments_count")
    public int comments_count;

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
