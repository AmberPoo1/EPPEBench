package com.jingpu.android.apersistance.sqlite.model;

/**
 * Created by Jing Pu on 2015/10/6.
 */
public class Orders {

	public static final String TABLE = "ORDERS";

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
    //Column(name="O_COMPO")
    private String compositeKey;

    //Id
	//Column(name="O_ID")
	private int iOId;

	//Id
	//Column(name="O_D_ID")
	private short sODId;

	//Id
	//Column(name="O_W_ID")
	private short sOWId;

	//Column(name="O_C_ID")
	private int iOCId;

	//Column(name="O_ENTRY_D")
	private String stOEntryD; // TIMESTAMP Date

	//Column(name="O_CARRIER_ID")
	private short sOCarrierId;

	//Column(name="O_OL_CNT")
	private short sOOlCnt;

	//Column(name="O_ALL_LOCAL")
	private short sOAllLocal;

	//Column(name="O_CARRIER_ID_INITIAL")
	private short sOCarIdIni;

	//Column(name="O_INITIAL", nullable=true)
	private boolean bOInitial;

	public Orders(){

	}

	public void clear(){
        this.setCompositeKey(null);
		this.iOId = 0;
		this.sODId = 0;
		this.sOWId = 0;
		this.iOCId = 0;
		this.stOEntryD = null;
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

	public String getOEntryD() {
		return stOEntryD;
	}

	public void setOEntryD(String stOEntryD) {
		this.stOEntryD = stOEntryD;
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
