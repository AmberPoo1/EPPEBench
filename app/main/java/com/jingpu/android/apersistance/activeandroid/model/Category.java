package com.jingpu.android.apersistance.activeandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Jing Pu on 2016/1/31.
 */
@Table(name = "CATEGORY")
public class Category extends Model {

    // Labels: Table Column names
    public static final String COL_C_ID = "C_ID";
    public static final String COL_C_TITLE = "C_TITLE";
    public static final String COL_C_PAGES = "C_PAGES";
    public static final String COL_C_SUBCATS = "C_SUBCATS";
    public static final String COL_C_FILES = "C_FILES";

    // Columns
    @Column(name="C_ID", unique = true)
    private long lCid;

    @Column(name="C_TITLE", notNull = true)
    private String strCTitle;

    @Column(name="C_PAGES", notNull = true)
    private int iCPages;

    @Column(name="C_SUBCATS", notNull = true)
    private int iCSubCats;

    @Column(name="C_FILES", notNull = true)
    private int iCFiles;

    public Category() {
        super();
    }

    public long getLCid() {
        return lCid;
    }

    public void setLCid(long lCid) {
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
