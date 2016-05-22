package com.jingpu.android.apersistance.sqlite.model;

/**
 * Created by Jing Pu on 2015/10/6.
 */
public class DeliveryOrders {

	public static final String TABLE = "DELIVERY_ORDERS";

	// Labels Table Column names
	public static final String COL_DO_ID = "DO_ID";
	public static final String COL_DO_DR_ID = "DO_DR_ID";
	public static final String COL_DO_D_ID = "DO_D_ID";
	public static final String COL_DO_O_ID = "DO_O_ID";

	// property help us to keep data
	//Column(name="DO_ID")
	private long lDoId;

	//Column(name="DO_DR_ID")
	private int iDoDrId;
	
	//Column(name="DO_D_ID")
	private short sDoDId;
	
	//Column(name="DO_O_ID")
	private int iDoOId;

    public long getDoId() {
        return lDoId;
    }

    public void setDoId(long iDoId) {
        this.lDoId = lDoId;
    }

    public int getDoDrId() {
		return iDoDrId;
	}

	public void setDoDrId(int iDoDrId) {
		this.iDoDrId = iDoDrId;
	}

	public short getDoDId() {
		return sDoDId;
	}

	public void setDoDId(short sDoDId) {
		this.sDoDId = sDoDId;
	}

	public Integer getDoOId() {
		return iDoOId;
	}

	public void setDoOId(int iDoOId) {
		this.iDoOId = iDoOId;
	}

}
