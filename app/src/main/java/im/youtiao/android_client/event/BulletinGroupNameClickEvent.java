package im.youtiao.android_client.event;

import im.youtiao.android_client.model.Bulletin;

public class BulletinGroupNameClickEvent {
    public Bulletin bulletin;

    public BulletinGroupNameClickEvent(Bulletin bulletin) {
        this.bulletin = bulletin;
    }
}
