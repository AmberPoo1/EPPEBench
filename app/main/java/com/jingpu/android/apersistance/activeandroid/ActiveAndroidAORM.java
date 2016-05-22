package com.jingpu.android.apersistance.activeandroid;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.BaseBenchmark;
import com.jingpu.android.apersistance.SimpleBenchmark;
import com.jingpu.android.apersistance.activeandroid.model.CItem;
import com.jingpu.android.apersistance.activeandroid.model.Category;

import java.util.Date;
import java.util.List;

/**
 * Created by Jing Pu on 2016/1/30.
 */
public class ActiveAndroidAORM extends SimpleBenchmark {

    public ActiveAndroidAORM(Context context) {
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
                + "**  Before populateAllTables:" + (new Date()).getTime()  // System.currentTimeMillis()
                + ". Table count[category=" + new Select().from(Category.class).count()
                + ", citem=" + new Select().from(CItem.class).count() + "]";
        AppContext.getInstance().SetUIInfo(log);

        categoryAndItemTable();

        log = "**  After populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[category=" + new Select().from(Category.class).count()
                + ", citem=" + new Select().from(CItem.class).count() +"]";
        AppContext.getInstance().SetUIInfo(log);

        reportAfterRun();
    }

    private void categoryAndItemTable() throws Exception {

        // Category table size: scale
        // CItem table size: AORM transactions * scale
        Category cg = null;
        CItem cItem = null;
        ActiveAndroid.beginTransaction();

        long insertId = -1;
        int j = 1;

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            cg = new Category();
            cg.setLCid(c);
            cg.setStrCTitle(random.randomAString26_50());
            cg.setiCPages(random.randomInt(1, 10000));
            cg.setiCSubCats(random.randomInt(1, 5000));
            cg.setiCFiles(random.randomInt(1, 20000));
            insertId = cg.save();

            for (int i = 1; i <= AppContext.getInstance().getAORMTrans(); i++) {
                cItem = new CItem();
                cItem.setLIId(j++);
                cItem.setCategory(cg);
                cItem.setiIImId(random.randomInt(1, 10000));
                cItem.setStrIName(random.randomAString14_24());
                cItem.setfIPrice(Float.parseFloat(random.randomDecimalString(100, 9999, 2)));
                cItem.setiData(random.randomData());
                insertId = cItem.save();
            }
        }

        ActiveAndroid.setTransactionSuccessful();

        ActiveAndroid.endTransaction();
    }

    @Override
    public void update() throws Exception {
        // update db record: Scale + Scale * AORM transactions
        reportBeforeRun();

        Category cg = null;
        CItem cItem = null;

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {

            ActiveAndroid.beginTransaction();

            // update Category Table
            cg = Category.load(Category.class, c);
            cg.setStrCTitle(this.random.randomAString26_50());
            cg.setiCPages(this.random.randomInt(1, 10000));
            cg.setiCSubCats(this.random.randomInt(1, 5000));
            cg.setiCFiles(this.random.randomInt(1, 20000));
            cg.save();

            // update CItem Table
            new Update(CItem.class).set("I_IM_ID = ?, I_NAME=?, I_PRICE=?, I_DATA=?",
                    this.random.randomInt(1, 10000),
                    this.random.randomAString14_24(),
                    this.random.randomDecimalString(100, 9999, 2),
                    this.random.randomData())
                    .where("Category = ?", c).execute();

            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
        }

        reportAfterRun();
    }

    @Override
    public void select() throws Exception {
        // select db record: Scale + Scale * AORM transactions

        reportBeforeRun();

        Category category = null;
        List<Category> cList = null;
        List<CItem> ciList = null;

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            // Select from Category table
            Category cg = Category.load(Category.class, c);

            cg.getStrCTitle();
            cg.getiCPages();
            cg.getiCFiles();
            cg.getiCSubCats();

            // Select from CItem table
            ciList = new Select().from(CItem.class).where("Category = ?", c).execute();
            if (null != ciList) {
                for (CItem cItem: ciList) {
                    category = cItem.getCategory();
                    cItem.getStrIName();
                    cItem.getiIImId();
                    cItem.getLIId();
                    cItem.getfIPrice();
                    cItem.getiData();
                }
            }
        }

        reportAfterRun();
    }

    @Override
    public void delete() throws Exception {

        // delete db record: Scale + Scale * AORM transactions
        reportBeforeRun();

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {

            // Clear CItem table
            new Delete().from(CItem.class).where("Category = ?", c).execute();

            // Clear Category
            Category.delete(Category.class, c);
        }

        reportAfterRun();
    }

    @Override
    public void initialize() throws Exception {

        reportBeforeRun();

        // delete old database
        AppContext.getInstance().deleteDatabase(BaseBenchmark.DATABASE_NAME);

        // init configuration
        // it can only been called once
        Configuration dbConfiguration = new Configuration.Builder(AppContext.getInstance())
                .setDatabaseName(BaseBenchmark.DATABASE_NAME).create();
        ActiveAndroid.initialize(dbConfiguration);

        // don't need to create tables
        reportAfterRun();
    }
}
