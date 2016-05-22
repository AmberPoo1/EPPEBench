package com.jingpu.android.apersistance.sqlite.model;

/**
 * Created by Jing Pu on 2015/10/6.
 */
public class DeliveryRequest {
	public static final String TABLE = "DELIVERY_REQUEST";

	// Labels Table Column names
	public static final String COL_DR_ID= "DR_ID";
	public static final String COL_DR_W_ID = "DR_W_ID";
	public static final String COL_DR_CARRIER_ID = "DR_CARRIER_ID";
	public static final String COL_DR_QUEUED = "DR_QUEUED";
	public static final String COL_DR_COMPLETED = "DR_COMPLETED";
	public static final String COL_DR_STATE = "DR_STATE";

	// property help us to keep data

	//Id
	//Column(name="DR_ID")
	private long lDrId;
	
	//Column(name="DR_W_ID")
	private short sDrWId;
	
	//Column(name="DR_CARRIER_ID")
	private short sDrCarrierId;
	
	//Column(name="DR_QUEUED")
	private String stDrQueued; //TIMESTAMP
	
	//Column(name="DR_COMPLETED")
	private String stDrCompleted; // TIMESTAMP
	
	//Column(name="DR_STATE")
	private String sDrState;

	public long getDrId() {
		return lDrId;
	}

	public void setDrId(long lDrId) {
		this.lDrId = lDrId;
	}

	public short getDrWId() {
		return sDrWId;
	}

	public void setDrWId(short sDrWId) {
		this.sDrWId = sDrWId;
	}

	public short getDrCarrierId() {
		return sDrCarrierId;
	}

	public void setDrCarrierId(short sDrCarrierId) {
		this.sDrCarrierId = sDrCarrierId;
	}

	public String getDrQueued() {
		return stDrQueued;
	}

	public void setDrQueued(String tDrQueued) {
		this.stDrQueued = stDrQueued;
	}

	public String getDrCompleted() {
		return stDrCompleted;
	}

	public void setDrCompleted(String stDrCompleted) {
		this.stDrCompleted = stDrCompleted;
	}

	public String getDrState() {
		return sDrState;
	}

	public void setDrState(String sDrState) {
		this.sDrState = sDrState;
	}
}
