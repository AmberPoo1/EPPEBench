package com.jingpu.android.apersistance.ormlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Jing Pu on 2015/10/9.
 */

@DatabaseTable(tableName = "DELIVERY_ORDERS")
public class DeliveryOrders {

	// Labels Table Column names
    public static final String COL_DO_ID = "DO_ID";
	public static final String COL_DO_DR_ID = "DO_DR_ID";
	public static final String COL_DO_D_ID = "DO_D_ID";
	public static final String COL_DO_O_ID = "DO_O_ID";

	// property help us to keep data

    @DatabaseField(id=true, columnName="DO_ID")
    private Long lDoId;

	@DatabaseField(columnName="DO_DR_ID", canBeNull = false)
	private int iDoDrId;

	@DatabaseField(columnName="DO_D_ID", canBeNull = false)
	private short sDoDId;

	@DatabaseField(columnName="DO_O_ID")
	private int iDoOId;

	public DeliveryOrders(){

	}

    public Long getDoId() {
        return lDoId;
    }

    public void setDoId(Long lDoId) {
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
