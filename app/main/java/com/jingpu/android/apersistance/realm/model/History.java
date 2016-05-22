package com.jingpu.android.apersistance.realm.model;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Jing Pu on 2016/01/26.
 */

public class History extends RealmObject {

    // property help us to keep data
    @PrimaryKey
    private long lHId;

    private int iHCId; // Required

    private short sHCDId; // Required

    private short sHCWId; // Required

    private short sHDId; // Required

    private short sHWId; // Required

    @Required
	private Date tHDate; //TIMESTAMP

    private float fHAmount; // Required

    @Required
	private String strHData;

	private boolean bHInitial; // Required

    public long getHId() {
        return lHId;
    }

    public void setHId(long lHId) {
        this.lHId = lHId;
    }

    public int getiHCId() {
        return iHCId;
    }

    public void setiHCId(int iHCId) {
        this.iHCId = iHCId;
    }

    public short getsHCDId() {
        return sHCDId;
    }

    public void setsHCDId(short sHCDId) {
        this.sHCDId = sHCDId;
    }

    public short getsHCWId() {
        return sHCWId;
    }

    public void setsHCWId(short sHCWId) {
        this.sHCWId = sHCWId;
    }

    public short getsHDId() {
        return sHDId;
    }

    public void  setsHDId(short sHDId) {
        this.sHDId = sHDId;
    }

    public short getsHWId() {
        return sHWId;
    }

    public void setsHWId(short sHWId) {
        this.sHWId = sHWId;
    }

    public Date gettHDate() {
        return tHDate;
    }

    public void settHDate(Date tHDate) {
        this.tHDate = tHDate;
    }

    public float getfHAmount() {
        return fHAmount;
    }

    public void setfHAmount(float fHAmount) {
        this.fHAmount = fHAmount;
    }

    public String getStrHData() {
        return strHData;
    }

    public void setStrHData(String strHData) {
        this.strHData = strHData;
    }

    public boolean isbHInitial() {
        return bHInitial;
    }

    public void setbHInitial(boolean bHInitial) {
        this.bHInitial = bHInitial;
    }
}
