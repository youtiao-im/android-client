package im.youtiao.android_client.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class ChannelContentProvider extends ContentProvider {
    private static final String TAG = ChannelContentProvider.class
            .getCanonicalName();
    public static final String AUTHORITY = ChannelContentProvider.class
            .getCanonicalName();
    public static final String CHANNEL_TABLE_NAME = "channels";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SERVER_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_STATUS_FLAG = "status_flag";

    public static final String GROUP_BY_ROLE = "role";

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + CHANNEL_TABLE_NAME);

    public static final String DEFAULT_SORT_ORDER = "_id ASC";

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.youtiao.channel";
    public static final String CONTENT_TYPE_ID = "vnd.android.cursor.item/vnd.youtiao.channel";

    private static HashMap<String, String> projectionMap;
    private static final UriMatcher sUriMatcher;

    private DatabaseHelper dbHelper;

    private static final int CHANNEL = 1;
    private static final int CHANNEL_ID = 2;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, CHANNEL_TABLE_NAME, CHANNEL);
        sUriMatcher.addURI(AUTHORITY, CHANNEL_TABLE_NAME + "/#", CHANNEL_ID);

        projectionMap = new HashMap<String, String>();
        projectionMap.put(COLUMN_ID, COLUMN_ID);
        projectionMap.put(COLUMN_SERVER_ID, COLUMN_SERVER_ID);
        projectionMap.put(COLUMN_NAME, COLUMN_NAME);
        projectionMap.put(COLUMN_ROLE, COLUMN_ROLE);
        projectionMap.put(COLUMN_STATUS_FLAG, COLUMN_STATUS_FLAG);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String groupBy, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case CHANNEL:
                qb.setTables(CHANNEL_TABLE_NAME);
                qb.setProjectionMap(projectionMap);
                break;
            case CHANNEL_ID:
                qb.setTables(CHANNEL_TABLE_NAME);
                qb.setProjectionMap(projectionMap);
                qb.appendWhere(COLUMN_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new RuntimeException("Unknown URI");
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, groupBy,
                null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return this.query(uri, projection, selection, selectionArgs, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CHANNEL:
                return CONTENT_TYPE;
            case CHANNEL_ID:
                return CONTENT_TYPE_ID;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        String table = null;
        String nullableCol = null;

        switch (sUriMatcher.match(uri)) {
            case CHANNEL:
                table = CHANNEL_TABLE_NAME;
                nullableCol = COLUMN_NAME;
                break;
            default:
                new RuntimeException("Invalid URI for inserting: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(table, nullableCol, values);

        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;

        switch (sUriMatcher.match(uri)) {
            case CHANNEL:
                count = db.delete(CHANNEL_TABLE_NAME, selection, selectionArgs);
                break;
            case CHANNEL_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(CHANNEL_TABLE_NAME, COLUMN_ID
                        + "="
                        + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")"
                        : ""), selectionArgs);
                break;
            default:
                throw new RuntimeException("Unkown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case CHANNEL:
                count = db.update(CHANNEL_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CHANNEL_ID:
                count = db.update(
                        CHANNEL_TABLE_NAME,
                        values,
                        COLUMN_ID
                                + "="
                                + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                + ")" : ""), selectionArgs);
                break;
            default:
                throw new RuntimeException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
