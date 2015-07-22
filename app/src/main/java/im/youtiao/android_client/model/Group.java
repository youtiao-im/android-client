package im.youtiao.android_client.model;


import org.codehaus.jackson.annotate.JsonProperty;

import java.lang.reflect.Member;
import java.util.Date;

public class Group implements java.io.Serializable {
    public enum Role {
        OWNER,
        ADMIN,
        MEMBER
    }

    public String type;

    public String id;

    public String name;

    public String code;

    @JsonProperty("memberships_count")
    public Integer membershipsCount;

    @JsonProperty("created_at")
    public String createdAt;

    @JsonProperty("updated_at")
    public String updatedAt;

    @JsonProperty("current_membership")
    public Membership membership;
}
