package com.jingpu.android.apersistance.realm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jing Pu on 2016/1/26.
 */
public class C extends RealmObject {

	// property help us to keep data
    @PrimaryKey
	private long cId;

	private int iCLoad;

    public long getCId() {
        return cId;
    }

    public void setCId(long cId) {
        this.cId = cId;
    }

    public int getiCLoad() {
		return iCLoad;
	}

	public void setiCLoad(int iCLoad) {
		this.iCLoad = iCLoad;
	}

}
