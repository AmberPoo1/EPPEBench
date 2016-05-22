package com.jingpu.android.apersistance.activeandroid.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by Jing Pu on 2016/01/3.
 */

@Table(name = "ORDERLINE")
public class OrderLine extends Model {

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
	@Column(name="OL_COMPO", unique = true)
	private String compositeKey;

    @Column(name="OL_O_ID", notNull = true)
    private int iOlOId; // Id

    @Column(name="OL_D_ID", notNull = true)
    private short sOlDId; // Id

    @Column(name="OL_W_ID", notNull = true)
    private short sOlWId; // Id

    @Column(name="OL_NUMBER", notNull = true)
    private short sOlNumber; // Id

    @Column(name="OL_I_ID", notNull = true)
	private int iOlIId;

    @Column(name="OL_SUPPLY_W_ID", notNull = true)
	private short sOlSupplyWId;

    @Column(name="OL_DELIVERY_D")
    private Date tOlDeliveryD; //TIMESTAMP

    @Column(name="OL_QUANTITY", notNull = true)
	private short sOlQuantity;

    @Column(name="OL_AMOUNT", notNull = true)
    private float fOlAmount;

    @Column(name="OL_DIST_INFO", notNull = true)
    private String strOlDistInfo;

    @Column(name="OL_DELIVERY_D_INITIAL")
    private Date tOlDlvDIni; //TIMESTAMP

    @Column(name="OL_INITIAL")
    private boolean bOlInitial;
	
	public OrderLine(){
		super();
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
