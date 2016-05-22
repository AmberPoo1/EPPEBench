package com.jingpu.android.apersistance.sugarorm.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

import java.util.Date;

/**
 * Created by Jing Pu on 2016/01/16.
 */

@Table(name = "DELIVERY_REQUEST")
public class DeliveryRequest extends SugarRecord {

	// property help us to keep data
	@Column(name="DR_ID", notNull = true)
    private Long id;

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
	}

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
