package com.jingpu.android.apersistance.activeandroid.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by Jing Pu on 2016/01/3.
 */

@Table(name = "ORDERS")
public class Orders extends Model {

	// Labels Table Column names
    public static final String COL_O_COMPO = "O_COMPO";
	public static final String COL_O_ID = "O_ID";
	public static final String COL_O_D_ID = "O_D_ID";
	public static final String COL_O_W_ID = "O_W_ID";
	public static final String COL_O_C_ID = "O_C_ID";
	public static final String COL_O_ENTRY_D = "O_ENTRY_D";
	public static final String COL_O_CARRIER_ID = "O_CARRIER_ID";
	public static final String COL_O_OL_CNT = "O_OL_CNT";
	public static final String COL_O_ALL_LOCAL = "O_ALL_LOCAL";
	public static final String COL_O_CARRIER_ID_INITIAL = "O_CARRIER_ID_INITIAL";
	public static final String COL_O_INITIAL = "O_INITIAL";

	// property help us to keep data
	@Column(name="O_COMPO", unique = true)
	private String compositeKey;

    @Column(name="O_ID", notNull = true)
    private int iOId; //Id

    @Column(name="O_D_ID", notNull = true)
    private short sODId; // Id

    @Column(name="O_W_ID", notNull = true)
    private short sOWId;// Id

    @Column(name="O_C_ID", notNull = true)
    private int iOCId;

    @Column(name="O_ENTRY_D", notNull = true)
	private Date tOEntryD; // TIMESTAMP

    @Column(name="O_CARRIER_ID")
	private short sOCarrierId;

    @Column(name="O_OL_CNT", notNull = true)
	private short sOOlCnt;

    @Column(name="O_ALL_LOCAL", notNull = true)
	private short sOAllLocal;

    @Column(name="O_CARRIER_ID_INITIAL")
	private short sOCarIdIni;

    @Column(name="O_INITIAL")
	private boolean bOInitial;
	
	public Orders(){
		super();
	}

    public void clear(){
        this.setCompositeKey(null);
		this.iOId = 0;
        this.sODId = 0;
        this.sOWId = 0;
        this.iOCId = 0;
		this.tOEntryD = null;
		this.sOCarrierId = -1;  // null
		this.sOOlCnt = 0;
		this.sOAllLocal = 0; // false
	}

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public String getCompositeKey() {
        return this.compositeKey;
    }

    public static String getCompositeKey(Orders obj) {
        return Integer.toString(obj.getOId()) + "-"  + Short.toString(obj.getODId()) + "-"  + Short.toString(obj.getOWId());
    }

	public int getOId() {
		return iOId;
	}

	public void setOId(int iOId) {
		this.iOId = iOId;
	}

    public short getODId() {
        return sODId;
    }

    public void setODId(short sODId) {
        this.sODId = sODId;
    }

    public short getOWId() {
        return sOWId;
    }

    public void setOWId(short sOWId) {
        this.sOWId = sOWId;
    }

    public int getOCId() {
        return iOCId;
    }

    public void setOCId(int iOCId) {
        this.iOCId = iOCId;
    }

	public Date getOEntryD() {
		return tOEntryD;
	}

	public void setOEntryD(Date tOEntryD) {
		this.tOEntryD = tOEntryD;
	}

	public short getOCarrierId() {
		return sOCarrierId;
	}

	public void setOCarrierId(short sOCarrierId) {
		this.sOCarrierId = sOCarrierId;
	}

	public short getOOlCnt() {
		return sOOlCnt;
	}

    public void setOOlCnt(short sOOlCnt) {
		this.sOOlCnt = sOOlCnt;
	}

	public short getOAllLocal() {
		return sOAllLocal;
	}

	public void setOAllLocal(short sOAllLocal) {
		this.sOAllLocal = sOAllLocal;
	}

	public short getOCarIdIni() {
		return sOCarIdIni;
	}

	public void setOCarIdIni(short sOCarIdIni) {
		this.sOCarIdIni = sOCarIdIni;
	}

	public boolean isOInitial() {
		return bOInitial;
	}

	public void setOInitial(boolean bOInitial) {
		this.bOInitial = bOInitial;
	}

}
