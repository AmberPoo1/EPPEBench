package com.jingpu.android.apersistance.sugarorm.model;

import com.orm.SugarRecord;

/**
 * Created by Jing Pu on 2016/1/31.
 */
//@Table(name = "CITEM")
public class CItem extends SugarRecord {

    // property help us to keep data

    // super.id: I_ID

    // defining a relationship
    Category category;

    //@Column(name="I_IM_ID", notNull = true)
    private int iIImId;

    //@Column(name="I_NAME", notNull = true)
    private String strIName;

    //@Column(name="I_PRICE", notNull = true)
    private float fIPrice;

    //@Column(name="I_DATA", notNull = true)
    private String iData;

    public CItem() {
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
