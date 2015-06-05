package im.youtiao.android_client.greendao;

import android.database.Cursor;
import android.net.Uri;
import android.content.UriMatcher;
import android.content.ContentResolver;

public final class FeedHelper {

    private FeedHelper() { }

    public static final String ID = FeedDao.Properties.Id.columnName;
    public static final String SERVERID = FeedDao.Properties.ServerId.columnName;
    public static final String TEXT = FeedDao.Properties.Text.columnName;
    public static final String SYMBOL = FeedDao.Properties.Symbol.columnName;
    public static final String ISSTARRED = FeedDao.Properties.IsStarred.columnName;
    public static final String CREATEDAT = FeedDao.Properties.CreatedAt.columnName;
    public static final String CHANNELID = FeedDao.Properties.ChannelId.columnName;
    public static final String CREATEDBY = FeedDao.Properties.CreatedBy.columnName;

    public static final String TABLENAME = FeedDao.TABLENAME;
    public static final String PK = FeedDao.Properties.Id.columnName;

    public static final int FEED_DIR = 6;
    public static final int FEED_ID = 7;

    public static final String BASE_PATH = "feed";
    public static final Uri CONTENT_URI = Uri.parse("content://" + LibraryProvider.AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_PATH;


    public static void addURI(UriMatcher sURIMatcher) {
        sURIMatcher.addURI(LibraryProvider.AUTHORITY, BASE_PATH, FEED_DIR);
        sURIMatcher.addURI(LibraryProvider.AUTHORITY, BASE_PATH + "/#", FEED_ID);
    }

    private static final String[] PROJECTION = {
        ID,
        SERVERID,
        TEXT,
        SYMBOL,
        ISSTARRED,
        CREATEDAT,
        CHANNELID,
        CREATEDBY
    };

    public static String[] getProjection() {
        return PROJECTION;
    }

    public static String DEFAULT_SORT_ORDER = CREATEDBY + " DESC";

    public static Feed fromCursor(Cursor data) {
        final Feed entity = new Feed();
        entity.setId(data.getLong(data.getColumnIndex(ID)));
        entity.setServerId(data.getString(data.getColumnIndex(SERVERID)));
        entity.setText(data.getString(data.getColumnIndex(TEXT)));
        entity.setSymbol(data.getString(data.getColumnIndex(SYMBOL)));
        entity.setIsStarred(data.getInt(data.getColumnIndex(ISSTARRED)) > 0);
        entity.setCreatedAt(new java.util.Date(data.getInt(data.getColumnIndex(CREATEDAT))));
        entity.setChannelId(data.getLong(data.getColumnIndex(CHANNELID)));
        entity.setCreatedBy(data.getLong(data.getColumnIndex(CREATEDBY)));
        return entity;
    }
}
