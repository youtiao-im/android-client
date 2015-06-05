package im.youtiao.android_client.greendao;

import android.database.Cursor;
import android.net.Uri;
import android.content.UriMatcher;
import android.content.ContentResolver;

public final class ChannelHelper {

    private ChannelHelper() { }

    public static final String ID = ChannelDao.Properties.Id.columnName;
    public static final String SERVERID = ChannelDao.Properties.ServerId.columnName;
    public static final String NAME = ChannelDao.Properties.Name.columnName;
    public static final String ROLE = ChannelDao.Properties.Role.columnName;
    public static final String USERSCOUNT = ChannelDao.Properties.UsersCount.columnName;
    public static final String CREATEDAT = ChannelDao.Properties.CreatedAt.columnName;
    public static final String UPDATEDAT = ChannelDao.Properties.UpdatedAt.columnName;
    public static final String USERID = ChannelDao.Properties.UserId.columnName;

    public static final String TABLENAME = ChannelDao.TABLENAME;
    public static final String PK = ChannelDao.Properties.Id.columnName;

    public static final int CHANNEL_DIR = 3;
    public static final int CHANNEL_ID = 4;

    public static final String BASE_PATH = "channel";
    public static final Uri CONTENT_URI = Uri.parse("content://" + LibraryProvider.AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_PATH;


    public static void addURI(UriMatcher sURIMatcher) {
        sURIMatcher.addURI(LibraryProvider.AUTHORITY, BASE_PATH, CHANNEL_DIR);
        sURIMatcher.addURI(LibraryProvider.AUTHORITY, BASE_PATH + "/#", CHANNEL_ID);
    }

    private static final String[] PROJECTION = {
        ID,
        SERVERID,
        NAME,
        ROLE,
        USERSCOUNT,
        CREATEDAT,
        UPDATEDAT,
        USERID
    };

    public static String[] getProjection() {
        return PROJECTION;
    }

    public static String DEFAULT_SORT_ORDER = ROLE + " DESC";

    public static Channel fromCursor(Cursor data) {
        final Channel entity = new Channel();
        entity.setId(data.getLong(data.getColumnIndex(ID)));
        entity.setServerId(data.getString(data.getColumnIndex(SERVERID)));
        entity.setName(data.getString(data.getColumnIndex(NAME)));
        entity.setRole(data.getString(data.getColumnIndex(ROLE)));
        entity.setUsersCount(data.getInt(data.getColumnIndex(USERSCOUNT)));
        entity.setCreatedAt(new java.util.Date(data.getInt(data.getColumnIndex(CREATEDAT))));
        entity.setUpdatedAt(new java.util.Date(data.getInt(data.getColumnIndex(UPDATEDAT))));
        entity.setUserId(data.getLong(data.getColumnIndex(USERID)));
        return entity;
    }
}
