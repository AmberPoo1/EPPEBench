package com.jingpu.android.apersistance.realm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Jing Pu on 2016/2/1.
 */
public class CItem extends RealmObject {

    // property help us to keep data
    @PrimaryKey
    private long lIId;

    private Category category;

    private int iIImId;

    @Required
    private String strIName;

    private float fIPrice;

    @Required
    private String iData;

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
