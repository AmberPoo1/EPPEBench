package com.jingpu.android.apersistance.realm.model;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Jing Pu on 2016/01/26.
 */

public class Customer extends RealmObject {

    // property help us to keep data
	@PrimaryKey
	private String compositeKey;

    private int iCId; //Id

	private short sCDId; // Id

	private short sCWId; // Id

    @Required
	private String strCFst;

    @Required
    private String strCMid;

    @Required
	private String strCLst;

    @Required
	private String strCStreet1;

    @Required
	private String strCStreet2;

    @Required
	private String strCCity;

    @Required
	private String strCState;

    @Required
	private String strCZip;

    @Required
	private String strCPhone;

    @Required
    private Date tCSince; //TIMESTAMP

    @Required
    private String strCCredit;

    private float fCCreditLim;

    private float fCDiscount;

    private float fCBalance;

    private float fCYTDPayment;

    private int iCPaymentCnt; // Required

    private int iCDeliveryCnt; // Required

    @Required
	private String strCData;

    @Required
    private String strCDataInit;

	public static String getCompositeKey(Customer obj) {
		return Integer.toString(obj.getiCId()) + "-"  + Short.toString(obj.getsCDId()) + "-"  + Short.toString(obj.getsCWId());
	}

    public String getCompositeKey() {
        return this.compositeKey;
    }

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public int getiCId() {
        return iCId;
    }

    public void setiCId(int iCId) {
        this.iCId = iCId;
    }

    public short getsCDId() {
        return sCDId;
    }

    public void setsCDId(short sCDId) {
        this.sCDId = sCDId;
    }

    public short getsCWId() {
        return sCWId;
    }

    public void setsCWId(short sCWId) {
        this.sCWId = sCWId;
    }

    public String getStrCFst() {
        return strCFst;
    }

    public void setStrCFst(String strCFst) {
        this.strCFst = strCFst;
    }

    public String getStrCMid() {
        return strCMid;
    }

    public void setStrCMid(String strCMid) {
        this.strCMid = strCMid;
    }

    public String getStrCLst() {
        return strCLst;
    }

    public void setStrCLst(String strCLst) {
        this.strCLst = strCLst;
    }

    public String getStrCStreet1() {
        return strCStreet1;
    }

    public void setStrCStreet1(String strCStreet1) {
        this.strCStreet1 = strCStreet1;
    }

    public String getStrCStreet2() {
        return strCStreet2;
    }

    public void setStrCStreet2(String strCStreet2) {
        this.strCStreet2 = strCStreet2;
    }

    public String getStrCCity() {
        return strCCity;
    }

    public void setStrCCity(String strCCity) {
        this.strCCity = strCCity;
    }

    public String getStrCState() {
        return strCState;
    }

    public void setStrCState(String strCState) {
        this.strCState = strCState;
    }

    public String getStrCZip() {
        return strCZip;
    }

    public void setStrCZip(String strCZip) {
        this.strCZip = strCZip;
    }

    public String getStrCPhone() {
        return strCPhone;
    }

    public void setStrCPhone(String strCPhone) {
        this.strCPhone = strCPhone;
    }

    public Date gettCSince() {
        return tCSince;
    }

    public void settCSince(Date tCSince) {
        this.tCSince = tCSince;
    }

    public String getStrCCredit() {
        return strCCredit;
    }

    public void setStrCCredit(String strCCredit) {
        this.strCCredit = strCCredit;
    }

    public float getfCCreditLim() {
        return fCCreditLim;
    }

    public void setfCCreditLim(float fCCreditLim) {
        this.fCCreditLim = fCCreditLim;
    }

    public float getfCDiscount() {
        return fCDiscount;
    }

    public void setfCDiscount(float fCDiscount) {
        this.fCDiscount = fCDiscount;
    }

    public float getfCBalance() {
        return fCBalance;
    }

    public void setfCBalance(float fCBalance) {
        this.fCBalance = fCBalance;
    }

    public float getfCYTDPayment() {
        return fCYTDPayment;
    }

    public void setfCYTDPayment(float fCYTDPayment) {
        this.fCYTDPayment = fCYTDPayment;
    }

    public int getiCPaymentCnt() {
        return iCPaymentCnt;
    }

    public void setiCPaymentCnt(int iCPaymentCnt) {
        this.iCPaymentCnt = iCPaymentCnt;
    }

    public int getiCDeliveryCnt() {
        return iCDeliveryCnt;
    }

    public void setiCDeliveryCnt(int iCDeliveryCnt) {
        this.iCDeliveryCnt = iCDeliveryCnt;
    }

    public String getStrCData() {
        return strCData;
    }

    public void setStrCData(String strCData) {
        this.strCData = strCData;
    }

    public String getStrCDataInit() {
        return strCDataInit;
    }

    public void setStrCDataInit(String strCDataInit) {
        this.strCDataInit = strCDataInit;
    }

}
