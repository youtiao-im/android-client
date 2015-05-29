package im.youtiao.android_client.greendao;

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
    android:name="im.youtiao.android_client.greendao.LibraryProvider"
    android:authorities="im.youtiao.android_client.greendao.provider"/>
*/

public class LibraryProvider extends RoboContentProvider {

    public static final String AUTHORITY = "im.youtiao.android_client.greendao.provider";
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        UserHelper.addURI(URI_MATCHER);
        ChannelHelper.addURI(URI_MATCHER);
        FeedHelper.addURI(URI_MATCHER);
        CommentHelper.addURI(URI_MATCHER);
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
            case UserHelper.USER_DIR:
                id = getDatabase().insert(UserHelper.TABLENAME, null, values);
                path = UserHelper.BASE_PATH + "/" + id;
                break;
            case ChannelHelper.CHANNEL_DIR:
                id = getDatabase().insert(ChannelHelper.TABLENAME, null, values);
                path = ChannelHelper.BASE_PATH + "/" + id;
                break;
            case FeedHelper.FEED_DIR:
                id = getDatabase().insert(FeedHelper.TABLENAME, null, values);
                path = FeedHelper.BASE_PATH + "/" + id;
                break;
            case CommentHelper.COMMENT_DIR:
                id = getDatabase().insert(CommentHelper.TABLENAME, null, values);
                path = CommentHelper.BASE_PATH + "/" + id;
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
                case UserHelper.USER_DIR:
                    rowsDeleted = db.delete(UserHelper.TABLENAME, selection, selectionArgs);
                    break;
                case UserHelper.USER_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsDeleted = db.delete(UserHelper.TABLENAME,
                            UserHelper.PK + "=" + id, null);
                    } else {
                        rowsDeleted = db.delete(UserHelper.TABLENAME,
                            UserHelper.PK + "=" + id + " and " + selection, selectionArgs);
                    }
                    break;
                case ChannelHelper.CHANNEL_DIR:
                    rowsDeleted = db.delete(ChannelHelper.TABLENAME, selection, selectionArgs);
                    break;
                case ChannelHelper.CHANNEL_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsDeleted = db.delete(ChannelHelper.TABLENAME,
                            ChannelHelper.PK + "=" + id, null);
                    } else {
                        rowsDeleted = db.delete(ChannelHelper.TABLENAME,
                            ChannelHelper.PK + "=" + id + " and " + selection, selectionArgs);
                    }
                    break;
                case FeedHelper.FEED_DIR:
                    rowsDeleted = db.delete(FeedHelper.TABLENAME, selection, selectionArgs);
                    break;
                case FeedHelper.FEED_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsDeleted = db.delete(FeedHelper.TABLENAME,
                            FeedHelper.PK + "=" + id, null);
                    } else {
                        rowsDeleted = db.delete(FeedHelper.TABLENAME,
                            FeedHelper.PK + "=" + id + " and " + selection, selectionArgs);
                    }
                    break;
                case CommentHelper.COMMENT_DIR:
                    rowsDeleted = db.delete(CommentHelper.TABLENAME, selection, selectionArgs);
                    break;
                case CommentHelper.COMMENT_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsDeleted = db.delete(CommentHelper.TABLENAME,
                            CommentHelper.PK + "=" + id, null);
                    } else {
                        rowsDeleted = db.delete(CommentHelper.TABLENAME,
                            CommentHelper.PK + "=" + id + " and " + selection, selectionArgs);
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
                case UserHelper.USER_DIR:
                    rowsUpdated = db.update(UserHelper.TABLENAME, values, selection, selectionArgs);
                    break;
                case UserHelper.USER_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsUpdated = db.update(UserHelper.TABLENAME,
                            values, UserHelper.PK + "=" + id, null);
                    } else {
                        rowsUpdated = db.update(UserHelper.TABLENAME,
                            values, UserHelper.PK + "=" + id + " and "
                            + selection, selectionArgs);
                    }
                    break;
                case ChannelHelper.CHANNEL_DIR:
                    rowsUpdated = db.update(ChannelHelper.TABLENAME, values, selection, selectionArgs);
                    break;
                case ChannelHelper.CHANNEL_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsUpdated = db.update(ChannelHelper.TABLENAME,
                            values, ChannelHelper.PK + "=" + id, null);
                    } else {
                        rowsUpdated = db.update(ChannelHelper.TABLENAME,
                            values, ChannelHelper.PK + "=" + id + " and "
                            + selection, selectionArgs);
                    }
                    break;
                case FeedHelper.FEED_DIR:
                    rowsUpdated = db.update(FeedHelper.TABLENAME, values, selection, selectionArgs);
                    break;
                case FeedHelper.FEED_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsUpdated = db.update(FeedHelper.TABLENAME,
                            values, FeedHelper.PK + "=" + id, null);
                    } else {
                        rowsUpdated = db.update(FeedHelper.TABLENAME,
                            values, FeedHelper.PK + "=" + id + " and "
                            + selection, selectionArgs);
                    }
                    break;
                case CommentHelper.COMMENT_DIR:
                    rowsUpdated = db.update(CommentHelper.TABLENAME, values, selection, selectionArgs);
                    break;
                case CommentHelper.COMMENT_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsUpdated = db.update(CommentHelper.TABLENAME,
                            values, CommentHelper.PK + "=" + id, null);
                    } else {
                        rowsUpdated = db.update(CommentHelper.TABLENAME,
                            values, CommentHelper.PK + "=" + id + " and "
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
            case UserHelper.USER_DIR:
                queryBuilder.setTables(UserHelper.TABLENAME);
                break;
            case UserHelper.USER_ID:
                queryBuilder.setTables(UserHelper.TABLENAME);
                queryBuilder.appendWhere(UserHelper.PK + "=" + uri.getLastPathSegment());
                break;
            case ChannelHelper.CHANNEL_DIR:
                queryBuilder.setTables(ChannelHelper.TABLENAME);
                break;
            case ChannelHelper.CHANNEL_ID:
                queryBuilder.setTables(ChannelHelper.TABLENAME);
                queryBuilder.appendWhere(ChannelHelper.PK + "=" + uri.getLastPathSegment());
                break;
            case FeedHelper.FEED_DIR:
                queryBuilder.setTables(FeedHelper.TABLENAME);
                break;
            case FeedHelper.FEED_ID:
                queryBuilder.setTables(FeedHelper.TABLENAME);
                queryBuilder.appendWhere(FeedHelper.PK + "=" + uri.getLastPathSegment());
                break;
            case CommentHelper.COMMENT_DIR:
                queryBuilder.setTables(CommentHelper.TABLENAME);
                break;
            case CommentHelper.COMMENT_ID:
                queryBuilder.setTables(CommentHelper.TABLENAME);
                queryBuilder.appendWhere(CommentHelper.PK + "=" + uri.getLastPathSegment());
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
            case UserHelper.USER_DIR:
                return UserHelper.CONTENT_TYPE;
            case UserHelper.USER_ID:
                return UserHelper.CONTENT_ITEM_TYPE;
            case ChannelHelper.CHANNEL_DIR:
                return ChannelHelper.CONTENT_TYPE;
            case ChannelHelper.CHANNEL_ID:
                return ChannelHelper.CONTENT_ITEM_TYPE;
            case FeedHelper.FEED_DIR:
                return FeedHelper.CONTENT_TYPE;
            case FeedHelper.FEED_ID:
                return FeedHelper.CONTENT_ITEM_TYPE;
            case CommentHelper.COMMENT_DIR:
                return CommentHelper.CONTENT_TYPE;
            case CommentHelper.COMMENT_ID:
                return CommentHelper.CONTENT_ITEM_TYPE;
            default :
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
