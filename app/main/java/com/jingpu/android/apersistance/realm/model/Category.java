package com.jingpu.android.apersistance.realm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Jing Pu on 2016/2/1.
 */
public class Category extends RealmObject {

    // property help us to keep data
    @PrimaryKey
    private long lCid;

    @Required
    private String strCTitle;

    private int iCPages;

    private int iCSubCats;

    private int iCFiles;

    public long getlCid() {
        return lCid;
    }

    public void setlCid(long lCid) {
        this.lCid = lCid;
    }

    public String getStrCTitle() {
        return strCTitle;
    }

    public void setStrCTitle(String strCTitle) {
        this.strCTitle = strCTitle;
    }

    public int getiCPages() {
        return iCPages;
    }

    public void setiCPages(int iCPages) {
        this.iCPages = iCPages;
    }

    public int getiCSubCats() {
        return iCSubCats;
    }

    public void setiCSubCats(int iCSubCats) {
        this.iCSubCats = iCSubCats;
    }

    public int getiCFiles() {
        return iCFiles;
    }

    public void setiCFiles(int iCFiles) {
        this.iCFiles = iCFiles;
    }
}
