package com.jingpu.android.apersistance.ormlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Jing Pu on 2015/10/3.
 */

@DatabaseTable(tableName = "WAREHOUSE")
public class Warehouse {

	// Labels Table Column names
	public static final String COL_W_ID = "W_ID";
	public static final String COL_W_NAME = "W_NAME";
	public static final String COL_W_STREET_1 = "W_STREET_1";
	public static final String COL_W_STREET_2 = "W_STREET_2";
	public static final String COL_W_CITY = "W_CITY";
	public static final String COL_W_STATE = "W_STATE";
	public static final String COL_W_ZIP = "W_ZIP";
	public static final String COL_W_TAX = "W_TAX";
	public static final String COL_W_YTD = "W_YTD";

	// property help us to keep data
	@DatabaseField(id = true, columnName="W_ID")
	private long lWId;

	@DatabaseField(columnName="W_NAME", canBeNull = false)
	private String strWName;

	@DatabaseField(columnName="W_STREET_1", canBeNull = false)
	private String strWStreet1;

	@DatabaseField(columnName="W_STREET_2", canBeNull = false)
	private String strWStreet2;

	@DatabaseField(columnName="W_CITY", canBeNull = false)
	private String strWCity;

	@DatabaseField(columnName="W_STATE", canBeNull = false)
	private String strWState;

	@DatabaseField(columnName="W_ZIP", canBeNull = false)
	private String strWZip;

	@DatabaseField(columnName="W_TAX", canBeNull = false)
	private float fWTax;

	@DatabaseField(columnName="W_YTD", canBeNull = false)
	private float fWYtd;
	
	public Warehouse(){
		
	}

	public void clear(){
		this.lWId = 0;
		this.strWName = null;
		this.strWStreet1 = null;
		this.strWStreet2 = null;
		this.strWCity = null;
		this.strWState = null;
		this.strWZip = null;
		this.fWTax = 0;
		this.fWYtd = 0;
	}

	public long getWId() {
		return lWId;
	}

	public void setWId(long lWId) {
		this.lWId = lWId;
	}

	public String getWName() {
		return strWName;
	}

	public void setWName(String strWName) {
		this.strWName = strWName;
	}

	public String getWStreet1() {
		return strWStreet1;
	}

	public void setWStreet1(String strWStreet1) {
		this.strWStreet1 = strWStreet1;
	}

	public String getWStreet2() {
		return strWStreet2;
	}

	public void setWStreet2(String strWStreet2) {
		this.strWStreet2 = strWStreet2;
	}

	public String getWCity() {
		return strWCity;
	}

	public void setWCity(String strWCity) {
		this.strWCity = strWCity;
	}

	public String getWState() {
		return strWState;
	}

	public void setWState(String strWState) {
		this.strWState = strWState;
	}

	public String getWZip() {
		return strWZip;
	}

	public void setWZip(String strWZip) {
		this.strWZip = strWZip;
	}

	public float getWTax() {
		return fWTax;
	}

	public void setWTax(float fWTax) {
		this.fWTax = fWTax;
	}

	public float getWYtd() {
		return fWYtd;
	}

	public void setWYtd(float fWYtd) {
		this.fWYtd = fWYtd;
	}

}
