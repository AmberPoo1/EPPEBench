package com.jingpu.android.apersistance.sqlite.model;

/**
 * Created by Jing Pu on 2015/10/6.
 */
public class Item {

	public static final String TABLE = "ITEM";

	// Labels Table Column names
	public static final String COL_I_ID = "I_ID";
	public static final String COL_I_IM_ID = "I_IM_ID";
	public static final String COL_I_NAME = "I_NAME";
	public static final String COL_I_PRICE = "I_PRICE";
	public static final String COL_I_DATA = "I_DATA";

	// property help us to keep data

	//Id
	//Column(name="I_ID")
	private long lIId;
	
	//Column(name="I_IM_ID")
	private int iIImId;
	
	//Column(name="I_NAME")
	private String strIName;
	
	//Column(name="I_PRICE")
	private float fIPrice;
	
	//Column(name="I_DATA")
	private String strIData;

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
