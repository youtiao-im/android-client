package im.youtiao.android_client.event;

import im.youtiao.android_client.model.Bulletin;

public class BulletinStampEvent {

    public Bulletin bulletin;
    public String symbol;

    public BulletinStampEvent(Bulletin bulletin, String symbol) {
        this.bulletin = bulletin;
        this.symbol = symbol;
    }
}
