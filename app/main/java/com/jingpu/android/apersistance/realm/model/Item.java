package com.jingpu.android.apersistance.realm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Jing Pu on 2016/01/3.
 */

public class Item extends RealmObject {

	// property help us to keep data
    @PrimaryKey
	private long lIId;

	private int iIImId; //Required

    @Required
	private String strIName;

    private float fIPrice; 	//Required

    @Required
	private String strIData;

    public long getlIId() {
        return lIId;
    }

    public void setlIId(long lIId) {
        this.lIId = lIId;
    }

    public int getiIImId() {
        return iIImId;
    }

    public void setiIImId(int iIImId) {
        this.iIImId = iIImId;
    }

    public String getStrIName() {
        return strIName;
    }

    public void setStrIName(String strIName) {
        this.strIName = strIName;
    }

    public float getfIPrice() {
        return fIPrice;
    }

    public void setfIPrice(float fIPrice) {
        this.fIPrice = fIPrice;
    }

    public String getStrIData() {
        return strIData;
    }

    public void setStrIData(String strIData) {
        this.strIData = strIData;
    }

}
