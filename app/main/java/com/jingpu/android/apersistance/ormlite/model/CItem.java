package com.jingpu.android.apersistance.ormlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Jing Pu on 2016/1/31.
 */
@DatabaseTable(tableName = "CITEM")
public class CItem {

    // Labels: Table Column names
    public static final String COL_I_ID = "I_ID";
    public static final String COL_I_CAT_ID =  "C_ID";
    public static final String COL_I_IM_ID = "I_IM_ID";
    public static final String COL_I_NAME = "I_NAME";
    public static final String COL_I_PRICE = "I_PRICE";
    public static final String COL_I_DATA = "I_DATA";

    // Columns
    @DatabaseField(id = true, columnName="I_ID")
    private long lIId;

    @DatabaseField(columnName="C_ID", canBeNull = false, foreign = true)
    private Category category;

    @DatabaseField(columnName="I_IM_ID", canBeNull = false)
    private int iIImId;

    @DatabaseField(columnName="I_NAME", canBeNull = false)
    private String strIName;

    @DatabaseField(columnName="I_PRICE", canBeNull = false)
    private float fIPrice;

    @DatabaseField(columnName="I_DATA", canBeNull = false)
    private String iData;

    public CItem() {

    }

    public long getlIId() {
        return lIId;
    }

    public void setlIId(long lIId) {
        this.lIId = lIId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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
