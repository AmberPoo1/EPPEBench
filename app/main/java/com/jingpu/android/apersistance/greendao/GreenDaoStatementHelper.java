package com.jingpu.android.apersistance.greendao;

import android.database.sqlite.SQLiteException;

/**
 * Created by Jing Pu on 2015/11/17.
 */
public class GreenDaoStatementHelper {
    protected final GreenDaoAgent ga;

    GreenDaoStatementHelper(GreenDaoAgent ga) throws SQLiteException {
        this.ga = ga;
    }
}
