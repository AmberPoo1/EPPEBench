package com.jingpu.android.apersistance.activeandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Jing Pu on 2016/01/3.
 */

@Table(name = "DELIVERY_ORDERS", id = "clientId")
public class DeliveryOrders extends Model {

	// Labels Table Column names
	public static final String COL_DO_ID = "Id"; //DO_ID
	public static final String COL_DO_DR_ID = "DO_DR_ID";
	public static final String COL_DO_D_ID = "DO_D_ID";
	public static final String COL_DO_O_ID = "DO_O_ID";

	// property help us to keep data
	@Column(name="Id") //, unique = true
	private Long Id;

    @Column(name="DO_DR_ID", notNull = true)
    private int iDoDrId;

    @Column(name="DO_D_ID", notNull = true)
	private short sDoDId;

    @Column(name="DO_O_ID")
	private int iDoOId;

	public DeliveryOrders(){
        super();
	}

    public Long getDoId() {
        return Id;
    }

    public void setDoId(Long lDoId) {
        this.Id = lDoId;
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

	public int getDoOId() {
		return iDoOId;
	}

	public void setDoOId(int iDoOId) {
		this.iDoOId = iDoOId;
	}
}
