package com.jingpu.android.apersistance.activeandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


/**
 * Created by Jing Pu on 2016/01/3.
 */

@Table(name = "WAREHOUSE")
public class Warehouse extends Model {

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
    @Column(name="W_ID", unique = true)
	private long lWId;

    @Column(name="W_NAME", notNull = true)
	private String strWName;

    @Column(name="W_STREET_1", notNull = true)
	private String strWStreet1;

	@Column(name="W_STREET_2", notNull = true)
	private String strWStreet2;

	@Column(name="W_CITY", notNull = true)
	private String strWCity;

	@Column(name="W_STATE", notNull = true)
	private String strWState;

	@Column(name="W_ZIP", notNull = true)
	private String strWZip;

	@Column(name="W_TAX", notNull = true)
	private float fWTax;

	@Column(name="W_YTD", notNull = true)
	private float fWYtd;
	
	public Warehouse(){
        super();
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

	public void setWId(long sWId) {
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

	public void setWTax(float bdWTax) {
		this.fWTax = fWTax;
	}

	public float getWYtd() {
		return fWYtd;
	}

	public void setWYtd(float fWYtd) {
		this.fWYtd = fWYtd;
	}

}
