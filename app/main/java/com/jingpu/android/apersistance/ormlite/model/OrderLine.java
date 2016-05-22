package com.jingpu.android.apersistance.ormlite.model;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Jing Pu on 2015/10/6.
 */

@DatabaseTable(tableName = "ORDERLINE")
public class OrderLine {

	// Labels Table Column names
	public static final String COL_OL_COMPO = "OL_COMPO";
	public static final String COL_OL_O_ID = "OL_O_ID";
	public static final String COL_OL_D_ID = "OL_D_ID";
	public static final String COL_OL_W_ID = "OL_W_ID";
	public static final String COL_OL_NUMBER = "OL_NUMBER";
	public static final String COL_OL_I_ID = "OL_I_ID";
	public static final String COL_OL_SUPPLY_W_ID = "OL_SUPPLY_W_ID";
	public static final String COL_OL_DELIVERY_D = "OL_DELIVERY_D";
	public static final String COL_OL_QUANTITY = "OL_QUANTITY";
	public static final String COL_OL_AMOUNT = "OL_AMOUNT";
	public static final String COL_OL_DIST_INFO = "OL_DIST_INFO";
	public static final String COL_OL_DELIVERY_D_INITIAL = "OL_DELIVERY_D_INITIAL";
	public static final String COL_OL_INITIAL = "OL_INITIAL";

	// property help us to keep data

    @DatabaseField(id = true, columnName="OL_COMPO")
    private String compositeKey;

    @DatabaseField(columnName="OL_O_ID", canBeNull = false)
	private int iOlOId; //Id

    @DatabaseField(columnName="OL_D_ID", canBeNull = false)
	private short sOlDId; //Id

    @DatabaseField(columnName="OL_W_ID", canBeNull = false)
	private short sOlWId; //Id

	@DatabaseField(columnName="OL_NUMBER", canBeNull = false)
	private short sOlNumber; //Id

    @DatabaseField(columnName="OL_I_ID", canBeNull = false)
	private int iOlIId;

    @DatabaseField(columnName="OL_SUPPLY_W_ID", canBeNull = false)
	private short sOlSupplyWId;

	@DatabaseField(columnName="OL_DELIVERY_D", dataType= DataType.DATE)
	private Date tOlDeliveryD; //TIMESTAMP

	@DatabaseField(columnName="OL_QUANTITY", canBeNull = false)
	private short sOlQuantity;

	@DatabaseField(columnName="OL_AMOUNT", canBeNull = false)
	private float fOlAmount;

	@DatabaseField(columnName="OL_DIST_INFO", canBeNull = false)
	private String strOlDistInfo;

	@DatabaseField(columnName="OL_DELIVERY_D_INITIAL", dataType= DataType.DATE)
	private Date tOlDlvDIni; //TIMESTAMP

    @DatabaseField(columnName="OL_INITIAL", dataType = DataType.BOOLEAN)
	private boolean bOlInitial;
	
	public OrderLine(){
		
	}

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public String getCompositeKey() {
        return this.compositeKey;
    }

    public static String getCompositeKey(OrderLine obj) {
        return Integer.toString(obj.getOlOId()) + "-" + Short.toString(obj.getOlDId()) + "-"  + Short.toString(obj.getOlWId()) + "-"  + Short.toString(obj.getOlNumber());
    }

    public int getOlOId() {
        return iOlOId;
    }

    public void setOlOId(int iOlOId) {
        this.iOlOId = iOlOId;
    }

    public short getOlDId() {
        return sOlDId;
    }

    public void setOlDId(short sOlDId) {
        this.sOlDId = sOlDId;
    }

    public short getOlWId() {
        return sOlWId;
    }

    public void setOlWId(short sOlWId) {
        this.sOlWId = sOlWId;
    }

    public short getOlNumber() {
		return sOlNumber;
	}

	public void setOlNumber(short sOlNumber) {
		this.sOlNumber = sOlNumber;
	}

    public int getOlIId() {
        return iOlIId;
    }

    public void setOlIId(int iOlIId) {
        this.iOlIId = iOlIId;
    }

    public short getOlSupplyWId() {
        return sOlSupplyWId;
    }

    public void setOlSupplyWId(short sOlSupplyWId) {
        this.sOlSupplyWId = sOlSupplyWId;
    }

    public Date getOlDeliveryD() {
		return tOlDeliveryD;
	}

	public void setOlDeliveryD(Date tOlDeliveryD) {
		this.tOlDeliveryD = tOlDeliveryD;
	}

	public short getOlQuantity() {
		return sOlQuantity;
	}

	public void setOlQuantity(short sOlQuantity) {
		this.sOlQuantity = sOlQuantity;
	}

	public float getOlAmount() {
		return fOlAmount;
	}

	public void setOlAmount(float fOlAmount) {
		this.fOlAmount = fOlAmount;
	}

	public String getOlDistInfo() {
		return strOlDistInfo;
	}

	public void setOlDistInfo(String strOlDistInfo) {
		this.strOlDistInfo = strOlDistInfo;
	}

	public Date getOlDlvDIni() {
		return tOlDlvDIni;
	}

	public void setOlDlvDIni(Date tOlDlvDIni) {
		this.tOlDlvDIni = tOlDlvDIni;
	}

	public boolean isOlInitial() {
		return bOlInitial;
	}

	public void setOlInitial(boolean bOlInitial) {
		this.bOlInitial = bOlInitial;
	}
}
