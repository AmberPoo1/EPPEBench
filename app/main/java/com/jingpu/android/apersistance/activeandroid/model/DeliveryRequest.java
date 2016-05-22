package com.jingpu.android.apersistance.activeandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by Jing Pu on 2016/01/3.
 */

@Table(name = "DELIVERY_REQUEST")
public class DeliveryRequest extends Model {

	// Labels Table Column names
	public static final String COL_DR_ID= "DR_ID";
	public static final String COL_DR_W_ID = "DR_W_ID";
	public static final String COL_DR_CARRIER_ID = "DR_CARRIER_ID";
	public static final String COL_DR_QUEUED = "DR_QUEUED";
	public static final String COL_DR_COMPLETED = "DR_COMPLETED";
	public static final String COL_DR_STATE = "DR_STATE";

	// property help us to keep data
    @Column(name="DR_ID", unique = true)
	private long lDrId;

    @Column(name="DR_W_ID", notNull = true)
	private short sDrWId;

    @Column(name="DR_CARRIER_ID", notNull = true)
	private short sDrCarrierId;

	@Column(name="DR_QUEUED", notNull = true)
    private Date tDrQueued; //TIMESTAMP
	
    @Column(name="DR_COMPLETED", notNull = true)
	private Date tDrCompleted; // TIMESTAMP

	@Column(name="DR_STATE", notNull = true)
	private String sDrState;

	public DeliveryRequest(){
		super();
	}

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
