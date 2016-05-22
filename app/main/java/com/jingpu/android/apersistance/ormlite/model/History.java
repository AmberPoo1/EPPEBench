package com.jingpu.android.apersistance.ormlite.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Jing Pu on 2015/10/3.
 */

@DatabaseTable(tableName = "HISTORY")
public class History {

	// Labels Table Column names
	public static final String COL_H_ID = "H_ID";
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
    @DatabaseField(id=true, columnName="H_ID")
    private Long lHId;

	@DatabaseField(columnName="H_C_ID", canBeNull = false)
    private int iHCId;

	@DatabaseField(columnName="H_C_D_ID", canBeNull = false)
    private short sHCDId;

	@DatabaseField(columnName="H_C_W_ID", canBeNull = false)
    private short sHCWId;

	@DatabaseField(columnName="H_D_ID", canBeNull = false)
    private short sHDId;

	@DatabaseField(columnName="H_W_ID", canBeNull = false)
    private short sHWId;

    @DatabaseField(columnName="H_DATE", dataType=DataType.DATE, canBeNull = false)
	private Date tHDate; //TIMESTAMP Date

    @DatabaseField(columnName="H_AMOUNT", canBeNull = false)
	private float fHAmount;

    @DatabaseField(columnName="H_DATA", canBeNull = false)
	private String strHData;

    @DatabaseField(columnName="H_INITIAL", dataType = DataType.BOOLEAN)
	private boolean bHInitial;

    public History(){

    }

    public Long getHId() {
        return lHId;
    }

    public void setHId(Long lHId) {
        this.lHId = lHId;
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
