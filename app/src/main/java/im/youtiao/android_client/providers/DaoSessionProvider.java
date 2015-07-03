package im.youtiao.android_client.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.inject.Inject;
import com.google.inject.Provider;

import im.youtiao.android_client.dao.DaoMaster;
import im.youtiao.android_client.dao.DaoSession;

public class DaoSessionProvider implements Provider<DaoSession> {
    @Inject
    private Context mContext;

    @Override public DaoSession get() {
        final DaoMaster daoMaster;
        SQLiteDatabase db;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, "youtiao-db-4", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }
}
