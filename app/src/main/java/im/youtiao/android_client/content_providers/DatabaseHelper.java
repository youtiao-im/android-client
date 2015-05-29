package im.youtiao.android_client.content_providers;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class
            .getCanonicalName();
    public static final String DATABASE_NAME = "youtiao.db";
    private static final int DATABASE_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ChannelContentProvider.CHANNEL_TABLE_NAME
                + " (" + ChannelContentProvider.COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ChannelContentProvider.COLUMN_SERVER_ID + " INTEGER,"
                + ChannelContentProvider.COLUMN_NAME + " LONGTEXT,"
                + ChannelContentProvider.COLUMN_ROLE + " LONGTEXT,"
                + ChannelContentProvider.COLUMN_STATUS_FLAG + " INTEGER" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS "
                + ChannelContentProvider.CHANNEL_TABLE_NAME);
        onCreate(db);
    }
}
