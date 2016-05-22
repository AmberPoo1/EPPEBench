package com.jingpu.android.apersistance.sugarorm;

import android.database.sqlite.SQLiteException;

/**
 * Created by Jing Pu on 2016/1/17.
 */
public class SugarOrmStatementHelper {
    protected final SugarOrmAgent soa;

    SugarOrmStatementHelper(SugarOrmAgent soa) throws SQLiteException {
        this.soa = soa;
    }
}
