package im.youtiao.android_client.greendao;

import android.database.Cursor;
import android.net.Uri;
import android.content.UriMatcher;
import android.content.ContentResolver;

public final class CommentHelper {

    private CommentHelper() { }

    public static final String ID = CommentDao.Properties.Id.columnName;
    public static final String SERVERID = CommentDao.Properties.ServerId.columnName;
    public static final String TEXT = CommentDao.Properties.Text.columnName;
    public static final String UPDATEDAT = CommentDao.Properties.UpdatedAt.columnName;
    public static final String CREATEDBY = CommentDao.Properties.CreatedBy.columnName;
    public static final String FEEDID = CommentDao.Properties.FeedId.columnName;
    public static final String CREATEDAT = CommentDao.Properties.CreatedAt.columnName;

    public static final String TABLENAME = CommentDao.TABLENAME;
    public static final String PK = CommentDao.Properties.Id.columnName;

    public static final int COMMENT_DIR = 9;
    public static final int COMMENT_ID = 10;

    public static final String BASE_PATH = "comment";
    public static final Uri CONTENT_URI = Uri.parse("content://" + LibraryProvider.AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_PATH;


    public static void addURI(UriMatcher sURIMatcher) {
        sURIMatcher.addURI(LibraryProvider.AUTHORITY, BASE_PATH, COMMENT_DIR);
        sURIMatcher.addURI(LibraryProvider.AUTHORITY, BASE_PATH + "/#", COMMENT_ID);
    }

    private static final String[] PROJECTION = {
        ID,
        SERVERID,
        TEXT,
        UPDATEDAT,
        CREATEDBY,
        FEEDID,
        CREATEDAT
    };

    public static String[] getProjection() {
        return PROJECTION;
    }

    public static String DEFAULT_SORT_ORDER = CREATEDBY + " DESC";

    public static Comment fromCursor(Cursor data) {
        final Comment entity = new Comment();
        entity.setId(data.getLong(data.getColumnIndex(ID)));
        entity.setServerId(data.getString(data.getColumnIndex(SERVERID)));
        entity.setText(data.getString(data.getColumnIndex(TEXT)));
        entity.setUpdatedAt(new java.util.Date(data.getInt(data.getColumnIndex(UPDATEDAT))));
        entity.setCreatedBy(data.getLong(data.getColumnIndex(CREATEDBY)));
        entity.setFeedId(data.getLong(data.getColumnIndex(FEEDID)));
        entity.setCreatedAt(new java.util.Date(data.getInt(data.getColumnIndex(CREATEDAT))));
        return entity;
    }
}
