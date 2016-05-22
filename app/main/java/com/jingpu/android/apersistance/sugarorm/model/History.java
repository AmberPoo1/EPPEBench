package com.jingpu.android.apersistance.sugarorm.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

import java.util.Date;

/**
 * Created by Jing Pu on 2016/01/16.
 */

@Table(name = "HISTORY")
public class History extends SugarRecord {

	// property help us to keep data
	private Long id; // "H_ID"

    @Column(name="H_C_ID", notNull = true)
    private int iHCId;

    @Column(name="H_C_D_ID", notNull = true)
    private short sHCDId;

    @Column(name="H_C_W_ID", notNull = true)
    private short sHCWId;

    @Column(name="H_D_ID", notNull = true)
    private short sHDId;

    @Column(name="H_W_ID", notNull = true)
    private short sHWId;

    @Column(name="H_DATE", notNull = true)
	private Date tHDate; //TIMESTAMP

    @Column(name="H_AMOUNT", notNull = true)
    private float fHAmount;

    @Column(name="H_DATA", notNull = true)
	private String strHData;

    @Column(name="H_INITIAL")
	private boolean bHInitial;

    public History(){
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public int getHCId() {
		return iHCId;
	}

	public void setHCId(int iHCId) {
		this.iHCId = iHCId;
	}

	public short getHCDId() {
		return sHCDId;
	}

	public void setHCDId(short sHCDId) {
		this.sHCDId = sHCDId;
	}

	public short getHCWId() {
		return sHCWId;
	}

	public void setHCWId(short sHCWId) {
		this.sHCWId = sHCWId;
	}

	public short getHDId() {
		return sHDId;
	}

	public void setHDId(short sHDId) {
		this.sHDId = sHDId;
	}

	public short getHWId() {
		return sHWId;
	}

	public void setHWId(short sHWId) {
		this.sHWId = sHWId;
	}

	public Date getHDate() {
		return tHDate;
	}

	public void setHDate(Date tHDate) {
		this.tHDate = tHDate;
	}

	public float getHAmount() {
		return fHAmount;
	}

	public void setHAmount(float fHAmount) {
		this.fHAmount = fHAmount;
	}

	public String getHData() {
		return strHData;
	}

	public void setHData(String strHData) {
		this.strHData = strHData;
	}

	public boolean isHInitial() {
		return bHInitial;
	}

	public void setHInitial(boolean bHInitial) {
		this.bHInitial = bHInitial;
	}
}
