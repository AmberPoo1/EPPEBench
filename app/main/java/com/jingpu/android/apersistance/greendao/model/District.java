package com.jingpu.android.apersistance.greendao.model;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "DISTRICT".
 */
public class District {

    private String dCompo;
    private short dId;
    private short dWId;
    /** Not-null value. */
    private String dName;
    /** Not-null value. */
    private String dStreet1;
    /** Not-null value. */
    private String dStreet2;
    /** Not-null value. */
    private String dCity;
    /** Not-null value. */
    private String dState;
    /** Not-null value. */
    private String dZip;
    private float dTax;
    private float dYtd;
    private int dNextOId;

    public District() {
    }

    public District(String dCompo) {
        this.dCompo = dCompo;
    }

    public District(String dCompo, short dId, short dWId, String dName, String dStreet1, String dStreet2, String dCity, String dState, String dZip, float dTax, float dYtd, int dNextOId) {
        this.dCompo = dCompo;
        this.dId = dId;
        this.dWId = dWId;
        this.dName = dName;
        this.dStreet1 = dStreet1;
        this.dStreet2 = dStreet2;
        this.dCity = dCity;
        this.dState = dState;
        this.dZip = dZip;
        this.dTax = dTax;
        this.dYtd = dYtd;
        this.dNextOId = dNextOId;
    }

    public void clear(){
        this.dCompo = null;
        this.dWId = 0;
        this.dId = 0;
        this.dName = null;
        this.dStreet1 = null;
        this.dStreet2 = null;
        this.dCity = null;
        this.dState = null;
        this.dZip = null;
        this.dTax = 0;
        this.dYtd = 0;
    }

    public static String getDCompo(District obj) {
        return Short.toString(obj.getDWId()) + "-" + Short.toString(obj.getDId());
    }

    public String getDCompo() {
        return dCompo;
    }

    public void setDCompo(String dCompo) {
        this.dCompo = dCompo;
    }

    public short getDId() {
        return dId;
    }

    public void setDId(short dId) {
        this.dId = dId;
    }

    public short getDWId() {
        return dWId;
    }

    public void setDWId(short dWId) {
        this.dWId = dWId;
    }

    /** Not-null value. */
    public String getDName() {
        return dName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDName(String dName) {
        this.dName = dName;
    }

    /** Not-null value. */
    public String getDStreet1() {
        return dStreet1;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDStreet1(String dStreet1) {
        this.dStreet1 = dStreet1;
    }

    /** Not-null value. */
    public String getDStreet2() {
        return dStreet2;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDStreet2(String dStreet2) {
        this.dStreet2 = dStreet2;
    }

    /** Not-null value. */
    public String getDCity() {
        return dCity;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDCity(String dCity) {
        this.dCity = dCity;
    }

    /** Not-null value. */
    public String getDState() {
        return dState;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDState(String dState) {
        this.dState = dState;
    }

    /** Not-null value. */
    public String getDZip() {
        return dZip;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDZip(String dZip) {
        this.dZip = dZip;
    }

    public float getDTax() {
        return dTax;
    }

    public void setDTax(float dTax) {
        this.dTax = dTax;
    }

    public float getDYtd() {
        return dYtd;
    }

    public void setDYtd(float dYtd) {
        this.dYtd = dYtd;
    }

    public int getDNextOId() {
        return dNextOId;
    }

    public void setDNextOId(int dNextOId) {
        this.dNextOId = dNextOId;
    }

}
