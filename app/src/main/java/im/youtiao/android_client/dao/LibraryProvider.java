package im.youtiao.android_client.dao;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.google.inject.Inject;
import de.greenrobot.dao.DaoLog;
import roboguice.content.RoboContentProvider;

/* Copy this code snippet into your AndroidManifest.xml inside the
<application> element:

<provider
    android:name="im.youtiao.android_client.dao.LibraryProvider"
    android:authorities="im.youtiao.android_client.dao.provider"/>
*/

public class LibraryProvider extends RoboContentProvider {

    public static final String AUTHORITY = "im.youtiao.android_client.dao.provider";
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        GroupHelper.addURI(URI_MATCHER);
        BulletinHelper.addURI(URI_MATCHER);
    }

    /**
    * This must be set from outside, it's recommended to do this inside your Application object.
    * Subject to change (static isn't nice).
    */
    @Inject
    private DaoSession daoSession;

    @Override
    public boolean onCreate() {
        DaoLog.d("Content Provider started: " + AUTHORITY);
        return super.onCreate();
    }

    protected SQLiteDatabase getDatabase() {
        if (daoSession == null) {
            throw new IllegalStateException("DaoSession must be set during content provider is active");
        }
        return daoSession.getDatabase();
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = URI_MATCHER.match(uri);
        long id;
        String path;
        switch (uriType) {
            case GroupHelper.GROUP_DIR:
                id = getDatabase().insert(GroupHelper.TABLENAME, null, values);
                path = GroupHelper.BASE_PATH + "/" + id;
                break;
            case BulletinHelper.BULLETIN_DIR:
                id = getDatabase().insert(BulletinHelper.TABLENAME, null, values);
                path = BulletinHelper.BASE_PATH + "/" + id;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return Uri.parse(path);
        }

        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            int uriType = URI_MATCHER.match(uri);
            SQLiteDatabase db = getDatabase();
            int rowsDeleted;
            String id;
            switch (uriType) {
                case GroupHelper.GROUP_DIR:
                    rowsDeleted = db.delete(GroupHelper.TABLENAME, selection, selectionArgs);
                    break;
                case GroupHelper.GROUP_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsDeleted = db.delete(GroupHelper.TABLENAME,
                            GroupHelper.PK + "=" + id, null);
                    } else {
                        rowsDeleted = db.delete(GroupHelper.TABLENAME,
                            GroupHelper.PK + "=" + id + " and " + selection, selectionArgs);
                    }
                    break;
                case BulletinHelper.BULLETIN_DIR:
                    rowsDeleted = db.delete(BulletinHelper.TABLENAME, selection, selectionArgs);
                    break;
                case BulletinHelper.BULLETIN_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsDeleted = db.delete(BulletinHelper.TABLENAME,
                            BulletinHelper.PK + "=" + id, null);
                    } else {
                        rowsDeleted = db.delete(BulletinHelper.TABLENAME,
                            BulletinHelper.PK + "=" + id + " and " + selection, selectionArgs);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return rowsDeleted;
        }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
        String[] selectionArgs) {
            int uriType = URI_MATCHER.match(uri);
            SQLiteDatabase db = getDatabase();
            int rowsUpdated;
            String id;
            switch (uriType) {
                case GroupHelper.GROUP_DIR:
                    rowsUpdated = db.update(GroupHelper.TABLENAME, values, selection, selectionArgs);
                    break;
                case GroupHelper.GROUP_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsUpdated = db.update(GroupHelper.TABLENAME,
                            values, GroupHelper.PK + "=" + id, null);
                    } else {
                        rowsUpdated = db.update(GroupHelper.TABLENAME,
                            values, GroupHelper.PK + "=" + id + " and "
                            + selection, selectionArgs);
                    }
                    break;
                case BulletinHelper.BULLETIN_DIR:
                    rowsUpdated = db.update(BulletinHelper.TABLENAME, values, selection, selectionArgs);
                    break;
                case BulletinHelper.BULLETIN_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsUpdated = db.update(BulletinHelper.TABLENAME,
                            values, BulletinHelper.PK + "=" + id, null);
                    } else {
                        rowsUpdated = db.update(BulletinHelper.TABLENAME,
                            values, BulletinHelper.PK + "=" + id + " and "
                            + selection, selectionArgs);
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return rowsUpdated;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = URI_MATCHER.match(uri);
        switch (uriType) {
            case GroupHelper.GROUP_DIR:
                queryBuilder.setTables(GroupHelper.TABLENAME);
                break;
            case GroupHelper.GROUP_ID:
                queryBuilder.setTables(GroupHelper.TABLENAME);
                queryBuilder.appendWhere(GroupHelper.PK + "=" + uri.getLastPathSegment());
                break;
            case BulletinHelper.BULLETIN_DIR:
                queryBuilder.setTables(BulletinHelper.TABLENAME);
                break;
            case BulletinHelper.BULLETIN_ID:
                queryBuilder.setTables(BulletinHelper.TABLENAME);
                queryBuilder.appendWhere(BulletinHelper.PK + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = getDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
        selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public final String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case GroupHelper.GROUP_DIR:
                return GroupHelper.CONTENT_TYPE;
            case GroupHelper.GROUP_ID:
                return GroupHelper.CONTENT_ITEM_TYPE;
            case BulletinHelper.BULLETIN_DIR:
                return BulletinHelper.CONTENT_TYPE;
            case BulletinHelper.BULLETIN_ID:
                return BulletinHelper.CONTENT_ITEM_TYPE;
            default :
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
