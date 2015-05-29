package im.youtiao.android_client.greendao;

import android.database.Cursor;
import android.net.Uri;
import android.content.UriMatcher;
import android.content.ContentResolver;

public final class UserHelper {

    private UserHelper() { }

    public static final String ID = UserDao.Properties.Id.columnName;
    public static final String SERVERID = UserDao.Properties.ServerId.columnName;
    public static final String EMAIL = UserDao.Properties.Email.columnName;
    public static final String CREATEDAT = UserDao.Properties.CreatedAt.columnName;
    public static final String UPDATEDAT = UserDao.Properties.UpdatedAt.columnName;

    public static final String TABLENAME = UserDao.TABLENAME;
    public static final String PK = UserDao.Properties.Id.columnName;

    public static final int USER_DIR = 0;
    public static final int USER_ID = 1;

    public static final String BASE_PATH = "user";
    public static final Uri CONTENT_URI = Uri.parse("content://" + LibraryProvider.AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_PATH;


    public static void addURI(UriMatcher sURIMatcher) {
        sURIMatcher.addURI(LibraryProvider.AUTHORITY, BASE_PATH, USER_DIR);
        sURIMatcher.addURI(LibraryProvider.AUTHORITY, BASE_PATH + "/#", USER_ID);
    }

    private static final String[] PROJECTION = {
        ID,
        SERVERID,
        EMAIL,
        CREATEDAT,
        UPDATEDAT
    };

    public static String[] getProjection() {
        return PROJECTION;
    }

    public static User fromCursor(Cursor data) {
        final User entity = new User();
        entity.setId(data.getLong(data.getColumnIndex(ID)));
        entity.setServerId(data.getString(data.getColumnIndex(SERVERID)));
        entity.setEmail(data.getString(data.getColumnIndex(EMAIL)));
        entity.setCreatedAt(new java.util.Date(data.getInt(data.getColumnIndex(CREATEDAT))));
        entity.setUpdatedAt(new java.util.Date(data.getInt(data.getColumnIndex(UPDATEDAT))));
        return entity;
    }
}
