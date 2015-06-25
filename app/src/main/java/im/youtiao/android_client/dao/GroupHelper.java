package im.youtiao.android_client.dao;

import android.database.Cursor;
import android.net.Uri;
import android.content.UriMatcher;
import android.content.ContentResolver;

public final class GroupHelper {

    private GroupHelper() { }

    public static final String ID = GroupDao.Properties.Id.columnName;
    public static final String SERVERID = GroupDao.Properties.ServerId.columnName;
    public static final String ROLE = GroupDao.Properties.Role.columnName;
    public static final String JSON = GroupDao.Properties.Json.columnName;

    public static final String TABLENAME = GroupDao.TABLENAME;
    public static final String PK = GroupDao.Properties.Id.columnName;

    public static final int GROUP_DIR = 0;
    public static final int GROUP_ID = 1;

    public static final String BASE_PATH = "group";
    public static final Uri CONTENT_URI = Uri.parse("content://" + LibraryProvider.AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_PATH;


    public static void addURI(UriMatcher sURIMatcher) {
        sURIMatcher.addURI(LibraryProvider.AUTHORITY, BASE_PATH, GROUP_DIR);
        sURIMatcher.addURI(LibraryProvider.AUTHORITY, BASE_PATH + "/#", GROUP_ID);
    }

    private static final String[] PROJECTION = {
        ID,
        SERVERID,
        ROLE,
        JSON
    };

    public static String[] getProjection() {
        return PROJECTION;
    }

    public static Group fromCursor(Cursor data) {
        final Group entity = new Group();
        entity.setId(data.getLong(data.getColumnIndex(ID)));
        entity.setServerId(data.getString(data.getColumnIndex(SERVERID)));
        entity.setRole(data.getString(data.getColumnIndex(ROLE)));
        entity.setJson(data.getString(data.getColumnIndex(JSON)));
        return entity;
    }
}
