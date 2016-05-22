package com.jingpu.android.apersistance.realm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Jing Pu on 2016/01/26.
 */

public class District extends RealmObject {

    // property help us to keep data
	@PrimaryKey
	private String compositeKey;

    private short sDId; //Id

    private short sDWId; // Id

    @Required
	private String strDName;

    @Required
	private String strDStreet1;

    @Required
	private String strDStreet2;

    @Required
	private String strDCity;

    @Required
    private String strDState;

    @Required
	private String strDZip;

	private float fDTax; // Required

	private float fDYTD; // Required

	private int iDNxtOId; // Required

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public String getCompositeKey() {
        return this.compositeKey;
    }

    public static String getCompositeKey(District obj) {
        return Short.toString(obj.getsDWId()) + "-"  + Short.toString(obj.getsDId());
    }

    public short getsDId() {
        return sDId;
    }

    public void setsDId(short sDId) {
        this.sDId = sDId;
    }

    public short getsDWId() {
        return sDWId;
    }

    public void setsDWId(short sDWId) {
        this.sDWId = sDWId;
    }

    public String getStrDName() {
        return strDName;
    }

    public void setStrDName(String strDName) {
        this.strDName = strDName;
    }

    public String getStrDStreet1() {
        return strDStreet1;
    }

    public void setStrDStreet1(String strDStreet1) {
        this.strDStreet1 = strDStreet1;
    }

    public String getStrDStreet2() {
        return strDStreet2;
    }

    public void setStrDStreet2(String strDStreet2) {
        this.strDStreet2 = strDStreet2;
    }

    public String getStrDCity() {
        return strDCity;
    }

    public void setStrDCity(String strDCity) {
        this.strDCity = strDCity;
    }

    public String getStrDState() {
        return strDState;
    }

    public void setStrDState(String strDState) {
        this.strDState = strDState;
    }

    public String getStrDZip() {
        return strDZip;
    }

    public void setStrDZip(String strDZip) {
        this.strDZip = strDZip;
    }

    public float getfDTax() {
        return fDTax;
    }

    public void setfDTax(float fDTax) {
        this.fDTax = fDTax;
    }

    public float getfDYTD() {
        return fDYTD;
    }

    public void setfDYTD(float fDYTD) {
        this.fDYTD = fDYTD;
    }

    public int getiDNxtOId() {
        return iDNxtOId;
    }

    public void setiDNxtOId(int iDNxtOId) {
        this.iDNxtOId = iDNxtOId;
    }

}
