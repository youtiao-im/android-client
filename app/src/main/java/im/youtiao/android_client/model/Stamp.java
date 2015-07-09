package im.youtiao.android_client.model;


import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class Stamp implements java.io.Serializable {

    public enum Mark {
        CHECK,
        CROSS,
    }

    public String type;

    public String id;

    @JsonProperty("bulletin_id")
    public String bulletinId;

    public String symbol;

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
