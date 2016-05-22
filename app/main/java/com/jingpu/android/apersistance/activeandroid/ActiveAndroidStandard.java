package com.jingpu.android.apersistance.activeandroid;

import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.jingpu.android.apersistance.activeandroid.model.Customer;
import com.jingpu.android.apersistance.activeandroid.model.DeliveryOrders;
import com.jingpu.android.apersistance.activeandroid.model.DeliveryRequest;
import com.jingpu.android.apersistance.activeandroid.model.District;
import com.jingpu.android.apersistance.activeandroid.model.History;
import com.jingpu.android.apersistance.activeandroid.model.Item;
import com.jingpu.android.apersistance.activeandroid.model.NewOrders;
import com.jingpu.android.apersistance.activeandroid.model.OrderLine;
import com.jingpu.android.apersistance.activeandroid.model.Orders;
import com.jingpu.android.apersistance.activeandroid.model.Stock;
import com.jingpu.android.apersistance.activeandroid.model.Warehouse;
import com.jingpu.android.apersistance.util.OrderStatusException;
import com.jingpu.android.apersistance.util.PaymentException;
import com.jingpu.android.apersistance.util.TPCCLog;

import org.dacapo.derby.Data;
import org.dacapo.derby.OrderItem4Sort;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jing Pu on 2016/1/4.
 */
public class ActiveAndroidStandard extends ActiveAndroidStatementHelper implements ActiveAndroidOperations{
    private final Customer customer = new Customer();
    private final List nameList = new ArrayList();
    private final Orders order = new Orders();
    private final District district = new District();
    private final Warehouse warehouse = new Warehouse();

    // Jing Pu test 5/14/2016
    //long transStartTime = 0;
    //long transEndTime = 0;

    public ActiveAndroidStandard(ActiveAndroidAgent aaa) throws SQLiteException {
        super(aaa);
    }

    public void stockLevel(int terminalId, ActiveAndroidDisplay display, Object displayData, short w, short d, int threshold) throws Exception {
        int iLowStock = 0;

        //transStartTime = new Date().getTime();

        // "SELECT D_NEXT_O_ID FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?"
        District dist = new Select().from(District.class)  //new String[]{District.COL_D_NEXT_O_ID}, must identify "id" field first
                .where("D_W_ID = ? AND D_ID = ?", w, d).executeSingle();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(ActiveAndroidStandard.class.getName(), "stockLevel s1 duration[" + (dist == null ?0:1) + "] = " + (transEndTime - transStartTime));

        int nextOrder = -1;
        if (null != dist) {
            nextOrder = dist.getDNxtOId();
        }

        //transStartTime = new Date().getTime();
        // "SELECT COUNT(DISTINCT(S_I_ID)) AS LOW_STOCK FROM ORDERLINE, STOCK WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID < ? AND OL_O_ID >= ? AND S_W_ID = ? AND S_I_ID = OL_I_ID AND S_QUANTITY < ?"
        iLowStock = new Select().distinct().from(Stock.class)
                .innerJoin(OrderLine.class)
                .on("S_I_ID = OL_I_ID")
                .where("OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID < ? AND OL_O_ID >= ? AND S_W_ID = ? AND S_QUANTITY < ?",
                        w, d, nextOrder, (nextOrder - 20), w, threshold)
                .groupBy("S_I_ID").execute().size();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(ActiveAndroidStandard.class.getName(), "stockLevel s2 duration = " + (transEndTime - transStartTime));


        if (display != null) {
            display.displayStockLevel(displayData, w, d, threshold, iLowStock);
        }
    }

    @SuppressWarnings("unchecked")
    public void orderStatus(int terminalId, ActiveAndroidDisplay display, Object displayData, short w, short d, String customerLast) throws Exception {

        //transStartTime = new Date().getTime();
        // "SELECT C_ID, C_BALANCE, C_FIRST, C_MIDDLE FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_LAST = ? ORDER BY C_FIRST"
        List<Customer> customers = new Select().from(Customer.class) // new String[]{Customer.COL_C_ID, Customer.COL_C_BALANCE, Customer.COL_C_FIRST, Customer.COL_C_MIDDLE}
                .where("C_W_ID = ? AND C_D_ID = ? AND C_LAST = ?", w, d, customerLast).orderBy("C_FIRST ASC").execute();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(ActiveAndroidStandard.class.getName(), "orderStatus 132 s1 prepare duration[" + customers.size() + "] = " + (transEndTime - transStartTime));

        this.nameList.clear();
        if (null != customers && customers.size() > 0) {
            this.nameList.addAll(customers);
        }

        if (this.nameList.isEmpty()) {
            throw new OrderStatusException("Order Status by name: Tml[" + terminalId + "] - no matching customer " + customerLast);
        }

        int mid = this.nameList.size() / 2;
        if ((mid != 0) && (this.nameList.size() % 2 == 1)) {
            mid++;
        }

        Customer customer = (Customer)this.nameList.get(mid);
        this.nameList.clear();

        getOrderStatusForCustomer(terminalId, display, displayData, true, customer);
    }

    public void orderStatus(int terminalId, ActiveAndroidDisplay display, Object displayData, short w, short d, int c) throws Exception {
        this.customer.clear();
        this.customer.setCWId(w);
        this.customer.setCDId(d);
        this.customer.setCId(c);

        //transStartTime = new Date().getTime();
        // "SELECT C_BALANCE, C_FIRST, C_MIDDLE, C_LAST FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        Customer cus = new Select().from(Customer.class) // new String[]{Customer.COL_C_BALANCE, Customer.COL_C_FIRST, Customer.COL_C_MIDDLE, Customer.COL_C_LAST}
                .where("C_W_ID = ? AND C_D_ID = ? AND C_ID = ?", w, d, c).executeSingle();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(ActiveAndroidStandard.class.getName(), "orderStatus 174 s1 duration[" + (cus == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));


        if (null != cus) {
            customer.setCBalance(cus.getCBalance());
            customer.setCFst(cus.getCFst());
            customer.setCMid(cus.getCMid());
            customer.setCLst(cus.getCLst());
        }

        getOrderStatusForCustomer(terminalId, display, displayData, false, this.customer);
    }

    private void getOrderStatusForCustomer(int terminalId, ActiveAndroidDisplay display, Object displayData, boolean byName, Customer customer) throws Exception {

        //transStartTime = new Date().getTime();
        // "SELECT MAX(O_ID) AS LAST_ORDER FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_C_ID = ?";
        Orders o = new Select().from(Orders.class).where("O_W_ID = ? AND O_D_ID = ? AND O_C_ID = ?", customer.getCWId(), customer.getCDId(), customer.getCId())
                .orderBy("O_ID DESC").executeSingle(); //new String[]{Orders.COL_O_ID}

        //transEndTime = new Date().getTime();
        //TPCCLog.v(ActiveAndroidStandard.class.getName(), "getOrderStatusForCustomer s1 duration[" + (o == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));


        int iLastOrder = -1;
        if (null != o) {
            iLastOrder = o.getOId();
        }

        this.order.clear();
        this.order.setOWId(customer.getCWId());
        this.order.setODId(customer.getCDId());
        this.order.setOId(iLastOrder);

        //transStartTime = new Date().getTime();
        // "SELECT O_ENTRY_D, O_CARRIER_ID, O_OL_CNT FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
        o = new Select().from(Orders.class) // new String[]{Orders.COL_O_ENTRY_D, Orders.COL_O_CARRIER_ID, Orders.COL_O_OL_CNT}
                .where("O_W_ID = ? AND O_D_ID = ? AND O_ID = ?", customer.getCWId(), customer.getCDId(), this.order.getOId()).executeSingle();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(ActiveAndroidStandard.class.getName(), "getOrderStatusForCustomer s2 duration[" + (o == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));

        if (null != o) {
            this.order.setOEntryD(o.getOEntryD());
            this.order.setOCarrierId(o.getOCarrierId());
            this.order.setOOlCnt(o.getOOlCnt());
        }

        //transStartTime = new Date().getTime();
        // "SELECT OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
        List<OrderLine> ols = new Select() // new String[]{OrderLine.COL_OL_I_ID, OrderLine.COL_OL_SUPPLY_W_ID, OrderLine.COL_OL_QUANTITY, OrderLine.COL_OL_AMOUNT, OrderLine.COL_OL_DELIVERY_D}
                .from(OrderLine.class).where("OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?", this.order.getOWId(), this.order.getODId(), this.order.getOId()).execute();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(ActiveAndroidStandard.class.getName(), "getOrderStatusForCustomer s3 duration[" + ols.size() + "] = " + (transEndTime - transStartTime));

        OrderLine[] lineItems = new OrderLine[this.order.getOOlCnt()];
        OrderLine ol = null;
        int oli = 0;

        if (null != ols) {
            for (OrderLine olRec : ols) {
                ol = new OrderLine();
                ol.setOlIId(olRec.getOlIId());
                ol.setOlSupplyWId(olRec.getOlSupplyWId());
                ol.setOlQuantity(olRec.getOlQuantity());
                ol.setOlAmount(olRec.getOlAmount());
                ol.setOlDeliveryD(olRec.getOlDeliveryD());

                if (oli < this.order.getOOlCnt()) {
                    lineItems[(oli++)] = ol;
                } else {
                    oli++;
                }
            }
        }

        if (display != null) {
            display.displayOrderStatus(displayData, byName, customer, this.order, lineItems);
        }
    }

    public void payment(int terminalId, ActiveAndroidDisplay display, Object displayData, short w, short d, short cw, short cd, String customerLast, String amount)
            throws Exception {

       //transStartTime = new Date().getTime();
        // "SELECT C_ID FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_LAST = ? ORDER BY C_FIRST"
        List<Customer> customers = new Select().from(Customer.class) // new String[]{Customer.COL_C_ID}
                .where("C_W_ID = ? AND C_D_ID = ? AND C_LAST = ?", cw, cd, customerLast).orderBy("C_FIRST ASC").execute();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(ActiveAndroidStandard.class.getName(), "payment s1 duration[" + customers.size() + "] = " + (transEndTime - transStartTime));

        this.nameList.clear();

        if (null != customers) {
            for (Customer cus: customers) {
                this.nameList.add(cus.getCId());
            }
        }

        if (this.nameList.isEmpty()) {
            //throw new SQLiteException("Payment by name: Tml[" + terminalId + "] - no matching customer " + customerLast);
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

    public void payment(int terminalId, ActiveAndroidDisplay display, Object displayData, short w, short d, short cw, short cd, int c, String amount)
            throws Exception {
        paymentById(terminalId, display, displayData, w, d, cw, cd, c, amount);

        if (display != null) {

        }
    }

    private void paymentById(int terminalId, ActiveAndroidDisplay display, Object displayData, short w, short d, short cw, short cd, int c, String s_amount)
            throws Exception {

        // "UPDATE CUSTOMER SET C_BALANCE = C_BALANCE - ?, C_YTD_PAYMENT = C_YTD_PAYMENT + ?, C_PAYMENT_CNT = C_PAYMENT_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        Customer customer = new Customer();
        customer.setCWId(cw);
        customer.setCDId(cd);
        customer.setCId(c);
        customer.setCompositeKey(customer.getCompositeKey(customer));

        //transStartTime = new Date().getTime();

        new Update(Customer.class).set("C_BALANCE = C_BALANCE - ?, C_YTD_PAYMENT = C_YTD_PAYMENT + ?, C_PAYMENT_CNT = C_PAYMENT_CNT + 1", Float.parseFloat(s_amount), Float.parseFloat(s_amount))
                .where("C_W_ID = ? AND C_D_ID = ? AND C_ID = ?", cw, cd, c).execute();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(ActiveAndroidStandard.class.getName(), "paymentById u1 duration = " + (transEndTime - transStartTime));

        //transStartTime = new Date().getTime();

        // "SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE, C_STREET_1, C_STREET_2, C_CITY, C_STATE, C_ZIP,
        // C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_DATA FROM CUSTOMER
        // WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?
        Customer cus = new Select().from(Customer.class).where("C_W_ID = ? AND C_D_ID = ? AND C_ID = ?", cw, cd, c).executeSingle();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(ActiveAndroidStandard.class.getName(), "paymentById s2 duration[" + (cus == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));

        if (null != cus) {
            customer.setCFst(cus.getCFst());
            customer.setCMid(cus.getCMid());
            customer.setCLst(cus.getCLst());
            customer.setCBalance(cus.getCBalance());

            // Address
            customer.setCStreet1(cus.getCStreet1());
            customer.setCStreet2(cus.getCStreet2());
            customer.setCCity(cus.getCCity());
            customer.setCState(cus.getCState());
            customer.setCZip(cus.getCZip());

            customer.setCPhone(cus.getCPhone());
            customer.setCSince(cus.getCSince());
            customer.setCCredit(cus.getCCredit());
            customer.setCCreditLim(cus.getCCreditLim());
            customer.setCDiscount(cus.getCDiscount());
            customer.setCData(cus.getCData());
        }


        if ("BC".equals(customer.getCCredit())) {
            // "UPDATE CUSTOMER SET C_DATA = ? WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
            new Update(Customer.class).set("C_DATA = ?", Data.dataForBadCredit(customer.getCData(), w, d, cw, cd, c, new BigDecimal(s_amount)))
                    .where("C_W_ID = ? AND C_D_ID = ? AND C_ID = ?", cw, cd, c).execute();

            // "SELECT SUBSTR(C_DATA, 1, 200) AS C_DATA_200 FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
            cus = new Select().from(Customer.class).where("C_W_ID = ? AND C_D_ID = ? AND C_ID = ?", cw, cd, c).executeSingle(); // new String[]{Customer.COL_C_DATA}

            String strData = null;
            String cData = null;
            if (null != cus) {
                cData = cus.getCData();
                if (cData.length() > 200) {
                    strData = cData.substring(0, 200);
                } else {
                    strData = cData;
                }
            }

            customer.setCData(strData);
        }

        // "UPDATE DISTRICT SET D_YTD = D_YTD + ? WHERE D_W_ID = ? AND D_ID = ?"
        this.district.clear();
        this.district.setDWId(w);
        this.district.setDId(d);

        new Update(District.class).set("D_YTD = D_YTD + ?", Float.parseFloat(s_amount))
                .where("D_W_ID = ? AND D_ID = ?", w, d).execute();

        // "SELECT D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ? "
        District dist = new Select()
                .from(District.class).where("D_W_ID = ? AND D_ID = ?", w, d).executeSingle();

        if (null != dist) {
            this.district.setDName(dist.getDName());
            this.district.setDStreet1(dist.getDStreet1());
            this.district.setDStreet2(dist.getDStreet2());
            this.district.setDCity(dist.getDCity());
            this.district.setDState(dist.getDState());
            this.district.setDZip(dist.getDZip());
        }

        // "UPDATE WAREHOUSE SET W_YTD = W_YTD + ? WHERE W_ID = ?"
        new Update(Warehouse.class).set("W_YTD = W_YTD + ?", Float.parseFloat(s_amount)).where("W_ID = ?", w).execute();

        // "SELECT W_NAME, W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP FROM WAREHOUSE WHERE W_ID = ?"
        Warehouse wh = new Select() //new String[]{Warehouse.COL_W_NAME, Warehouse.COL_W_STREET_1, Warehouse.COL_W_STREET_2, Warehouse.COL_W_CITY, Warehouse.COL_W_STATE, Warehouse.COL_W_ZIP}
                .from(Warehouse.class).where("W_ID = ?", w).executeSingle();

        if (null != wh) {
            this.warehouse.setWName(wh.getWName());
            this.warehouse.setWStreet1(wh.getWStreet1());
            this.warehouse.setWStreet2(wh.getWStreet2());
            this.warehouse.setWCity(wh.getWCity());
            this.warehouse.setWState(wh.getWState());
            this.warehouse.setWZip(wh.getWZip());
        }

        // "INSERT INTO HISTORY(H_C_ID, H_C_D_ID, H_C_W_ID, H_D_ID, H_W_ID, H_AMOUNT, H_DATA, H_DATE, H_INITIAL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, FALSe)"
        History h = new History();
        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        h.setHCId(c);
        h.setHCDId(cd);
        h.setHCWId(cw);
        h.setHDId(d);
        h.setHWId(w);
        h.setHAmount(Float.parseFloat(s_amount));

        StringBuffer hData = new StringBuffer(24);
        hData.append(this.warehouse.getWName());
        hData.append("    ");
        hData.append(this.district.getDName());
        h.setHData(hData.toString());
        h.setHDate(currentTimeStamp);
        h.setHInitial(false);
        h.save();
    }

    public void newOrder(int terminalId, ActiveAndroidDisplay display, Object displayData, short w, short d, int c, int[] items, short[] quantities, short[] supplyW)
            throws Exception {
        if (quantities == null || quantities.length == 0) {
            TPCCLog.e(ActiveAndroidStandard.class.getName(), "newOrder Tml[" + terminalId + "]: quantities is null");
        }

        if (items == null || items.length == 0) {
            TPCCLog.e(ActiveAndroidStandard.class.getName(), "newOrder Tml[" + terminalId + "]: items is null");
        }

        if (supplyW == null || supplyW.length == 0) {
            TPCCLog.e(ActiveAndroidStandard.class.getName(), "newOrder Tml[" + terminalId + "]: supplyW is null");
        }

        sortOrderItems(items, quantities, supplyW);

        // "SELECT W_TAX FROM WAREHOUSE WHERE W_ID = ?"
        Warehouse wh = new Select().from(Warehouse.class).where("W_ID = ?", w).executeSingle(); // new String[]{Warehouse.COL_W_TAX}

        if (null != wh) {
            float warehouseTax = wh.getWTax();
        }

        // "UPDATE DISTRICT SET D_NEXT_O_ID = D_NEXT_O_ID + 1 WHERE D_W_ID = ? AND D_ID = ?"
        new Update(District.class).set("D_NEXT_O_ID = D_NEXT_O_ID + 1").where("D_W_ID = ? AND D_ID = ?", w, d).execute();

        // "SELECT D_NEXT_O_ID - 1, D_TAX FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?"
        District dist = new Select().from(District.class)
                .where("D_W_ID = ? AND D_ID = ?", w, d).executeSingle();

        int orderNumber = 0;
        float districtTax = -1;
        if (null != dist) {
            orderNumber = dist.getDNxtOId() - 1;
            districtTax = dist.getDTax();
        }

        // "SELECT C_LAST, C_DISCOUNT, C_CREDIT FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        Customer cus = new Select().from(Customer.class)
                .where("C_W_ID = ? AND C_D_ID = ? AND C_ID = ?", w, d, c).executeSingle();

        short allLocal = 1;
        for (int i = 0; i < supplyW.length; i++) {
            if (supplyW[i] != w) {
                allLocal = 0;
                break;
            }
        }

        long insertId = -1;

        // "INSERT INTO ORDERS VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, NULL, ?, ?, NULL, FALSE)"
        Orders o = new Orders();
        o.setOId(orderNumber);
        o.setODId(d);
        o.setOWId(w);
        o.setCompositeKey(o.getCompositeKey(o));
        o.setOCId(c);

        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        o.setOEntryD(currentTimeStamp);

        if (null != items) {
            o.setOOlCnt((short)items.length);
        } else {
            o.setOOlCnt((short) 0);
        }
        o.setOAllLocal(allLocal);
        o.setOInitial(false);

        insertId = o.save();
        if (-1 == insertId) {
            throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert Orders[" + orderNumber + "-"  + d + "-" + w + "] fail ");
        }

        // "INSERT INTO NEWORDERS VALUES(?, ?, ?, FALSE, TRUE)"
        NewOrders no = new NewOrders();
        no.setNoOId(orderNumber);
        no.setNoDId(d);
        no.setNoWId(w);
        no.setCompositeKey(no.getCompositeKey(no));
        no.setNoInitial(false);
        no.setNoLive(true);

        insertId = no.save();
        if (-1 == insertId) {
            throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert NewOrders[" + orderNumber + "-" + d + "-" + w + "] fail ");
        }

        int length = 0;
        if (null != items) {
            length = items.length;
        }

        Item item = null;
        float itemPrice = 0;
        String itemName = null;
        String itemData = null;
        Stock stock = null;
        int stockQuantity = -1;
        String stockDistInfo = null;
        String stockData = null;
        Class<?> stockClazz = Class.forName("com.jingpu.android.apersistance.activeandroid.model.Stock");
        String methodName = d < 10 ? "getSDist0" + d : "getSDist" + d;
        Method method = null;
        OrderLine ol = null;
        OrderLine qryOl = null;

        ActiveAndroid.beginTransaction();
        for (int i = 0; i < length; i++) {
            // "SELECT I_PRICE, I_NAME, I_DATA FROM ITEM WHERE I_ID = ?"
            item = new Select().from(Item.class)
                    .where("I_ID = ?", items[i]).executeSingle();

            itemPrice = 0;
            itemName = null;
            itemData = null;
            if (null != item) {
                itemPrice = item.getIPrice();
                itemName = item.getIName();
                itemData = item.getIData();
            }

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
            stock = new Select().from(Stock.class)
                    .where("S_I_ID = ? AND S_W_ID = ?", items[i], w).executeSingle();

            stockQuantity = -1;
            stockData = null;
            stockDistInfo = null;
            if (null != stock) {
                stockQuantity = stock.getSQuantity();
                stockData = stock.getSData();

                method = stockClazz.getMethod(methodName);
                stockDistInfo = (String)method.invoke(stock);
            }

            // "UPDATE STOCK SET S_ORDER_CNT = S_ORDER_CNT + 1, S_YTD = S_YTD + ?, S_REMOTE_CNT = S_REMOTE_CNT + ?, S_QUANTITY = ?
            // WHERE S_I_ID = ? AND S_W_ID = ?"

            if (null != quantities){
                if (stockQuantity - quantities[i] > 10) {
                    stockQuantity -= quantities[i];
                } else {
                    stockQuantity = stockQuantity - quantities[i] + 91;
                }
            }

            new Update(Stock.class)
                    .set("S_ORDER_CNT=S_ORDER_CNT+1, S_YTD=S_YTD+?, S_REMOTE_CNT=S_REMOTE_CNT+?, S_QUANTITY=?",
                            (null != quantities ? quantities[i] : 0), (null != supplyW ? (w == supplyW[i] ? 0 : 1) : 1), stockQuantity)
                    .where("S_I_ID = ? AND S_W_ID = ?", items[i], w).execute();

            if (stockDistInfo != null) {
                // "INSERT INTO ORDERLINE(OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER, OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO, OL_DELIVERY_D, OL_DELIVERY_D_INITIAL, OL_INITIAL) VALUES (?, ?, ?, ?, ?, ?, ?, CAST (? AS DECIMAL(5,2)) * CAST (? AS SMALLINT), ?, NULL, NULL, FALSE)"
                // CAST(expression AS data type)
                // SELECT CAST('2000-10-31' AS DATE)
                // SELECT CAST(1+2 AS CHAR)
                // SELECT CAST('Surname' AS CHAR(5))
                ol = new OrderLine();
                ol.setOlWId(w);
                ol.setOlDId(d);
                ol.setOlOId(orderNumber);
                ol.setOlNumber((short) (i + 1));
                ol.setCompositeKey(ol.getCompositeKey(ol));
                ol.setOlIId(items[i]);
                ol.setOlSupplyWId(supplyW[i]);
                ol.setOlQuantity(quantities[i]);
                ol.setOlAmount(itemPrice * quantities[i]);
                ol.setOlDistInfo(stockDistInfo);
                ol.setOlInitial(false);
                insertId = ol.save();
                if (-1 == insertId) {
                    throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert OrderLine[" + orderNumber + "-" + d + "-" + w + "-" + (i + 1) + "] fail ");
                }
            }
        }
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();

        // "SELECT SUM(OL_AMOUNT) FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
        float orderTotal = 0;

        List<OrderLine> olList = new Select().from(OrderLine.class)
                .where("OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?", w, d, orderNumber).execute();
        if (null != olList) {
            for (OrderLine orl: olList) {
                orderTotal += orl.getOlAmount();
            }
        }
    }

    public void scheduleDelivery(int terminalId, ActiveAndroidDisplay display, Object displayData, short w, short carrier)
            throws Exception {

        // "INSERT INTO DELIVERY_REQUEST(DR_W_ID, DR_CARRIER_ID, DR_STATE) VALUES(?, ?, 'Q')"
        DeliveryRequest dr = new DeliveryRequest();
        dr.setDrWId(w);
        dr.setDrCarrierId(carrier);
        dr.setDrState("Q");
        long insertId = dr.save();

        if (display != null) {
            display.displayScheduleDelivery(displayData, w, carrier);
        }
    }

    public void delivery(int terminalId) throws Exception {
        // "SELECT DR_ID, DR_W_ID, DR_CARRIER_ID FROM DELIVERY_REQUEST WHERE DR_STATE = 'Q' ORDER BY DR_QUEUED"
        DeliveryRequest dr = new Select().from(DeliveryRequest.class) // new String[]{DeliveryRequest.COL_DR_ID, DeliveryRequest.COL_DR_W_ID, DeliveryRequest.COL_DR_CARRIER_ID}
                .where("DR_STATE = 'Q'").orderBy("DR_QUEUED ASC").executeSingle();

        int request = -1;
        short w = -1;
        short carrier = -1;
        if (null != dr) {
            request = (int)dr.getDrId();
            w = dr.getDrWId();
            carrier = dr.getDrCarrierId();
        }

        // "UPDATE DELIVERY_REQUEST SET DR_STATE = ? WHERE DR_ID = ?"
        new Update(DeliveryRequest.class).set("DR_STATE = ?", "I").where("DR_ID = ?", request).execute();
        Timestamp currentTimeStamp = null;

        final List<DeliveryOrders> vDos = new ArrayList<DeliveryOrders>();
        DeliveryOrders dos = null;
        List<OrderLine> olList = null;
        float fAmount = 0;
        int iOCId = 0;
        Orders ord = null;
        String strSQL = null;
        NewOrders no = null;

        for (short d = 1; d <= 10; d = (short)(d + 1)) {
            // "INSERT INTO DELIVERY_ORDERS(DO_DR_ID, DO_D_ID, DO_O_ID) VALUES (?, ?, ?)"
            dos = new DeliveryOrders();
            dos.setDoDrId(request);
            dos.setDoDId(d);

            // "SELECT MIN(NO_O_ID) AS ORDER_TO_DELIVER FROM NEWORDERS WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_LIVE"
            no = new Select().from(NewOrders.class) // new String[]{NewOrders.COL_NO_O_ID}
                    .where("NO_W_ID = ? AND NO_D_ID = ? AND NO_LIVE=true", w, d).orderBy("NO_O_ID DESC").executeSingle();

            int order = -1;
            if (null != no) {
                order = no.getNoOId();
            } else {
                vDos.add(dos);
            }

            // "UPDATE NEWORDERS SET NO_LIVE = FALSE WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_O_ID = ?"
            new Update(NewOrders.class).set("NO_LIVE = FALSE")
                    .where("NO_W_ID = ? AND NO_D_ID = ? AND NO_O_ID = ?", w, d, order).execute();

            // "UPDATE ORDERS SET O_CARRIER_ID = ? WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
            new Update(Orders.class).set("O_CARRIER_ID = ?", carrier)
                    .where("O_W_ID = ? AND O_D_ID = ? AND O_ID = ?", w, d, order).execute();

            // "UPDATE ORDERLINE SET OL_DELIVERY_D = CURRENT TIMESTAMP WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
            currentTimeStamp = new Timestamp(System.currentTimeMillis());
            new Update(OrderLine.class).set("OL_DELIVERY_D=?", currentTimeStamp)
                    .where("OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?", w, d, order).execute();

            // "UPDATE CUSTOMER SET C_BALANCE = (SELECT SUM(OL_AMOUNT) FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?), C_DELIVERY_CNT = C_DELIVERY_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = (SELECT O_C_ID FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?)"
            olList = new Select().from(OrderLine.class)
                    .where("OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?", w, d, order).execute();
            fAmount = 0;
            if (null != olList) {
                for (OrderLine ol: olList) {
                    fAmount += ol.getOlAmount();
                }
            }

            iOCId = -1;
            ord = new Select().from(Orders.class)
                    .where("O_W_ID = ? AND O_D_ID = ? AND O_ID = ?", w, d, order).executeSingle();
            if (null != ord) {
                iOCId = ord.getOCId();
            }

             new Update(Customer.class).set("C_BALANCE = ?, C_DELIVERY_CNT = C_DELIVERY_CNT + 1", fAmount)
                     .where("C_W_ID = ? AND C_D_ID = ? AND C_ID = ?", w, d, iOCId).execute();

            if (-1 != order) {
                dos.setDoOId(order);
                vDos.add(dos);
            }
        }

        // "INSERT INTO DELIVERY_ORDERS(DO_DR_ID, DO_D_ID, DO_O_ID) VALUES (?, ?, ?)"
        ActiveAndroid.beginTransaction();
        for (DeliveryOrders deo: vDos){
            deo.save();
        }
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();

        // "UPDATE DELIVERY_REQUEST SET DR_STATE = 'C', DR_COMPLETED = CURRENT TIMESTAMP WHERE DR_ID = ?"
        currentTimeStamp = new Timestamp(System.currentTimeMillis());
        new Update(DeliveryRequest.class).set("DR_STATE = 'C', DR_COMPLETED=?", currentTimeStamp)
                .where("DR_ID = ?", request).execute();
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
