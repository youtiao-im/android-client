package im.youtiao.android_client.dao;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import im.youtiao.android_client.content_providers.ChannelContentProvider;
import im.youtiao.android_client.content_providers.StatusFlag;
import im.youtiao.android_client.model.Channel;

public class ChannelDAO {

    private static final ChannelDAO instance = new ChannelDAO();

    private ChannelDAO() {
    }

    public static ChannelDAO getInstance() {
        return instance;
    }

    public void addNewChannel(ContentResolver contentResolver, Channel item) {
        addNewChannel(contentResolver, item, StatusFlag.ADD);
    }

    public void addNewChannel(ContentResolver contentResolver, Channel list,
                              int flag) {
        ContentValues contentValue = getChannelContentValues(list, flag);
        contentResolver.insert(ChannelContentProvider.CONTENT_URI,
                contentValue);
    }

    private ContentValues getChannelContentValues(Channel channel, int flag) {
        ContentValues cv = new ContentValues();
        cv.put(ChannelContentProvider.COLUMN_SERVER_ID, channel.getId());
        cv.put(ChannelContentProvider.COLUMN_NAME, channel.getName());
        cv.put(ChannelContentProvider.COLUMN_ROLE, channel.getRole());
        cv.put(ChannelContentProvider.COLUMN_STATUS_FLAG, flag);
        return cv;
    }

    public List<Channel> getCleanChannels(ContentResolver contentResolver) {
        return getTodosWithSelection(contentResolver,
                ChannelContentProvider.COLUMN_STATUS_FLAG + " = "
                        + StatusFlag.CLEAN);
    }

    private List<Channel> getTodosWithSelection(
            ContentResolver contentResolver, String selection) {
        Cursor cursor = contentResolver.query(
                ChannelContentProvider.CONTENT_URI, null, selection, null, null);

        List<Channel> list = new ArrayList<Channel>();

        while (cursor.moveToNext()) {
            int localId = cursor.getInt(cursor
                    .getColumnIndexOrThrow(ChannelContentProvider.COLUMN_ID));
            int serverId = cursor.getInt(cursor
                    .getColumnIndexOrThrow(ChannelContentProvider.COLUMN_SERVER_ID));
            String name = cursor.getString(cursor
                    .getColumnIndexOrThrow(ChannelContentProvider.COLUMN_NAME));
            int status = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(ChannelContentProvider.COLUMN_STATUS_FLAG));

            Channel currentChannel = new Channel();

            if (status == StatusFlag.ADD) {
                currentChannel.setId(localId);
            } else {
                currentChannel.setId(serverId);
            }
            currentChannel.setName(name);
            currentChannel.setStatus(status);
            list.add(currentChannel);
        }
        cursor.close();
        return list;
    }

    public int deleteChannel(ContentResolver contentResolver, Long id) {
        int ret = 0;

        /* Using the local id */
        int status = getTodoStatus(contentResolver, id);

        switch (status) {
            case StatusFlag.ADD:
                ret = contentResolver.delete(ChannelContentProvider.CONTENT_URI,
                        ChannelContentProvider.COLUMN_ID + "=" + id, null);
                break;
            case StatusFlag.MOD:
            case StatusFlag.CLEAN:
                ContentValues cv = new ContentValues();
                cv.put(ChannelContentProvider.COLUMN_STATUS_FLAG, StatusFlag.DELETE);
                contentResolver.update(ChannelContentProvider.CONTENT_URI, cv,
                        ChannelContentProvider.COLUMN_ID + "=" + id, null);
                break;
            default:
                throw new RuntimeException(
                        "Tried to delete a todo with invalid status");
        }

        return ret;
    }

    public int getTodoStatus(ContentResolver contentResolver, Long id) {
        Cursor c = contentResolver.query(ChannelContentProvider.CONTENT_URI,
                null, ChannelContentProvider.COLUMN_ID + "=" + id, null, null);

        int status = 0;

        try {
            if (c.moveToNext()) {
                status = c
                        .getInt(c
                                .getColumnIndexOrThrow(ChannelContentProvider.COLUMN_STATUS_FLAG));

            } else {
                throw new RuntimeException(
                        "Tried to delete a non existent todo");
            }
        } finally {
            c.close();
        }

        return status;
    }

    public int forcedDeleteChannel(ContentResolver contentResolver, Long id) {
        return contentResolver.delete(ChannelContentProvider.CONTENT_URI,
                ChannelContentProvider.COLUMN_SERVER_ID + "=" + id, null);
    }

    public Channel isTodoInDb(ContentResolver contentResolver, Long serverId) {
        Channel channel = null;

        Cursor cursor = contentResolver.query(
                ChannelContentProvider.CONTENT_URI, null,
                ChannelContentProvider.COLUMN_SERVER_ID + "=" + serverId, null,
                null);

        if (cursor.moveToNext()) {
            String name = cursor.getString(cursor
                    .getColumnIndexOrThrow(ChannelContentProvider.COLUMN_NAME));
            int status = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(ChannelContentProvider.COLUMN_STATUS_FLAG));

            channel = new Channel();
            channel.setId(serverId);
            channel.setName(name);
            channel.setStatus(status);
        }

        cursor.close();
        return channel;
    }

    public void modifyChannelFromServer(ContentResolver contentResolver,
                                        Channel list) {
        ContentValues cv = new ContentValues();
        cv.put(ChannelContentProvider.COLUMN_NAME, list.getName());
        cv.put(ChannelContentProvider.COLUMN_STATUS_FLAG, StatusFlag.CLEAN);

        contentResolver
                .update(ChannelContentProvider.CONTENT_URI, cv,
                        ChannelContentProvider.COLUMN_SERVER_ID + "=" + list.getId(),
                        null);

    }

    public List<Channel> getDirtyList(ContentResolver mContentResolver) {
        return getTodosWithSelection(mContentResolver,
                ChannelContentProvider.COLUMN_STATUS_FLAG + " != "
                        + StatusFlag.CLEAN);
    }

    public void clearAdd(ContentResolver contentResolver, long id,
                         Channel serverChannel) {
        ContentValues cv = getChannelContentValues(serverChannel,
                StatusFlag.CLEAN);
        contentResolver.update(ChannelContentProvider.CONTENT_URI, cv,
                ChannelContentProvider.COLUMN_ID + "=" + id, null);

    }

    public int deleteChannelForced(ContentResolver contentResolver, long id) {
        return contentResolver.delete(ChannelContentProvider.CONTENT_URI,
                ChannelContentProvider.COLUMN_SERVER_ID + "=" + id, null);
    }
}
