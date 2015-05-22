package im.youtiao.android_client.model;


import org.codehaus.jackson.annotate.JsonProperty;

public class Channel {

    public enum Role {
        OWNER,
        MEMBER
    }

    private long id;

    private String title;

    private String role;

    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }

    @Override
    public String toString() {
        return title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean equals(Channel object) {
        return this.id == object.getId();
    }
}
