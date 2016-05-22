package com.jingpu.android.apersistance.sugarorm;

import android.content.Context;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.BaseBenchmark;
import com.jingpu.android.apersistance.SimpleBenchmark;
import com.jingpu.android.apersistance.sugarorm.model.CItem;
import com.jingpu.android.apersistance.sugarorm.model.Category;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jing Pu on 2016/1/30.
 */
public class SugarOrmAORM extends SimpleBenchmark {

    public SugarOrmAORM(Context context) {
        setupRandom();
    }

    @Override
    public void insert() throws Exception {
        // insert db record: Scale + Scale*AORMTrans

        reportBeforeRun();

        String log = "Parameters[benchmark="
                + AppContext.getInstance().getBenchmark()
                + ", scale=" + AppContext.getInstance().getScale()
                + ", transactions per scale=" + AppContext.getInstance().getAORMTrans()
                + "].\n"
                + "**  Before populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[category=" + Category.count(Category.class)
                + ", citem=" + CItem.count(CItem.class) +"]";
        AppContext.getInstance().SetUIInfo(log);

        categoryAndItemTable();

        log = "**  After populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[category=" + Category.count(Category.class)
                + ", citem=" + CItem.count(CItem.class) +"]";
        AppContext.getInstance().SetUIInfo(log);
        reportAfterRun();
    }

    private void categoryAndItemTable() throws Exception {

        // Category table size: scale
        // CItem table size: AORM transactions * scale
        List<Category> cList = new ArrayList<Category>();
        List<CItem> iList = new ArrayList<CItem>();
        Category cg = null;
        CItem cItem = null;
        int j = 1;
        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            cg = new Category();
            cg.setId((long)c); //CId
            cg.setStrCTitle(random.randomAString26_50());
            cg.setiCPages(random.randomInt(1, 10000));
            cg.setiCSubCats(random.randomInt(1, 5000));
            cg.setiCFiles(random.randomInt(1, 20000));
            cList.add(cg);

            for (int i = 1; i <= AppContext.getInstance().getAORMTrans(); i++) {
                cItem = new CItem();
                cItem.setId((long)j++); //IId
                cItem.setCategory(cg);
                cItem.setiIImId(random.randomInt(1, 10000));
                cItem.setStrIName(random.randomAString14_24());
                cItem.setfIPrice(Float.parseFloat(random.randomDecimalString(100, 9999, 2)));
                cItem.setiData(random.randomData());
                iList.add(cItem);
            }
        }

        Category.saveInTx(cList);
        CItem.saveInTx(iList);
    }
    @Override
    public void update() throws Exception {

        // update db record: Scale + Scale * AORM transactions
        reportBeforeRun();

        Category cg = null;
        List<CItem> iList = null;
        String strCTitle = null;
        int iCPages = 0;
        int iCSubCats = 0;
        int iCFiles = 0;
        int iIImId = 0;
        String StrIName = null;
        float fIPrice = 0;
        String strIData = null;

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            // update Category Table
            cg = Category.findById(Category.class, c);

            strCTitle = this.random.randomAString26_50();
            iCPages = this.random.randomInt(1, 10000);
            iCSubCats = this.random.randomInt(1, 5000);
            iCFiles = this.random.randomInt(1, 20000);

            cg.setStrCTitle(strCTitle);
            cg.setiCPages(iCPages);
            cg.setiCSubCats(iCSubCats);
            cg.setiCFiles(iCFiles);
            Category.save(cg);

            // update CItem Table
            iList = CItem.find(CItem.class, "category = ? ", String.valueOf(c));

            iIImId = this.random.randomInt(1, 10000);
            StrIName = this.random.randomAString14_24();
            fIPrice = Float.parseFloat(this.random.randomDecimalString(100, 9999, 2));
            strIData = this.random.randomData();

            for (CItem cItem : iList) {
                cItem.setiIImId(iIImId);
                cItem.setStrIName(StrIName);
                cItem.setfIPrice(fIPrice);
                cItem.setiData(strIData);
            }
            CItem.saveInTx(iList);
        }

        reportAfterRun();
    }

    @Override
    public void select() throws Exception {

        // select db record: Scale + Scale * AORM transactions
        reportBeforeRun();

        List<CItem> iList = null;
        Category category = null;

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            // Select from Category table
            category = Category.findById(Category.class, c);
            if (null != category) {
                category.getStrCTitle();
                category.getiCPages();
                category.getiCFiles();
                category.getiCSubCats();
            }

            // Select from CItem table
            iList = CItem.find(CItem.class, "category = ? ", String.valueOf(c));

            if (null != iList) {
                for (CItem cItem : iList) {
                    cItem.getCategory();
                    cItem.getfIPrice();
                    cItem.getiData();
                    cItem.getId();
                    cItem.getiIImId();
                    cItem.getStrIName();
                }
            }
        }

        reportAfterRun();
    }

    @Override
    public void delete() throws Exception {
        // delete db record: Scale + Scale * AORM transactions

        reportBeforeRun();

        int delNum = 0;
        boolean deleted = false;
        Category category = null;
        List<CItem> iList = new ArrayList<CItem>();

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            // Clear CItem table
            iList = CItem.find(CItem.class, "category = ? ", String.valueOf(c));
            delNum = CItem.deleteInTx(iList);

            // Clear Category Table
            category = Category.findById(Category.class, c);
            deleted = Category.delete(category);
        }

        reportAfterRun();
    }

    @Override
    public void initialize() throws Exception {
        reportBeforeRun();

        // delete old database
        AppContext.getInstance().deleteDatabase(BaseBenchmark.DATABASE_NAME);

        // init configuration
        SugarContext.init(AppContext.getInstance());

        // don't need to create tables
        reportAfterRun();
    }
}
