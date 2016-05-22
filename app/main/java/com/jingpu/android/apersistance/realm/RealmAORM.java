package com.jingpu.android.apersistance.realm;

import android.content.Context;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.SimpleBenchmark;
import com.jingpu.android.apersistance.realm.model.CItem;
import com.jingpu.android.apersistance.realm.model.Category;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Jing Pu on 2016/1/30.
 */
public class RealmAORM extends SimpleBenchmark {

    private RealmAgent ra = null;

    public RealmAORM(Context context) {
        ra = new RealmAgent(context);
        setupRandom();
    }

    @Override
    public void insert() throws Exception {
        // insert db record: Scale + Scale*AORMTrans
        reportBeforeRun();

        if (null == ra) {
            throw new NullPointerException();
        }

        Realm realm = null;
        String log = null;
        try {
            realm = ra.getRealmInstance();
            log = "Parameters[benchmark="
            + AppContext.getInstance().getBenchmark()
            + ", scale=" + AppContext.getInstance().getScale()
            + ", transactions per scale=" + AppContext.getInstance().getAORMTrans()
            + "].\n"
            + "**  Before populateAllTables:" + (new Date()).getTime()
            + ". Table count[category=" + realm.where(Category.class).count()
            + ", citem=" + realm.where(CItem.class).count() +"]";
            AppContext.getInstance().SetUIInfo(log);
        } finally {
            if (null != realm) {
                realm.close();
            }
        }

        categoryAndItemTable();

        try {
            realm = ra.getRealmInstance();
            log = "**  After populateAllTables:" + (new Date()).getTime()
                    + ". Table count[category=" + realm.where(Category.class).count()
                    + ", citem=" + realm.where(CItem.class).count() + "]";
            AppContext.getInstance().SetUIInfo(log);
        
        } finally {
            if (null != realm) {
                realm.close();
            }
        }

        reportAfterRun();
    }

    private void categoryAndItemTable() throws Exception {

        // Category table size: scale
        // CItem table size: AORM transactions * scale

        if (null == ra) {
            throw new NullPointerException();
        }

        Category cg = null;
        CItem cItem = null;
        Realm realm = null;
        int j = 1;

        try {
            realm = ra.getRealmInstance();
            realm.beginTransaction();

            for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
                cg = realm.createObject(Category.class);
                cg.setlCid(c);
                cg.setStrCTitle(random.randomAString26_50());
                cg.setiCPages(random.randomInt(1, 10000));
                cg.setiCSubCats(random.randomInt(1, 5000));
                cg.setiCFiles(random.randomInt(1, 20000));

                for (int i = 1; i <= AppContext.getInstance().getAORMTrans(); i++) {
                    cItem = realm.createObject(CItem.class);
                    cItem.setlIId(j++);
                    cItem.setCategory(cg);
                    cItem.setiIImId(random.randomInt(1, 10000));
                    cItem.setStrIName(random.randomAString14_24());
                    cItem.setfIPrice(Float.parseFloat(random.randomDecimalString(100, 9999, 2)));
                    cItem.setiData(random.randomData());
                }
            }

            realm.commitTransaction();
        }  finally {
            if (null != realm) {
                realm.close();
            }
        }
    }

    @Override
    public void update() throws Exception {
        // update db record: Scale + Scale * AORM transactions
        reportBeforeRun();

        if (null == ra) {
            throw new NullPointerException();
        }

        Realm realm = null;
        try {
            realm = ra.getRealmInstance();

            Category cg = null;
            CItem cItem = null;
            RealmResults<Category> cs = null;
            RealmResults<CItem> is = null;

            String StrCTitle = null;
            int iCPages = 0;
            int iCSubCats = 0;
            int iCFiles =0;
            int iIImId = 0;
            String StrIName = null;
            float fIPrice = 0;
            String strIData = null;

            for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
                cs = realm.where(Category.class)
                        .equalTo("lCid", c).findAll();

                is = realm.where(CItem.class)
                        .equalTo("category.lCid", c).findAll();

                realm.beginTransaction();

                // update Category Table
                StrCTitle = this.random.randomAString26_50();
                iCPages = this.random.randomInt(1, 10000);
                iCSubCats = this.random.randomInt(1, 5000);
                iCFiles = this.random.randomInt(1, 20000);

                for (int k=0; k<cs.size(); k++) {
                    cg = cs.get(k);
                    cg.setStrCTitle(StrCTitle);
                    cg.setiCPages(iCPages);
                    cg.setiCSubCats(iCSubCats);
                    cg.setiCFiles(iCFiles);
                }

                // update CItem Table
                iIImId = this.random.randomInt(1, 10000);
                StrIName = this.random.randomAString14_24();
                fIPrice = Float.parseFloat(this.random.randomDecimalString(100, 9999, 2));
                strIData = this.random.randomData();

                for (int k=0; k<is.size(); k++) {
                    cItem = is.get(k);
                    cItem.setiIImId(iIImId);
                    cItem.setStrIName(StrIName);
                    cItem.setfIPrice(fIPrice);
                    cItem.setiData(strIData);
                }

                realm.commitTransaction();
            }
        } finally {
            if (null != realm) {
                realm.close();
            }
        }

        reportAfterRun();
    }

    @Override
    public void select() throws Exception {
        // select db record: Scale + Scale * AORM transactions
        reportBeforeRun();

        if (null == ra) {
            throw new NullPointerException();
        }

        Realm realm = null;
        try {
            realm = ra.getRealmInstance();

            Category category = null;
            RealmResults<CItem> is = null;

            for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
                // Select from Category table
                category = realm.where(Category.class)
                        .equalTo("lCid", c).findFirst();
                if (null != category) {
                    category.getStrCTitle();
                    category.getiCFiles();
                    category.getiCPages();
                    category.getiCSubCats();
                }

                // Select from CItem table
                is = realm.where(CItem.class)
                        .equalTo("category.lCid", c).findAll();

                if (null != is) {
                    for (CItem cItem : is) {
                        category = cItem.getCategory();
                        cItem.getfIPrice();
                        cItem.getiData();
                        cItem.getlIId();
                        cItem.getiIImId();
                        cItem.getStrIName();
                    }
                }
            }
        }  finally {
            if (null != realm) {
                realm.close();
            }
        }

        reportAfterRun();
    }

    @Override
    public void delete() throws Exception {

        // delete db record: Scale + Scale * AORM transactions
        reportBeforeRun();

        Realm realm = null;

        try {
            realm = ra.getRealmInstance();

            RealmResults<CItem> is = null;
            RealmResults<Category> cs = null;
            for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
                realm.beginTransaction();

                // Clear CItem table
                is = realm.where(CItem.class).equalTo("category.lCid", c).findAll();
                is.clear();

                // Clear Category Table
                cs = realm.where(Category.class).equalTo("lCid", c).findAll();
                cs.clear();

                realm.commitTransaction();
            }
        } finally {
            if (null != realm) {
                realm.close();
            }
        }

        reportAfterRun();
    }

    @Override
    public void initialize() throws Exception {
        reportBeforeRun();
        // delete old database
        RealmConfiguration config = new RealmConfiguration.Builder(AppContext.getInstance())
                .deleteRealmIfMigrationNeeded().build();
        Realm.deleteRealm(config);

        // don't need to create tables
        reportAfterRun();
    }
}
