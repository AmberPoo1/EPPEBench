package com.jingpu.android.apersistance.sugarorm;

import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;

import com.jingpu.android.apersistance.sugarorm.model.Customer;
import com.jingpu.android.apersistance.sugarorm.model.DeliveryOrders;
import com.jingpu.android.apersistance.sugarorm.model.DeliveryRequest;
import com.jingpu.android.apersistance.sugarorm.model.District;
import com.jingpu.android.apersistance.sugarorm.model.History;
import com.jingpu.android.apersistance.sugarorm.model.Item;
import com.jingpu.android.apersistance.sugarorm.model.NewOrders;
import com.jingpu.android.apersistance.sugarorm.model.OrderLine;
import com.jingpu.android.apersistance.sugarorm.model.Orders;
import com.jingpu.android.apersistance.sugarorm.model.Stock;
import com.jingpu.android.apersistance.sugarorm.model.Warehouse;
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
 * Created by Jing Pu on 2016/1/18.
 */
public class SugarOrmStandard extends SugarOrmStatementHelper implements SugarOrmOperations{
    private final Customer customer = new Customer();
    private final List nameList = new ArrayList();
    private final Orders order = new Orders();
    private final District district = new District();
    private final Warehouse warehouse = new Warehouse();

    // Jing Pu test 5/14/2016
    //long transStartTime = 0;
    //long transEndTime = 0;

    public SugarOrmStandard(SugarOrmAgent soa) throws SQLiteException {
        super(soa);
    }

    public void stockLevel(int terminalId, SugarOrmDisplay display, Object displayData, short w, short d, int threshold) throws Exception {
        Long count = -1l;
        int iLowStock = 0;

        //transStartTime = new Date().getTime();

        // "SELECT D_NEXT_O_ID FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?"
        List<District> distList = District.find(District.class, "D_W_ID = " + w + " AND D_ID = " + d); // , new String[]{String.valueOf(w), String.valueOf(d)}
        int nextOrder = -1;
        if (null != distList && distList.size() > 0) {
            nextOrder = distList.get(0).getDNxtOId();
        }

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SugarOrmStandard.class.getName(), "stockLevel s1 duration[" + (nextOrder == -1 ?0:1) + "] = " + (transEndTime - transStartTime));


        //transStartTime = new Date().getTime();

        // "SELECT COUNT(DISTINCT(S_I_ID)) AS LOW_STOCK FROM ORDERLINE, STOCK WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID < ? AND OL_O_ID >= ? AND S_W_ID = ? AND S_I_ID = OL_I_ID AND S_QUANTITY < ?"

        List<OrderLine> olList = OrderLine.find(OrderLine.class, "OL_W_ID = " + w + " AND OL_D_ID = " + d
                + " AND OL_O_ID < " + nextOrder + " AND OL_O_ID >= " + (nextOrder - 20));
        List<Stock> skList = Stock.find(Stock.class, "S_W_ID = " + w + " AND S_QUANTITY < " + threshold);

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SugarOrmStandard.class.getName(), "stockLevel s2 duration = " + (transEndTime - transStartTime));


        if (null != olList && null != skList) {
            count = 0l;
            for (OrderLine ol : olList) {
                for (Stock sk : skList) {
                    if (ol.getOlIId() == sk.getSIId()) {
                        count++;
                    }
                }
            }
        }

        iLowStock = count.intValue();

        if (display != null) {
            display.displayStockLevel(displayData, w, d, threshold, iLowStock);
        }
    }

    @SuppressWarnings("unchecked")
    public void orderStatus(int terminalId, SugarOrmDisplay display, Object displayData, short w, short d, String customerLast) throws Exception {

        //transStartTime = new Date().getTime();

        // "SELECT C_ID, C_BALANCE, C_FIRST, C_MIDDLE FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_LAST = ? ORDER BY C_FIRST"
        List<Customer> customers = Customer.find(Customer.class, "C_W_ID = " + w + " AND C_D_ID = " + d + " AND C_LAST = '" + customerLast + "'",
                null,null, "C_FIRST ASC",null);

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SugarOrmStandard.class.getName(), "orderStatus 132 s1 prepare duration[" + customers.size() + "] = " + (transEndTime - transStartTime));


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

    public void orderStatus(int terminalId, SugarOrmDisplay display, Object displayData, short w, short d, int c) throws Exception {
        this.customer.clear();
        this.customer.setCWId(w);
        this.customer.setCDId(d);
        this.customer.setCId(c);

        //transStartTime = new Date().getTime();

        // "SELECT C_BALANCE, C_FIRST, C_MIDDLE, C_LAST FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        List<Customer> cusList = Customer.find(Customer.class, "C_W_ID = " + w + " AND C_D_ID = " + d +" AND C_ID = " + c);

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SugarOrmStandard.class.getName(), "orderStatus 174 s1 duration[" + cusList.size() + "] = " + (transEndTime - transStartTime));


        Customer cus = null;
        if (null != cusList && cusList.size() > 0) {
            cus = cusList.get(0);
            customer.setCBalance(cus.getCBalance());
            customer.setCFst(cus.getCFst());
            customer.setCMid(cus.getCMid());
            customer.setCLst(cus.getCLst());
        }

        getOrderStatusForCustomer(terminalId, display, displayData, false, this.customer);
    }

    private void getOrderStatusForCustomer(int terminalId, SugarOrmDisplay display, Object displayData, boolean byName, Customer customer) throws Exception {

        //transStartTime = new Date().getTime();

        // "SELECT MAX(O_ID) AS LAST_ORDER FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_C_ID = ?";
        List<Orders> oList = Orders.find(Orders.class, "O_W_ID = " + customer.getCWId()
                        + " AND O_D_ID = " + customer.getCDId() + " AND O_C_ID = " + customer.getCId(),
                        null, null, "O_ID DESC", null);

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SugarOrmStandard.class.getName(), "getOrderStatusForCustomer s1 duration[" + oList.size() + "] = " + (transEndTime - transStartTime));

        int iLastOrder = -1;
        if (null != oList && oList.size() > 0) {
            iLastOrder = oList.get(0).getOId();
        }

        this.order.clear();
        this.order.setOWId(customer.getCWId());
        this.order.setODId(customer.getCDId());
        this.order.setOId(iLastOrder);

        //transStartTime = new Date().getTime();

        // "SELECT O_ENTRY_D, O_CARRIER_ID, O_OL_CNT FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
        oList = Orders.find(Orders.class, "O_W_ID = " + customer.getCWId() + " AND O_D_ID = " + customer.getCDId()
                + " AND O_ID = " + this.order.getOId());

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SugarOrmStandard.class.getName(), "getOrderStatusForCustomer s2 duration[" + oList.size() + "] = " + (transEndTime - transStartTime));


        if (null != oList && oList.size() > 0) {
            Orders o = oList.get(0);
            this.order.setOEntryD(o.getOEntryD());
            this.order.setOCarrierId(o.getOCarrierId());
            this.order.setOOlCnt(o.getOOlCnt());
        }

        //transStartTime = new Date().getTime();

        // "SELECT OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
        List<OrderLine> olList = OrderLine.find(OrderLine.class, "OL_W_ID = " + this.order.getOWId()
                + " AND OL_D_ID = " + this.order.getODId() + " AND OL_O_ID = " + this.order.getOId());

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SugarOrmStandard.class.getName(), "getOrderStatusForCustomer s3 duration[" + olList.size() + "] = " + (transEndTime - transStartTime));


        OrderLine[] lineItems = new OrderLine[this.order.getOOlCnt()];
        OrderLine ol = null;
        int oli = 0;

        if (null != olList) {
            for (OrderLine olRec : olList) {
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

    public void payment(int terminalId, SugarOrmDisplay display, Object displayData, short w, short d, short cw, short cd, String customerLast, String amount)
            throws Exception {

        //transStartTime = new Date().getTime();
        // "SELECT C_ID FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_LAST = ? ORDER BY C_FIRST"
        List<Customer> customers = Customer.find(Customer.class, "C_W_ID = " + cw + " AND C_D_ID = " + cd + " AND C_LAST = '" + customerLast + "'",
                null, null, "C_FIRST ASC", null);

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SugarOrmStandard.class.getName(), "payment s1 duration[" + customers.size() + "] = " + (transEndTime - transStartTime));


        this.nameList.clear();

        if (null != customers) {
            for (Customer cus: customers) {
                this.nameList.add(cus.getCId());
            }
        }

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

    public void payment(int terminalId, SugarOrmDisplay display, Object displayData, short w, short d, short cw, short cd, int c, String amount)
            throws Exception {
        paymentById(terminalId, display, displayData, w, d, cw, cd, c, amount);

        if (display != null) {

        }
    }

    private void paymentById(int terminalId, SugarOrmDisplay display, Object displayData, short w, short d, short cw, short cd, int c, String s_amount)
            throws Exception {

        //transStartTime = new Date().getTime();

        // "UPDATE CUSTOMER SET C_BALANCE = C_BALANCE - ?, C_YTD_PAYMENT = C_YTD_PAYMENT + ?, C_PAYMENT_CNT = C_PAYMENT_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        Customer customer = new Customer();
        customer.setCWId(cw);
        customer.setCDId(cd);
        customer.setCId(c);
        customer.setCompositeKey(customer.getCompositeKey(customer));
        List<Customer> cusList = Customer.find(Customer.class, "C_W_ID = " + cw + " AND C_D_ID = " + cd + " AND C_ID = " + c);
        for (Customer cus : cusList) {
            cus.setCBalance(cus.getCBalance() - Float.parseFloat(s_amount));
            cus.setCYTDPayment(cus.getCYTDPayment() + Float.parseFloat(s_amount));
            cus.setCPaymentCnt(cus.getCPaymentCnt() + 1);
        }
        Customer.saveInTx(cusList);

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SugarOrmStandard.class.getName(), "paymentById u1 duration = " + (transEndTime - transStartTime));


        //transStartTime = new Date().getTime();

        // "SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE, C_STREET_1, C_STREET_2, C_CITY, C_STATE, C_ZIP,
        // C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_DATA FROM CUSTOMER
        // WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?
        cusList = Customer.find(Customer.class, "C_W_ID = " + cw + " AND C_D_ID = " + cd + " AND C_ID = " + c);

        //transEndTime = new Date().getTime();
        //TPCCLog.v(SugarOrmStandard.class.getName(), "paymentById s2 duration[" + cusList.size() + "] = " + (transEndTime - transStartTime));


        if (null != cusList && cusList.size() > 0) {
            Customer cus = cusList.get(0);
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
            cusList = Customer.find(Customer.class, "C_W_ID = " + cw + " AND C_D_ID = " + cd + " AND C_ID = " + c);
            for (Customer cus : cusList) {
                cus.setCData(Data.dataForBadCredit(customer.getCData(), w, d, cw, cd, c, new BigDecimal(s_amount)));
            }
            Customer.saveInTx(cusList);

            // "SELECT SUBSTR(C_DATA, 1, 200) AS C_DATA_200 FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
            cusList = Customer.find(Customer.class, "C_W_ID = " + cw + " AND C_D_ID = " + cd + " AND C_ID = " + c);

            String strData = null;
            String cData = null;
            if (null != cusList && cusList.size() > 0) {
                cData = cusList.get(0).getCData();
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

        List<District> distList = District.find(District.class, "D_W_ID = " + w + " AND D_ID = " + d);
        for (District dist : distList) {
            dist.setDYTD(dist.getDYTD() + Float.parseFloat(s_amount));
        }
        District.saveInTx(distList);

        // "SELECT D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ? "
        distList = District.find(District.class, "D_W_ID = " + w + " AND D_ID = " + d);

        if (null != distList && distList.size() > 0) {
            District dist = distList.get(0);
            this.district.setDName(dist.getDName());
            this.district.setDStreet1(dist.getDStreet1());
            this.district.setDStreet2(dist.getDStreet2());
            this.district.setDCity(dist.getDCity());
            this.district.setDState(dist.getDState());
            this.district.setDZip(dist.getDZip());
        }

        // "UPDATE WAREHOUSE SET W_YTD = W_YTD + ? WHERE W_ID = ?"
        List<Warehouse> whList = Warehouse.find(Warehouse.class, "W_ID = " + w); //new String[]{String.valueOf(w)}
        for (Warehouse wh : whList) {
            wh.setWYtd(wh.getWYtd() + Float.parseFloat(s_amount));
        }
        Warehouse.saveInTx(whList);

        // "SELECT W_NAME, W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP FROM WAREHOUSE WHERE W_ID = ?"
        whList = Warehouse.find(Warehouse.class, "W_ID = " + w); //new String[]{String.valueOf(w)}

        Warehouse wh = null;
        if (null != whList && whList.size() > 0) {
            wh = whList.get(0);
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
        long hId = h.save();
    }

    public void newOrder(int terminalId, SugarOrmDisplay display, Object displayData, short w, short d, int c, int[] items, short[] quantities, short[] supplyW)
            throws Exception {
        if (quantities == null || quantities.length == 0) {
            TPCCLog.e(SugarOrmStandard.class.getName(), "newOrder Tml[" + terminalId + "]: quantities is null");

        }

        if (items == null || items.length == 0) {
            TPCCLog.e(SugarOrmStandard.class.getName(), "newOrder Tml[" + terminalId + "]: items is null");
        }

        if (supplyW == null || supplyW.length == 0) {
            TPCCLog.e(SugarOrmStandard.class.getName(), "newOrder Tml[" + terminalId + "]: supplyW is null");
        }

        sortOrderItems(items, quantities, supplyW);

        // "SELECT W_TAX FROM WAREHOUSE WHERE W_ID = ?"
        List<Warehouse> whList = Warehouse.find(Warehouse.class, "W_ID = " + w);

        if (null != whList && whList.size() > 0) {
            float warehouseTax = whList.get(0).getWTax();
        }

        // "UPDATE DISTRICT SET D_NEXT_O_ID = D_NEXT_O_ID + 1 WHERE D_W_ID = ? AND D_ID = ?"
        List<District> distList = District.find(District.class, "D_W_ID = " + w + " AND D_ID = " + d);
        for (District dist : distList) {
            dist.setDNxtOId(dist.getDNxtOId() + 1);
        }
        District.saveInTx(distList);

        // "SELECT D_NEXT_O_ID - 1, D_TAX FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?"
        distList = District.find(District.class, "D_W_ID = " + w + " AND D_ID = " + d);

        int orderNumber = 0;
        float districtTax = -1;
        if (null != distList && distList.size() > 0) {
            District dist = distList.get(0);
            orderNumber = dist.getDNxtOId() - 1;
            districtTax = dist.getDTax();
        }

        // "SELECT C_LAST, C_DISCOUNT, C_CREDIT FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        List<Customer> cusList = Customer.find(Customer.class, "C_W_ID = " + w + " AND C_D_ID = " + d + " AND C_ID = " + c);

        short allLocal = 1;
        for (int i = 0; i < supplyW.length; i++) {
            if (supplyW[i] != w) {
                allLocal = 0;
                break;
            }
        }

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

        long insertId = o.save();
        if (-1 == insertId) {
            throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert Orders[" + orderNumber + "-" + d + "-" + w + "] fail ");
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
            throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert NewOrders[" + orderNumber + "-"  + d + "-" + w + "] fail ");
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
        Class<?> stockClazz = Class.forName("com.jingpu.android.apersistance.sugarorm.model.Stock");
        String methodName = null;
        Method method = null;
        OrderLine ol = null;
        List<Item> iList = null;
        List<Stock> skList = null;
        List<OrderLine> qryOlList = null;

        for (int i = 0; i < length; i++) {
            // "SELECT I_PRICE, I_NAME, I_DATA FROM ITEM WHERE I_ID = ?"
            iList = Item.find(Item.class, "I_ID = " + items[i]); // new String[]{String.valueOf(items[i])});

            itemPrice = 0;
            itemName = null;
            itemData = null;
            if (null != iList && iList.size() > 0) {
                item = iList.get(0);
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

            skList = Stock.find(Stock.class, "S_I_ID = " + items[i] + " AND S_W_ID = " + w);

            stockQuantity = -1;
            stockDistInfo = null;
            stockData = null;
            if (null != skList && skList.size() > 0) {
                stock = skList.get(0);
                stockQuantity = stock.getSQuantity();
                stockData = stock.getSData();

                methodName = d < 10 ? "getSDist0" + d : "getSDist" + d;
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

            skList = Stock.find(Stock.class, "S_I_ID = " + items[i] + " AND S_W_ID = " + w);
            for (Stock s : skList) {
                s.setSOrderCnt(s.getSOrderCnt() + 1);
                s.setSYTD(s.getSYTD() + (null != quantities ? quantities[i] : 0));
                s.setSRemoteCnt(s.getSRemoteCnt() + (null != supplyW ? (w == supplyW[i] ? 0 : 1) : 1));
                s.setSQuantity(stockQuantity);
            }
            Stock.saveInTx(skList);

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

        // "SELECT SUM(OL_AMOUNT) FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
        float orderTotal = 0;
        List<OrderLine> olList = OrderLine.find(OrderLine.class, "OL_W_ID = " + w + " AND OL_D_ID = " + d
                + " AND OL_O_ID = " + orderNumber);
        for (OrderLine orl : olList) {
            orderTotal += orl.getOlAmount();
        }
    }

    public void scheduleDelivery(int terminalId, SugarOrmDisplay display, Object displayData, short w, short carrier)
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
        List<DeliveryRequest> drList = DeliveryRequest.find(DeliveryRequest.class, "DR_STATE = 'Q'", null, null, "DR_QUEUED ASC", null);
        DeliveryRequest dlvRqst = null;
        int request = -1;
        short w = -1;
        short carrier = -1;
        if (null != drList && drList.size() > 0) {
            dlvRqst = drList.get(0);
            request = dlvRqst.getId().intValue();
            w = dlvRqst.getDrWId();
            carrier = dlvRqst.getDrCarrierId();
        }

        // "UPDATE DELIVERY_REQUEST SET DR_STATE = ? WHERE DR_ID = ?"
        drList = DeliveryRequest.find(DeliveryRequest.class, "DR_ID = " + request);// new String[]{String.valueOf(request)});
        for (DeliveryRequest dr : drList) {
            dr.setDrState("I");
        }
        DeliveryRequest.saveInTx(drList);

        Timestamp currentTimeStamp = null;

        final List<DeliveryOrders> vDos = new ArrayList<DeliveryOrders>();
        DeliveryOrders dos = null;
        List<NewOrders> noList = null;
        List<Orders> oList = null;
        List<OrderLine> olList = null;
        List<Customer> cusList = null;
        String strSQL = null;
        NewOrders no = null;
        long sum = 0;
        int ocid = 0;

        for (short d = 1; d <= 10; d = (short)(d + 1)) {
            // "INSERT INTO DELIVERY_ORDERS(DO_DR_ID, DO_D_ID, DO_O_ID) VALUES (?, ?, ?)"
            dos = new DeliveryOrders();
            dos.setDoDrId(request);
            dos.setDoDId(d);

            // "SELECT MIN(NO_O_ID) AS ORDER_TO_DELIVER FROM NEWORDERS WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_LIVE"
            noList = NewOrders.find(NewOrders.class, "NO_W_ID = " + w + " AND NO_D_ID = " + d + " AND NO_LIVE=true",
                    null, null , "NO_O_ID DESC", null);

            int order = -1;
            if (null != noList) {
                order = noList.get(0).getNoOId();
            } else {
                vDos.add(dos);
            }

            // "UPDATE NEWORDERS SET NO_LIVE = FALSE WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_O_ID = ?"
            noList = NewOrders.find(NewOrders.class, "NO_W_ID = " + w + " AND NO_D_ID = " + d + " AND NO_O_ID = " + order);
            for (NewOrders n : noList) {
                n.setNoLive(false);
            }
            NewOrders.saveInTx(noList);

            // "UPDATE ORDERS SET O_CARRIER_ID = ? WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
            oList = Orders.find(Orders.class, "O_W_ID = " + w + " AND O_D_ID = " + d + " AND O_ID = " + order);
            for (Orders o : oList) {
                o.setOCarrierId(carrier);
            }
            Orders.saveInTx(oList);

            // "UPDATE ORDERLINE SET OL_DELIVERY_D = CURRENT TIMESTAMP WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
            currentTimeStamp = new Timestamp(System.currentTimeMillis());
            olList = OrderLine.find(OrderLine.class, "OL_W_ID = " + w + " AND OL_D_ID = " + d + " AND OL_O_ID = " + order);
            for (OrderLine ol : olList) {
                ol.setOlDeliveryD(currentTimeStamp);
            }
            OrderLine.saveInTx(olList);

            // "UPDATE CUSTOMER SET C_BALANCE = (SELECT SUM(OL_AMOUNT) FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?), C_DELIVERY_CNT = C_DELIVERY_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = (SELECT O_C_ID FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?)"
            sum = 0;
            olList = OrderLine.find(OrderLine.class, "WHERE OL_W_ID = " + w + " AND OL_D_ID = " + d + " AND OL_O_ID = " + order);
            for (OrderLine ol : olList) {
                sum += ol.getOlAmount();
            }

            oList = Orders.find(Orders.class, "WHERE O_W_ID = " + w + " AND O_D_ID = " + d + " AND O_ID = " + order);
            ocid = -1;
            if (null != oList) {
                ocid = oList.get(0).getOCId();
            }
            cusList = Customer.find(Customer.class, "C_W_ID = " + w + " AND C_D_ID = " + d + " AND C_ID = " + ocid);
            for (Customer cus : cusList) {
                cus.setCBalance(sum);
                cus.setCDeliveryCnt(cus.getCDeliveryCnt() + 1);
            }
            Customer.saveInTx(cusList);

            if (-1 != order) {
                dos.setDoOId(order);
                vDos.add(dos);
            }
        }

        // "INSERT INTO DELIVERY_ORDERS(DO_DR_ID, DO_D_ID, DO_O_ID) VALUES (?, ?, ?)"
        DeliveryOrders.saveInTx(vDos);

        // "UPDATE DELIVERY_REQUEST SET DR_STATE = 'C', DR_COMPLETED = CURRENT TIMESTAMP WHERE DR_ID = ?"
        currentTimeStamp = new Timestamp(System.currentTimeMillis());
        drList = DeliveryRequest.find(DeliveryRequest.class, "DR_ID = " + request);
        for (DeliveryRequest dr : drList) {
            dr.setDrState("C");
            dr.setDrCompleted(currentTimeStamp);
        }
        DeliveryRequest.saveInTx(drList);
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
