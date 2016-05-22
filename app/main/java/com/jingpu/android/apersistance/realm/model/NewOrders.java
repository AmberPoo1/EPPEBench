package com.jingpu.android.apersistance.realm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jing Pu on 2016/01/26.
 */

public class NewOrders extends RealmObject {

    // property help us to keep data
    @PrimaryKey
    private String compositeKey;


    private int iNoOId; // Id, Required

    private short sNoDId; // Id, Required

    private short sNoWId; //Id, Required

	private boolean bNoInitial;

	private boolean bNoLive;

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public String getCompositeKey() {
        return this.compositeKey;
    }

    public static String getCompositeKey(NewOrders obj) {
        return Integer.toString(obj.getiNoOId()) + "-"  + Short.toString(obj.getsNoDId()) + "-"  + Short.toString(obj.getsNoWId());
    }

    public int getiNoOId() {
        return iNoOId;
    }

    public void setiNoOId(int iNoOId) {
        this.iNoOId = iNoOId;
    }

    public short getsNoDId() {
        return sNoDId;
    }

    public void setsNoDId(short sNoDId) {
        this.sNoDId = sNoDId;
    }

    public short getsNoWId() {
        return sNoWId;
    }

    public void setsNoWId(short sNoWId) {
        this.sNoWId = sNoWId;
    }

    public boolean isbNoInitial() {
        return bNoInitial;
    }

    public void setbNoInitial(boolean bNoInitial) {
        this.bNoInitial = bNoInitial;
    }

    public boolean isbNoLive() {
        return bNoLive;
    }

    public void setbNoLive(boolean bNoLive) {
        this.bNoLive = bNoLive;
    }

}
