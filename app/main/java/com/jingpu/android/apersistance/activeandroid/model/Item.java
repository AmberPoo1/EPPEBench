package com.jingpu.android.apersistance.activeandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Jing Pu on 2016/01/3.
 */

@Table(name = "ITEM")
public class Item extends Model {

	// Labels Table Column names
	public static final String COL_I_ID = "I_ID";
	public static final String COL_I_IM_ID = "I_IM_ID";
	public static final String COL_I_NAME = "I_NAME";
	public static final String COL_I_PRICE = "I_PRICE";
	public static final String COL_I_DATA = "I_DATA";

	// property help us to keep data
    @Column(name="I_ID", unique = true)
	private long lIId;

    @Column(name="I_IM_ID", notNull = true)
	private int iIImId;

    @Column(name="I_NAME", notNull = true)
	private String strIName;

    @Column(name="I_PRICE", notNull = true)
    private float fIPrice;

    @Column(name="I_DATA", notNull = true)
	private String strIData;

	public Item(){
        super();
	}

	public long getIId() {
		return lIId;
	}

	public void setIId(long lIId) {
		this.lIId = lIId;
	}

	public int getIImId() {
		return iIImId;
	}

	public void setIImId(int iIImId) {
		this.iIImId = iIImId;
	}

	public String getIName() {
		return strIName;
	}

	public void setIName(String strIName) {
		this.strIName = strIName;
	}

	public float getIPrice() {
		return fIPrice;
	}

	public void setIPrice(float fIPrice) {
		this.fIPrice = fIPrice;
	}

	public String getIData() {
		return strIData;
	}

	public void setIData(String strIData) {
		this.strIData = strIData;
	}
}
