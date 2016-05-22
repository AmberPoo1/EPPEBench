package com.jingpu.android.apersistance.ormlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Jing Pu on 2015/10/6.
 */

@DatabaseTable(tableName = "ITEM")
public class Item {

	// Labels Table Column names
	public static final String COL_I_ID = "I_ID";
	public static final String COL_I_IM_ID = "I_IM_ID";
	public static final String COL_I_NAME = "I_NAME";
	public static final String COL_I_PRICE = "I_PRICE";
	public static final String COL_I_DATA = "I_DATA";

	// property help us to keep data
	@DatabaseField(id = true, columnName="I_ID")
	private long lIId;

	@DatabaseField(columnName="I_IM_ID", canBeNull = false)
	private int iIImId;

	@DatabaseField(columnName="I_NAME", canBeNull = false)
	private String strIName;

	@DatabaseField(columnName="I_PRICE", canBeNull = false)
	private float fIPrice;

	@DatabaseField(columnName="I_DATA", canBeNull = false)
	private String strIData;

	public Item(){

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
