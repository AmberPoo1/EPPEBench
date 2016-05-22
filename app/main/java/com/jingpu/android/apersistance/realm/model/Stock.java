package com.jingpu.android.apersistance.realm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


/**
 * Created by Jing Pu on 2016/01/26.
 */

public class Stock extends RealmObject {

	// property help us to keep data
	@PrimaryKey
	private String compositeKey;

    private short sSWId; // Id, Required

    private int iSIId; // Id, Required

	private int iSQuantity; // Required

	@Required
	private String strSDist01;

	@Required
	private String strSDist02;

	@Required
	private String strSDist03;

	@Required
	private String strSDist04;

	@Required
	private String strSDist05;

	@Required
	private String strSDist06;

	@Required
	private String strSDist07;

	@Required
	private String strSDist08;

	@Required
	private String strSDist09;

	@Required
	private String strSDist10;

	private float fSYTD; // Required

	private int iSOrderCnt; // Required

	private int iSRemoteCnt; // Required

	@Required
	private String strSData;

	private int iSQtyInit; // Required

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public String getCompositeKey() {
        return this.compositeKey;
    }

    public static String getCompositeKey(Stock obj) {
        return Integer.toString(obj.getsSWId()) + "-"  + Integer.toString(obj.getiSIId());
    }

	public short getsSWId() {
		return sSWId;
	}

	public void setsSWId(short sSWId) {
		this.sSWId = sSWId;
	}

	public int getiSIId() {
		return iSIId;
	}

	public void setiSIId(int iSIId) {
		this.iSIId = iSIId;
	}

	public int getiSQuantity() {
		return iSQuantity;
	}

	public void setiSQuantity(int iSQuantity) {
		this.iSQuantity = iSQuantity;
	}

	public String getStrSDist01() {
		return strSDist01;
	}

	public void setStrSDist01(String strSDist01) {
		this.strSDist01 = strSDist01;
	}

	public String getStrSDist02() {
		return strSDist02;
	}

	public void setStrSDist02(String strSDist02) {
		this.strSDist02 = strSDist02;
	}

	public String getStrSDist03() {
		return strSDist03;
	}

	public void setStrSDist03(String strSDist03) {
		this.strSDist03 = strSDist03;
	}

	public String getStrSDist04() {
		return strSDist04;
	}

	public void setStrSDist04(String strSDist04) {
		this.strSDist04 = strSDist04;
	}

	public String getStrSDist05() {
		return strSDist05;
	}

	public void setStrSDist05(String strSDist05) {
		this.strSDist05 = strSDist05;
	}

	public String getStrSDist06() {
		return strSDist06;
	}

	public void setStrSDist06(String strSDist06) {
		this.strSDist06 = strSDist06;
	}

	public String getStrSDist07() {
		return strSDist07;
	}

	public void setStrSDist07(String strSDist07) {
		this.strSDist07 = strSDist07;
	}

	public String getStrSDist08() {
		return strSDist08;
	}

	public void setStrSDist08(String strSDist08) {
		this.strSDist08 = strSDist08;
	}

	public String getStrSDist09() {
		return strSDist09;
	}

	public void setStrSDist09(String strSDist09) {
		this.strSDist09 = strSDist09;
	}

	public String getStrSDist10() {
		return strSDist10;
	}

	public void setStrSDist10(String strSDist10) {
		this.strSDist10 = strSDist10;
	}

	public float getfSYTD() {
		return fSYTD;
	}

	public void setfSYTD(float fSYTD) {
		this.fSYTD = fSYTD;
	}

	public int getiSOrderCnt() {
		return iSOrderCnt;
	}

	public void setiSOrderCnt(int iSOrderCnt) {
		this.iSOrderCnt = iSOrderCnt;
	}

	public int getiSRemoteCnt() {
		return iSRemoteCnt;
	}

	public void setiSRemoteCnt(int iSRemoteCnt) {
		this.iSRemoteCnt = iSRemoteCnt;
	}

	public String getStrSData() {
		return strSData;
	}

	public void setStrSData(String strSData) {
		this.strSData = strSData;
	}

	public int getiSQtyInit() {
		return iSQtyInit;
	}

	public void setiSQtyInit(int iSQtyInit) {
		this.iSQtyInit = iSQtyInit;
	}
}
