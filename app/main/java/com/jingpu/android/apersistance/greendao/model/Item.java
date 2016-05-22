package com.jingpu.android.apersistance.greendao.model;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "ITEM".
 */
public class Item {

    private Long id;
    private int iImId;
    /** Not-null value. */
    private String iName;
    private float iPrice;
    /** Not-null value. */
    private String iData;

    public Item() {
    }

    public Item(Long id) {
        this.id = id;
    }

    public Item(Long id, int iImId, String iName, float iPrice, String iData) {
        this.id = id;
        this.iImId = iImId;
        this.iName = iName;
        this.iPrice = iPrice;
        this.iData = iData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIImId() {
        return iImId;
    }

    public void setIImId(int iImId) {
        this.iImId = iImId;
    }

    /** Not-null value. */
    public String getIName() {
        return iName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setIName(String iName) {
        this.iName = iName;
    }

    public float getIPrice() {
        return iPrice;
    }

    public void setIPrice(float iPrice) {
        this.iPrice = iPrice;
    }

    /** Not-null value. */
    public String getIData() {
        return iData;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setIData(String iData) {
        this.iData = iData;
    }

}
