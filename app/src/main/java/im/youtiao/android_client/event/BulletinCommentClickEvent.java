package im.youtiao.android_client.event;

import im.youtiao.android_client.model.Bulletin;

public class BulletinCommentClickEvent {
    public Bulletin bulletin;

    public BulletinCommentClickEvent(Bulletin bulletin) {
        this.bulletin = bulletin;
    }
}
