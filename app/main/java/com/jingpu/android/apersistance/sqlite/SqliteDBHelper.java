package com.jingpu.android.apersistance.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jing Pu on 2015/10/1.
 */
    // version number to updrade darabase version
    public class SqliteDBHelper extends SQLiteOpenHelper {
    // each time if you add, edit table, you need to change the version number.
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "aptest.db";

    private static SqliteDBHelper instance;

    public static synchronized SqliteDBHelper getHelper(Context context)
    {
        if (instance == null)
            instance = new SqliteDBHelper(context);

        return instance;
    }

    private SqliteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}