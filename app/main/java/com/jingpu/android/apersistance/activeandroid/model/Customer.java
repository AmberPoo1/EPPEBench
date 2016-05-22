package com.jingpu.android.apersistance.activeandroid.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by Jing Pu on 2016/01/3.
 */

@Table(name = "CUSTOMER")
public class Customer extends Model {

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
    @Column(name="C_COMPO", unique = true)
    private String compositeKey;

    @Column(name="C_ID", notNull = true)
    private int iCId; //Id

    @Column(name="C_D_ID", notNull = true)
	private short sCDId; // Id

    @Column(name="C_W_ID", notNull = true)
	private short sCWId; // Id

    @Column(name="C_FIRST", notNull = true)
	private String strCFst;

    @Column(name="C_MIDDLE", notNull = true)
    private String strCMid;

    @Column(name="C_LAST", notNull = true)
	private String strCLst;

    @Column(name="C_STREET_1", notNull = true)
	private String strCStreet1;

    @Column(name="C_STREET_2", notNull = true)
	private String strCStreet2;

    @Column(name="C_CITY", notNull = true)
	private String strCCity;

    @Column(name="C_STATE", notNull = true)
	private String strCState;

    @Column(name="C_ZIP", notNull = true)
	private String strCZip;

    @Column(name="C_PHONE", notNull = true)
	private String strCPhone;

    @Column(name="C_SINCE", notNull = true)
    private Date tCSince; //TIMESTAMP

    @Column(name="C_CREDIT", notNull = true)
    private String strCCredit;

    @Column(name="C_CREDIT_LIM", notNull = true)
    private float fCCreditLim;

    @Column(name="C_DISCOUNT", notNull = true)
    private float fCDiscount;

    @Column(name="C_BALANCE", notNull = true)
    private float fCBalance;

    @Column(name="C_YTD_PAYMENT", notNull = true)
    private float fCYTDPayment;

    @Column(name="C_PAYMENT_CNT", notNull = true)
    private int iCPaymentCnt;

    @Column(name="C_DELIVERY_CNT", notNull = true)
    private int iCDeliveryCnt;

    @Column(name="C_DATA", notNull = true)
	private String strCData;

    @Column(name="C_DATA_INITIAL", notNull = true)
    private String strCDataInit;

    public Customer() {
        super();
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

	public void setCBalance(float bgCBalance) {
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
