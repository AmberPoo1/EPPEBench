package com.jingpu.android.apersistance.sqlite.model;

/**
 * Created by Jing Pu on 2015/10/3.
 */
public class Customer {

	public static final String TABLE = "CUSTOMER";

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

	//Column(name="C_COMPO")
	private String compositeKey;

	//Id
	//Column(name="C_ID")
	private int iCId;
	
	//Id
	//Column(name="C_D_ID")
	private short sCDId;

	//Id
	//Column(name="C_W_ID")
	private short sCWId;
	
	//Column(name="C_FIRST")
	private String strCFst;
	
	//Column(name="C_MIDDLE")
	private String strCMid;
	
	//Column(name="C_LAST")
	private String strCLst;
	
	//private Address address;
	
	//Column(name="C_STREET_1")
	private String strCStreet1;
	
	//Column(name="C_STREET_2")
	private String strCStreet2;
	
	//Column(name="C_CITY")
	private String strCCity;
	
	//Column(name="C_STATE")
	private String strCState;
	
	//Column(name="C_ZIP")
	private String strCZip;
	
	//Column(name="C_PHONE")
	private String strCPhone;
	
	//Column(name="C_SINCE")
	private String stCSince; //TIMESTAMP Date
	
	//Column(name="C_CREDIT")
	private String strCCredit;
	
	//Column(name="C_CREDIT_LIM")
	private float fCCreditLim;
	
	//Column(name="C_DISCOUNT")
	private float fCDiscount;
	
	//Column(name="C_BALANCE")
	private float fCBalance;
	
	//Column(name="C_YTD_PAYMENT")
	private float fCYTDPayment;
	
	//Column(name="C_PAYMENT_CNT")
	private int iCPaymentCnt;
	
	//Column(name="C_DELIVERY_CNT")
	private int iCDeliveryCnt;
	
	//Column(name="C_DATA")
	private String strCData;
	
	//Column(name="C_DATA_INITIAL")
	private String strCDataInit;	
	
	public Customer(){
		
	}
	
	public void clear(){
        this.compositeKey = null;
		this.sCDId = 0;
		this.sCWId = 0;
		this.iCId = 0;
		this.strCFst = null;
		this.strCMid = null;
		this.strCLst = null;
		this.strCStreet1 = null;
		this.strCStreet2 = null;
		this.strCCity = null;
		this.strCState = null;
		this.strCZip = null;
		this.strCPhone = null;
		this.stCSince = null;
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

	public String getCSince() {
		return stCSince;
	}

	public void setCSince(String stCSince) {
		this.stCSince = stCSince;
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
