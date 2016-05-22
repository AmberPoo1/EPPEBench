package com.jingpu.android.apersistance.ormlite;

import android.database.sqlite.SQLiteConstraintException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
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
import com.jingpu.android.apersistance.util.OrderStatusException;
import com.jingpu.android.apersistance.util.PaymentException;
import com.jingpu.android.apersistance.util.TPCCLog;

import org.dacapo.derby.Data;
import org.dacapo.derby.OrderItem4Sort;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Jing Pu on 2015/10/27.
 */
public class OrmliteStandard extends OrmliteStatementHelper
        implements OrmliteOperations {

    private final Customer customer = new Customer();
    private final List nameList = new ArrayList();
    private final Orders order = new Orders();
    private final District district = new District();
    private final Warehouse warehouse = new Warehouse();

    // Jing Pu test 5/14/2016
    //long transStartTime = 0;
    //long transEndTime = 0;

    public OrmliteStandard(OrmliteAgent oa) throws SQLException {
        super(oa);
    }

    public void stockLevel(int terminalId, OrmliteDisplay display, Object displayData, short w, short d, int threshold) throws Exception {
        Long count = -1l;
        int iLowStock = 0;

        OrmliteDBHelper dbHelper = oa.getDbHelper();

        //transStartTime = new Date().getTime();

        // "SELECT D_NEXT_O_ID FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?"
        QueryBuilder<District, String> dQb = dbHelper.getDistrictDao().queryBuilder();
        Where<District, String> dWhere = dQb.where();
        dWhere.and(dWhere.eq(District.COL_D_W_ID, w), dWhere.eq(District.COL_D_ID, d));
        District dist = dQb.queryForFirst();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(OrmliteStandard.class.getName(), "stockLevel s1 duration[" + (dist == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));

        int nextOrder = -1;
        if (null != dist) {
            nextOrder = dist.getDNxtOId();
        }

        //transStartTime = new Date().getTime();
        // "SELECT COUNT(DISTINCT(S_I_ID)) AS LOW_STOCK FROM ORDERLINE, STOCK WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID < ? AND OL_O_ID >= ? AND S_W_ID = ? AND S_I_ID = OL_I_ID AND S_QUANTITY < ?"

        // Ormlite join must specific foreign fields.
        QueryBuilder<Stock, String> sQb = dbHelper.getStockDao().queryBuilder();
        Where<Stock, String> sWhere = sQb.where();
        sWhere.and(sWhere.eq(Stock.COL_S_W_ID, w), sWhere.lt(Stock.COL_S_QUANTITY, threshold));
        List<Stock> sList = sQb.query();
        QueryBuilder<OrderLine, String> olQb = dbHelper.getOrderLineDao().queryBuilder();
        Where<OrderLine, String> olWhere = olQb.where();
        olWhere.and(olWhere.eq(OrderLine.COL_OL_W_ID, w), olWhere.eq(OrderLine.COL_OL_D_ID, d), olWhere.lt(OrderLine.COL_OL_O_ID, nextOrder), olWhere.ge(OrderLine.COL_OL_O_ID, nextOrder - 20));
        List<OrderLine> olList = olQb.query();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(OrmliteStandard.class.getName(), "stockLevel s2[" + (iLowStock < 0 ? 0 : 1) + "] duration = " + (transEndTime - transStartTime));

        Set<Integer> siidSet = new HashSet<Integer>();
        if (null != sList && null != olList) {
            for (Stock s : sList) {
                for (OrderLine ol : olList) {
                    if (s.getSIId() == ol.getOlIId()) {
                        siidSet.add(s.getSIId());
                    }
                }
            }
        }

        iLowStock = siidSet.size();

        if (display != null) {
            display.displayStockLevel(displayData, w, d, threshold, iLowStock);
        }
    }

    @SuppressWarnings("unchecked")
    public void orderStatus(int terminalId, OrmliteDisplay display, Object displayData, short w, short d, String customerLast) throws Exception {
        OrmliteDBHelper dbHelper = oa.getDbHelper();

        //transStartTime = new Date().getTime();

        // "SELECT C_ID, C_BALANCE, C_FIRST, C_MIDDLE FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_LAST = ? ORDER BY C_FIRST"
        QueryBuilder<Customer, String> cQb = dbHelper.getCustomerDao().queryBuilder();
        Where<Customer, String> cWhere = cQb.where();
        cWhere.and(cWhere.eq(Customer.COL_C_W_ID, w), cWhere.eq(Customer.COL_C_D_ID, d), cWhere.eq(Customer.COL_C_LAST, customerLast));
        List<Customer> cusList = cQb.orderBy(Customer.COL_C_FIRST, true).query();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(OrmliteStandard.class.getName(), "orderStatus 132 s1 prepare duration[" + cusList.size() + "] = " + (transEndTime - transStartTime));


        this.nameList.clear();
        if (null != cusList && cusList.size() > 0) {
            this.nameList.addAll(cusList);
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

    public void orderStatus(int terminalId, OrmliteDisplay display, Object displayData, short w, short d, int c) throws Exception {
        this.customer.clear();
        this.customer.setCWId(w);
        this.customer.setCDId(d);
        this.customer.setCId(c);

        OrmliteDBHelper dbHelper = oa.getDbHelper();

        //transStartTime = new Date().getTime();

        // "SELECT C_BALANCE, C_FIRST, C_MIDDLE, C_LAST FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        QueryBuilder<Customer, String> cQb = dbHelper.getCustomerDao().queryBuilder();
        Where<Customer, String> cWhere = cQb.where();
        cWhere.and(cWhere.eq(Customer.COL_C_W_ID, w), cWhere.eq(Customer.COL_C_D_ID, d), cWhere.eq(Customer.COL_C_ID, c));
        Customer cus = cQb.queryForFirst();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(OrmliteStandard.class.getName(), "orderStatus 174 s1 duration[" + (cus == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));


        if (null != cus) {
            customer.setCBalance(cus.getCBalance());
            customer.setCFst(cus.getCFst());
            customer.setCMid(cus.getCMid());
            customer.setCLst(cus.getCLst());
        }

        getOrderStatusForCustomer(terminalId, display, displayData, false, this.customer);
    }

    private void getOrderStatusForCustomer(int terminalId, OrmliteDisplay display, Object displayData, boolean byName, Customer customer) throws Exception {
        OrmliteDBHelper dbHelper = oa.getDbHelper();
        //transStartTime = new Date().getTime();
        // "SELECT MAX(O_ID) AS LAST_ORDER FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_C_ID = ?";
        Dao<Orders, String> oDao = dbHelper.getOrdersDao();
        QueryBuilder<Orders, String> oQb = oDao.queryBuilder();
        Where<Orders, String> ow = oQb.where();
        ow.and(ow.eq(Orders.COL_O_W_ID, customer.getCWId()), ow.eq(Orders.COL_O_D_ID, customer.getCDId()), ow.eq(Orders.COL_O_C_ID, customer.getCId()));
        Orders o = oQb.orderBy(Orders.COL_O_ID, false).queryForFirst();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(OrmliteStandard.class.getName(), "getOrderStatusForCustomer s1 duration[" + (o == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));

        this.order.clear();
        this.order.setOWId(customer.getCWId());
        this.order.setODId(customer.getCDId());

        int iLastOrder = -1;
        if (null != o) {
            iLastOrder = o.getOId();
        }

        this.order.setOId(iLastOrder);

        //transStartTime = new Date().getTime();
        // "SELECT O_ENTRY_D, O_CARRIER_ID, O_OL_CNT FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
        oQb = oDao.queryBuilder();
        ow = oQb.where();
        ow.and(ow.eq(Orders.COL_O_W_ID, customer.getCWId()), ow.eq(Orders.COL_O_D_ID, customer.getCDId()), ow.eq(Orders.COL_O_ID, this.order.getOId()));
        o = oQb.queryForFirst();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(OrmliteStandard.class.getName(), "getOrderStatusForCustomer s2 duration[" + (o == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));

        if (null != o) {
            this.order.setOEntryD(o.getOEntryD());
            this.order.setOCarrierId(o.getOCarrierId());
            this.order.setOOlCnt(o.getOOlCnt());
        }

        //transStartTime = new Date().getTime();
        // "SELECT OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
        QueryBuilder<OrderLine, String> olQb = dbHelper.getOrderLineDao().queryBuilder();
        Where<OrderLine, String> olWhere = olQb.where();
        olWhere.and(olWhere.eq(OrderLine.COL_OL_W_ID, this.order.getOWId()), olWhere.eq(OrderLine.COL_OL_D_ID, this.order.getODId()), olWhere.eq(OrderLine.COL_OL_O_ID, this.order.getOId()));
        List<OrderLine> olList = olQb.query();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(OrmliteStandard.class.getName(), "getOrderStatusForCustomer s3 duration[" + olList.size() + "] = " + (transEndTime - transStartTime));

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

    public void payment(int terminalId, OrmliteDisplay display, Object displayData, short w, short d, short cw, short cd, String customerLast, String amount)
            throws Exception {
        OrmliteDBHelper dbHelper = oa.getDbHelper();

        //transStartTime = new Date().getTime();
        // "SELECT C_ID FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_LAST = ? ORDER BY C_FIRST"
        QueryBuilder<Customer, String> cQb = dbHelper.getCustomerDao().queryBuilder();
        Where<Customer, String> cWhere = cQb.where();
        cWhere.and(cWhere.eq(Customer.COL_C_W_ID, cw), cWhere.eq(Customer.COL_C_D_ID, cd), cWhere.eq(Customer.COL_C_LAST, customerLast));
        cQb.orderBy(Customer.COL_C_FIRST, true);
        List<Customer> cusList = cQb.query();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(OrmliteStandard.class.getName(), "payment s1 duration[" + cusList.size() + "] = " + (transEndTime - transStartTime));

        this.nameList.clear();

        if (null != cusList) {
            for (Customer cus: cusList) {
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

    public void payment(int terminalId, OrmliteDisplay display, Object displayData, short w, short d, short cw, short cd, int c, String amount)
            throws Exception {
        paymentById(terminalId, display, displayData, w, d, cw, cd, c, amount);

        if (display != null) {

        }
    }

    private void paymentById(int terminalId, OrmliteDisplay display, Object displayData, short w, short d, short cw, short cd, int c, String s_amount)
            throws Exception {
        OrmliteDBHelper dbHelper = oa.getDbHelper();

        // "UPDATE CUSTOMER SET C_BALANCE = C_BALANCE - ?, C_YTD_PAYMENT = C_YTD_PAYMENT + ?, C_PAYMENT_CNT = C_PAYMENT_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        Customer customer = new Customer();
        customer.setCWId(cw);
        customer.setCDId(cd);
        customer.setCId(c);
        customer.setCompositeKey(customer.getCompositeKey(customer));

        //transStartTime = new Date().getTime();

        Dao<Customer, String> cusDao = dbHelper.getCustomerDao();
        UpdateBuilder<Customer, String> cUb = cusDao.updateBuilder();
        Where<Customer, String> cWhere = cUb.where();
        cWhere.and(cWhere.eq(Customer.COL_C_W_ID, cw), cWhere.eq(Customer.COL_C_D_ID, cd), cWhere.eq(Customer.COL_C_ID, c));
        cUb.updateColumnExpression(Customer.COL_C_BALANCE, Customer.COL_C_BALANCE + "-" + s_amount);
        cUb.updateColumnExpression(Customer.COL_C_YTD_PAYMENT, Customer.COL_C_YTD_PAYMENT + "+" + s_amount);
        cUb.updateColumnExpression(Customer.COL_C_PAYMENT_CNT, Customer.COL_C_PAYMENT_CNT + "+ 1");
        cUb.update();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(OrmliteStandard.class.getName(), "paymentById u1 duration = " + (transEndTime - transStartTime));

        //transStartTime = new Date().getTime();

        // "SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE, C_STREET_1, C_STREET_2, C_CITY, C_STATE, C_ZIP, C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_DATA FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?
        QueryBuilder<Customer, String> cQb = cusDao.queryBuilder();
        cWhere = cQb.where();
        cWhere.and(cWhere.eq(Customer.COL_C_W_ID, cw), cWhere.eq(Customer.COL_C_D_ID, cd), cWhere.eq(Customer.COL_C_ID, c));
        Customer cus = cQb.queryForFirst();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(OrmliteStandard.class.getName(), "paymentById s2 duration[" + (cus == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));

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

        //endTransaction(db);

        if ("BC".equals(customer.getCCredit())) {
            // "UPDATE CUSTOMER SET C_DATA = ? WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
            cUb = cusDao.updateBuilder();
            cWhere = cUb.where();
            cWhere.and(cWhere.eq(Customer.COL_C_W_ID, cw), cWhere.eq(Customer.COL_C_D_ID, cd), cWhere.eq(Customer.COL_C_ID, c));
            cUb.updateColumnValue(Customer.COL_C_DATA,
                    Data.dataForBadCredit(customer.getCData(), w, d, cw, cd, c, new BigDecimal(s_amount)));
            cUb.update();

            // "SELECT SUBSTR(C_DATA, 1, 200) AS C_DATA_200 FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
            cQb = cusDao.queryBuilder();
            cWhere = cQb.where();
            cWhere.and(cWhere.eq(Customer.COL_C_W_ID, cw), cWhere.eq(Customer.COL_C_D_ID, cd), cWhere.eq(Customer.COL_C_ID, c));
            cus = cQb.queryForFirst();

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

        Dao<District, String> dDao = dbHelper.getDistrictDao();
        UpdateBuilder<District, String> dub = dDao.updateBuilder();
        Where<District, String> dWhere = dub.where();
        dWhere.and(dWhere.eq(District.COL_D_W_ID, w), dWhere.eq(District.COL_D_ID, d));
        dub.updateColumnExpression(District.COL_D_YTD, District.COL_D_YTD + "+" + s_amount);
        dub.update();

        // "SELECT D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ? "
        QueryBuilder<District, String> dQb = dDao.queryBuilder();
        Where<District, String> dw = dQb.where();
        dw.and(dw.eq(District.COL_D_W_ID, w), dw.eq(District.COL_D_ID, d));
        District dist = dQb.queryForFirst();

        if (null != dist) {
            this.district.setDName(dist.getDName());
            this.district.setDStreet1(dist.getDStreet1());
            this.district.setDStreet2(dist.getDStreet2());
            this.district.setDCity(dist.getDCity());
            this.district.setDState(dist.getDState());
            this.district.setDZip(dist.getDZip());
        }

        // "UPDATE WAREHOUSE SET W_YTD = W_YTD + ? WHERE W_ID = ?"
        Dao<Warehouse, Long> wDao = dbHelper.getWarehouseDao();
        UpdateBuilder<Warehouse, Long> wUb = wDao.updateBuilder();
        wUb.where().eq(Warehouse.COL_W_ID, w);
        wUb.updateColumnExpression(Warehouse.COL_W_YTD, Warehouse.COL_W_YTD + "+" + s_amount);
        wUb.update();

        // "SELECT W_NAME, W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP FROM WAREHOUSE WHERE W_ID = ?"
        QueryBuilder<Warehouse, Long> wQb = dbHelper.getWarehouseDao().queryBuilder();
        wQb.where().eq(Warehouse.COL_W_ID, w);
        Warehouse wh = wQb.queryForFirst();

        if (null != wh) {
            this.warehouse.setWName(wh.getWName());
            this.warehouse.setWStreet1(wh.getWStreet1());
            this.warehouse.setWStreet2(wh.getWStreet2());
            this.warehouse.setWCity(wh.getWCity());
            this.warehouse.setWState(wh.getWState());
            this.warehouse.setWZip(wh.getWZip());
        }

        // "INSERT INTO HISTORY(H_C_ID, H_C_D_ID, H_C_W_ID, H_D_ID, H_W_ID, H_AMOUNT, H_DATA, H_DATE, H_INITIAL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, FALSe)"
        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        History history = new History();
        history.setHId(null);
        history.setHCId(c);
        history.setHCDId(cd);
        history.setHCWId(cw);
        history.setHDId(d);
        history.setHWId(w);
        history.setHAmount(Float.parseFloat(s_amount));

        StringBuffer hData = new StringBuffer(24);
        hData.append(this.warehouse.getWName());
        hData.append("    ");
        hData.append(this.district.getDName());
        history.setHData(hData.toString());
        history.setHDate(currentTimeStamp);
        history.setHInitial(false);
        Dao<History, Long> hDao = dbHelper.getHistroyDao();
        hDao.create(history);
    }

    public void newOrder(int terminalId, OrmliteDisplay display, Object displayData, short w, short d, int c, int[] items, short[] quantities, short[] supplyW)
            throws Exception {
        if (quantities == null || quantities.length == 0) {
            TPCCLog.e(OrmliteStandard.class.getName(), "newOrder Tml[" + terminalId + "]: quantities is null");
        }

        if (items == null || items.length == 0) {
            TPCCLog.e(OrmliteStandard.class.getName(), "newOrder Tml[" + terminalId + "]: items is null");
        }

        if (supplyW == null || supplyW.length == 0) {
            TPCCLog.e(OrmliteStandard.class.getName(), "newOrder Tml[" + terminalId + "]: supplyW is null");
        }

        sortOrderItems(items, quantities, supplyW);
        OrmliteDBHelper dbHelper = oa.getDbHelper();

        // "SELECT W_TAX FROM WAREHOUSE WHERE W_ID = ?"
        QueryBuilder<Warehouse, Long> wQb = dbHelper.getWarehouseDao().queryBuilder();
        Warehouse wh = wQb.where().eq(Warehouse.COL_W_ID, w).queryForFirst();

        if (null != wh) {
            float warehouseTax = wh.getWTax();
        }

        // "UPDATE DISTRICT SET D_NEXT_O_ID = D_NEXT_O_ID + 1 WHERE D_W_ID = ? AND D_ID = ?"
        Dao<District, String> dDao = dbHelper.getDistrictDao();

        UpdateBuilder<District, String> dUb = dDao.updateBuilder();
        Where<District, String> dw = dUb.where();
        dw.and(dw.eq(District.COL_D_W_ID, w), dw.eq(District.COL_D_ID, d));
        dUb.updateColumnExpression(District.COL_D_NEXT_O_ID, District.COL_D_NEXT_O_ID + " + 1");
        dUb.update();

        // "SELECT D_NEXT_O_ID - 1, D_TAX FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?"
        QueryBuilder<District, String> dQb = dDao.queryBuilder();
        dw = dQb.where();
        dw.and(dw.eq(District.COL_D_W_ID, w), dw.eq(District.COL_D_ID, d));
        District dist = dQb.queryForFirst();

        int orderNumber = 0;
        float districtTax = -1;
        if (null != dist) {
            orderNumber = dist.getDNxtOId() - 1;
            districtTax = dist.getDTax();
        }

        // "SELECT C_LAST, C_DISCOUNT, C_CREDIT FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        QueryBuilder<Customer, String> cQb = dbHelper.getCustomerDao().queryBuilder();
        // Table without id field cannot use "selectColumns" method
        Where<Customer, String> cw = cQb.where();
        cw.and(cw.eq(Customer.COL_C_W_ID, w), cw.eq(Customer.COL_C_D_ID, d), cw.eq(Customer.COL_C_ID, c));
        Customer cus = cQb.queryForFirst();

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
        // Insert Row
        try {
            Dao<Orders, String> oDao = dbHelper.getOrdersDao();
            oDao.createOrUpdate(o);
        } catch (Exception e) {
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
        try {
            Dao<NewOrders, String> noDao = dbHelper.getNewOrdersDao();
            noDao.createOrUpdate(no);
        } catch (Exception e) {
            throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert NewOrders[" + orderNumber + "-" + d + "-" + w + "] fail ");
        }

        int length = 0;
        if (null != items) {
            length = items.length;
        }

        // "SELECT I_PRICE, I_NAME, I_DATA FROM ITEM WHERE I_ID = ?"
        Dao<Item, Long> iDao = dbHelper.getItemDao();
        QueryBuilder<Item, Long> iQb = null;
        Where<Item, Long> iWhere = null;

        Item item = null;
        float itemPrice = 0;
        String itemName = null;
        String itemData = null;

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
        Dao<Stock, String> sDao = dbHelper.getStockDao();
        QueryBuilder<Stock, String> sQb = null;
        Where<Stock, String> sWhere = null;

        String methodName = d < 10 ? "getSDist0" + d : "getSDist" + d;

        Stock stock = null;
        int stockQuantity = -1;
        String stockDistInfo = null;
        String stockData = null;
        Class<?> clazz = null;
        Method method = null;

        Dao<OrderLine, String> olDao = dbHelper.getOrderLineDao();
        UpdateBuilder<Stock, String> sUb = null;

        OrderLine ol = null;

        for (int i = 0; i < length; i++) {
            // "SELECT I_PRICE, I_NAME, I_DATA FROM ITEM WHERE I_ID = ?"

            iQb = iDao.queryBuilder();
            iWhere = iQb.where();
            iWhere.eq(Item.COL_I_ID, items[i]);
            item = iQb.queryForFirst();

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

            sQb = sDao.queryBuilder();
            sWhere = sQb.where();
            sWhere.and(sWhere.eq(Stock.COL_S_I_ID, items[i]), sWhere.eq(Stock.COL_S_W_ID, w));
            stock = sQb.queryForFirst();

            stockQuantity = -1;
            stockDistInfo = null;
            stockData = null;
            if (null != stock) {
                stockQuantity = stock.getSQuantity();

                clazz = Class.forName("com.jingpu.android.apersistance.ormlite.model.Stock");
                method = clazz.getMethod(methodName);
                stockDistInfo = (String)method.invoke(stock);

                stockData = stock.getSData();
            }

            // "UPDATE STOCK SET S_ORDER_CNT = S_ORDER_CNT + 1, S_YTD = S_YTD + ?, S_REMOTE_CNT = S_REMOTE_CNT + ?, S_QUANTITY = ? WHERE S_I_ID = ? AND S_W_ID = ?"
            if (null != quantities){
                if (stockQuantity - quantities[i] > 10) {
                    stockQuantity -= quantities[i];
                } else {
                    stockQuantity = stockQuantity - quantities[i] + 91;
                }
            }

            sUb = sDao.updateBuilder();
            sWhere = sUb.where();
            sWhere.and(sWhere.eq(Stock.COL_S_I_ID, items[i]), sWhere.eq(Stock.COL_S_W_ID, w));
            sUb.updateColumnExpression(Stock.COL_S_ORDER_CNT, Stock.COL_S_ORDER_CNT + " + 1");
            sUb.updateColumnExpression(Stock.COL_S_YTD, Stock.COL_S_YTD + " + " + (null != quantities ? quantities[i] : 0));
            sUb.updateColumnExpression(Stock.COL_S_REMOTE_CNT, Stock.COL_S_REMOTE_CNT + " + " + (null != supplyW ? (w == supplyW[i] ? 0 : 1) : 1));
            sUb.updateColumnValue(Stock.COL_S_QUANTITY, stockQuantity);
            sUb.update();


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

                // Insert Row
                try {
                    olDao.createOrUpdate(ol);
                } catch (Exception e) {
                    throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert OrderLine[" + orderNumber + "-" + d + "-" + w + "-" + (i + 1) + "] fail ");
                }
            }
        }

        // "SELECT (OL_AMOUNT) FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
        float orderTotal = 0;

        QueryBuilder<OrderLine, String> olQb = olDao.queryBuilder();
        Where<OrderLine, String> olWhere = olQb.where();
        olWhere.and(olWhere.eq(OrderLine.COL_OL_W_ID, w), olWhere.eq(OrderLine.COL_OL_D_ID, d), olWhere.eq(OrderLine.COL_OL_O_ID, orderNumber));
        List<OrderLine> olList = olQb.query();
        if (null != olList) {
            for (OrderLine orl : olList) {
                orderTotal += orl.getOlAmount();
            }
        }
    }

    public void scheduleDelivery(int terminalId, OrmliteDisplay display, Object displayData, short w, short carrier)
            throws Exception {
        OrmliteDBHelper dbHelper = oa.getDbHelper();

        // "INSERT INTO DELIVERY_REQUEST(DR_W_ID, DR_CARRIER_ID, DR_STATE) VALUES(?, ?, 'Q')"
        DeliveryRequest dr = new DeliveryRequest();
        dr.setDrWId(w);
        dr.setDrCarrierId(carrier);
        dr.setDrState("Q");

        // Insert Row
        long insertId = dbHelper.getDeliveryRequestDao().create(dr);

        if (display != null) {
            display.displayScheduleDelivery(displayData, w, carrier);
        }
    }

    public void delivery(int terminalId) throws Exception {

        OrmliteDBHelper dbHelper = oa.getDbHelper();

        Dao<DeliveryRequest, Long> drDao = dbHelper.getDeliveryRequestDao();

        // "SELECT DR_ID, DR_W_ID, DR_CARRIER_ID FROM DELIVERY_REQUEST WHERE DR_STATE = 'Q' ORDER BY DR_QUEUED"
        final QueryBuilder<DeliveryRequest, Long> drQb = drDao.queryBuilder();

        drQb.where().eq(DeliveryRequest.COL_DR_STATE, "Q");
        DeliveryRequest dr = drQb.orderBy(DeliveryRequest.COL_DR_QUEUED, true).queryForFirst();

        int request = -1;
        short w = -1;
        short carrier = -1;
        if (null != dr) {
            request = (int)dr.getDrId();
            w = dr.getDrWId();
            carrier = dr.getDrCarrierId();
        }

        // "UPDATE DELIVERY_REQUEST SET DR_STATE = ? WHERE DR_ID = ?"
        UpdateBuilder<DeliveryRequest, Long> drUb = drDao.updateBuilder();
        drUb.where().eq(DeliveryRequest.COL_DR_ID, request);
        drUb.updateColumnValue(DeliveryRequest.COL_DR_STATE, "I");
        drUb.update();

        final List<DeliveryOrders> vDos = new ArrayList<DeliveryOrders>();
        final Dao<DeliveryOrders, Long> doDao = dbHelper.getDeliveryOrdersDao();
        DeliveryOrders dos = null;

        Dao<NewOrders, String> noDao = dbHelper.getNewOrdersDao();
        QueryBuilder<NewOrders, String> noQb = null;
        Where<NewOrders, String> noWhere = null;
        NewOrders no = null;

        // "UPDATE NEWORDERS SET NO_LIVE = FALSE WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_O_ID = ?"
        UpdateBuilder<NewOrders, String> noUb = null;

        // "UPDATE ORDERS SET O_CARRIER_ID = ? WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
        Dao<Orders, String> oDao = dbHelper.getOrdersDao();
        UpdateBuilder<Orders, String> oUb = null;
        QueryBuilder<Orders, String> oQb = null;
        Where<Orders, String> oWhere = null;
        Orders o = null;

        // "UPDATE ORDERLINE SET OL_DELIVERY_D = CURRENT TIMESTAMP WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
        final Dao<OrderLine, String> olDao = dbHelper.getOrderLineDao();
        UpdateBuilder<OrderLine, String> olUb = null;
        QueryBuilder<OrderLine, String> olQb = null;
        Where<OrderLine, String> olWhere = null;
        Timestamp currentTimeStamp = null;

        Dao<Customer, String> cDao = dbHelper.getCustomerDao();
        UpdateBuilder<Customer, String> cUb = null;
        Where<Customer, String> cWhere = null;
        String strSQL = null;
        float fAmount = 0;
        int iOCId = 0;
        List<OrderLine> olList = null;

        for (short d = 1; d <= 10; d = (short)(d + 1)) {
            // "INSERT INTO DELIVERY_ORDERS(DO_DR_ID, DO_D_ID, DO_O_ID) VALUES (?, ?, ?)"
            dos = new DeliveryOrders();
            dos.setDoId(null);
            dos.setDoDrId(request);
            dos.setDoDId(d);

            // "SELECT MIN(NO_O_ID) AS ORDER_TO_DELIVER FROM NEWORDERS WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_LIVE"
            noQb = noDao.queryBuilder();
            noWhere = noQb.where();
            noWhere.and(noWhere.eq(NewOrders.COL_NO_W_ID, w), noWhere.eq(NewOrders.COL_NO_D_ID, d), noWhere.eq(NewOrders.COL_NO_LIVE, true));
            no = noQb.orderBy(NewOrders.COL_NO_O_ID, true).queryForFirst();

            int order = -1;
            if (null != no) {
                order = no.getNoOId();
            } else {
                vDos.add(dos);
            }

            // "UPDATE NEWORDERS SET NO_LIVE = FALSE WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_O_ID = ?"

            noUb = noDao.updateBuilder();
            noWhere = noUb.where();
            noWhere.and(noWhere.eq(NewOrders.COL_NO_W_ID, w), noWhere.eq(NewOrders.COL_NO_D_ID, d), noWhere.eq(NewOrders.COL_NO_O_ID, order));
            noUb.updateColumnValue(NewOrders.COL_NO_LIVE, false);
            noUb.update();

            // "UPDATE ORDERS SET O_CARRIER_ID = ? WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
            oUb = oDao.updateBuilder();
            oWhere = oUb.where();
            oWhere.and(oWhere.eq(Orders.COL_O_W_ID, w), oWhere.eq(Orders.COL_O_D_ID, d), oWhere.eq(Orders.COL_O_ID, order));
            oUb.updateColumnValue(Orders.COL_O_CARRIER_ID, carrier);
            oUb.update();

            // "UPDATE ORDERLINE SET OL_DELIVERY_D = CURRENT TIMESTAMP WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
            olUb = olDao.updateBuilder();
            olWhere = olUb.where();
            olWhere.and(olWhere.eq(OrderLine.COL_OL_W_ID, w), olWhere.eq(OrderLine.COL_OL_D_ID, d), olWhere.eq(OrderLine.COL_OL_O_ID, order));
            currentTimeStamp = new Timestamp(System.currentTimeMillis());
            olUb.updateColumnValue(OrderLine.COL_OL_DELIVERY_D, currentTimeStamp);
            olUb.update();

            // "UPDATE CUSTOMER SET C_BALANCE = (SELECT (OL_AMOUNT) FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?), C_DELIVERY_CNT = C_DELIVERY_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = (SELECT O_C_ID FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?)"
            olQb = olDao.queryBuilder();
            olWhere = olQb.where();
            olWhere.and(olWhere.eq(OrderLine.COL_OL_W_ID, w), olWhere.eq(OrderLine.COL_OL_D_ID, d), olWhere.eq(OrderLine.COL_OL_O_ID, order));
            olList = olQb.query();
            fAmount = 0;
            if (null != olList) {
                for (OrderLine ol: olList) {
                    fAmount += ol.getOlAmount();
                }
            }

            oQb = oDao.queryBuilder();
            oWhere = oQb.where();
            oWhere.and(oWhere.eq(Orders.COL_O_W_ID, w), oWhere.eq(Orders.COL_O_D_ID, d), oWhere.eq(Orders.COL_O_ID, order));
            o = oQb.queryForFirst();
            iOCId = -1;
            if (null != o) {
                iOCId = o.getOCId();
            }

            cUb = cDao.updateBuilder();
            cWhere = cUb.where();
            cWhere.and(cWhere.eq(Customer.COL_C_W_ID, w), cWhere.eq(Customer.COL_C_D_ID, d), cWhere.eq(Customer.COL_C_ID, iOCId));
            cUb.updateColumnValue(Customer.COL_C_BALANCE, fAmount);
            cUb.updateColumnExpression(Customer.COL_C_DELIVERY_CNT, Customer.COL_C_DELIVERY_CNT + " + 1");
            cUb.update();

            if (-1 != order) {
                dos.setDoOId(order);
                vDos.add(dos);
            }
        }

        // "INSERT INTO DELIVERY_ORDERS(DO_DR_ID, DO_D_ID, DO_O_ID) VALUES (?, ?, ?)"
        TransactionManager.callInTransaction(dbHelper.getConnectionSource(), new Callable<Void>() {
            public Void call() throws Exception {
                long insertId;
                for (DeliveryOrders dlo : vDos) {
                    // Insert Row
                    doDao.create(dlo);
                }
                return null;
            }
        });

        // "UPDATE DELIVERY_REQUEST SET DR_STATE = 'C', DR_COMPLETED = CURRENT TIMESTAMP WHERE DR_ID = ?"
        drUb = drDao.updateBuilder();
        drUb.where().eq(DeliveryRequest.COL_DR_ID, request);
        drUb.updateColumnValue(DeliveryRequest.COL_DR_STATE, "C");
        currentTimeStamp = new Timestamp(System.currentTimeMillis());
        drUb.updateColumnValue(DeliveryRequest.COL_DR_COMPLETED, currentTimeStamp);
        drUb.update();
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
