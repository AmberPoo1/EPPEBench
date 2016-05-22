package com.jingpu.android.apersistance.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Created by Jing Pu on 2015/10/1.
 */
public class SqliteStatementHelper {
    protected final SqliteAgent sa;

    SqliteStatementHelper(SqliteAgent sa) throws SQLiteException {
        this.sa = sa;
    }

    /**
     *  get Writable Database
     * @return
     * @throws SQLiteException
     */
    protected SQLiteDatabase getWritableDatabase() throws SQLiteException {
        if (null == this.sa) {
            throw new NullPointerException();
        }

        return sa.getWritableDatabase();
    }

    /**
     * getReadable Database
     * @return
     * @throws SQLiteException
     */
    protected SQLiteDatabase getReadableDatabase() throws SQLiteException {
        if (null == this.sa) {
            throw new NullPointerException();
        }
        return sa.getReadableDatabase();
    }

    /**
     * Begin transaction
     * @param db
     * @throws SQLiteException
     */
    protected void beginTransaction(SQLiteDatabase db) throws SQLiteException {
        if (db != null) {
            db.beginTransaction();
        }
    }

    /**
     * Marks the current transaction as successful. Do not do any more database work between
     * calling this and calling endTransaction. Do as little non-database work as possible in that
     * situation too. If any errors are encountered between this and endTransaction the transaction
     * will still be committed.
     *
     * @param db
     * @throws SQLiteException
     */
    protected  void setTransactionSuccessful (SQLiteDatabase db) throws SQLiteException {
        if (db != null) {
            db.setTransactionSuccessful();
        }
    }


    /**
     * End  transaction
     * @param db
     * @throws SQLiteException
     */
    protected void endTransaction(SQLiteDatabase db) throws SQLiteException {
        if (db != null) {
            db.endTransaction();
        }
    }
}

