package com.jingpu.android.apersistance.ormlite.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Jing Pu on 2015/10/6.
 */

@DatabaseTable(tableName = "DELIVERY_REQUEST")
public class DeliveryRequest {

	// Labels Table Column names
	public static final String COL_DR_ID= "DR_ID";
	public static final String COL_DR_W_ID = "DR_W_ID";
	public static final String COL_DR_CARRIER_ID = "DR_CARRIER_ID";
	public static final String COL_DR_QUEUED = "DR_QUEUED";
	public static final String COL_DR_COMPLETED = "DR_COMPLETED";
	public static final String COL_DR_STATE = "DR_STATE";

	// property help us to keep data

	@DatabaseField(id = true, columnName="DR_ID")
	private long lDrId;

	@DatabaseField(columnName="DR_W_ID", canBeNull = false)
	private short sDrWId;

	@DatabaseField(columnName="DR_CARRIER_ID", canBeNull = false)
	private short sDrCarrierId;

	@DatabaseField(columnName="DR_QUEUED", dataType= DataType.DATE, canBeNull = false)
	private Date tDrQueued; //TIMESTAMP
	
	@DatabaseField(columnName="DR_COMPLETED", dataType= DataType.DATE)
	private Date tDrCompleted; // TIMESTAMP
	
	@DatabaseField(columnName="DR_STATE")
	private String sDrState;

	public DeliveryRequest(){

	}

	public long getDrId() {
		return lDrId;
	}

	public void setDrId(int lDrId) {
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

	public Date getDrQueued() {
		return tDrQueued;
	}

	public void setDrQueued(Date tDrQueued) {
		this.tDrQueued = tDrQueued;
	}

	public Date getDrCompleted() {
		return tDrCompleted;
	}

	public void setDrCompleted (Date tDrCompleted) {
		this.tDrCompleted = tDrCompleted;
	}

	public String getDrState() {
		return sDrState;
	}

	public void setDrState(String sDrState) {
		this.sDrState = sDrState;
	}
}
