package com.jingpu.android.apersistance.ormlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Jing Pu on 2016/1/31.
 */
@DatabaseTable(tableName = "CATEGORY")
public class Category {

    // Labels: Table Column names
    public static final String COL_C_ID = "C_ID";
    public static final String COL_C_TITLE = "C_TITLE";
    public static final String COL_C_PAGES = "C_PAGES";
    public static final String COL_C_SUBCATS = "C_SUBCATS";
    public static final String COL_C_FILES = "C_FILES";

    // Columns
    @DatabaseField(id = true, columnName="C_ID")
    private long lCid;

    @DatabaseField(columnName="C_TITLE", canBeNull = false)
    private String strCTitle;

    @DatabaseField(columnName="C_PAGES", canBeNull = false)
    private int iCPages;

    @DatabaseField(columnName="C_SUBCATS", canBeNull = false)
    private int iCSubCats;

    @DatabaseField(columnName="C_FILES", canBeNull = false)
    private int iCFiles;

    public Category() {

    }

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
