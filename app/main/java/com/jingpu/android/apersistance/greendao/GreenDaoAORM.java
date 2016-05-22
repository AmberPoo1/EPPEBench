package com.jingpu.android.apersistance.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.BaseBenchmark;
import com.jingpu.android.apersistance.SimpleBenchmark;
import com.jingpu.android.apersistance.greendao.model.CItem;
import com.jingpu.android.apersistance.greendao.model.CItemDao;
import com.jingpu.android.apersistance.greendao.model.Category;
import com.jingpu.android.apersistance.greendao.model.CategoryDao;
import com.jingpu.android.apersistance.greendao.model.DaoMaster;
import com.jingpu.android.apersistance.greendao.model.DaoSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Jing Pu on 2016/1/30.
 */
public class GreenDaoAORM extends SimpleBenchmark {

    private GreenDaoAgent ga = null;

    public GreenDaoAORM(Context context) {
        ga = new GreenDaoAgent(context);

        setupRandom();
    }

    @Override
    public void insert() throws Exception {
        // insert db record: Scale + Scale*AORMTrans

        reportBeforeRun();

        SQLiteDatabase db = ga.getDatabase();
        DaoSession ds = ga.getDaoSession();

        String log = "Parameters[benchmark="
            + AppContext.getInstance().getBenchmark()
            + ", scale=" + AppContext.getInstance().getScale()
            + ", transactions per scale=" + AppContext.getInstance().getAORMTrans()
            + "].\n"
            + "**  Before populateAllTables:" + (new Date()).getTime()
            + ". Table count[category=" + ds.getCategoryDao().count()
            + ", citem=" + ds.getCItemDao().count()
            + "]";

        AppContext.getInstance().SetUIInfo(log);

        categoryAndItemTable(ds);

        log = "**  After populateAllTables:" + (new Date()).getTime()
                + ". Table count[category=" + ds.getCategoryDao().count()
                + ", citem=" + ds.getCItemDao().count()
                + "]";

        AppContext.getInstance().SetUIInfo(log);

        reportAfterRun();
    }

    private void categoryAndItemTable(DaoSession ds) throws Exception {

        // Category table size: scale
        // CItem table size: AORM transactions * scale
        if (null == ds) {
            throw new NullPointerException();
        }

        List<CItem> iList = new ArrayList<CItem>();
        List<Category> cList = new ArrayList<Category>();
        CItem cItem = null;
        Category cg = null;
        int j = 1;

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            cg = new Category();
            cg.setId((long) c);
            cg.setStrCTitle(random.randomAString26_50());
            cg.setICPages(random.randomInt(1, 10000));
            cg.setICSubCats(random.randomInt(1, 5000));
            cg.setICFiles(random.randomInt(1, 20000));
            cList.add(cg);

            for (int i = 1; i <= AppContext.getInstance().getAORMTrans(); i++) {
                cItem = new CItem();
                cItem.setId((long)j++); // IIId
                cItem.setCId((long) c);
                cItem.setCategory(cg);
                cItem.setIImId(random.randomInt(1, 10000));
                cItem.setIName(random.randomAString14_24());
                cItem.setIPrice(Float.parseFloat(random.randomDecimalString(100, 9999, 2)));
                cItem.setIData(random.randomData());
                iList.add(cItem);
            }
        }

        ds.getCategoryDao().insertInTx(cList);
        ds.getCItemDao().insertInTx(iList);
    }

    @Override
    public void update() throws Exception {

        // update db record: Scale + Scale * AORM transactions
        reportBeforeRun();

        if (null == ga) {
            throw new NullPointerException();
        }

        SQLiteDatabase db = ga.getDatabase();
        DaoSession ds = ga.getDaoSession();

        CategoryDao cDao = ds.getCategoryDao();
        QueryBuilder cQb = null;
        CItemDao iDao = ds.getCItemDao();
        QueryBuilder iQb = null;

        List<Category> cList = null;
        List<CItem> iList = null;

        String strCTitle = null;
        int iCPages = 0;
        int iCSubCats = 0;
        int iCFiles = 0;
        int iImId = 0;
        String strIName = null;
        float fIPrice = 0;
        String strIData = null;

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {

            // update Category Table
            cQb = cDao.queryBuilder();
            cQb.where(CategoryDao.Properties.Id.eq((long)c));
            cList = cQb.list();

            strCTitle = this.random.randomAString26_50();
            iCPages = this.random.randomInt(1, 10000);
            iCSubCats = this.random.randomInt(1, 5000);
            iCFiles = this.random.randomInt(1, 20000);

            if (null != cList) {
                for (Category category : cList) {
                    category.setStrCTitle(strCTitle);
                    category.setICPages(iCPages);
                    category.setICSubCats(iCSubCats);
                    category.setICFiles(iCFiles);
                }
            }

            cDao.updateInTx(cList);

            // update CItem Table
            iQb = iDao.queryBuilder();
            iQb.where(CItemDao.Properties.CId.eq((long)c));

            iList = iQb.list();

            iImId = this.random.randomInt(1, 10000);
            strIName = this.random.randomAString14_24();
            fIPrice = Float.parseFloat(this.random.randomDecimalString(100, 9999, 2));
            strIData = this.random.randomData();

            if (null != iList) {
                for (CItem cItem : iList) {
                    cItem.setIImId(iImId);
                    cItem.setIName(strIName);
                    cItem.setIPrice(fIPrice);
                    cItem.setIData(strIData);
                }
            }

            iDao.updateInTx(iList);
        }

        reportAfterRun();
    }

    @Override
    public void select() throws Exception {
        // select db record: Scale + Scale * AORM transactions

        reportBeforeRun();

        if (null == ga) {
            throw new NullPointerException();
        }

        SQLiteDatabase db = ga.getDatabase();
        DaoSession ds = ga.getDaoSession();

        CategoryDao cDao = ds.getCategoryDao();;
        QueryBuilder cQb = null;
        CItemDao iDao = null;
        QueryBuilder iQb = null;
        List<Category> cList = null;
        List<CItem> iList = null;
        Category category = null;
        int size = 0;

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            // Select from Category table
            cQb = cDao.queryBuilder();
            cQb.where(CategoryDao.Properties.Id.eq((long)c));
            cList = cQb.limit(1).list();

            if (null != cList) {
                for (Category cg : cList) {
                    cg.getStrCTitle();
                    cg.getICPages();
                    cg.getICFiles();
                    cg.getICSubCats();
                }
            }

            // Select from CItem table
            iDao = ds.getCItemDao();
            iQb = iDao.queryBuilder();
            iQb.where(CItemDao.Properties.CId.eq((long)c));
            iList = iQb.list();

            if (null != iList) {
                for (CItem cItem : iList) {
                    category = cItem.getCategory();
                    cItem.getIName();
                    cItem.getIImId();
                    cItem.getId();
                    cItem.getIPrice();
                    cItem.getIData();
                }
            }
        }

        reportAfterRun();
    }

    @Override
    public void delete() throws Exception {

        // delete db record: Scale + Scale * AORM transactions

        reportBeforeRun();

        if (null == ga) {
            throw new NullPointerException();
        }

        SQLiteDatabase db = ga.getDatabase();
        DaoSession ds = ga.getDaoSession();

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {

            // Clear CItem table
            ds.getCItemDao().queryBuilder().where(CItemDao.Properties.CId.eq((long)c)).buildDelete().executeDeleteWithoutDetachingEntities();

            // Clear Category Table
            ds.getCategoryDao().queryBuilder().where(CategoryDao.Properties.Id.eq(c)).buildDelete().executeDeleteWithoutDetachingEntities();
        }

        reportAfterRun();
    }

    @Override
    public void initialize() throws Exception {

        reportBeforeRun();

        if (null == ga) {
            throw new NullPointerException();
        }

        // delete old database
        AppContext.getInstance().deleteDatabase(BaseBenchmark.DATABASE_NAME);

        // create tables
        SQLiteDatabase db = ga.getDatabase();
        DaoMaster daoMaster = ga.getDaoMaster();

        daoMaster.createAllTables(db, true);

        reportAfterRun();
    }
}
