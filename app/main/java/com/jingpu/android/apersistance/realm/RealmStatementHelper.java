package com.jingpu.android.apersistance.realm;

import io.realm.exceptions.RealmException;

/**
 * Created by Jing Pu on 2016/1/26.
 */
public class RealmStatementHelper {
    protected final RealmAgent ra;

    RealmStatementHelper(RealmAgent ra) throws RealmException {
        this.ra = ra;
    }
}
