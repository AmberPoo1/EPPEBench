package com.jingpu.android.apersistance.realm.model;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Jing Pu on 2016/01/26.
 */

public class Orders extends RealmObject {

	// property help us to keep data
	@PrimaryKey
	private String compositeKey;

    private int iOId; //Id, Required

    private short sODId; // Id, Required

    private short sOWId;// Id, Required

    private int iOCId; // Required

    @Required
	private Date tOEntryD; // TIMESTAMP

	private short sOCarrierId;

	private short sOOlCnt; // Required

	private short sOAllLocal; // Required

	private short sOCarIdIni;

	private boolean bOInitial;

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public String getCompositeKey() {
        return this.compositeKey;
    }

    public static String getCompositeKey(Orders obj) {
        return Integer.toString(obj.getiOId()) + "-"  + Short.toString(obj.getsODId()) + "-"  + Short.toString(obj.getsOWId());
    }

	public int getiOId() {
		return iOId;
	}

	public void setiOId(int iOId) {
		this.iOId = iOId;
	}

	public short getsODId() {
		return sODId;
	}

	public void setsODId(short sODId) {
		this.sODId = sODId;
	}

	public short getsOWId() {
		return sOWId;
	}

	public void setsOWId(short sOWId) {
		this.sOWId = sOWId;
	}

	public int getiOCId() {
		return iOCId;
	}

	public void setiOCId(int iOCId) {
		this.iOCId = iOCId;
	}

	public Date gettOEntryD() {
		return tOEntryD;
	}

	public void settOEntryD(Date tOEntryD) {
		this.tOEntryD = tOEntryD;
	}

	public short getsOCarrierId() {
		return sOCarrierId;
	}

	public void setsOCarrierId(short sOCarrierId) {
		this.sOCarrierId = sOCarrierId;
	}

	public short getsOOlCnt() {
		return sOOlCnt;
	}

	public void setsOOlCnt(short sOOlCnt) {
		this.sOOlCnt = sOOlCnt;
	}

	public short getsOAllLocal() {
		return sOAllLocal;
	}

	public void setsOAllLocal(short sOAllLocal) {
		this.sOAllLocal = sOAllLocal;
	}

	public short getsOCarIdIni() {
		return sOCarIdIni;
	}

	public void setsOCarIdIni(short sOCarIdIni) {
		this.sOCarIdIni = sOCarIdIni;
	}

	public boolean isbOInitial() {
		return bOInitial;
	}

	public void setbOInitial(boolean bOInitial) {
		this.bOInitial = bOInitial;
	}

}
