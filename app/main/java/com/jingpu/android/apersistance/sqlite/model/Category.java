package com.jingpu.android.apersistance.sqlite.model;

/**
 * Created by Jing Pu on 2016/1/31.
 */
public class Category {

    // Table Name
    public static final String TABLE = "CATEGORY";

    // Labels: Table Column names
    public static final String COL_C_ID = "C_ID";
    public static final String COL_C_TITLE = "C_TITLE";
    public static final String COL_C_PAGES = "C_PAGES";
    public static final String COL_C_SUBCATS = "C_SUBCATS";
    public static final String COL_C_FILES = "C_FILES";

    // Columns
    private long lCId;
    private String strCTitle;
    private int iCPages;
    private int iCSubCats;
    private int iCFiles;

    public Category() {

    }

    public long getlCId() {
        return lCId;
    }

    public void setlCId(long lCId) {
        this.lCId = lCId;
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
