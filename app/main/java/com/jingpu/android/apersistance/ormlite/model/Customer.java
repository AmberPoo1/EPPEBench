package com.jingpu.android.apersistance.ormlite.model;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Jing Pu on 2015/10/9.
 */

@DatabaseTable(tableName = "CUSTOMER")
public class Customer {

	// Labels Table Column names
	public static final String COL_C_COMPO = "C_COMPO";
	public static final String COL_C_ID = "C_ID";
	public static final String COL_C_D_ID = "C_D_ID";
	public static final String COL_C_W_ID = "C_W_ID";
	public static final String COL_C_FIRST = "C_FIRST";
	public static final String COL_C_MIDDLE = "C_MIDDLE";
	public static final String COL_C_LAST = "C_LAST";
	public static final String COL_C_STREET_1 = "C_STREET_1";
	public static final String COL_C_STREET_2 = "C_STREET_2";
	public static final String COL_C_CITY = "C_CITY";
	public static final String COL_C_STATE = "C_STATE";
	public static final String COL_C_ZIP = "C_ZIP";
	public static final String COL_C_PHONE = "C_PHONE";
	public static final String COL_C_SINCE = "C_SINCE";
	public static final String COL_C_CREDIT = "C_CREDIT";
	public static final String COL_C_CREDIT_LIM = "C_CREDIT_LIM";
	public static final String COL_C_DISCOUNT = "C_DISCOUNT";
	public static final String COL_C_BALANCE = "C_BALANCE";
	public static final String COL_C_YTD_PAYMENT = "C_YTD_PAYMENT";
	public static final String COL_C_PAYMENT_CNT = "C_PAYMENT_CNT";
	public static final String COL_C_DELIVERY_CNT = "C_DELIVERY_CNT";
	public static final String COL_C_DATA = "C_DATA";
	public static final String COL_C_DATA_INITIAL = "C_DATA_INITIAL";

	// property help us to keep data

    @DatabaseField(id = true, columnName="C_COMPO")
    private String compositeKey;

	@DatabaseField(columnName="C_ID", canBeNull = false)
	private int iCId; //Id

    @DatabaseField(columnName="C_D_ID", canBeNull = false)
	private short sCDId; // Id

    @DatabaseField(columnName="C_W_ID", canBeNull = false)
	private short sCWId; // Id

    @DatabaseField(columnName="C_FIRST", canBeNull = false)
	private String strCFst;

	@DatabaseField(columnName="C_MIDDLE", canBeNull = false)
	private String strCMid;

	@DatabaseField(columnName="C_LAST", canBeNull = false)
	private String strCLst;
	
	@DatabaseField(columnName="C_STREET_1", canBeNull = false)
	private String strCStreet1;

	@DatabaseField(columnName="C_STREET_2", canBeNull = false)
	private String strCStreet2;

	@DatabaseField(columnName="C_CITY", canBeNull = false)
	private String strCCity;

	@DatabaseField(columnName="C_STATE", canBeNull = false)
	private String strCState;

	@DatabaseField(columnName="C_ZIP", canBeNull = false)
	private String strCZip;

	@DatabaseField(columnName="C_PHONE", canBeNull = false)
	private String strCPhone;

	@DatabaseField(columnName="C_SINCE", dataType= DataType.DATE, canBeNull = false)
	private Date tCSince; //TIMESTAMP

	@DatabaseField(columnName="C_CREDIT",dataType=DataType.STRING, canBeNull = false)
	private String strCCredit;

	@DatabaseField(columnName="C_CREDIT_LIMS", canBeNull = false)
	private float fCCreditLim;

	@DatabaseField(columnName="C_DISCOUNT", canBeNull = false)
	private float fCDiscount;

	@DatabaseField(columnName="C_BALANCE", canBeNull = false)
	private float fCBalance;

	@DatabaseField(columnName="C_YTD_PAYMENT", canBeNull = false)
	private float fCYTDPayment;

	@DatabaseField(columnName="C_PAYMENT_CNT", canBeNull = false)
	private int iCPaymentCnt;

	@DatabaseField(columnName="C_DELIVERY_CNT", canBeNull = false)
	private int iCDeliveryCnt;

	@DatabaseField(columnName="C_DATA", canBeNull = false)
	private String strCData;

	@DatabaseField(columnName="C_DATA_INITIAL", canBeNull = false)
	private String strCDataInit;	
	
	public Customer(){

	}

	public void clear(){
        this.setCompositeKey(null);
		this.iCId = 0;
        this.sCDId = 0;
        this.sCWId = 0;
		this.strCFst = null;
		this.strCMid = null;
		this.strCLst = null;
		this.strCStreet1 = null;
		this.strCStreet2 = null;
		this.strCCity = null;
		this.strCState = null;
		this.strCZip = null;
		this.strCPhone = null;
		this.tCSince = null;
		this.fCCreditLim = 0;
		this.fCDiscount = 0;
		this.strCCredit = null;
		this.fCYTDPayment = 0;
		this.iCDeliveryCnt = 0;
		this.iCPaymentCnt = 0;
		this.strCData = null;
	}

    public static String getCompositeKey(Customer obj) {
        return Integer.toString(obj.getCId()) + "-"  + Short.toString(obj.getCDId()) + "-"  + Short.toString(obj.getCWId());
    }

    public String getCompositeKey() {
        return this.compositeKey;
    }

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }
	
	public int getCId() {
		return iCId;
	}

	public void setCId(int iCId) {
		this.iCId = iCId;
	}

    public short getCDId() {
        return sCDId;
    }

    public void setCDId(short sCDId) {
        this.sCDId = sCDId;
    }

    public short getCWId() {
        return sCWId;
    }

    public void setCWId(short sCWId) {
        this.sCWId = sCWId;
    }

	public String getCFst() {
		return strCFst;
	}

	public void setCFst(String strCFst) {
		this.strCFst = strCFst;
	}

	public String getCMid() {
		return strCMid;
	}

	public void setCMid(String strCMid) {
		this.strCMid = strCMid;
	}

	public String getCLst() {
		return strCLst;
	}

	public void setCLst(String strCLst) {
		this.strCLst = strCLst;
	}

	public String getCStreet1() {
		return strCStreet1;
	}

	public void setCStreet1(String strCStreet1) {
		this.strCStreet1 = strCStreet1;
	}

	public String getCStreet2() {
		return strCStreet2;
	}

	public void setCStreet2(String strCStreet2) {
		this.strCStreet2 = strCStreet2;
	}

	public String getCCity() {
		return strCCity;
	}

	public void setCCity(String strCCity) {
		this.strCCity = strCCity;
	}

	public String getCState() {
		return strCState;
	}

	public void setCState(String strCState) {
		this.strCState = strCState;
	}

	public String getCZip() {
		return strCZip;
	}

	public void setCZip(String strCZip) {
		this.strCZip = strCZip;
	}

	public String getCPhone() {
		return strCPhone;
	}

	public void setCPhone(String strCPhone) {
		this.strCPhone = strCPhone;
	}

	public Date getCSince() {
		return tCSince;
	}

	public void setCSince(Date tCSince) {
		this.tCSince = tCSince;
	}

	public String getCCredit() {
		return strCCredit;
	}

	public void setCCredit(String strCCredit) {
		this.strCCredit = strCCredit;
	}

	public float getCCreditLim() {
		return fCCreditLim;
	}

	public void setCCreditLim(float fCCreditLim) {
		this.fCCreditLim = fCCreditLim;
	}

	public float getCDiscount() {
		return fCDiscount;
	}

	public void setCDiscount(float fCDiscount) {
		this.fCDiscount = fCDiscount;
	}

	public float getCBalance() {
		return fCBalance;
	}

	public void setCBalance(float fCBalance) {
		this.fCBalance = fCBalance;
	}

	public float getCYTDPayment() {
		return fCYTDPayment;
	}

	public void setCYTDPayment(float fCYTDPayment) {
		this.fCYTDPayment = fCYTDPayment;
	}

	public int getCPaymentCnt() {
		return iCPaymentCnt;
	}

	public void setCPaymentCnt(int iCPaymentCnt) {
		this.iCPaymentCnt = iCPaymentCnt;
	}

	public int getCDeliveryCnt() {
		return iCDeliveryCnt;
	}

	public void setCDeliveryCnt(int iCDeliveryCnt) {
		this.iCDeliveryCnt = iCDeliveryCnt;
	}

	public String getCData() {
		return strCData;
	}

	public void setCData(String strCData) {
		this.strCData = strCData;
	}

	public String getCDataInit() {
		return strCDataInit;
	}

	public void setCDataInit(String strCDataInit) {
		this.strCDataInit = strCDataInit;
	}
}
