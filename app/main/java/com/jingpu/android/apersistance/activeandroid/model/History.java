package com.jingpu.android.apersistance.activeandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by Jing Pu on 2016/01/3.
 */

@Table(name = "HISTORY", id = "clientId")
public class History extends Model {

	// Labels Table Column names
	public static final String COL_H_ID = "Id"; //H_ID
	public static final String COL_H_C_ID = "H_C_ID";
	public static final String COL_H_C_D_ID = "H_C_D_ID";
	public static final String COL_H_C_W_ID = "H_C_W_ID";
	public static final String COL_H_D_ID = "H_D_ID";
	public static final String COL_H_W_ID = "H_W_ID";
	public static final String COL_H_DATE = "H_DATE";
	public static final String COL_H_AMOUNT = "H_AMOUNT";
	public static final String COL_H_DATA = "H_DATA";
	public static final String COL_H_INITIAL = "H_INITIAL";

    // property help us to keep data
    @Column(name="Id") //, unique = true
    private Long Id;

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
        super();
    }

    public Long getHId() {
        return Id;
    }

    public void setHId(Long lHId) {
        this.Id = lHId;
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
