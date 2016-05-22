package com.jingpu.android.apersistance.sqlite.model;

/**
 * Created by Jing Pu on 2015/10/3.
 */
public class History {

	public static final String TABLE = "HISTORY";

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

    //Column(name="H_ID")
    private long lHId;

	//Column(name="H_C_ID")
	private int iHCId;
	
	//Column(name="H_C_D_ID")
	private short sHCDId;
	
	//Column(name="H_C_W_ID")
	private short sHCWId;
	
	//Column(name="H_D_ID")
	private short sHDId;
	
	//Column(name="H_W_ID")
	private short sHWId;
	
	//Column(name="H_DATE")
	private String stHDate; //TIMESTAMP
	
	//Column(name="H_AMOUNT")
	private float fHAmount;
	
	//Column(name="H_DATA")
	private String strHData;
	
	//Column(name="H_INITIAL")
	private boolean bHInitial;

    public long getHId() {
        return lHId;
    }

    public void setHId(long lHId) {
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

	public String getHDate() {
		return stHDate;
	}

	public void setHDate(String tHDate) {
		this.stHDate = tHDate;
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
