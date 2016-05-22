package com.jingpu.android.apersistance.sqlite.model;


/**
 * Created by Jing Pu on 2016/1/31.
 */
public class CItem {
    // Table Name
    public static final String TABLE = "CITEM";

    // Labels: Table Column names
    public static final String COL_I_ID = "I_ID";
    public static final String COL_I_CAT_ID = "I_CAT_ID";
    public static final String COL_I_IM_ID = "I_IM_ID";
    public static final String COL_I_NAME = "I_NAME";
    public static final String COL_I_PRICE = "I_PRICE";
    public static final String COL_I_DATA = "I_DATA";

    // Columns
    private long lIId;
    private long lICatId;
    private int iIImId;
    private String strIName;
    private float fIPrice;
    private String iData;

    public CItem() {

    }

    public long getlIId() {
        return lIId;
    }

    public void setlIId(long lIId) {
        this.lIId = lIId;
    }

    public long getlICatId() {
        return lICatId;
    }

    public void setlICatId(long lICatId) {
        this.lICatId = lICatId;
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

    public String getiData() {
        return iData;
    }

    public void setiData(String iData) {
        this.iData = iData;
    }

}
