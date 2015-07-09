package im.youtiao.android_client.event;

import im.youtiao.android_client.model.User;

public class AccountModifyEvent {
    public User user;

    public AccountModifyEvent(User user) {
        this.user = user;
    }
}
