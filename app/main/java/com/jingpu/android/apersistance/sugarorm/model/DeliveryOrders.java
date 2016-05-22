package com.jingpu.android.apersistance.sugarorm.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

/**
 * Created by Jing Pu on 2016/01/16.
 */

@Table(name = "DELIVERY_ORDERS")
public class DeliveryOrders extends SugarRecord {
	// property help us to keep data
	private Long id; // "DO_ID"

	@Column(name="DO_DR_ID", notNull = true)
    private int iDoDrId;

    @Column(name="DO_D_ID", notNull = true)
	private short sDoDId;

    @Column(name="DO_O_ID")
	private int iDoOId;

	public DeliveryOrders(){
	}

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
