package im.youtiao.android_client.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Membership implements java.io.Serializable {

    public enum Role {
        OWNER,
        MEMBER
    }

    public String type;

    public String id;

    @JsonProperty("group_id")
    public String groupId;

    @JsonProperty("user_id")
    public String userId;

    public String role;

    @JsonProperty("created_at")
    public String createdAt;

    @JsonProperty("updated_at")
    public String updatedAt;

    public User user;
}
