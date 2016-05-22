package com.jingpu.android.apersistance.realm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Jing Pu on 2016/01/3.
 */

public class Warehouse extends RealmObject {

	// property help us to keep data
    @PrimaryKey
	private long lWId;

    @Required
	private String strWName;

    @Required
	private String strWStreet1;

	@Required
	private String strWStreet2;

	@Required
	private String strWCity;

	@Required
	private String strWState;

	@Required
	private String strWZip;

	private float fWTax; // Required

	private float fWYtd; // Required

    public long getlWId() {
        return lWId;
    }

    public void setlWId(long lWId) {
        this.lWId = lWId;
    }

    public String getStrWName() {
        return strWName;
    }

    public void setStrWName(String strWName) {
        this.strWName = strWName;
    }

    public String getStrWStreet1() {
        return strWStreet1;
    }

    public void setStrWStreet1(String strWStreet1) {
        this.strWStreet1 = strWStreet1;
    }

    public String getStrWStreet2() {
        return strWStreet2;
    }

    public void setStrWStreet2(String strWStreet2) {
        this.strWStreet2 = strWStreet2;
    }

    public String getStrWCity() {
        return strWCity;
    }

    public void setStrWCity(String strWCity) {
        this.strWCity = strWCity;
    }

    public String getStrWState() {
        return strWState;
    }

    public void setStrWState(String strWState) {
        this.strWState = strWState;
    }

    public String getStrWZip() {
        return strWZip;
    }

    public void setStrWZip(String strWZip) {
        this.strWZip = strWZip;
    }

    public float getfWTax() {
        return fWTax;
    }

    public void setfWTax(float fWTax) {
        this.fWTax = fWTax;
    }

    public float getfWYtd() {
        return fWYtd;
    }

    public void setfWYtd(float fWYtd) {
        this.fWYtd = fWYtd;
    }
}
