package im.youtiao.android_client.greendao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table USER.
 */
public class User implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String serverId;
    /** Not-null value. */
    private String email;
    private java.util.Date createdAt;
    private java.util.Date updatedAt;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, String serverId, String email, java.util.Date createdAt, java.util.Date updatedAt) {
        this.id = id;
        this.serverId = serverId;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getServerId() {
        return serverId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    /** Not-null value. */
    public String getEmail() {
        return email;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setEmail(String email) {
        this.email = email;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    public java.util.Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.util.Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
