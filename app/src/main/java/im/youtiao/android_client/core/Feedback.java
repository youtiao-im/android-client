package im.youtiao.android_client.core;

import org.codehaus.jackson.annotate.JsonProperty;

public class Feedback {

    @JsonProperty("sticker")
    private String sticker;

    @JsonProperty("creator_id")
    private String creatorId;

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}
