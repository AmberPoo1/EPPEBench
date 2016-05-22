package com.jingpu.android.apersistance.greendao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jingpu.android.apersistance.greendao.model.DaoMaster;
import com.jingpu.android.apersistance.greendao.model.DaoSession;

/**
 * Created by Jing Pu on 2015/11/17.
 */
public class GreenDaoDBHelper extends DaoMaster.DevOpenHelper {
    // version number to updrade darabase version
    // each time if you add, edit table, you need to change the version number.

    // Database Name
    private static final String DATABASE_NAME = "aptest.db";

    private static GreenDaoDBHelper instance;
    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    public static synchronized GreenDaoDBHelper getHelper(Context context)
    {
        if (null == instance)
            instance = new GreenDaoDBHelper(context);

        return instance;
    }

    public GreenDaoDBHelper(Context context) {
        super(context, DATABASE_NAME, null);
    }

    public static synchronized SQLiteDatabase getDatabase() {
        if (null == db) {
            db = instance.getWritableDatabase();
        }

        return db;
    }

    public static synchronized DaoMaster getDaoMaster() {
        if (null == daoMaster) {
            daoMaster = new DaoMaster(getDatabase());
        }

        return daoMaster;
    }

    public static synchronized DaoSession getDaoSession() {
        if (null == daoSession) {
            daoSession = new DaoMaster(getDatabase()).newSession();
        }

        return daoSession;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
