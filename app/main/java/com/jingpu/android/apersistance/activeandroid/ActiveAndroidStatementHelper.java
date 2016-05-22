package com.jingpu.android.apersistance.activeandroid;

import android.database.sqlite.SQLiteException;

/**
 * Created by Jing Pu on 2016/1/4.
 */
public class ActiveAndroidStatementHelper {
    protected final ActiveAndroidAgent aaa;

    ActiveAndroidStatementHelper(ActiveAndroidAgent aaa) throws SQLiteException {
        this.aaa = aaa;
    }
}
