package com.jingpu.android.apersistance.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.jingpu.android.apersistance.greendao.model.DaoMaster;
import com.jingpu.android.apersistance.greendao.model.DaoSession;

/**
 * Created by Jing Pu on 2015/10/17.
 */
public class GreenDaoAgent {
    private GreenDaoDBHelper dbHelper;

    public GreenDaoAgent(Context context) {
        dbHelper = GreenDaoDBHelper.getHelper(context);
    }

    public SQLiteDatabase getDatabase() {
        return dbHelper.getDatabase();
    }

    public DaoMaster getDaoMaster() {
        return dbHelper.getDaoMaster();
    }

    public DaoSession getDaoSession() {
        return dbHelper.getDaoSession();
    }

    protected  void closeHelper() throws SQLiteException {
        dbHelper.close();
        dbHelper = null;
    }

    public void createSchemaAndConstraints() throws SQLiteException {
        dropTables();
        getDaoMaster().createAllTables(getDatabase(), true);
    }

    public void dropTables() throws SQLiteException {
        getDaoMaster().dropAllTables(getDatabase(), true);
    }
}
