package com.jingpu.android.apersistance.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.BaseBenchmark;
import com.jingpu.android.apersistance.SimpleBenchmark;
import com.jingpu.android.apersistance.sqlite.model.CItem;
import com.jingpu.android.apersistance.sqlite.model.Category;
import com.jingpu.android.apersistance.util.TPCCLog;

import java.util.Date;

/**
 * Created by Jing Pu on 2016/1/30.
 */
public class SqliteAORM extends SimpleBenchmark {

    private SqliteAgent sa = null;

    public SqliteAORM(Context context) {
        sa = new SqliteAgent(context);
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
                + ". Table count[category=" + getTableCount(Category.TABLE)
                + ", citem=" + getTableCount(CItem.TABLE) + "]";
        AppContext.getInstance().SetUIInfo(log);
        categoryAndItemTable();

        log = "**  After populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[category=" + getTableCount(Category.TABLE)
                + ", citem=" + getTableCount(CItem.TABLE) +"]"
                + "\n LogFilePath= " + TPCCLog.getLogFilePath();
        AppContext.getInstance().SetUIInfo(log);

        reportAfterRun();

        //reportEnd();
    }

    private long getTableCount(String tableName) {
        return DatabaseUtils.queryNumEntries(sa.getReadableDatabase(), tableName);
    }

    private void categoryAndItemTable() throws Exception {
        // Category table size: scale
        // CItem table size: AORM transactions * scale

        if (null == sa) {
            throw new NullPointerException();
        }

        boolean bgTrans = false;
        long insertId = -1;
        int j = 1;

        SQLiteDatabase db = sa.getWritableDatabase();
        ContentValues values = new ContentValues();
        sa.beginTransaction(db);
        bgTrans = true;

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            values.clear();
            values.put(Category.COL_C_ID, c);
            values.put(Category.COL_C_TITLE, this.random.randomAString26_50());
            values.put(Category.COL_C_PAGES, this.random.randomInt(1, 10000));
            values.put(Category.COL_C_SUBCATS, this.random.randomInt(1, 5000));
            values.put(Category.COL_C_FILES, this.random.randomInt(1, 20000));

            // Insert Row
            insertId = db.insert(Category.TABLE, null, values);

            for (int i = 1; i <= AppContext.getInstance().getAORMTrans(); i++) {
                values = new ContentValues();
                values.put(CItem.COL_I_ID, j++);
                values.put(CItem.COL_I_CAT_ID, c);
                values.put(CItem.COL_I_IM_ID, this.random.randomInt(1, 10000));
                values.put(CItem.COL_I_NAME, this.random.randomAString14_24());
                values.put(CItem.COL_I_PRICE, this.random.randomDecimalString(100, 9999, 2));
                values.put(CItem.COL_I_DATA, this.random.randomData());

                // Insert Row
                insertId = db.insert(CItem.TABLE, null, values);
            }
        }

        if (bgTrans) {
            sa.setTransactionSuccessful(db);
            sa.endTransaction(db);
            bgTrans = false;
        }
    }

    @Override
    public void update() throws Exception {
        // update db record: Scale + Scale * AORM transactions

        reportBeforeRun();

        if (null == sa) {
            throw new NullPointerException();
        }

        SQLiteDatabase db = sa.getWritableDatabase();
        ContentValues values = new ContentValues();
        String iWhereClause = CItem.COL_I_CAT_ID + " = ?";;
        String cWhereClause = Category.COL_C_ID + " = ?";
        String[] whereArgs = null;
        boolean bgTrans = false;

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {

            sa.beginTransaction(db);
            bgTrans = true;

            whereArgs = new String[]{ String.valueOf(c) };

            // update Category Table
            values.clear();
            values.put(Category.COL_C_TITLE, this.random.randomAString26_50());
            values.put(Category.COL_C_PAGES, String.valueOf(this.random.randomInt(1, 10000)));
            values.put(Category.COL_C_SUBCATS,  String.valueOf(this.random.randomInt(1, 5000)));
            values.put(Category.COL_C_FILES, String.valueOf(this.random.randomInt(1, 20000)));
            db.update(Category.TABLE, values, cWhereClause, whereArgs);

            // update CItem Table
            values.clear();
            values.put(CItem.COL_I_IM_ID, this.random.randomInt(1, 10000));
            values.put(CItem.COL_I_NAME, this.random.randomAString14_24());
            values.put(CItem.COL_I_PRICE, this.random.randomDecimalString(100, 9999, 2));
            values.put(CItem.COL_I_DATA, this.random.randomData());
            db.update(CItem.TABLE, values, iWhereClause, whereArgs);

            if (bgTrans) {
                sa.setTransactionSuccessful(db);
                sa.endTransaction(db);
                bgTrans = false;
            }
        }

        reportAfterRun();
    }

    @Override
    public void select() throws Exception {

        // select db record: Scale + Scale * AORM transactions
        reportBeforeRun();

        if (null == sa) {
            throw new NullPointerException();
        }

        SQLiteDatabase db = sa.getReadableDatabase();

        String[] cColumns = new String[]{ Category.COL_C_TITLE, Category.COL_C_PAGES, Category.COL_C_SUBCATS, Category.COL_C_FILES };
        String cWhereClause = Category.COL_C_ID + " = ?";

        String[] iColumns = new String[]{ CItem.COL_I_ID, CItem.COL_I_IM_ID, CItem.COL_I_NAME, CItem.COL_I_PRICE, CItem.COL_I_DATA };
        String iWhereClause = CItem.COL_I_CAT_ID + " = ?";

        String[] whereArgs = null;
        Cursor cursor = null;
        Category category = new Category();
        CItem cItem = new CItem();

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            whereArgs = new String[]{String.valueOf(c)};

            // Select from Category table
            cursor = db.query(Category.TABLE, cColumns, cWhereClause, whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                category.setStrCTitle(cursor.getString(cursor.getColumnIndex(Category.COL_C_TITLE)));
                category.setiCPages(cursor.getInt(cursor.getColumnIndex(Category.COL_C_PAGES)));
                category.setiCSubCats(cursor.getInt(cursor.getColumnIndex(Category.COL_C_SUBCATS)));
                category.setiCFiles(cursor.getInt(cursor.getColumnIndex(Category.COL_C_FILES)));
            }
            cursor.close();

            // select from CItem table
            cursor = db.query(CItem.TABLE, iColumns, iWhereClause, whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    cItem.setlIId(cursor.getLong(cursor.getColumnIndex(CItem.COL_I_ID)));
                    cItem.setiIImId(cursor.getInt(cursor.getColumnIndex(CItem.COL_I_IM_ID)));
                    cItem.setStrIName(cursor.getString(cursor.getColumnIndex(CItem.COL_I_NAME)));
                    cItem.setfIPrice(cursor.getFloat(cursor.getColumnIndex(CItem.COL_I_PRICE)));
                    cItem.setiData(cursor.getString(cursor.getColumnIndex(CItem.COL_I_DATA)));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        reportAfterRun();
    }

    @Override
    public void delete() throws Exception {
        // delete db record: Scale + Scale * AORM transactions

        reportBeforeRun();

        if (null == sa) {
            throw new NullPointerException();
        }

        SQLiteDatabase db = sa.getWritableDatabase();

        for (int c = 1; c <= AppContext.getInstance().getScale(); c++) {
            // Clear CItem table
            db.delete(CItem.TABLE, CItem.COL_I_CAT_ID + "=?", new String[]{String.valueOf(c)});

            // Clear Category Table
            db.delete(Category.TABLE, Category.COL_C_ID + "=?", new String[]{String.valueOf(c)});
        }

        reportAfterRun();
    }

    @Override
    public void initialize() throws Exception {
        reportBeforeRun();

        if (null == sa) {
            throw new NullPointerException();
        }

        // delete old database
        AppContext.getInstance().deleteDatabase(BaseBenchmark.DATABASE_NAME);

        SQLiteDatabase  db = sa.getWritableDatabase();

        // create tables
        // C
        String CREATE_TABLE_C = "CREATE TABLE C(C_ID INT PRIMARY KEY, CLOAD INT);";
        db.execSQL(CREATE_TABLE_C);

        // WAREHOUSE
        String CREATE_TABLE_WAREHOUSE = "CREATE TABLE WAREHOUSE (W_ID INT PRIMARY KEY,W_NAME TEXT NOT NULL,W_STREET_1 TEXT NOT NULL,W_STREET_2 TEXT NOT NULL,W_CITY TEXT NOT NULL,W_STATE TEXT NOT NULL,W_ZIP TEXT NOT NULL,W_TAX REAL NOT NULL,W_YTD REAL NOT NULL);";
        db.execSQL(CREATE_TABLE_WAREHOUSE);

        // DISTRICT
        String CREATE_TABLE_DISTRICT = "CREATE TABLE DISTRICT (D_COMPO TEXT PRIMARY KEY, D_ID INT NOT NULL,D_W_ID INT NOT NULL,D_NAME TEXT NOT NULL,D_STREET_1 TEXT NOT NULL,D_STREET_2 TEXT NOT NULL,D_CITY TEXT NOT NULL,D_STATE TEXT NOT NULL,D_ZIP TEXT NOT NULL,D_TAX REAL NOT NULL,D_YTD REAL NOT NULL,D_NEXT_O_ID INTEGER NOT NULL);";
        //"PRIMARY KEY(D_ID, D_W_ID));";
        db.execSQL(CREATE_TABLE_DISTRICT);

        // CUSTOMER
        String CREATE_TABLE_CUSTOMER = "CREATE TABLE CUSTOMER (C_COMPO TEXT PRIMARY KEY, C_ID INTEGER NOT NULL,C_D_ID INT NOT NULL,C_W_ID INT NOT NULL,C_FIRST TEXT NOT NULL,C_MIDDLE TEXT NOT NULL,C_LAST TEXT NOT NULL,C_STREET_1 TEXT NOT NULL,C_STREET_2 TEXT NOT NULL,C_CITY TEXT NOT NULL,C_STATE TEXT NOT NULL,C_ZIP TEXT NOT NULL,C_PHONE TEXT NOT NULL,C_SINCE TIMESTAMP NOT NULL,C_CREDIT TEXT NOT NULL,C_CREDIT_LIM REAL NOT NULL,C_DISCOUNT REAL NOT NULL,C_BALANCE REAL NOT NULL,C_YTD_PAYMENT REAL NOT NULL,C_PAYMENT_CNT INTEGER NOT NULL,C_DELIVERY_CNT INTEGER NOT NULL,C_DATA TEXT NOT NULL,C_DATA_INITIAL TEXT NOT NULL);";
        //"PRIMARY KEY(C_ID,C_D_ID,C_W_ID));";
        db.execSQL(CREATE_TABLE_CUSTOMER);

        // HISTORY
        String CREATE_TABLE_HISTORY = "CREATE TABLE HISTORY (H_ID INT PRIMARY KEY, H_C_ID INTEGER NOT NULL,H_C_D_ID INT NOT NULL,H_C_W_ID INT NOT NULL,H_D_ID INT NOT NULL,H_W_ID INT NOT NULL,H_DATE TIMESTAMP NOT NULL,H_AMOUNT REAL NOT NULL,H_DATA TEXT NOT NULL,H_INITIAL BOOLEAN);";
        db.execSQL(CREATE_TABLE_HISTORY);

        // ORDERS
        String CREATE_TABLE_ORDERS = "CREATE TABLE ORDERS (O_COMPO TEXT PRIMARY KEY, O_ID INTEGER NOT NULL,O_D_ID INT NOT NULL,O_W_ID INT NOT NULL,O_C_ID INTEGER NOT NULL,O_ENTRY_D TIMESTAMP NOT NULL,O_CARRIER_ID INT,O_OL_CNT INT NOT NULL,O_ALL_LOCAL INT NOT NULL,O_CARRIER_ID_INITIAL INT,O_INITIAL BOOLEAN);";
        //"PRIMARY KEY(O_ID,O_D_ID,O_W_ID));";
        db.execSQL(CREATE_TABLE_ORDERS);

        // NEWORDERS
        String CREATE_TABLE_NEWORDERS = "CREATE TABLE NEWORDERS (NO_COMPO TEXT PRIMARY KEY, NO_O_ID INTEGER NOT NULL,NO_D_ID INT NOT NULL,NO_W_ID INT NOT NULL,NO_INITIAL BOOLEAN,NO_LIVE BOOLEAN);";
        //"PRIMARY KEY(NO_O_ID,NO_D_ID,NO_W_ID));";
        db.execSQL(CREATE_TABLE_NEWORDERS);

        // ITEM
        String CREATE_TABLE_ITEM = "CREATE TABLE ITEM (I_ID INTEGER PRIMARY KEY,I_IM_ID INTEGER NOT NULL,I_NAME TEXT NOT NULL,I_PRICE REAL NOT NULL,I_DATA TEXT NOT NULL);";
        db.execSQL(CREATE_TABLE_ITEM);

        // STOCK
        String CREATE_TABLE_STOCK = "CREATE TABLE STOCK (S_COMPO TEXT PRIMARY KEY, S_I_ID INTEGER NOT NULL,S_W_ID INT NOT NULL,S_QUANTITY INTEGER NOT NULL,S_DIST_01 TEXT NOT NULL,S_DIST_02 TEXT NOT NULL,S_DIST_03 TEXT NOT NULL,S_DIST_04 TEXT NOT NULL,S_DIST_05 TEXT NOT NULL,S_DIST_06 TEXT NOT NULL,S_DIST_07 TEXT NOT NULL,S_DIST_08 TEXT NOT NULL,S_DIST_09 TEXT NOT NULL,S_DIST_10 TEXT NOT NULL,S_YTD REAL NOT NULL,S_ORDER_CNT INTEGER NOT NULL,S_REMOTE_CNT INTEGER NOT NULL,S_DATA VTEXT NOT NULL,S_QUANTITY_INITIAL INTEGER NOT NULL);";
        //"PRIMARY KEY(S_I_ID,S_W_ID));";
        db.execSQL(CREATE_TABLE_STOCK);

        // ORDERLINE
        String CREATE_TABLE_ORDERLINE = "CREATE TABLE ORDERLINE (OL_COMPO TEXT PRIMARY KEY, OL_O_ID INTEGER NOT NULL,OL_D_ID INT NOT NULL,OL_W_ID INT NOT NULL,OL_NUMBER INT NOT NULL,OL_I_ID INTEGER NOT NULL,OL_SUPPLY_W_ID INT NOT NULL,OL_DELIVERY_D TIMESTAMP,OL_QUANTITY INT NOT NULL,OL_AMOUNT REAL NOT NULL,OL_DIST_INFO TEXT NOT NULL,OL_DELIVERY_D_INITIAL TIMESTAMP,OL_INITIAL BOOLEAN);";
        //"PRIMARY KEY(OL_O_ID,OL_D_ID,OL_W_ID,OL_NUMBER));";
        db.execSQL(CREATE_TABLE_ORDERLINE);

        // DELIVERY_REQUEST
        String CREATE_TABLE_DELIVERY_REQUEST = "CREATE TABLE DELIVERY_REQUEST(DR_ID INTEGER PRIMARY KEY NOT NULL,DR_W_ID SMALLINT NOT NULL,DR_CARRIER_ID INT NOT NULL,DR_QUEUED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,DR_COMPLETED TIMESTAMP,DR_STATE TEXT CHECK (DR_STATE IN ('Q', 'I', 'C', 'E')));";
        db.execSQL(CREATE_TABLE_DELIVERY_REQUEST);

        // DELIVERY_ORDERS
        String CREATE_TABLE_DELIVERY_ORDERS = "CREATE TABLE DELIVERY_ORDERS(DO_ID INT PRIMARY KEY, DO_DR_ID INTEGER NOT NULL,DO_D_ID INT NOT NULL,DO_O_ID INTEGER);";
        db.execSQL(CREATE_TABLE_DELIVERY_ORDERS);

        // CATEGORY
        String CREATE_TABLE_CATEGORY = "CREATE TABLE CATEGORY (C_ID INTEGER PRIMARY KEY,C_TITLE TEXT NOT NULL,C_PAGES INTEGER NOT NULL,C_SUBCATS INTEGER NOT NULL,C_FILES INTEGER NOT NULL);";
        db.execSQL(CREATE_TABLE_CATEGORY);

        // CITEM
        String CREATE_TABLE_CITEM = "CREATE TABLE CITEM (I_ID INTEGER PRIMARY KEY, I_CAT_ID INTEGER NOT NULL,I_IM_ID INTEGER NOT NULL,I_NAME TEXT NOT NULL,I_PRICE DECIMAL(5,2) NOT NULL,I_DATA TEXT NOT NULL," +
                "FOREIGN KEY (I_CAT_ID) REFERENCES CATEGORY(C_ID));";
        db.execSQL(CREATE_TABLE_CITEM);

        reportAfterRun();
    }
}
