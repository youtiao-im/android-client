package im.youtiao.android_client.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements java.io.Serializable {
    public String type;

    public String id;

    public String email;

    public String name;

    @JsonProperty("avatar_id")
    public String avatarId;

    public String avatar;

    @JsonProperty("created_at")
    public String createdAt;

    @JsonProperty("updated_at")
    public String updatedAt;
}
