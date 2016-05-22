package com.jingpu.android.apersistance.realm.model;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Jing Pu on 2016/01/3.
 */

public class OrderLine extends RealmObject {

    // property help us to keep data
	@PrimaryKey
	private String compositeKey;

    private int iOlOId; //Id, Required

    private short sOlDId; //Id, Required

    private short sOlWId; //Id, Required

    private short sOlNumber; //Id, Required

	private int iOlIId; // Required

	private short sOlSupplyWId; // Required

    private Date tOlDeliveryD; //TIMESTAMP

	private short sOlQuantity; // Required

    private float fOlAmount; // Required

    @Required
    private String strOlDistInfo;

    private Date tOlDlvDIni; //TIMESTAMP

    private boolean bOlInitial;

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public String getCompositeKey() {
        return this.compositeKey;
    }

    public static String getCompositeKey(OrderLine obj) {
        return Integer.toString(obj.getiOlOId()) + "-" + Short.toString(obj.getsOlDId()) + "-"  + Short.toString(obj.getsOlWId()) + "-"  + Short.toString(obj.getsOlNumber());
    }

    public int getiOlOId() {
        return iOlOId;
    }

    public void setiOlOId(int iOlOId) {
        this.iOlOId = iOlOId;
    }

    public short getsOlDId() {
        return sOlDId;
    }

    public void setsOlDId(short sOlDId) {
        this.sOlDId = sOlDId;
    }

    public short getsOlWId() {
        return sOlWId;
    }

    public void setsOlWId(short sOlWId) {
        this.sOlWId = sOlWId;
    }

    public short getsOlNumber() {
        return sOlNumber;
    }

    public void setsOlNumber(short sOlNumber) {
        this.sOlNumber = sOlNumber;
    }

    public int getiOlIId() {
        return iOlIId;
    }

    public void setiOlIId(int iOlIId) {
        this.iOlIId = iOlIId;
    }

    public short getsOlSupplyWId() {
        return sOlSupplyWId;
    }

    public void setsOlSupplyWId(short sOlSupplyWId) {
        this.sOlSupplyWId = sOlSupplyWId;
    }

    public Date gettOlDeliveryD() {
        return tOlDeliveryD;
    }

    public void settOlDeliveryD(Date tOlDeliveryD) {
        this.tOlDeliveryD = tOlDeliveryD;
    }

    public short getsOlQuantity() {
        return sOlQuantity;
    }

    public void setsOlQuantity(short sOlQuantity) {
        this.sOlQuantity = sOlQuantity;
    }

    public float getfOlAmount() {
        return fOlAmount;
    }

    public void setfOlAmount(float fOlAmount) {
        this.fOlAmount = fOlAmount;
    }

    public String getStrOlDistInfo() {
        return strOlDistInfo;
    }

    public void setStrOlDistInfo(String strOlDistInfo) {
        this.strOlDistInfo = strOlDistInfo;
    }

    public Date gettOlDlvDIni() {
        return tOlDlvDIni;
    }

    public void settOlDlvDIni(Date tOlDlvDIni) {
        this.tOlDlvDIni = tOlDlvDIni;
    }

    public boolean isbOlInitial() {
        return bOlInitial;
    }

    public void setbOlInitial(boolean bOlInitial) {
        this.bOlInitial = bOlInitial;
    }

}
