package com.jingpu.android.apersistance.ormlite;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.BaseBenchmark;
import com.jingpu.android.apersistance.SimpleBenchmark;
import com.jingpu.android.apersistance.ormlite.model.C;
import com.jingpu.android.apersistance.ormlite.model.CItem;
import com.jingpu.android.apersistance.ormlite.model.Category;
import com.jingpu.android.apersistance.ormlite.model.Customer;
import com.jingpu.android.apersistance.ormlite.model.DeliveryOrders;
import com.jingpu.android.apersistance.ormlite.model.DeliveryRequest;
import com.jingpu.android.apersistance.ormlite.model.District;
import com.jingpu.android.apersistance.ormlite.model.History;
import com.jingpu.android.apersistance.ormlite.model.Item;
import com.jingpu.android.apersistance.ormlite.model.NewOrders;
import com.jingpu.android.apersistance.ormlite.model.OrderLine;
import com.jingpu.android.apersistance.ormlite.model.Orders;
import com.jingpu.android.apersistance.ormlite.model.Stock;
import com.jingpu.android.apersistance.ormlite.model.Warehouse;

import org.dacapo.derby.OERandom;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Jing Pu on 2016/1/30.
 */
public class OrmliteAORM extends SimpleBenchmark {

    private OrmliteAgent oa = null;

    public OrmliteAORM(Context context) {
        oa = new OrmliteAgent(context);
        setupRandom();
    }

    @Override
    public void insert() throws Exception {
        // insert db record: Scale + Scale*AORMTrans
        reportBeforeRun();

        if (null == oa) {
            throw new NullPointerException();
        }

        String log = "Parameters[benchmark="
                + AppContext.getInstance().getBenchmark()
                + ", scale=" + AppContext.getInstance().getScale()
                + ", transactions per scale=" + AppContext.getInstance().getAORMTrans()
                + "].\n"
                + "**  Before populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[category=" + oa.getDbHelper().getCategoryDao().countOf()
                + ", citem=" + oa.getDbHelper().getCItemDao().countOf() + "]";
        AppContext.getInstance().SetUIInfo(log);

        categoryAndItemTable();

        log = "**  After populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[category=" + oa.getDbHelper().getCategoryDao().countOf()
                + ", citem=" + oa.getDbHelper().getCItemDao().countOf() +"]";
        AppContext.getInstance().SetUIInfo(log);

        reportAfterRun();
    }

    private void categoryAndItemTable() throws Exception {

        // Category table size: scale
        // CItem table size: AORM transactions * scale
        if (null == oa) {
            throw new NullPointerException();
        }

        final OrmliteDBHelper dbHelper = oa.getDbHelper();
        final OERandom random = this.random;

        TransactionManager.callInTransaction(dbHelper.getConnectionSource(), new Callable<Void>() {
            public Void call() throws Exception {

                OrmliteDBHelper dbHelper = oa.getDbHelper();
                Dao<Category, Long> cgDao = dbHelper.getCategoryDao();
                Dao<CItem, Long> cItemDao = dbHelper.getCItemDao();
                CItem cItem = null;
                Category cg = null;
                int j = 1;

                for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
                    cg = new Category();
                    cg.setlCid(c);
                    cg.setStrCTitle(random.randomAString26_50());
                    cg.setiCPages(random.randomInt(1, 10000));
                    cg.setiCSubCats(random.randomInt(1, 5000));
                    cg.setiCFiles(random.randomInt(1, 20000));
                    cgDao.create(cg);

                    for (int i = 1; i <= AppContext.getInstance().getAORMTrans(); i++) {
                        cItem = new CItem();
                        cItem.setlIId(j++);
                        cItem.setCategory(cg);
                        cItem.setiIImId(random.randomInt(1, 10000));
                        cItem.setStrIName(random.randomAString14_24());
                        cItem.setfIPrice(Float.parseFloat(random.randomDecimalString(100, 9999, 2)));
                        cItem.setiData(random.randomData());
                        cItemDao.create(cItem);
                    }
                }

                return null;
            }
        });
    }

    @Override
    public void update() throws Exception {
        // update db record: Scale + Scale * AORM transactions

        reportBeforeRun();

        if (null == oa) {
            throw new NullPointerException();
        }

        final OrmliteDBHelper dbHelper = oa.getDbHelper();
        final OERandom random = this.random;
        final Dao<Category, Long> cgDao = dbHelper.getCategoryDao();
        final Dao<CItem, Long> cItemDao = dbHelper.getCItemDao();

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            final int cValue = c;
            TransactionManager.callInTransaction(dbHelper.getConnectionSource(), new Callable<Void>() {
                public Void call() throws Exception {

                    // update Category Table
                    Category category = cgDao.queryForId((long)cValue);
                    category.setStrCTitle(random.randomAString26_50());
                    category.setiCPages(random.randomInt(1, 10000));
                    category.setiCSubCats(random.randomInt(1, 5000));
                    category.setiCFiles(random.randomInt(1, 20000));
                    cgDao.update(category);

                    // update CItem Table

                    UpdateBuilder<CItem, Long> ciUb = cItemDao.updateBuilder();
                    Where<CItem, Long> ciWhere = ciUb.where();
                    ciWhere.eq(CItem.COL_I_CAT_ID, cValue);
                    ciUb.updateColumnValue(CItem.COL_I_IM_ID, random.randomInt(1, 10000));
                    ciUb.updateColumnValue(CItem.COL_I_NAME, random.randomAString14_24());
                    ciUb.updateColumnValue(CItem.COL_I_PRICE, Float.parseFloat(random.randomDecimalString(100, 9999, 2)));
                    ciUb.updateColumnValue(CItem.COL_I_DATA, random.randomData());
                    ciUb.update();

                    return null;
                }
            });
        }

        reportAfterRun();
    }

    @Override
    public void select() throws Exception {

        // select db record: Scale + Scale * AORM transactions

        reportBeforeRun();

        if (null == oa) {
            throw new NullPointerException();
        }

        OrmliteDBHelper dbHelper = oa.getDbHelper();
        Dao<Category, Long> cDao = dbHelper.getCategoryDao();
        Category category = null;

        Dao<CItem, Long> cItemDao = dbHelper.getCItemDao();

        List<CItem> ciList = null;

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            // Select from Category table
            category = cDao.queryForId((long)c);

            if (null != category) {
                category.getStrCTitle();
                category.getiCFiles();
                category.getiCPages();
                category.getiCSubCats();
            }

            // Select from CItem table
            ciList = cItemDao.queryForEq(CItem.COL_I_CAT_ID, c);

            if (null != ciList) {
                for (CItem cItem : ciList) {
                    category = cItem.getCategory();
                    cItem.getfIPrice();
                    cItem.getiData();
                    cItem.getlIId();
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

        if (null == oa) {
            throw new NullPointerException();
        }

        OrmliteDBHelper dbHelper = oa.getDbHelper();

        Dao<CItem, Long> cItemDao = dbHelper.getCItemDao();
        DeleteBuilder<CItem, Long> idb = null;
        Where<CItem, Long> iWhere = null;

        Dao<Category, Long> cDao = dbHelper.getCategoryDao();
        Category category = null;

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {

            // Clear CItem table

            idb = cItemDao.deleteBuilder();
            iWhere = idb.where();
            iWhere.eq(CItem.COL_I_CAT_ID, c);
            idb.delete();

            // Clear Category Table
            category = cDao.queryForId((long)c);
            if (null != category) {
                cDao.delete(category);
            }
        }

        reportAfterRun();
    }

    @Override
    public void initialize() throws Exception {
        reportBeforeRun();

        if (null == oa) {
            throw new NullPointerException();
        }

        // delete old database
        AppContext.getInstance().deleteDatabase(BaseBenchmark.DATABASE_NAME);

        // create tables
        ConnectionSource connectionSource = oa.getDbHelper() .getConnectionSource();

        TableUtils.createTable(connectionSource, C.class);
        TableUtils.createTable(connectionSource, Customer.class);
        TableUtils.createTable(connectionSource, DeliveryOrders.class);
        TableUtils.createTable(connectionSource, DeliveryRequest.class);
        TableUtils.createTable(connectionSource, District.class);
        TableUtils.createTable(connectionSource, History.class);
        TableUtils.createTable(connectionSource, Item.class);
        TableUtils.createTable(connectionSource, NewOrders.class);
        TableUtils.createTable(connectionSource, OrderLine.class);
        TableUtils.createTable(connectionSource, Orders.class);
        TableUtils.createTable(connectionSource, Stock.class);
        TableUtils.createTable(connectionSource, Warehouse.class);
        TableUtils.createTable(connectionSource, Category.class);
        TableUtils.createTable(connectionSource, CItem.class);

        reportAfterRun();
    }
}
