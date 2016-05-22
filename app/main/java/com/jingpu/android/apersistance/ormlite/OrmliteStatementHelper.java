package com.jingpu.android.apersistance.ormlite;

import java.sql.SQLException;

/**
 * Created by Jing Pu on 2015/10/23.
 */
public class OrmliteStatementHelper {
    protected final OrmliteAgent oa;

    OrmliteStatementHelper(OrmliteAgent oa) throws SQLException {
        this.oa = oa;
    }

    protected OrmliteDBHelper getDbHelper() {
        if (null == this.oa) {
            throw new NullPointerException();
        }

        return oa.getDbHelper();
    }

    protected  void releaseHelper() throws SQLException {
        if (null != oa) {
            oa.releaseHelper();
        }
    }
}
