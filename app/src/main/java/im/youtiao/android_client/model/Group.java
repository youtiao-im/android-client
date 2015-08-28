package im.youtiao.android_client.model;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.lang.reflect.Member;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group implements java.io.Serializable {
    public String type;

    public String id;

    public String name;

    public String code;

    @JsonProperty("memberships_count")
    public int membershipsCount;

    @JsonProperty("created_at")
    public String createdAt;

    @JsonProperty("updated_at")
    public String updatedAt;

    @JsonProperty("current_membership")
    public Membership membership;
}
