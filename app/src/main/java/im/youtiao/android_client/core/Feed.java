package im.youtiao.android_client.core;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;
import java.util.List;

public class Feed {

    @JsonProperty("id")
    private String id;

    @JsonProperty("channel_id")
    private String channelId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("creator_id")
    private String creatorId;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;

    @JsonProperty("feedbacks")
    private List<Feedback> feedbacks;

    @JsonProperty("comments")
    private List<Comment> comments;

    @JsonProperty("attachments")
    private List<Attachment> attachments;

    @JsonProperty("is_starred")
    private boolean isStarred;

    @JsonProperty("is_checked")
    private boolean isChecked;

    @JsonProperty("is_crossed")
    private boolean isCrossed;

    @JsonProperty("is_questioned")
    private boolean isQuestioned;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isCrossed() {
        return isCrossed;
    }

    public void setIsCrossed(boolean isCrossed) {
        this.isCrossed = isCrossed;
    }

    public boolean isQuestioned() {
        return isQuestioned;
    }

    public void setIsQuestioned(boolean isQuestioned) {
        this.isQuestioned = isQuestioned;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return createdAt;
    }

    public void setUpdatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Attachment> getAttachments() {
        return this.attachments;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setIsStarred(boolean isStar) {
        this.isStarred = isStar;
    }
}
