package im.youtiao.android_client.core;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Wang on 5/13/2015.
 */
public class Attachment {

    @JsonProperty("title")
    private String title;

    @JsonProperty("link")
    private String link;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
