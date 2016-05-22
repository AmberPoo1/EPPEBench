package com.jingpu.android.apersistance.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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
import com.jingpu.android.apersistance.util.OrderStatusException;
import com.jingpu.android.apersistance.util.PaymentException;
import com.jingpu.android.apersistance.util.TPCCLog;

import org.dacapo.derby.Data;
import org.dacapo.derby.OrderItem4Sort;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jing Pu on 2015/10/1.
 */
public class SqliteStandard extends SqliteStatementHelper
        implements SqliteOperations {

    private final Customer customer = new Customer();
    private final List nameList = new ArrayList();
    private final Orders order = new Orders();
    private final District district = new District();
    private final Warehouse warehouse = new Warehouse();

    // Jing Pu test 5/14/2016
    //long transStartTime = 0;
    //long transEndTime = 0;

    public SqliteStandard(SqliteAgent sa) throws SQLiteException {
        super(sa);
    }

    public void stockLevel(int terminalId, SqliteDisplay display, Object displayData, short w, short d, int threshold) throws Exception {
        Long count = -1l;
        int iLowStock = -1;
        SQLiteDatabase db = getReadableDatabase();

        //transStartTime = new Date().getTime();

        // "SELECT D_NEXT_O_ID FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?"
        String whereClause = District.COL_D_W_ID + " = ? AND " + District.COL_D_ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(w), String.valueOf(d) };

        Cursor cursor = db.query(District.TABLE, null, whereClause, whereArgs, null, null, null); //tableColumns
        int nextOrder = -1;
        if (cursor.moveToFirst()) {
            nextOrder = cursor.getInt(cursor.getColumnIndex(District.COL_D_NEXT_O_ID));
        }
        cursor.close();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SqliteStandard.class.getName(), "stockLevel s1 duration[" + (nextOrder == -1 ? 0 : 1) + "] = " + (transEndTime - transStartTime));


        //transStartTime = new Date().getTime();

        // "SELECT COUNT(DISTINCT(S_I_ID)) AS LOW_STOCK FROM ORDERLINE, STOCK WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID < ? AND OL_O_ID >= ? AND S_W_ID = ? AND S_I_ID = OL_I_ID AND S_QUANTITY < ?"
        String[] tableColumns = new String[] {Stock.COL_S_I_ID };
        whereClause = OrderLine.COL_OL_W_ID + " = ? AND " + OrderLine.COL_OL_D_ID  + "= ? AND " +  OrderLine.COL_OL_O_ID + " < ? AND " +  OrderLine.COL_OL_O_ID + " >= ? AND " +  Stock.COL_S_W_ID + " = ? AND " + Stock.COL_S_I_ID  + "=" + OrderLine.COL_OL_I_ID + " AND " + Stock.COL_S_QUANTITY + "< ?" ;
        whereArgs = new String[] { String.valueOf(w), String.valueOf(d), String.valueOf(nextOrder), String.valueOf(nextOrder - 20), String.valueOf(w), String.valueOf(threshold) };
        cursor = db.query(true, OrderLine.TABLE + ", " + Stock.TABLE, tableColumns, whereClause, whereArgs, null, null, null, null);
        iLowStock = cursor.getCount();
        cursor.close();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SqliteStandard.class.getName(), "stockLevel s2 duration = " + (transEndTime - transStartTime));

        if (display != null) {
            display.displayStockLevel(displayData, w, d, threshold, iLowStock);
        }
    }

    @SuppressWarnings("unchecked")
    public void orderStatus(int terminalId, SqliteDisplay display, Object displayData, short w, short d, String customerLast) throws Exception {

        SQLiteDatabase db = getReadableDatabase();

        //transStartTime = new Date().getTime();

        // "SELECT C_ID, C_BALANCE, C_FIRST, C_MIDDLE FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_LAST = ? ORDER BY C_FIRST"
        String whereClause = Customer.COL_C_W_ID + " = ? AND " + Customer.COL_C_D_ID + " = ? AND " + Customer.COL_C_LAST + " = ?";
        String[] whereArgs = new String[] {
                String.valueOf(w),
                String.valueOf(d),
                customerLast};
        String orderBy = Customer.COL_C_FIRST;

        this.nameList.clear();
        Customer customer = null;

        Cursor cursor = db.query(Customer.TABLE, null, whereClause, whereArgs, null, null, orderBy); //tableColumns
        if (cursor.moveToFirst()) {
            do {
                customer = new Customer();

                customer.setCWId(w);
                customer.setCDId(d);
                customer.setCLst(customerLast);
                customer.setCId(cursor.getInt(cursor.getColumnIndex(Customer.COL_C_ID)));
                customer.setCBalance(cursor.getFloat(cursor.getColumnIndex(Customer.COL_C_BALANCE)));
                customer.setCFst(cursor.getString(cursor.getColumnIndex(Customer.COL_C_FIRST)));
                customer.setCMid(cursor.getString(cursor.getColumnIndex(Customer.COL_C_MIDDLE)));
                this.nameList.add(customer);
            } while (cursor.moveToNext());
        }

        cursor.close();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SqliteStandard.class.getName(), "orderStatus 132 s1 prepare duration[" + this.nameList.size() + "] = " + (transEndTime - transStartTime));

        if (this.nameList.isEmpty()) {
            throw new OrderStatusException("Order Status by name: Tml[" + terminalId + "] - no matching customer " + customerLast);
        }

        int mid = this.nameList.size() / 2;
        if ((mid != 0) && (this.nameList.size() % 2 == 1)) {
            mid++;
        }

        customer = (Customer)this.nameList.get(mid);
        this.nameList.clear();

        getOrderStatusForCustomer(terminalId, display, displayData, true, customer);
    }

    public void orderStatus(int terminalId, SqliteDisplay display, Object displayData, short w, short d, int c) throws Exception {
        this.customer.clear();
        this.customer.setCWId(w);
        this.customer.setCDId(d);
        this.customer.setCId(c);

        SQLiteDatabase db = getReadableDatabase();

        //transStartTime = new Date().getTime();

        // "SELECT C_BALANCE, C_FIRST, C_MIDDLE, C_LAST FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        String whereClause = Customer.COL_C_W_ID + " = ? AND " + Customer.COL_C_D_ID + " = ? AND " + Customer.COL_C_ID + " = ?";
        String[] whereArgs = new String[] {
                String.valueOf(w),
                String.valueOf(d),
                String.valueOf(c)};

        Cursor cursor = db.query(Customer.TABLE, null, whereClause, whereArgs, null, null, null); //tableColumns

        if (cursor.moveToFirst()) {
            customer.setCBalance(cursor.getFloat(cursor.getColumnIndex(Customer.COL_C_BALANCE)));
            customer.setCFst(cursor.getString(cursor.getColumnIndex(Customer.COL_C_FIRST)));
            customer.setCMid(cursor.getString(cursor.getColumnIndex(Customer.COL_C_MIDDLE)));
            customer.setCLst(cursor.getString(cursor.getColumnIndex(Customer.COL_C_LAST)));
        }

        cursor.close();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SqliteStandard.class.getName(), "orderStatus 174 s1 duration[" + (customer.getCLst() == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));


        getOrderStatusForCustomer(terminalId, display, displayData, false, this.customer);
    }

    private void getOrderStatusForCustomer(int terminalId, SqliteDisplay display, Object displayData, boolean byName, Customer customer) throws Exception {
        SQLiteDatabase db = getReadableDatabase();

        //transStartTime = new Date().getTime();

        // "SELECT MAX(O_ID) AS LAST_ORDER FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_C_ID = ?"
        String[] tableColumns = new String[] {"(SELECT MAX(O_ID) FROM ORDERS) AS LAST_ORDER"};
        String whereClause = Orders.COL_O_W_ID + " = ? AND " + Orders.COL_O_D_ID + " = ? AND " + Orders.COL_O_C_ID + " = ?";
        String[] whereArgs = new String[] {
                String.valueOf(customer.getCWId()),
                String.valueOf(customer.getCDId()),
                String.valueOf(customer.getCId())};

        Cursor cursor = db.query(Orders.TABLE, tableColumns, whereClause, whereArgs, null, null, null);

        this.order.clear();
        this.order.setOWId(customer.getCWId());
        this.order.setODId(customer.getCDId());

        int iLastOrder = -1;
        if (cursor.moveToFirst()) {
            iLastOrder = cursor.getInt(cursor.getColumnIndex("LAST_ORDER"));
        }

        cursor.close();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SqliteStandard.class.getName(), "getOrderStatusForCustomer s1 duration[" + (iLastOrder == -1 ? 0 : 1) + "] = " + (transEndTime - transStartTime));


        this.order.setOId(iLastOrder);

        //transStartTime = new Date().getTime();
        //boolean exist = false;

        // "SELECT O_ENTRY_D, O_CARRIER_ID, O_OL_CNT FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
        whereClause = Orders.COL_O_W_ID + " = ? AND " + Orders.COL_O_D_ID + " = ? AND " + Orders.COL_O_ID + " = ?";
        whereArgs = new String[] {
                String.valueOf(customer.getCWId()),
                String.valueOf(customer.getCDId()),
                String.valueOf(this.order.getOId())};

        cursor = db.query(Orders.TABLE, null, whereClause, whereArgs, null, null, null); //tableColumns
        if (cursor.moveToFirst()) {
            this.order.setOEntryD(cursor.getString(cursor.getColumnIndex(Orders.COL_O_ENTRY_D)));
            this.order.setOCarrierId(cursor.getShort(cursor.getColumnIndex(Orders.COL_O_CARRIER_ID)));
            this.order.setOOlCnt(cursor.getShort(cursor.getColumnIndex(Orders.COL_O_OL_CNT)));
            //exist = true;
        }

        cursor.close();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SqliteStandard.class.getName(), "getOrderStatusForCustomer s2 duration[" + (exist == false ? 0 : 1) + "] = " + (transEndTime - transStartTime));

        //transStartTime = new Date().getTime();

        // "SELECT OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
        whereClause = OrderLine.COL_OL_W_ID + " = ? AND " + OrderLine.COL_OL_D_ID + " = ? AND " + OrderLine.COL_OL_O_ID + " = ?";
        whereArgs = new String[] {
                String.valueOf(this.order.getOWId()),
                String.valueOf(this.order.getODId()),
                String.valueOf(this.order.getOId())};
        cursor = db.query(OrderLine.TABLE, null, whereClause, whereArgs, null, null, null); //tableColumns

        OrderLine[] lineItems = new OrderLine[this.order.getOOlCnt()];
        OrderLine ol = null;
        int oli = 0;
        if (cursor.moveToFirst()) {
            do {
                ol = new OrderLine();
                ol.setOlIId(cursor.getInt(cursor.getColumnIndex(OrderLine.COL_OL_I_ID)));
                ol.setOlSupplyWId(cursor.getShort(cursor.getColumnIndex(OrderLine.COL_OL_SUPPLY_W_ID)));
                ol.setOlQuantity(cursor.getShort(cursor.getColumnIndex(OrderLine.COL_OL_QUANTITY)));
                ol.setOlAmount(cursor.getFloat(cursor.getColumnIndex(OrderLine.COL_OL_AMOUNT)));
                ol.setOlDeliveryD(cursor.getString(cursor.getColumnIndex(OrderLine.COL_OL_DELIVERY_D)));

                if (oli < this.order.getOOlCnt()) {
                    lineItems[(oli++)] = ol;
                } else {
                    oli++;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SqliteStandard.class.getName(), "getOrderStatusForCustomer s3 duration[" + oli + "] = " + (transEndTime - transStartTime));


        if (display != null) {
            display.displayOrderStatus(displayData, byName, customer, this.order, lineItems);
        }
    }

    public void payment(int terminalId, SqliteDisplay display, Object displayData, short w, short d, short cw, short cd, String customerLast, String amount)
            throws Exception {
        SQLiteDatabase db = getReadableDatabase();

        //transStartTime = new Date().getTime();

        // "SELECT C_ID FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_LAST = ? ORDER BY C_FIRST"
        String whereClause = Customer.COL_C_W_ID + " = ? AND " + Customer.COL_C_D_ID + " = ? AND " + Customer.COL_C_LAST + " = ?";
        String[] whereArgs = new String[] {
                String.valueOf(cw),
                String.valueOf(cd),
                customerLast};
        String orderBy = Customer.COL_C_FIRST;

        Cursor cursor = db.query(Customer.TABLE, null, whereClause, whereArgs, null, null, orderBy); //tableColumns

        this.nameList.clear();
        if (cursor.moveToFirst()) {
            do {
                this.nameList.add(cursor.getInt(cursor.getColumnIndex(Customer.COL_C_ID)));
            } while (cursor.moveToNext());
        }

        cursor.close();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SqliteStandard.class.getName(), "payment s1 duration[" + this.nameList.size() + "] = " + (transEndTime - transStartTime));


        if (this.nameList.isEmpty()) {
            throw new PaymentException("Payment by name: Tml[" + terminalId + "] - no matching customer " + customerLast);
        }

        int mid = this.nameList.size() / 2;
        if ((mid != 0) && (this.nameList.size() % 2 == 1)) {
            mid++;
        }
        int c = (int) this.nameList.get(mid);
        paymentById(terminalId, display, displayData, w, d, cw, cd, c, amount);

        if (display != null) {
        }
    }

    public void payment(int terminalId, SqliteDisplay display, Object displayData, short w, short d, short cw, short cd, int c, String amount)
            throws Exception {
        paymentById(terminalId, display, displayData, w, d, cw, cd, c, amount);

        if (display != null) {

        }
    }

    private void paymentById(int terminalId, SqliteDisplay display, Object displayData, short w, short d, short cw, short cd, int c, String s_amount)
            throws Exception {

        SQLiteDatabase db = getWritableDatabase();

        // "UPDATE CUSTOMER SET C_BALANCE = C_BALANCE - ?, C_YTD_PAYMENT = C_YTD_PAYMENT + ?, C_PAYMENT_CNT = C_PAYMENT_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        Customer customer = new Customer();
        customer.setCWId(cw);
        customer.setCDId(cd);
        customer.setCId(c);
        customer.setCompositeKey(customer.getCompositeKey(customer));

        //transStartTime = new Date().getTime();

        // sqlite "ContentValues" does not support expression update
        String strSQL = "UPDATE CUSTOMER SET C_BALANCE = C_BALANCE - ?, C_YTD_PAYMENT = C_YTD_PAYMENT + ?, C_PAYMENT_CNT = C_PAYMENT_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?";
        db.execSQL(strSQL, new String[]{
                s_amount,
                s_amount,
                String.valueOf(cw),
                String.valueOf(cd),
                String.valueOf(c)});

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SqliteStandard.class.getName(), "paymentById u1 duration = " + (transEndTime - transStartTime));


        db = getReadableDatabase();

        //transStartTime = new Date().getTime();

        // "SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE, C_STREET_1, C_STREET_2, C_CITY, C_STATE, C_ZIP, C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_DATA FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?
        String whereClause = Customer.COL_C_W_ID + " = ? AND " + Customer.COL_C_D_ID + " = ? AND " + Customer.COL_C_ID + " = ?";
        String[] whereArgs = new String[] {
                String.valueOf(cw),
                String.valueOf(cd),
                String.valueOf(c)};

        Cursor cursor = db.query(Customer.TABLE, null, whereClause, whereArgs, null, null, null); //tableColumns

        //boolean exist = false;

        if (cursor.moveToFirst()) {
            customer.setCFst(cursor.getString(cursor.getColumnIndex(Customer.COL_C_FIRST)));
            customer.setCMid(cursor.getString(cursor.getColumnIndex(Customer.COL_C_MIDDLE)));
            customer.setCLst(cursor.getString(cursor.getColumnIndex(Customer.COL_C_LAST)));
            customer.setCBalance(cursor.getFloat(cursor.getColumnIndex(Customer.COL_C_BALANCE)));

            // Address
            customer.setCStreet1(cursor.getString(cursor.getColumnIndex(Customer.COL_C_STREET_1)));
            customer.setCStreet2(cursor.getString(cursor.getColumnIndex(Customer.COL_C_STREET_2)));
            customer.setCCity(cursor.getString(cursor.getColumnIndex(Customer.COL_C_CITY)));
            customer.setCState(cursor.getString(cursor.getColumnIndex(Customer.COL_C_STATE)));
            customer.setCZip(cursor.getString(cursor.getColumnIndex(Customer.COL_C_ZIP)));

            customer.setCPhone(cursor.getString(cursor.getColumnIndex(Customer.COL_C_PHONE)));
            customer.setCSince(cursor.getString(cursor.getColumnIndex(Customer.COL_C_SINCE)));
            customer.setCCredit(cursor.getString(cursor.getColumnIndex(Customer.COL_C_CREDIT)));
            customer.setCCreditLim(Float.parseFloat(cursor.getString(cursor.getColumnIndex(Customer.COL_C_CREDIT_LIM))));
            customer.setCDiscount(Float.parseFloat(cursor.getString(cursor.getColumnIndex(Customer.COL_C_DISCOUNT))));
            customer.setCData(cursor.getString(cursor.getColumnIndex(Customer.COL_C_DATA)));

            //exist  = true;
        }

        cursor.close();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SqliteStandard.class.getName(), "paymentById s2 duration[" + (exist == false ? 0 : 1) + "] = " + (transEndTime - transStartTime));


        if ("BC".equals(customer.getCCredit())) {
            // "UPDATE CUSTOMER SET C_DATA = ? WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Customer.COL_C_DATA, Data.dataForBadCredit(customer.getCData(), w, d, cw, cd, c, new BigDecimal(s_amount)));
            whereClause = Customer.COL_C_W_ID + " = ?  AND " + Customer.COL_C_D_ID + " = ? AND " + Customer.COL_C_ID + " = ?";
            whereArgs = new String[] {
                    String.valueOf(cw),
                    String.valueOf(cd),
                    String.valueOf(c)};
            db.update(Customer.TABLE, values, whereClause, whereArgs);

            db = getReadableDatabase();

            // "SELECT SUBSTR(C_DATA, 1, 200) AS C_DATA_200 FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
            String[] tableColumns = new String[] { "(SELECT SUBSTR(C_DATA, 1, 200) FROM CUSTOMER) AS C_DATA_200" };
            cursor = db.query(Customer.TABLE, tableColumns, whereClause, whereArgs, null, null, null);
            String strData = null;
            if (cursor.moveToFirst()) {
                strData = cursor.getString(cursor.getColumnIndex("C_DATA_200"));
            }

            cursor.close();
            customer.setCData(strData);
        }

        db = getWritableDatabase();
        // "UPDATE DISTRICT SET D_YTD = D_YTD + ? WHERE D_W_ID = ? AND D_ID = ?"
        this.district.clear();
        this.district.setDWId(w);
        this.district.setDId(d);

        strSQL = "UPDATE DISTRICT SET D_YTD = D_YTD + ? WHERE D_W_ID = ? AND D_ID = ?";
        db.execSQL(strSQL, new String[]{
                s_amount,
                String.valueOf(w),
                String.valueOf(d)});

        db = getReadableDatabase();
        // "SELECT D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ? "
        whereClause = District.COL_D_W_ID + " = ? AND " + District.COL_D_ID + " = ?";
        whereArgs = new String[] {
                String.valueOf(w),
                String.valueOf(d)};

        cursor = db.query(District.TABLE, null, whereClause, whereArgs, null, null, null); //tableColumns

        if (cursor.moveToFirst()) {
            this.district.setDName(cursor.getString(cursor.getColumnIndex(District.COL_D_NAME)));
            this.district.setDStreet1(cursor.getString(cursor.getColumnIndex(District.COL_D_STREET_1)));
            this.district.setDStreet2(cursor.getString(cursor.getColumnIndex(District.COL_D_STREET_2)));
            this.district.setDCity(cursor.getString(cursor.getColumnIndex(District.COL_D_CITY)));
            this.district.setDState(cursor.getString(cursor.getColumnIndex(District.COL_D_STATE)));
            this.district.setDZip(cursor.getString(cursor.getColumnIndex(District.COL_D_ZIP)));
        }
        cursor.close();

        db = getWritableDatabase();
        // "UPDATE WAREHOUSE SET W_YTD = W_YTD + ? WHERE W_ID = ?"
        strSQL = "UPDATE WAREHOUSE SET W_YTD = W_YTD + ? WHERE W_ID = ?";
        db.execSQL(strSQL, new String[]{
                s_amount,
                String.valueOf(w)});

        db = getReadableDatabase();

        // "SELECT W_NAME, W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP FROM WAREHOUSE WHERE W_ID = ?"
        whereClause = Warehouse.COL_W_ID + " = ? ";
        whereArgs = new String[] { String.valueOf(w) };

        cursor = db.query(Warehouse.TABLE, null, whereClause, whereArgs, null, null, null); //tableColumns
        if (cursor.moveToFirst()) {
            this.warehouse.setWName(cursor.getString(cursor.getColumnIndex(Warehouse.COL_W_NAME)));
            this.warehouse.setWStreet1(cursor.getString(cursor.getColumnIndex(Warehouse.COL_W_STREET_1)));
            this.warehouse.setWStreet2(cursor.getString(cursor.getColumnIndex(Warehouse.COL_W_STREET_2)));
            this.warehouse.setWCity(cursor.getString(cursor.getColumnIndex(Warehouse.COL_W_CITY)));
            this.warehouse.setWState(cursor.getString(cursor.getColumnIndex(Warehouse.COL_W_STATE)));
            this.warehouse.setWZip(cursor.getString(cursor.getColumnIndex(Warehouse.COL_W_ZIP)));
        }

        cursor.close();

        db = getWritableDatabase();

        // "INSERT INTO HISTORY(H_C_ID, H_C_D_ID, H_C_W_ID, H_D_ID, H_W_ID, H_AMOUNT, H_DATA, H_DATE, H_INITIAL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, FALSe)"
        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());

        ContentValues values = new ContentValues();
        values.put(History.COL_H_C_ID, c);
        values.put(History.COL_H_C_D_ID, cd);
        values.put(History.COL_H_C_W_ID, cw);
        values.put(History.COL_H_D_ID, d);
        values.put(History.COL_H_W_ID, w);
        values.put(History.COL_H_AMOUNT, s_amount);

        StringBuffer hData = new StringBuffer(24);
        hData.append(this.warehouse.getWName());
        hData.append("    ");
        hData.append(this.district.getDName());
        values.put(History.COL_H_DATA, hData.toString());
        values.put(History.COL_H_DATE, currentTimeStamp.toString());
        values.put(History.COL_H_INITIAL, false);

        // Insert Row
        long insertId = db.insert(History.TABLE, null, values);
    }

    public void newOrder(int terminalId, SqliteDisplay display, Object displayData, short w, short d, int c, int[] items, short[] quantities, short[] supplyW)
            throws Exception {
        if (quantities == null || quantities.length == 0) {
            TPCCLog.e(SqliteStandard.class.getName(), "newOrder Tml[" + terminalId + "]: quantities is null");
        }

        if (items == null || items.length == 0) {
            TPCCLog.e(SqliteStandard.class.getName(), "newOrder Tml[" + terminalId + "]: items is null");
        }

        if (supplyW == null || supplyW.length == 0) {
            TPCCLog.e(SqliteStandard.class.getName(), "newOrder Tml[" + terminalId + "]: supplyW is null");
        }

        sortOrderItems(items, quantities, supplyW);

        SQLiteDatabase db = getReadableDatabase();

        // "SELECT W_TAX FROM WAREHOUSE WHERE W_ID = ?"
        String whereClause = Warehouse.COL_W_ID + " = ? ";
        String[] whereArgs = new String[] { String.valueOf(w) };

        Cursor cursor = db.query(Warehouse.TABLE, null, whereClause, whereArgs, null, null, null); //tableColumns
        if (cursor.moveToFirst()) {
            float warehouseTax = (cursor.getFloat(cursor.getColumnIndex(Warehouse.COL_W_TAX)));
        }
        cursor.close();

        db = getWritableDatabase();
        // "UPDATE DISTRICT SET D_NEXT_O_ID = D_NEXT_O_ID + 1 WHERE D_W_ID = ? AND D_ID = ?"
        String strSQL = "UPDATE DISTRICT SET D_NEXT_O_ID = D_NEXT_O_ID + 1 WHERE D_W_ID = ? AND D_ID = ?";
        db.execSQL(strSQL, new String[]{
                String.valueOf(w),
                String.valueOf(d)});

        db = getReadableDatabase();
        // "SELECT D_NEXT_O_ID - 1, D_TAX FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?"
        String[] tableColumns = new String[] {
            "(SELECT (D_NEXT_O_ID - 1) FROM DISTRICT) AS NEW_D_NEXT_O_ID",
            District.COL_D_TAX };
        whereClause = District.COL_D_W_ID + " = ? AND " + District.COL_D_ID + " = ?";
        whereArgs = new String[] {
            String.valueOf(w),
            String.valueOf(d)};
        cursor = db.query(District.TABLE, tableColumns, whereClause, whereArgs, null, null, null);

        int orderNumber = 0;
        float districtTax = -1;
        if (cursor.moveToFirst()) {
            orderNumber = cursor.getInt(cursor.getColumnIndex("NEW_D_NEXT_O_ID"));
            districtTax = cursor.getFloat(cursor.getColumnIndex(District.COL_D_TAX));
        }
        cursor.close();

        // "SELECT C_LAST, C_DISCOUNT, C_CREDIT FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        whereClause = Customer.COL_C_W_ID + " = ? AND " + Customer.COL_C_D_ID + " = ? AND " + Customer.COL_C_ID + " = ?";
        whereArgs = new String[] {
                String.valueOf(w),
                String.valueOf(d),
                String.valueOf(c)};

        String cusName = null;
        cursor = db.query(Customer.TABLE, null, whereClause, whereArgs, null, null, null); //tableColumns
        if (cursor.moveToFirst()) {
            cusName =  cursor.getString(cursor.getColumnIndex(Customer.COL_C_LAST));
        }
        cursor.close();

        short allLocal = 1;
        for (int i = 0; i < supplyW.length; i++) {
            if (supplyW[i] != w) {
                allLocal = 0;
                break;
            }
        }

        // "INSERT INTO ORDERS VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, NULL, ?, ?, NULL, FALSE)"
        ContentValues values = null;
        long insertId = -1;
        db = getWritableDatabase();
        values = new ContentValues();
        values.put(Orders.COL_O_ID, orderNumber);
        values.put(Orders.COL_O_D_ID, d);
        values.put(Orders.COL_O_W_ID, w);
        values.put(Orders.COL_O_COMPO, orderNumber + "-" + d + "-" + w);
        values.put(Orders.COL_O_C_ID, c);
        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        values.put(Orders.COL_O_ENTRY_D, currentTimeStamp.toString());
        if (null != items) {
            values.put(Orders.COL_O_OL_CNT, (short) items.length);
        } else {
            values.put(Orders.COL_O_OL_CNT, (short) 0);
        }
        values.put(Orders.COL_O_ALL_LOCAL, allLocal);
        values.put(Orders.COL_O_INITIAL, false);

        // Insert Row
        insertId = db.insert(Orders.TABLE, null, values);
        if (-1 == insertId) {
            throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert Orders[" + orderNumber + "-" + d + "-" + w + "] fail ");
        }

        // "INSERT INTO NEWORDERS VALUES(?, ?, ?, FALSE, TRUE)"
        db = getWritableDatabase();
        values = new ContentValues();
        values.put(NewOrders.COL_NO_O_ID, orderNumber);
        values.put(NewOrders.COL_NO_D_ID, d);
        values.put(NewOrders.COL_NO_W_ID, w);
        values.put(NewOrders.COL_NO_COMPO, orderNumber + "-" + d + "-" + w);
        values.put(NewOrders.COL_NO_INITIAL, false);
        values.put(NewOrders.COL_NO_LIVE, true);

        insertId = db.insert(NewOrders.TABLE, null, values);
        if (-1 == insertId) {
            throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert NewOrders[" + orderNumber + "-" + d + "-" + w + "] fail ");
        }

        String colDist = null;
        int length = 0;
        if (null != items) {
            length = items.length;
        }

        float itemPrice = 0;
        String itemName = null;
        String itemData = null;
        int stockQuantity = -1;
        String stockDistInfo = null;
        String stockData = null;
        for (int i = 0; i < length; i++) {
            db = getReadableDatabase();
            // "SELECT I_PRICE, I_NAME, I_DATA FROM ITEM WHERE I_ID = ?"
            whereClause = Item.COL_I_ID + " = ? ";
            whereArgs = new String[] { String.valueOf(items[i]) };

            cursor = db.query(Item.TABLE, null, whereClause, whereArgs, null, null, null); //tableColumns

            itemPrice = 0;
            itemName = null;
            itemData = null;
            if (cursor.moveToFirst()) {
                itemPrice = cursor.getFloat(cursor.getColumnIndex(Item.COL_I_PRICE));
                itemName = cursor.getString(cursor.getColumnIndex(Item.COL_I_NAME));
                itemData = cursor.getString(cursor.getColumnIndex(Item.COL_I_DATA));
            }

            cursor.close();

            // String[] STOCK_INFO = {
            // "SELECT S_QUANTITY, S_DIST_01, S_DATA FROM STOCK WHERE S_I_ID = ? AND S_W_ID = ?",
            // "SELECT S_QUANTITY, S_DIST_02, S_DATA FROM STOCK WHERE S_I_ID = ? AND S_W_ID = ?",
            // "SELECT S_QUANTITY, S_DIST_03, S_DATA FROM STOCK WHERE S_I_ID = ? AND S_W_ID = ?",
            // "SELECT S_QUANTITY, S_DIST_04, S_DATA FROM STOCK WHERE S_I_ID = ? AND S_W_ID = ?",
            // "SELECT S_QUANTITY, S_DIST_05, S_DATA FROM STOCK WHERE S_I_ID = ? AND S_W_ID = ?",
            // "SELECT S_QUANTITY, S_DIST_06, S_DATA FROM STOCK WHERE S_I_ID = ? AND S_W_ID = ?",
            // "SELECT S_QUANTITY, S_DIST_07, S_DATA FROM STOCK WHERE S_I_ID = ? AND S_W_ID = ?",
            // "SELECT S_QUANTITY, S_DIST_08, S_DATA FROM STOCK WHERE S_I_ID = ? AND S_W_ID = ?",
            // "SELECT S_QUANTITY, S_DIST_09, S_DATA FROM STOCK WHERE S_I_ID = ? AND S_W_ID = ?",
            // "SELECT S_QUANTITY, S_DIST_10, S_DATA FROM STOCK WHERE S_I_ID = ? AND S_W_ID = ?" };
            colDist = d < 10 ? "S_DIST_0" + d : "S_DIST_" + d;
            whereClause = Stock.COL_S_I_ID + " = ? AND " + Stock.COL_S_W_ID + " = ? ";
            whereArgs = new String[] {
                    String.valueOf(items[i]),
                    String.valueOf(w)};

            cursor = db.query(Stock.TABLE, null, whereClause, whereArgs, null, null, null); //tableColumns
            stockQuantity = -1;
            stockDistInfo = null;
            stockData = null;
            if (cursor.moveToFirst()) {
                stockQuantity = cursor.getInt(cursor.getColumnIndex(Stock.COL_S_QUANTITY));
                stockDistInfo = cursor.getString(cursor.getColumnIndex(colDist));
                stockData = cursor.getString(cursor.getColumnIndex(Stock.COL_S_DATA));
            }
            cursor.close();

            db = getWritableDatabase();
            // "UPDATE STOCK SET S_ORDER_CNT = S_ORDER_CNT + 1, S_YTD = S_YTD + ?, S_REMOTE_CNT = S_REMOTE_CNT + ?, S_QUANTITY = ? WHERE S_I_ID = ? AND S_W_ID = ?"
            strSQL = "UPDATE STOCK SET S_ORDER_CNT = S_ORDER_CNT + 1, S_YTD = S_YTD + ?, S_REMOTE_CNT = S_REMOTE_CNT + ?, S_QUANTITY = ? WHERE S_I_ID = ? AND S_W_ID = ?";

            if (null != quantities){
                if (stockQuantity - quantities[i] > 10) {
                    stockQuantity -= quantities[i];
                } else {
                    stockQuantity = stockQuantity - quantities[i] + 91;
                }
            }

            db.execSQL(strSQL, new String[]{
                    String.valueOf(null != quantities ? quantities[i] : 0),
                    String.valueOf(null != supplyW ? (w == supplyW[i] ? 0 : 1) : 1),
                    String.valueOf(stockQuantity),
                    String.valueOf(items[i]),
                    String.valueOf(w)});

            if (stockDistInfo != null) {
                // "INSERT INTO ORDERLINE(OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER, OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO, OL_DELIVERY_D, OL_DELIVERY_D_INITIAL, OL_INITIAL) VALUES (?, ?, ?, ?, ?, ?, ?, CAST (? AS DECIMAL(5,2)) * CAST (? AS SMALLINT), ?, NULL, NULL, FALSE)"

                // CAST(expression AS data type)
                // SELECT CAST('2000-10-31' AS DATE)
                // SELECT CAST(1+2 AS CHAR)
                // SELECT CAST('Surname' AS CHAR(5))
                values = new ContentValues();
                values.put(OrderLine.COL_OL_W_ID, w);
                values.put(OrderLine.COL_OL_D_ID, d);
                values.put(OrderLine.COL_OL_O_ID, orderNumber);
                values.put(OrderLine.COL_OL_NUMBER, (short) (i + 1));
                values.put(OrderLine.COL_OL_COMPO, orderNumber + "-" + d + "-"  + w + "-"  + (i + 1));
                values.put(OrderLine.COL_OL_I_ID, items[i]);
                values.put(OrderLine.COL_OL_SUPPLY_W_ID, supplyW[i]);
                values.put(OrderLine.COL_OL_QUANTITY, quantities[i]);
                values.put(OrderLine.COL_OL_AMOUNT, itemPrice * quantities[i]);
                values.put(OrderLine.COL_OL_DIST_INFO, stockDistInfo);
                values.put(OrderLine.COL_OL_INITIAL, false);

                // Insert Row
                insertId = db.insert(OrderLine.TABLE, null, values);
                if (-1 == insertId) {
                    throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert OrderLine[" + orderNumber + "-" + d + "-" + w + "-" + (i + 1) + "] fail ");
                }
            }
        }

        db = getReadableDatabase();
        // "SELECT SUM(OL_AMOUNT) FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
        tableColumns = new String[] { "(SELECT SUM(OL_AMOUNT) FROM ORDERLINE) AS OL_AMOUNT_SUM" };
        whereClause = OrderLine.COL_OL_W_ID + " = ? AND " + OrderLine.COL_OL_D_ID + " = ? AND " + OrderLine.COL_OL_O_ID + " = ?";
        whereArgs = new String[] {
                String.valueOf(w),
                String.valueOf(d),
                String.valueOf(orderNumber)};

        cursor = db.query(OrderLine.TABLE, tableColumns, whereClause, whereArgs, null, null, null);
        if (cursor.moveToFirst()) {
            float orderTotal = cursor.getFloat(cursor.getColumnIndex("OL_AMOUNT_SUM"));

        }
        cursor.close();
    }

    public void scheduleDelivery(int terminalId, SqliteDisplay display, Object displayData, short w, short carrier)
            throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        // "INSERT INTO DELIVERY_REQUEST(DR_W_ID, DR_CARRIER_ID, DR_STATE) VALUES(?, ?, 'Q')"
        ContentValues values= new ContentValues();
        values.put(DeliveryRequest.COL_DR_W_ID, w);
        values.put(DeliveryRequest.COL_DR_CARRIER_ID, carrier);
        values.put(DeliveryRequest.COL_DR_STATE, "Q");

        // Insert Row
        long insertId = db.insert(DeliveryRequest.TABLE, null, values);

        if (display != null) {
            display.displayScheduleDelivery(displayData, w, carrier);
        }
    }

    public void delivery(int terminalId) throws Exception {
        SQLiteDatabase db = getReadableDatabase();

        // "SELECT DR_ID, DR_W_ID, DR_CARRIER_ID FROM DELIVERY_REQUEST WHERE DR_STATE = 'Q' ORDER BY DR_QUEUED"
        String whereClause = DeliveryRequest.COL_DR_STATE + " = ?";
        String[] whereArgs = new String[] { "Q" };
        String orderBy = DeliveryRequest.COL_DR_QUEUED;

        Cursor cursor = db.query(DeliveryRequest.TABLE, null, whereClause, whereArgs, null, null, orderBy); //tableColumns
        int request = -1;
        short w = -1;
        short carrier = -1;
        if (cursor.moveToFirst()) {
            request = cursor.getInt(cursor.getColumnIndex(DeliveryRequest.COL_DR_ID));
            w = cursor.getShort(cursor.getColumnIndex(DeliveryRequest.COL_DR_W_ID));
            carrier = cursor.getShort(cursor.getColumnIndex(DeliveryRequest.COL_DR_CARRIER_ID));
        }

        cursor.close();

        db = getWritableDatabase();
        // "UPDATE DELIVERY_REQUEST SET DR_STATE = ? WHERE DR_ID = ?"
        ContentValues values = new ContentValues();
        values.put(DeliveryRequest.COL_DR_STATE, "I");
        whereClause = DeliveryRequest.COL_DR_ID + " = ?";
        whereArgs = new String[] { String.valueOf(request)};
        db.update(DeliveryRequest.TABLE, values, whereClause, whereArgs);

        List<DeliveryOrders> vDos = new ArrayList<DeliveryOrders>();

        DeliveryOrders dos = null;
        String strSQL = null;

        for (short d = 1; d <= 10; d = (short)(d + 1)) {
            // "INSERT INTO DELIVERY_ORDERS(DO_DR_ID, DO_D_ID, DO_O_ID) VALUES (?, ?, ?)"
            dos = new DeliveryOrders();
            dos.setDoDrId(request);
            dos.setDoDId(d);

            db = getReadableDatabase();
            // "SELECT MIN(NO_O_ID) AS ORDER_TO_DELIVER FROM NEWORDERS WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_LIVE"
            String[] tableColumns = new String[] {"(SELECT MIN(NO_O_ID) FROM NEWORDERS) AS ORDER_TO_DELIVER" };
            whereClause = NewOrders.COL_NO_W_ID + " = ? AND " + NewOrders.COL_NO_D_ID + " = ? AND " + NewOrders.COL_NO_LIVE + " = ?";
            whereArgs = new String[] {
                String.valueOf(w),
                String.valueOf(d),
                "true" };

            cursor = db.query(NewOrders.TABLE, tableColumns, whereClause, whereArgs, null, null, null);
            int order = -1;
            if (cursor.moveToFirst()) {
                order = cursor.getInt(cursor.getColumnIndex("ORDER_TO_DELIVER"));
            } else {
                vDos.add(dos);
            }
            cursor.close();

            db = getWritableDatabase();
            // "UPDATE NEWORDERS SET NO_LIVE = FALSE WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_O_ID = ?"
            values = new ContentValues();
            values.put(NewOrders.COL_NO_LIVE, false);
            whereClause = NewOrders.COL_NO_W_ID + " = ? AND " + NewOrders.COL_NO_D_ID + " = ? AND " + NewOrders.COL_NO_O_ID + " = ?";
            whereArgs = new String[] {
                    String.valueOf(w),
                    String.valueOf(d),
                    String.valueOf(order)};
            db.update(NewOrders.TABLE, values, whereClause, whereArgs);

            // "UPDATE ORDERS SET O_CARRIER_ID = ? WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
            values = new ContentValues();
            values.put(Orders.COL_O_CARRIER_ID, carrier);
            whereClause = Orders.COL_O_W_ID + " = ? AND " + Orders.COL_O_D_ID + " = ? AND " + Orders.COL_O_ID + " = ?";
            whereArgs = new String[] {
                    String.valueOf(w),
                    String.valueOf(d),
                    String.valueOf(order)};
            db.update(Orders.TABLE, values, whereClause, whereArgs);

            // "UPDATE ORDERLINE SET OL_DELIVERY_D = CURRENT TIMESTAMP WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
            values = new ContentValues();
            Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
            values.put(OrderLine.COL_OL_DELIVERY_D, currentTimeStamp.toString());
            whereClause = OrderLine.COL_OL_W_ID + " = ? AND " + OrderLine.COL_OL_D_ID + " = ? AND " + OrderLine.COL_OL_O_ID + " = ?";
            whereArgs = new String[] {
                    String.valueOf(w),
                    String.valueOf(d),
                    String.valueOf(order)};
            db.update(OrderLine.TABLE, values, whereClause, whereArgs);

            // "UPDATE CUSTOMER SET C_BALANCE = (SELECT SUM(OL_AMOUNT) FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?), C_DELIVERY_CNT = C_DELIVERY_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = (SELECT O_C_ID FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?)"
            strSQL = "UPDATE CUSTOMER SET C_BALANCE = (SELECT SUM(OL_AMOUNT) FROM ORDERLINE " +
                    "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?), C_DELIVERY_CNT = C_DELIVERY_CNT + 1 " +
                    "WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = (SELECT O_C_ID FROM ORDERS " +
                    "WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?)";
            db.execSQL(strSQL, new String[]{
                    String.valueOf(w),
                    String.valueOf(d),
                    String.valueOf(order),
                    String.valueOf(w),
                    String.valueOf(d),
                    String.valueOf(w),
                    String.valueOf(d),
                    String.valueOf(order)});

            if (-1 != order) {
                dos.setDoOId(order);
                vDos.add(dos);
            }
        }

        db = getWritableDatabase();
        beginTransaction(db);
        // "INSERT INTO DELIVERY_ORDERS(DO_DR_ID, DO_D_ID, DO_O_ID) VALUES (?, ?, ?)"
        long insertId;
        for (DeliveryOrders dlo : vDos) {
            values= new ContentValues();
            values.put(DeliveryOrders.COL_DO_DR_ID, dlo.getDoDrId());
            values.put(DeliveryOrders.COL_DO_D_ID, dlo.getDoDId());
            values.put(DeliveryOrders.COL_DO_O_ID, dlo.getDoOId());

            // Insert Row
            insertId = db.insert(DeliveryOrders.TABLE, null, values);
        }
        db.setTransactionSuccessful();
        endTransaction(db);

        // "UPDATE DELIVERY_REQUEST SET DR_STATE = 'C', DR_COMPLETED = CURRENT TIMESTAMP WHERE DR_ID = ?"
        values = new ContentValues();
        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        values.put(DeliveryRequest.COL_DR_STATE, "C");
        values.put(DeliveryRequest.COL_DR_COMPLETED, currentTimeStamp.toString());
        whereClause = DeliveryRequest.COL_DR_ID + " = ? ";
        whereArgs = new String[] {
                String.valueOf(request) };
        db.update(DeliveryRequest.TABLE, values, whereClause, whereArgs);
    }

    public void sortOrderItems(int[] items, short[] quantities, short[] supplyW) {
        OrderItem4Sort[] list = new OrderItem4Sort[items.length];
        for (int i = 0; i < items.length; i++) {
            list[i] = new OrderItem4Sort(items[i], quantities[i], supplyW[i]);
        }
        Arrays.sort(list);
        for (int i = 0; i < items.length; i++) {
            items[i] = list[i].getI();
            quantities[i] = list[i].getQ();
            supplyW[i] = list[i].getW();
        }
    }
}
