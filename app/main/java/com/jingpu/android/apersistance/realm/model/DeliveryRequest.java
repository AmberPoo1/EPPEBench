package com.jingpu.android.apersistance.realm.model;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Jing Pu on 2016/01/26.
 */

public class DeliveryRequest extends RealmObject {

	// property help us to keep data
    @PrimaryKey
	private long lDrId;

	private short sDrWId; // Required

	private short sDrCarrierId; // Required

    @Required
    private Date tDrQueued; //TIMESTAMP
	
    @Required
	private Date tDrCompleted; // TIMESTAMP
	
    @Required
	private String strDrState;

    public long getlDrId() {
        return lDrId;
    }

    public void setlDrId(long lDrId) {
        this.lDrId = lDrId;
    }

    public short getsDrWId() {
        return sDrWId;
    }

    public void setsDrWId(short sDrWId) {
        this.sDrWId = sDrWId;
    }

    public short getsDrCarrierId() {
        return sDrCarrierId;
    }

    public void setsDrCarrierId(short sDrCarrierId) {
        this.sDrCarrierId = sDrCarrierId;
    }

    public Date gettDrQueued() {
        return tDrQueued;
    }

    public void settDrQueued(Date tDrQueued) {
        this.tDrQueued = tDrQueued;
    }

    public Date gettDrCompleted() {
        return tDrCompleted;
    }

    public void settDrCompleted(Date tDrCompleted) {
        this.tDrCompleted = tDrCompleted;
    }

    public String getStrDrState() {
        return strDrState;
    }

    public void setStrDrState(String strDrState) {
        this.strDrState = strDrState;
    }
}
