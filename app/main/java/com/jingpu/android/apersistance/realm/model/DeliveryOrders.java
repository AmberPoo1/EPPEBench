package com.jingpu.android.apersistance.realm.model;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jing Pu on 2016/01/26.
 */

public class DeliveryOrders extends RealmObject {

	// property help us to keep data
    @PrimaryKey
    private long lDoId;

    private int iDoDrId; //Required

	private short sDoDId; //Required

	private int iDoOId;

    public long getDoId() {
        return lDoId;
    }

    public void setDoId(long lDoId) {
        this.lDoId = lDoId;
    }

    public int getiDoDrId() {
        return iDoDrId;
    }

    public void setiDoDrId(int iDoDrId) {
        this.iDoDrId = iDoDrId;
    }

    public short getsDoDId() {
        return sDoDId;
    }

    public void setsDoDId(short sDoDId) {
        this.sDoDId = sDoDId;
    }

    public int getiDoOId() {
        return iDoOId;
    }

    public void setiDoOId(int iDoOId) {
        this.iDoOId = iDoOId;
    }
}
