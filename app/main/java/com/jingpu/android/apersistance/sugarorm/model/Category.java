package com.jingpu.android.apersistance.sugarorm.model;


import com.orm.SugarRecord;

/**
 * Created by Jing Pu on 2016/1/31.
 */
//@Table(name = "CATEGORY")
public class Category extends SugarRecord {

    // property help us to keep data
    // super.id: C_ID

    //@Column(name="C_TITLE", notNull = true)
    private String strCTitle;

    //@Column(name="C_PAGES", notNull = true)
    private int iCPages;

    //@Column(name="C_SUBCATS", notNull = true)
    private int iCSubCats;

    //@Column(name="C_FILES", notNull = true)
    private int iCFiles;

    public Category() {
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
