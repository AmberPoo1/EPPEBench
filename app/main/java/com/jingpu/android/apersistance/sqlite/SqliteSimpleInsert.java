package com.jingpu.android.apersistance.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.sqlite.model.C;
import com.jingpu.android.apersistance.sqlite.model.Customer;
import com.jingpu.android.apersistance.sqlite.model.District;
import com.jingpu.android.apersistance.sqlite.model.History;
import com.jingpu.android.apersistance.sqlite.model.Item;
import com.jingpu.android.apersistance.sqlite.model.NewOrders;
import com.jingpu.android.apersistance.sqlite.model.OrderLine;
import com.jingpu.android.apersistance.sqlite.model.Orders;
import com.jingpu.android.apersistance.sqlite.model.Stock;
import com.jingpu.android.apersistance.sqlite.model.Warehouse;
import com.jingpu.android.apersistance.util.TPCCLoad;

import org.dacapo.derby.Load;
import org.dacapo.derby.OERandom;

import java.sql.Timestamp;

/**
 * Created by Jing Pu on 2015/10/1.
 */
public class SqliteSimpleInsert implements TPCCLoad {

    SqliteAgent sa = null;
    short scale = 1;
    long seed = System.currentTimeMillis();
    OERandom random;

    public void setupLoad(short scale) throws SQLiteException { //Session session,
        this.scale = scale;
        setupAgent();

        SQLiteDatabase db = sa.getWritableDatabase();

        // native SQL
        sa.beginTransaction(db);
        db.execSQL("DROP TABLE IF EXISTS " + C.TABLE);

        String CREATE_TABLE_C = "CREATE TABLE C(C_ID INT PRIMARY KEY, CLOAD INT);";
        db.execSQL(CREATE_TABLE_C);

        // "INSERT INTO C VALUES(" + loadRandomFactor + ")"
        this.random = new OERandom(-1, this.seed);
        int loadRandomFactor = this.random.randomInt(0, 255);

        ContentValues values= new ContentValues();
        values.put(C.COL_CLOAD, loadRandomFactor);
        // Insert Row
        long insertId = db.insert(C.TABLE, null, values);

        sa.setTransactionSuccessful(db);
        sa.endTransaction(db);

        setRandomGenerator();
    }

    public void setupAgent() throws SQLiteException { //Session session, short scale
        sa = new SqliteAgent(AppContext.getInstance());

        if (null == sa) {
            throw new NullPointerException();
        }
    }

    void setRandomGenerator() throws SQLiteException {
        if (null == sa) {
            throw new NullPointerException();
        }

        SQLiteDatabase db = sa.getReadableDatabase();
        // "SELECT CLOAD FROM C"

        Cursor cursor = db.query(C.TABLE, null, null, null, null, null, null);

        if (!cursor.moveToFirst()) {
            throw new IndexOutOfBoundsException();
        }

        int loadRandomFactor = cursor.getInt(cursor.getColumnIndex(C.COL_CLOAD));
        this.random = new OERandom(loadRandomFactor, this.seed);
    }

    public void populateAllTables() throws Exception {

        itemTable(1, Load.ITEM_COUNT); //10000

        for (short w = 1; w <= this.scale; w = (short)(w + 1)) {
            //for (short w = 1; w <= 1; w = (short)(w + 1)) {
            populateForOneWarehouse(w);
        }
    }

    void populateForOneWarehouse(short w) throws SQLiteException {
        warehouseTable(w);

        stockTable(1, Load.STOCK_COUNT_W, w); //10000

        for (short d = 1; d <= Load.DISTRICT_COUNT_W; d = (short)(d + 1)) { //4
            districtTable(w, d);
            customerTable(w, d);
            orderTable(w, d);
        }
    }

    public void itemTable(int itemStart, int itemEnd) throws SQLiteException {
        if (null == sa) {
            throw new NullPointerException();
        }

        Item item = null;
        long insertId = -1;
        SQLiteDatabase db = sa.getWritableDatabase();
        ContentValues values = null;

        sa.beginTransaction(db);

        // "INSERT INTO ITEM(I_ID,I_IM_ID,I_NAME,I_PRICE,I_DATA) VALUES (?, ?, ?, ?, ?)"
        for (int i = itemStart; i <= itemEnd; i++) {
            values= new ContentValues();
            values.put(Item.COL_I_ID, i);
            values.put(Item.COL_I_IM_ID, this.random.randomInt(1, 10000));
            values.put(Item.COL_I_NAME, this.random.randomAString14_24());
            values.put(Item.COL_I_PRICE, this.random.randomDecimalString(100, 9999, 2));
            values.put(Item.COL_I_DATA, this.random.randomData());

            // Insert Row
            insertId = db.insert(Item.TABLE, null, values);
        }

        sa.setTransactionSuccessful(db);
        sa.endTransaction(db);
    }

    public void warehouseTable(short w) throws SQLiteException {
        if (null == sa) {
            throw new NullPointerException();
        }

        // "INSERT INTO WAREHOUSE VALUES (?, ?, ?, ?, ?, ?, ?, ?, 300000.00)"
        SQLiteDatabase db = sa.getWritableDatabase();

        ContentValues values= new ContentValues();
        values.put(Warehouse.COL_W_ID, w);
        values.put(Warehouse.COL_W_NAME, this.random.randomAString(6, 10));
        values.put(Warehouse.COL_W_STREET_1, this.random.randomAString10_20());
        values.put(Warehouse.COL_W_STREET_2, this.random.randomAString10_20());
        values.put(Warehouse.COL_W_CITY, this.random.randomAString10_20());
        values.put(Warehouse.COL_W_STATE, this.random.randomState());
        values.put(Warehouse.COL_W_ZIP, this.random.randomZIP());
        values.put(Warehouse.COL_W_TAX, this.random.randomDecimalString(0, 2000, 4));
        values.put(Warehouse.COL_W_YTD, 300000.00);

        // Insert Row
        long insertId = db.insert(Warehouse.TABLE, null, values);
    }

    public void stockTable(int itemStart, int itemEnd, short w) throws SQLiteException {
        if (null == sa) {
            throw new NullPointerException();
        }

        int quantity = 0;
        ContentValues values = null;
        long insertId = -1;

        // "INSERT INTO STOCK (S_I_ID, S_W_ID, S_QUANTITY,S_DIST_01, S_DIST_02, S_DIST_03,S_DIST_04,S_DIST_05,
        // S_DIST_06,S_DIST_07,S_DIST_08,S_DIST_09,S_DIST_10,
        // S_ORDER_CNT, S_REMOTE_CNT, S_YTD, S_DATA, S_QUANTITY_INITIAL ) VALUES
        // (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0, 0, ?, ?)"
        SQLiteDatabase db = sa.getWritableDatabase();
        sa.beginTransaction(db);
        for (int i = itemStart; i <= itemEnd; i++) {
            quantity = this.random.randomInt(10, 100);
            values= new ContentValues();
            values.put(Stock.COL_S_I_ID, i);
            values.put(Stock.COL_S_W_ID, w);
            values.put(Stock.COL_S_COMPO, w + "-" + i);
            values.put(Stock.COL_S_QUANTITY, quantity);
            values.put(Stock.COL_S_DIST_01, this.random.randomAString24());
            values.put(Stock.COL_S_DIST_02, this.random.randomAString24());
            values.put(Stock.COL_S_DIST_03, this.random.randomAString24());
            values.put(Stock.COL_S_DIST_04, this.random.randomAString24());
            values.put(Stock.COL_S_DIST_05, this.random.randomAString24());
            values.put(Stock.COL_S_DIST_06, this.random.randomAString24());
            values.put(Stock.COL_S_DIST_07, this.random.randomAString24());
            values.put(Stock.COL_S_DIST_08, this.random.randomAString24());
            values.put(Stock.COL_S_DIST_09, this.random.randomAString24());
            values.put(Stock.COL_S_DIST_10, this.random.randomAString24());
            values.put(Stock.COL_S_ORDER_CNT, 0);
            values.put(Stock.COL_S_REMOTE_CNT, 0);
            values.put(Stock.COL_S_YTD, 0);
            values.put(Stock.COL_S_DATA, this.random.randomData());
            values.put(Stock.COL_S_QUANTITY_INITIAL, quantity);

            // Insert Row
            insertId = db.insert(Stock.TABLE, null, values);
        }

        sa.setTransactionSuccessful(db);
        sa.endTransaction(db);
    }

    public void districtTable(short w, short d) throws SQLiteException {
        if (null == sa) {
            throw new NullPointerException();
        }

        ContentValues values = null;
        long insertId = -1;

        //"INSERT INTO DISTRICT (D_ID, D_W_ID, D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP,
        // D_TAX, D_YTD, D_NEXT_O_ID)  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, 30000.00, 3001)");
        SQLiteDatabase db = sa.getWritableDatabase();
        values = new ContentValues();
        values.put(District.COL_D_ID, d);
        values.put(District.COL_D_W_ID, w);
        values.put(District.COL_D_COMPO, w + "-" + d);
        values.put(District.COL_D_NAME, this.random.randomAString(6, 10));
        values.put(District.COL_D_STREET_1, this.random.randomAString10_20());
        values.put(District.COL_D_STREET_2, this.random.randomAString10_20());
        values.put(District.COL_D_CITY, this.random.randomAString10_20());
        values.put(District.COL_D_STATE, this.random.randomState());
        values.put(District.COL_D_ZIP, this.random.randomZIP());
        values.put(District.COL_D_TAX, this.random.randomDecimalString(0, 2000, 4));
        values.put(District.COL_D_YTD, 30000.00);
        values.put(District.COL_D_NEXT_O_ID, 3001);

        insertId = db.insert(District.TABLE, null, values);
    }

    public void customerTable(short w, short d) throws SQLiteException {
        if (null == sa) {
            throw new NullPointerException();
        }

        String str = null;
        ContentValues values = null;
        long insertId = -1;
        Timestamp currentTimeStamp = null;
        SQLiteDatabase db = sa.getWritableDatabase();
        sa.beginTransaction(db);

        for (int c = 1; c <= Load.CUSTOMER_COUNT_W / Load.DISTRICT_COUNT_W; c++) { //1000
            currentTimeStamp = new Timestamp(System.currentTimeMillis());
            // "INSERT INTO CUSTOMER (C_ID, C_D_ID, C_W_ID, C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2,  C_CITY,
            // C_STATE, C_ZIP, C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT,
            // C_PAYMENT_CNT, C_DELIVERY_CNT, C_DATA, C_DATA_INITIAL)
            // VALUES (?, ?, ?, ?, 'OE', ?, ?, ?, ?, ?, ?, ?,
            // CURRENT TIMESTAMP ,?, 50000.00, ?, -10.0, 10.0, 1, 0, ?, ?)"

            values = new ContentValues();
            values.put(Customer.COL_C_ID, c);
            values.put(Customer.COL_C_D_ID, d);
            values.put(Customer.COL_C_W_ID, w);
            values.put(Customer.COL_C_COMPO, c + "-"  + d + "-"  + w);
            values.put(Customer.COL_C_FIRST, this.random.randomAString8_16());
            values.put(Customer.COL_C_MIDDLE, "OE");
            values.put(Customer.COL_C_LAST, this.random.randomCLastPopulate(c));
            values.put(Customer.COL_C_STREET_1, this.random.randomAString10_20());
            values.put(Customer.COL_C_STREET_2, this.random.randomAString10_20());
            values.put(Customer.COL_C_CITY, this.random.randomAString10_20());
            values.put(Customer.COL_C_STATE, this.random.randomState());
            values.put(Customer.COL_C_ZIP, this.random.randomZIP());
            values.put(Customer.COL_C_PHONE, this.random.randomNString(16, 16));
            values.put(Customer.COL_C_SINCE, currentTimeStamp.toString());
            values.put(Customer.COL_C_CREDIT, Math.random() < 0.1D ? "BC" : "GC");
            values.put(Customer.COL_C_CREDIT_LIM, 50000.00);
            values.put(Customer.COL_C_DISCOUNT, this.random.randomDecimalString(0, 5000, 4));
            values.put(Customer.COL_C_BALANCE, -10.0);
            values.put(Customer.COL_C_YTD_PAYMENT, 10.0);
            values.put(Customer.COL_C_PAYMENT_CNT, 1);
            values.put(Customer.COL_C_DELIVERY_CNT, 0);
            str = this.random.randomAString300_500();
            if (str.length() > 255) {
                str = str.substring(255);
            }
            values.put(Customer.COL_C_DATA, str);
            values.put(Customer.COL_C_DATA_INITIAL, str);
            insertId = db.insert(Customer.TABLE, null, values);

            // "INSERT INTO HISTORY (H_C_ID, H_C_D_ID, H_C_W_ID, H_D_ID, H_W_ID, H_DATE, H_AMOUNT, H_DATA, H_INITIAL)
            // VALUES (?, ?, ?, ?, ?, CURRENT TIMESTAMP, 10.00, ?, TRUE)"
            values = new ContentValues();
            values.put(History.COL_H_C_ID, c);
            values.put(History.COL_H_C_D_ID, d);
            values.put(History.COL_H_C_W_ID, w);
            values.put(History.COL_H_D_ID, d);
            values.put(History.COL_H_W_ID, w);
            values.put(History.COL_H_DATE, currentTimeStamp.toString());
            values.put(History.COL_H_AMOUNT, 10.00);
            values.put(History.COL_H_DATA, this.random.randomAString(12, 24));
            values.put(History.COL_H_INITIAL, true);
            insertId = db.insert(History.TABLE, null, values);

        }

        sa.setTransactionSuccessful(db);
        sa.endTransaction(db);
    }

    public void orderTable(short w, short d) throws SQLiteException {
        if (null == sa) {
            throw new NullPointerException();
        }

        Timestamp o_entry_d = null;
        short o_carrier_id;
        ContentValues values = null;
        long insertId = -1;
        SQLiteDatabase db = sa.getWritableDatabase();
        int[] cid = this.random.randomIntPerm(Load.CUSTOMER_COUNT_W / Load.DISTRICT_COUNT_W); //1000

        sa.beginTransaction(db);

        for (int o_id = 1; o_id <= cid.length; o_id++) {
            // "INSERT INTO ORDERS (O_ID, O_D_ID, O_W_ID, O_C_ID, O_ENTRY_D, O_CARRIER_ID,
            // O_OL_CNT, O_ALL_LOCAL, O_CARRIER_ID_INITIAL, O_INITIAL)
            // VALUES (?, ?, ?, ?, ?, ?, ?, 1, ?, TRUE)"
            values = new ContentValues();
            values.put(Orders.COL_O_ID, o_id);
            values.put(Orders.COL_O_D_ID, d);
            values.put(Orders.COL_O_W_ID, w);
            values.put(Orders.COL_O_COMPO, o_id + "-"  + d + "-"  + w);
            values.put(Orders.COL_O_C_ID, cid[(o_id - 1)]);
            o_entry_d = new Timestamp(System.currentTimeMillis());
            values.put(Orders.COL_O_ENTRY_D, o_entry_d.toString());
            if (o_id <= Load.NEWORDERS_BREAKPOINT) { //700
                o_carrier_id = (short)this.random.randomInt(1, Load.CARRIER_COUNT); //10
                values.put(Orders.COL_O_CARRIER_ID, o_carrier_id);
                values.put(Orders.COL_O_CARRIER_ID_INITIAL, o_carrier_id);
            }
            int o_ol_cnt = this.random.randomInt(5, 15);
            values.put(Orders.COL_O_OL_CNT, o_ol_cnt);
            values.put(Orders.COL_O_ALL_LOCAL, 1);
            values.put(Orders.COL_O_INITIAL, true);
            insertId = db.insert(Orders.TABLE, null, values);

            for (int ol_number = 1; ol_number <= o_ol_cnt; ol_number++) {
                // "INSERT INTO ORDERLINE (OL_O_ID, OL_D_ID, OL_W_ID, OL_NUMBER, OL_I_ID,
                // OL_SUPPLY_W_ID, OL_DELIVERY_D, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO,
                // OL_DELIVERY_D_INITIAL, OL_INITIAL)
                // VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE)"
                values = new ContentValues();
                values.put(OrderLine.COL_OL_O_ID, o_id);
                values.put(OrderLine.COL_OL_D_ID, d);
                values.put(OrderLine.COL_OL_W_ID, w);
                values.put(OrderLine.COL_OL_NUMBER, ol_number);
                values.put(OrderLine.COL_OL_COMPO, o_id + "-" + d + "-"  + w + "-"  + ol_number);
                values.put(OrderLine.COL_OL_I_ID, this.random.randomInt(1, Load.ITEM_COUNT));
                if (o_id <= Load.NEWORDERS_BREAKPOINT) { //700
                    values.put(OrderLine.COL_OL_DELIVERY_D, o_entry_d.toString());
                    values.put(OrderLine.COL_OL_DELIVERY_D_INITIAL, o_entry_d.toString());
                    values.put(OrderLine.COL_OL_AMOUNT, 0.00);
                } else {
                    values.put(OrderLine.COL_OL_AMOUNT, this.random.randomDecimalString(1, 999999, 2));
                }

                values.put(OrderLine.COL_OL_SUPPLY_W_ID, w);
                values.put(OrderLine.COL_OL_QUANTITY, 5);
                values.put(OrderLine.COL_OL_DIST_INFO, this.random.randomAString24());
                values.put(OrderLine.COL_OL_INITIAL, true);
                insertId = db.insert(OrderLine.TABLE, null, values);
            }

            if (o_id > Load.NEWORDERS_BREAKPOINT) { //700
                // "INSERT INTO NEWORDERS (NO_O_ID, NO_D_ID, NO_W_ID, NO_INITIAL, NO_LIVE)
                // VALUES (?, ?, ?, TRUE, TRUE)"
                values = new ContentValues();
                values.put(NewOrders.COL_NO_O_ID, o_id);
                values.put(NewOrders.COL_NO_D_ID, d);
                values.put(NewOrders.COL_NO_W_ID, w);
                values.put(NewOrders.COL_NO_COMPO, o_id + "-"  + d + "-"  + w);
                values.put(NewOrders.COL_NO_INITIAL, true);
                values.put(NewOrders.COL_NO_LIVE, true);
                insertId = db.insert(NewOrders.TABLE, null, values);
            }
        }

        sa.setTransactionSuccessful(db);
        sa.endTransaction(db);
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void setThreadCount(int threadCount) {
    }
}
