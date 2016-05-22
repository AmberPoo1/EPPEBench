package com.jingpu.android.apersistance.sugarorm.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;


/**
 * Created by Jing Pu on 2016/01/3.
 */
@Table(name = "WAREHOUSE")
public class Warehouse extends SugarRecord {

	// property help us to keep data
	@Column(name="W_ID", notNull = true)
    private Long id;

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
	}

	public void clear(){
		this.id = 0l;
		this.strWName = null;
		this.strWStreet1 = null;
		this.strWStreet2 = null;
		this.strWCity = null;
		this.strWState = null;
		this.strWZip = null;
		this.fWTax = 0;
		this.fWYtd = 0;
	}

	@Override
    public Long getId() {
        return id;
    }

	@Override
    public void setId(Long id) {
        this.id = id;
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
