package com.jingpu.android.apersistance.greendao;

import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.jingpu.android.apersistance.greendao.model.Customer;
import com.jingpu.android.apersistance.greendao.model.CustomerDao;
import com.jingpu.android.apersistance.greendao.model.DaoSession;
import com.jingpu.android.apersistance.greendao.model.DeliveryOrders;
import com.jingpu.android.apersistance.greendao.model.DeliveryOrdersDao;
import com.jingpu.android.apersistance.greendao.model.DeliveryRequest;
import com.jingpu.android.apersistance.greendao.model.DeliveryRequestDao;
import com.jingpu.android.apersistance.greendao.model.District;
import com.jingpu.android.apersistance.greendao.model.DistrictDao;
import com.jingpu.android.apersistance.greendao.model.History;
import com.jingpu.android.apersistance.greendao.model.HistoryDao;
import com.jingpu.android.apersistance.greendao.model.Item;
import com.jingpu.android.apersistance.greendao.model.ItemDao;
import com.jingpu.android.apersistance.greendao.model.NewOrders;
import com.jingpu.android.apersistance.greendao.model.NewOrdersDao;
import com.jingpu.android.apersistance.greendao.model.OrderLine;
import com.jingpu.android.apersistance.greendao.model.OrderLineDao;
import com.jingpu.android.apersistance.greendao.model.Orders;
import com.jingpu.android.apersistance.greendao.model.OrdersDao;
import com.jingpu.android.apersistance.greendao.model.Stock;
import com.jingpu.android.apersistance.greendao.model.StockDao;
import com.jingpu.android.apersistance.greendao.model.Warehouse;
import com.jingpu.android.apersistance.greendao.model.WarehouseDao;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Jing Pu on 2015/12/2.
 */
public class GreenDaoStandard extends GreenDaoStatementHelper
        implements GreenDaoOperations {

    private final Customer customer = new Customer();
    private final List nameList = new ArrayList();
    private final Orders order = new Orders();
    private final District district = new District();
    private final Warehouse warehouse = new Warehouse();

    // Jing Pu test 5/14/2016
    //long transStartTime = 0;
    //long transEndTime = 0;

    public GreenDaoStandard(GreenDaoAgent ga) throws SQLiteException {
        super(ga);
    }

    public void stockLevel(int terminalId, GreenDaoDisplay display, Object displayData, short w, short d, int threshold) throws Exception {
        Long count = -1l;
        int iLowStock = 0;

        SQLiteDatabase db = ga.getDatabase();
        DaoSession ds = ga.getDaoSession();

        //transStartTime = new Date().getTime();

        // "SELECT D_NEXT_O_ID FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?"
        DistrictDao distDao = ds.getDistrictDao();
        QueryBuilder qb = distDao.queryBuilder();

        qb.where(qb.and(DistrictDao.Properties.DWId.eq(w),
                DistrictDao.Properties.DId.eq(d)));

        //transEndTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "stockLevel s1 prepare duration = " + (transEndTime - transStartTime));

        List<District> dists = qb.limit(1).list();

        //transStartTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "stockLevel s1 duration[" + dists.size() + "] = " + (transStartTime - transEndTime));

        int nextOrder = -1;
        if (null != dists && dists.size() > 0) {
            nextOrder = dists.get(0).getDNextOId();
        }

        // "SELECT COUNT(DISTINCT(S_I_ID)) AS LOW_STOCK FROM ORDERLINE, STOCK WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID < ? AND OL_O_ID >= ? AND S_W_ID = ? AND S_I_ID = OL_I_ID AND S_QUANTITY < ?"

        //transStartTime = new Date().getTime();

        StockDao sDao = ds.getStockDao();
        qb = sDao.queryBuilder();
        qb.where(qb.and(StockDao.Properties.SWId.eq(w), StockDao.Properties.SQuantity.lt(threshold)));
        Join olJoin = qb.join(StockDao.Properties.SIId, OrderLine.class, OrderLineDao.Properties.OlIId);
        olJoin.where(olJoin.and(OrderLineDao.Properties.OlWId.eq(w), OrderLineDao.Properties.OlDId.eq(d),
                OrderLineDao.Properties.OlOId.lt(nextOrder), OrderLineDao.Properties.OlOId.ge(nextOrder - 20)));

        //transEndTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "stockLevel s2 prepare duration = " + (transEndTime - transStartTime));

        List<Stock> stocks = qb.list();

        //transStartTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "stockLevel s2 duration = " + (transStartTime - transEndTime));

        Set<Integer> siidSet = new HashSet<Integer>();
        if (null != stocks) {
            for (Stock sk : stocks) {
                siidSet.add(sk.getSIId());
            }
        }
        iLowStock = siidSet.size();

        if (display != null) {
                display.displayStockLevel(displayData, w, d, threshold, iLowStock);
        }
    }

    @SuppressWarnings("unchecked")
    public void orderStatus(int terminalId, GreenDaoDisplay display, Object displayData, short w, short d, String customerLast) throws Exception {

        SQLiteDatabase db = ga.getDatabase();
        DaoSession ds = ga.getDaoSession();

        //transStartTime = new Date().getTime();

        // "SELECT C_ID, C_BALANCE, C_FIRST, C_MIDDLE FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_LAST = ? ORDER BY C_FIRST"
        CustomerDao cusDao = ds.getCustomerDao();
        QueryBuilder qb = cusDao.queryBuilder();

        qb.where(qb.and(CustomerDao.Properties.CWId.eq(w), CustomerDao.Properties.CDId.eq(d),
                CustomerDao.Properties.CLast.eq(customerLast))).orderAsc(CustomerDao.Properties.CFirst);

        //transEndTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "orderStatus 132 s1 prepare duration = " + (transEndTime - transStartTime));

        List<Customer> customers = qb.list();

        //transStartTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "orderStatus 132 s1 duration[" + customers.size() + "] = " + (transStartTime - transEndTime));

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

    public void orderStatus(int terminalId, GreenDaoDisplay display, Object displayData, short w, short d, int c) throws Exception {
        this.customer.clear();
        this.customer.setCWId(w);
        this.customer.setCDId(d);
        this.customer.setCId(c);

        DaoSession ds = ga.getDaoSession();

        //transStartTime = new Date().getTime();

        // "SELECT C_BALANCE, C_FIRST, C_MIDDLE, C_LAST FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        CustomerDao cusDao = ds.getCustomerDao();
        QueryBuilder qb = cusDao.queryBuilder();

        qb.where(qb.and(CustomerDao.Properties.CWId.eq(w), CustomerDao.Properties.CDId.eq(d),
                CustomerDao.Properties.CId.eq(c)));

        //transEndTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "orderStatus 174 s1 prepare duration = " + (transEndTime - transStartTime));

        List<Customer> customers = qb.limit(1).list();

        //transStartTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "orderStatus 174 s1 duration[" + customers.size() + "] = " + (transStartTime - transEndTime));

        Customer cus = null;
        if (null != customers && customers.size() > 0) {
            cus = customers.get(0);
            customer.setCBalance(cus.getCBalance());
            customer.setCFirst(cus.getCFirst());
            customer.setCMiddle(cus.getCMiddle());
            customer.setCLast(cus.getCLast());
        }

        getOrderStatusForCustomer(terminalId, display, displayData, false, this.customer);
    }

    private void getOrderStatusForCustomer(int terminalId, GreenDaoDisplay display, Object displayData, boolean byName, Customer customer) throws Exception {
        DaoSession ds = ga.getDaoSession();

        //transStartTime = new Date().getTime();

        // "SELECT MAX(O_ID) AS LAST_ORDER FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_C_ID = ?";
        OrdersDao ordersDao = ds.getOrdersDao();
        QueryBuilder oQb = ordersDao.queryBuilder();

        oQb.where(oQb.and(OrdersDao.Properties.OWId.eq(customer.getCWId()),
                OrdersDao.Properties.ODId.eq(customer.getCDId()), OrdersDao.Properties.OCId.eq(customer.getCId())))
                .orderDesc(OrdersDao.Properties.OId);

        //transEndTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "getOrderStatusForCustomer s1 prepare duration = " + (transEndTime - transStartTime));

        List<Orders> orders = oQb.limit(1).list();

        //transStartTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "getOrderStatusForCustomer s1 duration[" + orders.size() + "] = " + (transStartTime - transEndTime));

        int iLastOrder = -1;
        if (null != orders && orders.size() > 0) {
            iLastOrder = orders.get(0).getOId();
        }

        this.order.clear();
        this.order.setOWId(customer.getCWId());
        this.order.setODId(customer.getCDId());
        this.order.setOId(iLastOrder);

        //transStartTime = new Date().getTime();

        // "SELECT O_ENTRY_D, O_CARRIER_ID, O_OL_CNT FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
        oQb = ordersDao.queryBuilder();

        oQb.where(oQb.and(OrdersDao.Properties.OWId.eq(customer.getCWId()),
                OrdersDao.Properties.ODId.eq(customer.getCDId()), OrdersDao.Properties.OId.eq(this.order.getOId())));

        //transEndTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "getOrderStatusForCustomer s2 prepare duration = " + (transEndTime - transStartTime));

        orders = oQb.limit(1).list();

        //transStartTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "getOrderStatusForCustomer s2 duration[" + orders.size() + "] = " + (transStartTime - transEndTime));

        if (null != orders && orders.size() > 0) {
            Orders o = orders.get(0);
            this.order.setOEntryD(o.getOEntryD());
            this.order.setOCarrierId(o.getOCarrierId());
            this.order.setOOlCnt(o.getOOlCnt());
        }

        //transStartTime = new Date().getTime();

        // "SELECT OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
        OrderLineDao olDao = ds.getOrderLineDao();
        QueryBuilder olQb = olDao.queryBuilder();

        // Jing Pu modified 5/18/2016
        olQb.where(olQb.and(OrderLineDao.Properties.OlWId.eq(this.order.getOWId()),
                OrderLineDao.Properties.OlDId.eq(this.order.getODId()), OrderLineDao.Properties.OlOId.eq(this.order.getOId())));

        //transEndTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "getOrderStatusForCustomer s3 prepare duration = " + (transEndTime - transStartTime));

        List<OrderLine> ols = olQb.list();

        //transStartTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "getOrderStatusForCustomer s3 duration[" + ols.size() + "] = " + (transStartTime - transEndTime));

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

    public void payment(int terminalId, GreenDaoDisplay display, Object displayData, short w, short d, short cw, short cd, String customerLast, String amount)
            throws Exception {

        DaoSession ds = ga.getDaoSession();

        //transStartTime = new Date().getTime();

        // "SELECT C_ID FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_LAST = ? ORDER BY C_FIRST"
        CustomerDao cusDao = ds.getCustomerDao();
        QueryBuilder qb = cusDao.queryBuilder();

        qb.where(qb.and(CustomerDao.Properties.CWId.eq(cw), CustomerDao.Properties.CDId.eq(cd),
                CustomerDao.Properties.CLast.eq(customerLast))).orderAsc(CustomerDao.Properties.CFirst);

        //transEndTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "payment s1 prepare duration = " + (transEndTime - transStartTime));

        List<Customer> customers = qb.list();

        //transStartTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "payment s1 duration[" + customers.size() + "] = " + (transStartTime - transEndTime));

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

    public void payment(int terminalId, GreenDaoDisplay display, Object displayData, short w, short d, short cw, short cd, int c, String amount)
            throws Exception {
        paymentById(terminalId, display, displayData, w, d, cw, cd, c, amount);

        if (display != null) {

        }
    }

    private void paymentById(int terminalId, GreenDaoDisplay display, Object displayData, short w, short d, short cw, short cd, int c, String s_amount)
            throws Exception {

        // "UPDATE CUSTOMER SET C_BALANCE = C_BALANCE - ?, C_YTD_PAYMENT = C_YTD_PAYMENT + ?, C_PAYMENT_CNT = C_PAYMENT_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        Customer customer = new Customer();
        customer.setCWId(cw);
        customer.setCDId(cd);
        customer.setCId(c);
        customer.setCCompo(customer.getCCompo(customer));

        DaoSession ds = ga.getDaoSession();

        //transStartTime = new Date().getTime();

        CustomerDao cusDao = ds.getCustomerDao();
        QueryBuilder qb = cusDao.queryBuilder();
        qb.where(qb.and(CustomerDao.Properties.CWId.eq(cw),
                CustomerDao.Properties.CDId.eq(cd), CustomerDao.Properties.CId.eq(c)));

        //transEndTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "paymentById u1 prepare duration = " + (transEndTime - transStartTime));

        List<Customer> customers = qb.list();

        if (null != customers) {
            for (Customer cus : customers) {
                cus.setCBalance(cus.getCBalance() - Float.parseFloat(s_amount));
                cus.setCYtdPayment(cus.getCYtdPayment() + Float.parseFloat(s_amount));
                cus.setCPaymentCnt(cus.getCPaymentCnt() + 1);
            }
            cusDao.updateInTx(customers);
        }

        //transStartTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "paymentById u1 duration = " + (transStartTime - transEndTime));

        // "SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE, C_STREET_1, C_STREET_2, C_CITY, C_STATE, C_ZIP,
        // C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_DATA FROM CUSTOMER
        // WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?

        //transStartTime = new Date().getTime();

        cusDao = ds.getCustomerDao();
        qb = cusDao.queryBuilder();

        qb.where(qb.and(CustomerDao.Properties.CWId.eq(cw), CustomerDao.Properties.CDId.eq(cd),
                CustomerDao.Properties.CId.eq(c)));

        //transEndTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "paymentById s2 prepare duration = " + (transEndTime - transStartTime));

        customers = qb.limit(1).list();

        //transStartTime = new Date().getTime();
        //TPCCLog.v(GreenDaoStandard.class.getName(), "paymentById s2 duration[" + customers.size() + "] = " + (transStartTime - transEndTime));

        if (null != customers && customers.size() > 0) {
            Customer cus = customers.get(0);
            customer.setCFirst(cus.getCFirst());
            customer.setCMiddle(cus.getCMiddle());
            customer.setCLast(cus.getCLast());
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

            cusDao = ds.getCustomerDao();
            qb = cusDao.queryBuilder();
            qb.where(qb.and(CustomerDao.Properties.CWId.eq(cw),
                    CustomerDao.Properties.CDId.eq(cd), CustomerDao.Properties.CId.eq(c)));
            customers = qb.list();
            if (null != customers) {
                for (Customer cus : customers) {
                    cus.setCData(Data.dataForBadCredit(customer.getCData(), w, d, cw, cd, c, new BigDecimal(s_amount)));
                }
                cusDao.updateInTx(customers);
            }

            // "SELECT SUBSTR(C_DATA, 1, 200) AS C_DATA_200 FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
            cusDao = ds.getCustomerDao();
            qb = cusDao.queryBuilder();
            qb.where(qb.and(CustomerDao.Properties.CWId.eq(cw),
                    CustomerDao.Properties.CDId.eq(cd), CustomerDao.Properties.CId.eq(c)));
            customers = qb.limit(1).list();

            String strData = null;
            String cData = null;
            if (null != customers && customers.size() > 0) {
                cData = customers.get(0).getCData();
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

        DistrictDao distDao = ds.getDistrictDao();
        qb = distDao.queryBuilder();
        qb.where(qb.and(DistrictDao.Properties.DWId.eq(w), DistrictDao.Properties.DId.eq(d)));

        List<District> dists = qb.list();
        if (null != dists) {
            for (District dist : dists) {
                dist.setDYtd(dist.getDYtd() + Float.parseFloat(s_amount));
            }
            distDao.updateInTx(dists);
        }

        // "SELECT D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ? "

        distDao = ds.getDistrictDao();
        qb = distDao.queryBuilder();

        qb.where(qb.and(DistrictDao.Properties.DWId.eq(w), DistrictDao.Properties.DId.eq(d)));
        dists = qb.limit(1).list();

        if (null != dists && dists.size() > 0) {
            District dist = dists.get(0);
            this.district.setDName(dist.getDName());
            this.district.setDStreet1(dist.getDStreet1());
            this.district.setDStreet2(dist.getDStreet2());
            this.district.setDCity(dist.getDCity());
            this.district.setDState(dist.getDState());
            this.district.setDZip(dist.getDZip());
        }

        // "UPDATE WAREHOUSE SET W_YTD = W_YTD + ? WHERE W_ID = ?"

        WarehouseDao whDao = ds.getWarehouseDao();
        qb = whDao.queryBuilder();
        qb.where(WarehouseDao.Properties.Id.eq(w));

        List<Warehouse> whs = qb.list();
        if (null != whs) {
            for (Warehouse wh : whs) {
                wh.setWYtd(wh.getWYtd() + Float.parseFloat(s_amount));
            }
            whDao.updateInTx(whs);
        }

        // "SELECT W_NAME, W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP FROM WAREHOUSE WHERE W_ID = ?"
        whDao = ds.getWarehouseDao();
        qb = whDao.queryBuilder();
        qb.where(WarehouseDao.Properties.Id.eq(w));
        whs = qb.limit(1).list();

        Warehouse wh = null;
        if (null != whs && whs.size() > 0) {
            wh = whs.get(0);
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

        HistoryDao hDao = ds.getHistoryDao();
        hDao.insert(h);
    }

    public void newOrder(int terminalId, GreenDaoDisplay display, Object displayData, short w, short d, int c, int[] items, short[] quantities, short[] supplyW)
            throws Exception {
        if (quantities == null || quantities.length == 0) {
            TPCCLog.e(GreenDaoStandard.class.getName(), "newOrder Tml[" + terminalId + "]: quantities is null");

        }

        if (items == null || items.length == 0) {
            TPCCLog.e(GreenDaoStandard.class.getName(), "newOrder Tml[" + terminalId + "]: items is null");
        }

        if (supplyW == null || supplyW.length == 0) {
            TPCCLog.e(GreenDaoStandard.class.getName(), "newOrder Tml[" + terminalId + "]: supplyW is null");
        }

        sortOrderItems(items, quantities, supplyW);

        // "SELECT W_TAX FROM WAREHOUSE WHERE W_ID = ?"

        DaoSession ds = ga.getDaoSession();
        WarehouseDao whDao = ds.getWarehouseDao();
        QueryBuilder qb = whDao.queryBuilder();

        qb.where(WarehouseDao.Properties.Id.eq(w));
        List<Warehouse> whs = qb.limit(1).list();
        if (whs != null && whs.size() > 0) {
            float warehouseTax = whs.get(0).getWTax();
        }

        // "UPDATE DISTRICT SET D_NEXT_O_ID = D_NEXT_O_ID + 1 WHERE D_W_ID = ? AND D_ID = ?"

        DistrictDao distDao = ds.getDistrictDao();
        qb = distDao.queryBuilder();
        qb.where(qb.and(DistrictDao.Properties.DWId.eq(w), DistrictDao.Properties.DId.eq(d)));

        List<District> dists = qb.list();
        if (null != dists) {
            for (District dist : dists) {
                dist.setDNextOId(dist.getDNextOId() + 1);
            }
            distDao.updateInTx(dists);
        }

        // "SELECT D_NEXT_O_ID - 1, D_TAX FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?"

        distDao = ds.getDistrictDao();
        qb = distDao.queryBuilder();

        qb.where(qb.and(DistrictDao.Properties.DWId.eq(w), DistrictDao.Properties.DId.eq(d)));
        dists = qb.limit(1).list();

        int orderNumber = 0;
        float districtTax = -1;
        if (null != dists && dists.size() > 0) {
            District dist = dists.get(0);
            orderNumber = dist.getDNextOId() - 1;
            districtTax = dist.getDTax();
        }

        // "SELECT C_LAST, C_DISCOUNT, C_CREDIT FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
        CustomerDao cusDao = ds.getCustomerDao();
        qb = cusDao.queryBuilder();

        qb.where(qb.and(CustomerDao.Properties.CWId.eq(w), CustomerDao.Properties.CDId.eq(d),
                CustomerDao.Properties.CId.eq(c)));
        List<Customer> customers = qb.limit(1).list();

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
        o.setOCompo(o.getOCompo(o));
        o.setOCId(c);

        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        o.setOEntryD(currentTimeStamp.toString());

        if (null != items) {
            o.setOOlCnt((short)items.length);
        } else {
            o.setOOlCnt((short) 0);
        }
        o.setOAllLocal(allLocal);
        o.setOInitial(false);

        OrdersDao oDao = ds.getOrdersDao();
        long insertId = oDao.insert(o);
        if (-1 == insertId) {
            throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert Orders[" + orderNumber + "-"  + d + "-" + w + "] fail ");
        }

        NewOrdersDao noDao = ds.getNewOrdersDao();

        // "INSERT INTO NEWORDERS VALUES(?, ?, ?, FALSE, TRUE)"
        NewOrders no = new NewOrders();
        no.setNoOId(orderNumber);
        no.setNoDId(d);
        no.setNoWId(w);
        no.setNoCompo(no.getNoCompo(no));
        no.setNoInitial(false);
        no.setNoLive(true);

        insertId = noDao.insert(no);
        if (-1 == insertId) {
            throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert NewOrders[" + orderNumber + "-" + d + "-" + w + "] fail ");
        }

        int length = 0;
        if (null != items) {
            length = items.length;
        }

        List<Item> is = null;
        List<Stock> stocks = null;
        List<OrderLine> ols = null;
        Item it = null;
        float itemPrice = 0;
        String itemName = null;
        String itemData = null;
        Stock stock = null;
        int stockQuantity = -1;
        String stockDistInfo = null;
        String stockData = null;
        Class<?> stockClazz = Class.forName("com.jingpu.android.apersistance.greendao.model.Stock");
        Method method = null;
        String methodName = d < 10 ? "getSDist0" + d : "getSDist" + d;
        OrderLine ol = null;

        StockDao sDao = null;
        ItemDao iDao = null;
        OrderLineDao olDao = null;

        for (int i = 0; i < length; i++) {
            // "SELECT I_PRICE, I_NAME, I_DATA FROM ITEM WHERE I_ID = ?"

            iDao = ds.getItemDao();
            qb = iDao.queryBuilder();

            qb.where(ItemDao.Properties.Id.eq(items[i]));
            is = qb.limit(1).list();

            itemPrice = 0;
            itemName = null;
            itemData = null;
            if (null != is && is.size() > 0) {
                it = is.get(0);
                itemPrice = it.getIPrice();
                itemName = it.getIName();
                itemData = it.getIData();
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
            sDao = ds.getStockDao();
            qb = sDao.queryBuilder();

            qb.where(qb.and(StockDao.Properties.SIId.eq(items[i]), StockDao.Properties.SWId.eq(w)));
            stocks = qb.limit(1).list();

            stockQuantity = -1;
            stockData = null;
            stockDistInfo = null;
            if (null != stocks && stocks.size() > 0) {
                stock = stocks.get(0);
                stockQuantity = stock.getSQuantity();
                stockData = stock.getSData();

                method = stockClazz.getMethod(methodName);
                stockDistInfo = (String)method.invoke(stock);
            }

            // "UPDATE STOCK SET S_ORDER_CNT = S_ORDER_CNT + 1, S_YTD = S_YTD + ?, S_REMOTE_CNT = S_REMOTE_CNT + ?, S_QUANTITY = ?
            // WHERE S_I_ID = ? AND S_W_ID = ?"

            sDao = ds.getStockDao();
            qb = sDao.queryBuilder();
            qb.where(qb.and(StockDao.Properties.SIId.eq(items[i]), StockDao.Properties.SWId.eq(w)));
            stocks = qb.list();

            if (null != quantities){
                if (stockQuantity - quantities[i] > 10) {
                    stockQuantity -= quantities[i];
                } else {
                    stockQuantity = stockQuantity - quantities[i] + 91;
                }
            }

            if (null != stocks) {
                for (Stock s : stocks) {
                    s.setSOrderCnt(s.getSOrderCnt() + 1);
                    s.setSYtd(s.getSYtd() + (null != quantities ? quantities[i] : 0));
                    s.setSRemoteCnt(s.getSRemoteCnt() + (null != supplyW ? (w == supplyW[i] ? 0 : 1) : 1));
                    s.setSQuantity(stockQuantity);
                }
                sDao.updateInTx(stocks);
            }

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
                ol.setOlCompo(ol.getOlCompo(ol));
                ol.setOlIId(items[i]);
                ol.setOlSupplyWId(supplyW[i]);
                ol.setOlQuantity(quantities[i]);
                ol.setOlAmount(itemPrice * quantities[i]);
                ol.setOlDistInfo(stockDistInfo);
                ol.setOlInitial(false);

                olDao = ds.getOrderLineDao();
                insertId = olDao.insert(ol);
                if (-1 == insertId) {
                    throw new SQLiteConstraintException("newOrder: Tml[" + terminalId + "] - insert OrderLine[" + orderNumber + "-"  + d + "-" + w + "-" + (i + 1) + "] fail ");
                }

            }
        }

        // "SELECT SUM(OL_AMOUNT) FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"

        float orderTotal = 0;
        olDao = ds.getOrderLineDao();
        qb = olDao.queryBuilder();
        qb.where(qb.and(OrderLineDao.Properties.OlWId.eq(w), OrderLineDao.Properties.OlDId.eq(d), OrderLineDao.Properties.OlOId.eq(orderNumber)));
        List<OrderLine> olList = qb.list();
        if (null != olList) {
            for (OrderLine orl: olList) {
                orderTotal += orl.getOlAmount();
            }
        }
    }

    public void scheduleDelivery(int terminalId, GreenDaoDisplay display, Object displayData, short w, short carrier)
            throws Exception {

        // "INSERT INTO DELIVERY_REQUEST(DR_W_ID, DR_CARRIER_ID, DR_STATE) VALUES(?, ?, 'Q')"

        DaoSession ds = ga.getDaoSession();
        DeliveryRequestDao drDao = ds.getDeliveryRequestDao();

        DeliveryRequest dr = new DeliveryRequest();
        dr.setDrWId(w);
        dr.setDrCarrierId(carrier);
        dr.setDrState("Q");
        long insertId = drDao.insert(dr);

        if (display != null) {
            display.displayScheduleDelivery(displayData, w, carrier);
        }
    }

    public void delivery(int terminalId) throws Exception {

        // "SELECT DR_ID, DR_W_ID, DR_CARRIER_ID FROM DELIVERY_REQUEST WHERE DR_STATE = 'Q' ORDER BY DR_QUEUED"

        DaoSession ds = ga.getDaoSession();
        DeliveryRequestDao drDao = ds.getDeliveryRequestDao();
        QueryBuilder qb = drDao.queryBuilder();
        qb.where(DeliveryRequestDao.Properties.DrState.eq("Q")).orderAsc(DeliveryRequestDao.Properties.DrQueued);

        List<DeliveryRequest> drs = qb.limit(1).list();

        int request = -1;
        short w = -1;
        short carrier = -1;
        DeliveryRequest dlvRqst = null;
        if (null != drs && drs.size() > 0) {
            dlvRqst = drs.get(0);
            request = dlvRqst.getId().intValue();
            w = dlvRqst.getDrWId();
            carrier = dlvRqst.getDrCarrierId();
        }

        // "UPDATE DELIVERY_REQUEST SET DR_STATE = ? WHERE DR_ID = ?"

        drDao = ds.getDeliveryRequestDao();
        qb = drDao.queryBuilder();
        qb.where(DeliveryRequestDao.Properties.Id.eq(request));

        drs = qb.list();
        if (null != drs) {
            for (DeliveryRequest dr : drs) {
                dr.setDrState("I");
            }
            drDao.updateInTx(drs);
        }

        Timestamp currentTimeStamp = null;

        final List<DeliveryOrders> vDos = new ArrayList<DeliveryOrders>();
        DeliveryOrders dos = null;
        NewOrdersDao noDao = null;
        OrdersDao oDao = null;
        OrderLineDao olDao = null;
        CustomerDao cusDao = null;
        List<NewOrders> noList = null;
        List<Orders> oList = null;
        List<OrderLine> olList = null;
        List<Customer> cusList = null;
        String strSQL = null;
        float fAmount = 0;
        int iOCId = 0;

        for (short d = 1; d <= 10; d = (short)(d + 1)) {
            // "INSERT INTO DELIVERY_ORDERS(DO_DR_ID, DO_D_ID, DO_O_ID) VALUES (?, ?, ?)"
            dos = new DeliveryOrders();
            dos.setDoDrId(request);
            dos.setDoDId(d);

            // "SELECT MIN(NO_O_ID) AS ORDER_TO_DELIVER FROM NEWORDERS WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_LIVE"

            noDao = ds.getNewOrdersDao();
            qb = noDao.queryBuilder();
            qb.where(qb.and(NewOrdersDao.Properties.NoWId.eq(w), NewOrdersDao.Properties.NoDId.eq(d),
                    NewOrdersDao.Properties.NoLive.eq(true))).orderDesc(NewOrdersDao.Properties.NoOId);
            noList = qb.limit(1).list();

            int order = -1;
            if (null != noList && noList.size() > 0) {
                order = noList.get(0).getNoOId();
            } else {
                vDos.add(dos);
            }

            // "UPDATE NEWORDERS SET NO_LIVE = FALSE WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_O_ID = ?"

            noDao = ds.getNewOrdersDao();
            qb = noDao.queryBuilder();
            qb.where(qb.and(NewOrdersDao.Properties.NoWId.eq(w), NewOrdersDao.Properties.NoDId.eq(d),
                    NewOrdersDao.Properties.NoOId.eq(order)));
            noList = qb.list();
            if (null != noList) {
                for (NewOrders no : noList) {
                    no.setNoLive(false);
                }
                noDao.updateInTx(noList);
            }

            // "UPDATE ORDERS SET O_CARRIER_ID = ? WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
            oDao = ds.getOrdersDao();
            qb = oDao.queryBuilder();
            qb.where(qb.and(OrdersDao.Properties.OWId.eq(w), OrdersDao.Properties.ODId.eq(d), OrdersDao.Properties.OId.eq(order)));
            oList = qb.list();
            if (null != oList) {
                for (Orders o : oList) {
                    o.setOCarrierId(carrier);
                }
                oDao.updateInTx(oList);
            }

            // "UPDATE ORDERLINE SET OL_DELIVERY_D = CURRENT TIMESTAMP WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
            olDao = ds.getOrderLineDao();
            qb = olDao.queryBuilder();
            qb.where(qb.and(OrderLineDao.Properties.OlWId.eq(w), OrderLineDao.Properties.OlDId.eq(d),
                    OrderLineDao.Properties.OlOId.eq(order)));
            olList = qb.list();
            if (null != olList) {
                currentTimeStamp = new Timestamp(System.currentTimeMillis());
                for (OrderLine ol : olList) {
                    ol.setOlDeliveryD(currentTimeStamp);
                }
                olDao.updateInTx(olList);
            }

            // "UPDATE CUSTOMER SET C_BALANCE = (SELECT SUM(OL_AMOUNT) FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?), C_DELIVERY_CNT = C_DELIVERY_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = (SELECT O_C_ID FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?)"
            qb = olDao.queryBuilder();
            qb.where(qb.and(OrderLineDao.Properties.OlWId.eq(w), OrderLineDao.Properties.OlDId.eq(d), OrderLineDao.Properties.OlOId.eq(order)));
            olList = qb.list();
            fAmount = 0;
            if (null != olList) {
                for (OrderLine ol: olList) {
                    fAmount += ol.getOlAmount();
                }
            }

            qb = oDao.queryBuilder();
            qb.where(qb.and(OrdersDao.Properties.OWId.eq(w), OrdersDao.Properties.ODId.eq(d), OrdersDao.Properties.OId.eq(order)));
            oList = qb.limit(1).list();
            iOCId = -1;
            if (null != oList) {
                iOCId = oList.get(0).getOCId();
            }

            cusDao = ds.getCustomerDao();
            qb = cusDao.queryBuilder();
            qb.where(qb.and(CustomerDao.Properties.CWId.eq(w), CustomerDao.Properties.CDId.eq(d), CustomerDao.Properties.CId.eq(iOCId)));
            cusList = qb.list();
            if (null != cusList) {
                for (Customer cus : cusList) {
                    cus.setCBalance(fAmount);
                    cus.setCDeliveryCnt(cus.getCDeliveryCnt()+1);
                }
                cusDao.updateInTx(cusList);
            }

            if (-1 != order) {
                dos.setDoOId(order);
                vDos.add(dos);
            }
        }

        // "INSERT INTO DELIVERY_ORDERS(DO_DR_ID, DO_D_ID, DO_O_ID) VALUES (?, ?, ?)"
        DeliveryOrdersDao doDao = ds.getDeliveryOrdersDao();
        doDao.insertInTx(vDos);

        // "UPDATE DELIVERY_REQUEST SET DR_STATE = 'C', DR_COMPLETED = CURRENT TIMESTAMP WHERE DR_ID = ?"
        drDao = ds.getDeliveryRequestDao();
        qb = drDao.queryBuilder();
        qb.where(DeliveryRequestDao.Properties.Id.eq(request));
        drs = qb.list();
        if (null != drs) {
            currentTimeStamp = new Timestamp(System.currentTimeMillis());
            for (DeliveryRequest dr : drs) {
                dr.setDrState("C");
                dr.setDrCompleted(currentTimeStamp);
            }
            drDao.updateInTx(drs);
        }
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
