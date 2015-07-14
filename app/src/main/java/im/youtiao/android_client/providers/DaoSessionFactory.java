package im.youtiao.android_client.providers;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import im.youtiao.android_client.AccountDescriptor;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.dao.DaoMaster;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.util.Log;


public class DaoSessionFactory {
    private static final String TAG = DaoSessionFactory.class.getCanonicalName();
    private static DaoSession instance;

    public static DaoSession getDaoSession(){
        //Log.e(TAG, "getDaoSession");
        if (instance == null) {
            Log.e(TAG, " DaoSession is null");
        }
        return instance;
    }

    public static void setDaoSession(Context context){
        //Log.e(TAG, "setDaoSession");
        final DaoMaster daoMaster;
        SQLiteDatabase db;
        YTApplication application = (YTApplication)context.getApplicationContext();
        AccountDescriptor account = application.getCurrentAccount();
        String dbName = "youtiao-db-5";
        if (account != null) {
            dbName = "youtiao-" + account.getEmail();
            //Log.e(TAG, "dbName = " + dbName);
        } else {
            //Log.e(TAG, "current account is null:" + context.getClass().toString());
            instance = null;
            return;
        }
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, dbName, null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        instance = daoMaster.newSession();
    }
}
