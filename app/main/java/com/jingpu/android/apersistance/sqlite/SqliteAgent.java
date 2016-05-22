package com.jingpu.android.apersistance.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.jingpu.android.apersistance.sqlite.model.Customer;
import com.jingpu.android.apersistance.sqlite.model.DeliveryOrders;
import com.jingpu.android.apersistance.sqlite.model.DeliveryRequest;
import com.jingpu.android.apersistance.sqlite.model.District;
import com.jingpu.android.apersistance.sqlite.model.History;
import com.jingpu.android.apersistance.sqlite.model.Item;
import com.jingpu.android.apersistance.sqlite.model.NewOrders;
import com.jingpu.android.apersistance.sqlite.model.OrderLine;
import com.jingpu.android.apersistance.sqlite.model.Orders;
import com.jingpu.android.apersistance.sqlite.model.Stock;
import com.jingpu.android.apersistance.sqlite.model.Warehouse;

/**
 * Created by Jing Pu on 2015/9/22.
 */
public class SqliteAgent {
    private SqliteDBHelper dbHelper;

    public SqliteAgent(Context context) {
        dbHelper = SqliteDBHelper.getHelper(context);
    }

    /**
     * get Writable Database
     * @return
     */
    public SQLiteDatabase getWritableDatabase() {
        return dbHelper.getWritableDatabase();
    }

    /**
     * get Readable Database
     * @return
     */
    public SQLiteDatabase getReadableDatabase(){

        return dbHelper.getReadableDatabase();
    }

    /**
     * Begin transaction
     * @param db
     * @throws SQLiteException
     */
    public void beginTransaction(SQLiteDatabase db) throws SQLiteException {
        if (db != null) {
            db.beginTransaction();
        }
    }

    /**
     * Marks the current transaction as successful. Do not do any more database work between
     * calling this and calling endTransaction. Do as little non-database work as possible in that
     * situation too. If any errors are encountered between this and endTransaction the transaction
     * will still be committed.
     *
     * @param db
     * @throws SQLiteException
     */
    public void setTransactionSuccessful(SQLiteDatabase db) throws SQLiteException {
        if (db != null) {
            db.setTransactionSuccessful();
        }
    }

    /**
     * End  transaction
     * @param db
     * @throws SQLiteException
     */
    protected void endTransaction(SQLiteDatabase db) throws SQLiteException {
        if (db != null) {
            db.endTransaction();
        }

        //this.ha.closeSession();
    }

    /**
     *
     */
    public void createSchemaAndConstraints() throws Exception {
        // drop old tables if exist
        dropTables();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

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
    }

    /**
     *
     */
    public void dropTables() throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + Warehouse.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + District.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Customer.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + History.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Orders.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + NewOrders.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Item.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Stock.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + OrderLine.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DeliveryRequest.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DeliveryOrders.TABLE);
    }

}
