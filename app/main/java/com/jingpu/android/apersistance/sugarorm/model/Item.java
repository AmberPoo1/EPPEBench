package com.jingpu.android.apersistance.sugarorm.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

/**
 * Created by Jing Pu on 2016/01/16.
 */

@Table(name = "ITEM")
public class Item extends SugarRecord {

	// property help us to keep data
	@Column(name="I_ID", notNull = true)
	private Long id;

    @Column(name="I_IM_ID", notNull = true)
	private int iIImId;

    @Column(name="I_NAME", notNull = true)
	private String strIName;

    @Column(name="I_PRICE", notNull = true)
    private float fIPrice;

    @Column(name="I_DATA", notNull = true)
	private String strIData;

	public Item(){
	}

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
