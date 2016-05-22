package com.jingpu.android.apersistance.activeandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Jing Pu on 2016/1/31.
 */
@Table(name = "CITEM")
public class CItem extends Model {

    // Labels: Table Column names
    public static final String COL_I_ID = "I_ID";
    public static final String COL_I_CAT_ID =  "C_ID"; //"I_CAT_ID";
    public static final String COL_I_IM_ID = "I_IM_ID";
    public static final String COL_I_NAME = "I_NAME";
    public static final String COL_I_PRICE = "I_PRICE";
    public static final String COL_I_DATA = "I_DATA";

    // Columns
    @Column(name="I_ID", unique=true)
    private long lIId;

    @Column(name="Category", notNull=true)
    private Category category;

    @Column(name="I_IM_ID", notNull = true)
    private int iIImId;

    @Column(name="I_NAME", notNull = true)
    private String strIName;

    @Column(name="I_PRICE", notNull = true)
    private float fIPrice;

    @Column(name="I_DATA", notNull = true)
    private String iData;

    public CItem() {
        super();
    }

    public long getLIId() {
        return lIId;
    }

    public void setLIId(long lIId) {
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
