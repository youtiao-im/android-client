package im.youtiao.android_client.dao;

import android.database.Cursor;
import android.net.Uri;
import android.content.UriMatcher;
import android.content.ContentResolver;

public final class BulletinHelper {

    private BulletinHelper() { }

    public static final String ID = BulletinDao.Properties.Id.columnName;
    public static final String SERVERID = BulletinDao.Properties.ServerId.columnName;
    public static final String JSON = BulletinDao.Properties.Json.columnName;

    public static final String TABLENAME = BulletinDao.TABLENAME;
    public static final String PK = BulletinDao.Properties.Id.columnName;

    public static final int BULLETIN_DIR = 3;
    public static final int BULLETIN_ID = 4;

    public static final String BASE_PATH = "bulletin";
    public static final Uri CONTENT_URI = Uri.parse("content://" + LibraryProvider.AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_PATH;


    public static void addURI(UriMatcher sURIMatcher) {
        sURIMatcher.addURI(LibraryProvider.AUTHORITY, BASE_PATH, BULLETIN_DIR);
        sURIMatcher.addURI(LibraryProvider.AUTHORITY, BASE_PATH + "/#", BULLETIN_ID);
    }

    private static final String[] PROJECTION = {
        ID,
        SERVERID,
        JSON
    };

    public static String[] getProjection() {
        return PROJECTION;
    }

    public static Bulletin fromCursor(Cursor data) {
        final Bulletin entity = new Bulletin();
        entity.setId(data.getLong(data.getColumnIndex(ID)));
        entity.setServerId(data.getString(data.getColumnIndex(SERVERID)));
        entity.setJson(data.getString(data.getColumnIndex(JSON)));
        return entity;
    }
}
