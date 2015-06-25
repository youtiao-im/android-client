package im.youtiao.android_client.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table BULLETINS.
 */
public class Bulletin {

    private Long id;
    /** Not-null value. */
    private String serverId;
    private String json;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Bulletin() {
    }

    public Bulletin(Long id) {
        this.id = id;
    }

    public Bulletin(Long id, String serverId, String json) {
        this.id = id;
        this.serverId = serverId;
        this.json = json;
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

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
