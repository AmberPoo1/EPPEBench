package com.jingpu.android.apersistance.realm;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.realm.model.Customer;
import com.jingpu.android.apersistance.realm.model.DeliveryOrders;
import com.jingpu.android.apersistance.realm.model.DeliveryRequest;
import com.jingpu.android.apersistance.realm.model.District;
import com.jingpu.android.apersistance.realm.model.History;
import com.jingpu.android.apersistance.realm.model.Item;
import com.jingpu.android.apersistance.realm.model.NewOrders;
import com.jingpu.android.apersistance.realm.model.OrderLine;
import com.jingpu.android.apersistance.realm.model.Orders;
import com.jingpu.android.apersistance.realm.model.Stock;
import com.jingpu.android.apersistance.realm.model.Warehouse;
import com.jingpu.android.apersistance.util.OrderStatusException;
import com.jingpu.android.apersistance.util.PaymentException;
import com.jingpu.android.apersistance.util.RealmInsertException;
import com.jingpu.android.apersistance.util.TPCCLog;

import org.dacapo.derby.Data;
import org.dacapo.derby.OrderItem4Sort;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

/**
 * Created by Jing Pu on 2016/1/26.
 */
public class RealmStandard extends RealmStatementHelper
        implements RealmOperations {

    private final Customer customer = new Customer();
    private final List nameList = new ArrayList();
    private final Orders order = new Orders();
    private final District district = new District();
    private final Warehouse warehouse = new Warehouse();

    // Jing Pu test 5/14/2016
    //long transStartTime = 0;
    //long transEndTime = 0;

    public RealmStandard(RealmAgent ra) throws RealmException {
        super(ra);
    }

    public void stockLevel(int terminalId, RealmDisplay display, Object displayData, short w, short d, int threshold) throws Exception {
        Long count = -1l;
        int iLowStock = 0;

        Realm realm = null;
        try {
            realm = ra.getRealmInstance();

            //transStartTime = new Date().getTime();

            // "SELECT D_NEXT_O_ID FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?"
            District dist = realm.where(District.class)
                    .equalTo("sDWId", w).equalTo("sDId", d).findFirst();

            //transEndTime = new Date().getTime();
            //TPCCLog.v(RealmStandard.class.getName(), "stockLevel s1 duration[" + (dist == null ?0:1) + "] = " + (transEndTime - transStartTime));

            int nextOrder = -1;
            if (null != dist) {
                nextOrder = dist.getiDNxtOId();
            }

            //transStartTime = new Date().getTime();

            // "SELECT COUNT(DISTINCT(S_I_ID)) AS LOW_STOCK FROM ORDERLINE, STOCK WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID < ? AND OL_O_ID >= ? AND S_W_ID = ? AND S_I_ID = OL_I_ID AND S_QUANTITY < ?"
            RealmResults<OrderLine> olList = realm.where(OrderLine.class)
                    .equalTo("sOlWId", w).equalTo("sOlDId", d)
                    .beginGroup()
                        .lessThan("iOlOId", nextOrder).greaterThanOrEqualTo("iOlOId", nextOrder - 20)
                    .endGroup().findAll();
            RealmResults<Stock> skList = realm.where(Stock.class)
                    .equalTo("sSWId", w).lessThan("iSQuantity", threshold).findAll();

            //transEndTime = new Date().getTime();
            //TPCCLog.v(RealmStandard.class.getName(), "stockLevel s2 duration = " + (transEndTime - transStartTime));

            if (null != olList && null != skList) {
                count = 0l;
                for (OrderLine ol : olList) {
                    for (Stock sk : skList) {
                        if (ol.getiOlIId() == sk.getiSIId()) {
                            count++;
                        }
                    }
                }
            }
            iLowStock = count.intValue();
        } finally {
            if (null != realm) {
                realm.close();
            }
        }

        if (display != null) {
            display.displayStockLevel(displayData, w, d, threshold, iLowStock);
        }
    }

    @SuppressWarnings("unchecked")
    public void orderStatus(int terminalId, RealmDisplay display, Object displayData, short w, short d, String customerLast) throws Exception {
        Realm realm = null;
        try {
            realm = ra.getRealmInstance();

            //transStartTime = new Date().getTime();

            // "SELECT C_ID, C_BALANCE, C_FIRST, C_MIDDLE FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_LAST = ? ORDER BY C_FIRST"
            RealmResults<Customer> customers = realm.where(Customer.class)
                    .equalTo("sCWId", w).equalTo("sCDId", d).equalTo("strCLst", customerLast).findAll();
            customers.sort("strCFst");

            //transEndTime = new Date().getTime();
            //TPCCLog.v(RealmStandard.class.getName(), "orderStatus 132 s1 prepare duration[" + customers.size() + "] = " + (transEndTime - transStartTime));


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

            getOrderStatusForCustomer(terminalId, display, displayData, true, customer, realm);
        } finally {
            if (null != realm) {
                realm.close();
            }
        }
    }

    public void orderStatus(int terminalId, RealmDisplay display, Object displayData, short w, short d, int c) throws Exception {
        clearCustomer(this.customer);
        this.customer.setsCWId(w);
        this.customer.setsCDId(d);
        this.customer.setiCId(c);

        Realm realm = null;
        try {
            realm = ra.getRealmInstance();

            //transStartTime = new Date().getTime();

            // "SELECT C_BALANCE, C_FIRST, C_MIDDLE, C_LAST FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
            Customer cus = realm.where(Customer.class)
                    .equalTo("sCWId", w).equalTo("sCDId", d).equalTo("iCId", c).findFirst();

            //transEndTime = new Date().getTime();
            //TPCCLog.v(RealmStandard.class.getName(), "orderStatus 174 s1 duration[" + (cus == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));


            if (null != cus) {
                customer.setfCBalance(cus.getfCBalance());
                customer.setStrCFst(cus.getStrCFst());
                customer.setStrCMid(cus.getStrCMid());
                customer.setStrCLst(cus.getStrCLst());
            }

            getOrderStatusForCustomer(terminalId, display, displayData, false, this.customer, realm);
        } finally {
            if (null != realm) {
                realm.close();
            }
        }
    }

    private void getOrderStatusForCustomer(int terminalId, RealmDisplay display, Object displayData, boolean byName, Customer customer, Realm realm) throws Exception {

        //transStartTime = new Date().getTime();

        // "SELECT MAX(O_ID) AS LAST_ORDER FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_C_ID = ?";
        RealmResults<Orders> orders = realm.where(Orders.class)
                .equalTo("sOWId", customer.getsCWId()).equalTo("sODId", customer.getsCDId()).equalTo("iOCId", customer.getiCId()).findAll();

        int iLastOrder = -1;
        if (null != orders && orders.size() > 0) {
           iLastOrder = orders.max("iOId").intValue();;
        }

        //transEndTime = new Date().getTime();
        //TPCCLog.v(RealmStandard.class.getName(), "getOrderStatusForCustomer s1 duration[" + (iLastOrder == -1 ? 0 : 1) + "] = " + (transEndTime - transStartTime));


        clearOrder(this.order);
        this.order.setsOWId(customer.getsCWId());
        this.order.setsODId(customer.getsCDId());
        this.order.setiOId(iLastOrder);

        //transStartTime = new Date().getTime();

        // "SELECT O_ENTRY_D, O_CARRIER_ID, O_OL_CNT FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
        Orders o = realm.where(Orders.class)
                .equalTo("sOWId", customer.getsCWId()).equalTo("sODId", customer.getsCDId()).equalTo("iOId", this.order.getiOId()).findFirst();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(RealmStandard.class.getName(), "getOrderStatusForCustomer s2 duration[" + (o == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));


        if (null != o) {
            this.order.settOEntryD(o.gettOEntryD());
            this.order.setsOCarrierId(o.getsOCarrierId());
            this.order.setsOOlCnt(o.getsOOlCnt());
        }

        //transStartTime = new Date().getTime();
        // "SELECT OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
        RealmResults<OrderLine> ols = realm.where(OrderLine.class)
                .equalTo("sOlWId", this.order.getsOWId()).equalTo("sOlDId", this.order.getsODId()).equalTo("iOlOId", this.order.getiOId()).findAll();

        //transEndTime = new Date().getTime();
        //TPCCLog.v(RealmStandard.class.getName(), "getOrderStatusForCustomer s3 duration[" + ols.size() + "] = " + (transEndTime - transStartTime));


        OrderLine[] lineItems = new OrderLine[this.order.getsOOlCnt()];
        OrderLine ol = null;
        int oli = 0;

        if (null != ols) {
            for (OrderLine olRec : ols) {
                ol = new OrderLine();
                ol.setiOlIId(olRec.getiOlIId());
                ol.setsOlSupplyWId(olRec.getsOlSupplyWId());
                ol.setsOlQuantity(olRec.getsOlQuantity());
                ol.setfOlAmount(olRec.getfOlAmount());
                ol.settOlDeliveryD(olRec.gettOlDeliveryD());

                if (oli < this.order.getsOOlCnt()) {
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

    public void payment(int terminalId, RealmDisplay display, Object displayData, short w, short d, short cw, short cd, String customerLast, String amount)
            throws Exception {
        Realm realm = null;
        try {
            realm = ra.getRealmInstance();

            //transStartTime = new Date().getTime();

            // "SELECT C_ID FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_LAST = ? ORDER BY C_FIRST"
            RealmResults<Customer> customers = realm.where(Customer.class)
                    .equalTo("sCWId", cw).equalTo("sCDId", cd).equalTo("strCLst", customerLast).findAll();
            customers.sort("strCFst");

            //transEndTime = new Date().getTime();
            //TPCCLog.v(RealmStandard.class.getName(), "payment s1 duration[" + customers.size() + "] = " + (transEndTime - transStartTime));

            this.nameList.clear();

            if (null != customers) {
                for (Customer cus: customers) {
                    this.nameList.add(cus.getiCId());
                }
            }

        } finally {
            if (null != realm) {
                realm.close();
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

    public void payment(int terminalId, RealmDisplay display, Object displayData, short w, short d, short cw, short cd, int c, String amount)
            throws Exception {
        paymentById(terminalId, display, displayData, w, d, cw, cd, c, amount);

        if (display != null) {

        }
    }

    private void paymentById(int terminalId, RealmDisplay display, Object displayData, short w, short d, short cw, short cd, int c, String s_amount)
            throws Exception {
        Realm realm = null;
        try {
            realm = ra.getRealmInstance();

            //transStartTime = new Date().getTime();

            // "UPDATE CUSTOMER SET C_BALANCE = C_BALANCE - ?, C_YTD_PAYMENT = C_YTD_PAYMENT + ?, C_PAYMENT_CNT = C_PAYMENT_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
            Customer customer = new Customer();
            customer.setsCWId(cw);
            customer.setsCDId(cd);
            customer.setiCId(c);
            customer.setCompositeKey(customer.getCompositeKey(customer));

            Customer cus = null;
            RealmResults<Customer> customers = realm.where(Customer.class)
                    .equalTo("sCWId", cw).equalTo("sCDId", cd).equalTo("iCId", c).findAll();
            if (null != customers) {
                realm.beginTransaction();
                for (int i=0; i<customers.size(); i++) {
                    cus = customers.get(i);
                    cus.setfCBalance(cus.getfCBalance() - Float.parseFloat(s_amount));
                    cus.setfCYTDPayment(cus.getfCYTDPayment() + Float.parseFloat(s_amount));
                    cus.setiCPaymentCnt(cus.getiCPaymentCnt() + 1);
                }
                realm.commitTransaction();
            }

            //transEndTime = new Date().getTime();
            //TPCCLog.v(RealmStandard.class.getName(), "paymentById u1 duration = " + (transEndTime - transStartTime));

            //transStartTime = new Date().getTime();

            // "SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE, C_STREET_1, C_STREET_2, C_CITY, C_STATE, C_ZIP,
            // C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_DATA FROM CUSTOMER
            // WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?
            cus = realm.where(Customer.class)
                    .equalTo("sCWId", cw).equalTo("sCDId", cd).equalTo("iCId", c).findFirst();

            //transEndTime = new Date().getTime();
            //TPCCLog.v(RealmStandard.class.getName(), "paymentById s2 duration[" + (cus == null ? 0 : 1) + "] = " + (transEndTime - transStartTime));


            if (null != cus) {
                customer.setStrCFst(cus.getStrCFst());
                customer.setStrCMid(cus.getStrCMid());
                customer.setStrCLst(cus.getStrCLst());
                customer.setfCBalance(cus.getfCBalance());

                // Address
                customer.setStrCStreet1(cus.getStrCStreet1());
                customer.setStrCStreet2(cus.getStrCStreet2());
                customer.setStrCCity(cus.getStrCCity());
                customer.setStrCState(cus.getStrCState());
                customer.setStrCZip(cus.getStrCZip());

                customer.setStrCPhone(cus.getStrCPhone());
                customer.settCSince(cus.gettCSince());
                customer.setStrCCredit(cus.getStrCCredit());
                customer.setfCCreditLim(cus.getfCCreditLim());
                customer.setfCDiscount(cus.getfCDiscount());
                customer.setStrCData(cus.getStrCData());
            }

            if ("BC".equals(customer.getStrCCredit())) {
                // "UPDATE CUSTOMER SET C_DATA = ? WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
                customers = realm.where(Customer.class)
                        .equalTo("sCWId", cw).equalTo("sCDId", cd).equalTo("iCId", c).findAll();
                if (null != customers) {
                    realm.beginTransaction();
                    for (int i=0; i<customers.size(); i++) {
                        customers.get(i).setStrCData(Data.dataForBadCredit(customer.getStrCData(), w, d, cw, cd, c, new BigDecimal(s_amount)));
                    }
                    realm.commitTransaction();
                }

                // "SELECT SUBSTR(C_DATA, 1, 200) AS C_DATA_200 FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
                cus = realm.where(Customer.class)
                        .equalTo("sCWId", cw).equalTo("sCDId", cd).equalTo("iCId", c).findFirst();
                String strData = null;
                String cData = null;
                if (null != cus) {
                    cData = cus.getStrCData();
                    if (cData.length() > 200) {
                        strData = cData.substring(0, 200);
                    } else {
                        strData = cData;
                    }
                }
                customer.setStrCData(strData);
            }

            // "UPDATE DISTRICT SET D_YTD = D_YTD + ? WHERE D_W_ID = ? AND D_ID = ?"
            clearDistrict(this.district);
            this.district.setsDWId(w);
            this.district.setsDId(d);

            District dist = null;
            RealmResults<District> dists = realm.where(District.class)
                    .equalTo("sDWId", w).equalTo("sDId", d).findAll();

            if (null != dists) {
                realm.beginTransaction();
                for (int i=0; i<dists.size(); i++) {
                    dist = dists.get(i);
                    dist.setfDYTD(dist.getfDYTD() + Float.parseFloat(s_amount));
                }
                realm.commitTransaction();
            }

            // "SELECT D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ? "
            dist = realm.where(District.class)
                    .equalTo("sDWId", w).equalTo("sDId", d).findFirst();
            if (null != dist) {
                this.district.setStrDName(dist.getStrDName());
                this.district.setStrDStreet1(dist.getStrDStreet1());
                this.district.setStrDStreet2(dist.getStrDStreet2());
                this.district.setStrDCity(dist.getStrDCity());
                this.district.setStrDState(dist.getStrDState());
                this.district.setStrDZip(dist.getStrDZip());
            }

            // "UPDATE WAREHOUSE SET W_YTD = W_YTD + ? WHERE W_ID = ?"
            Warehouse wh = null;
            RealmResults<Warehouse> whs = realm.where(Warehouse.class).equalTo("lWId", w).findAll();

            if (null != whs) {
                realm.beginTransaction();
                for (int i=0; i<whs.size(); i++) {
                    wh = whs.get(i);
                    wh.setfWYtd(wh.getfWYtd() + Float.parseFloat(s_amount));
                }
                realm.commitTransaction();
            }

            // "SELECT W_NAME, W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP FROM WAREHOUSE WHERE W_ID = ?"
            wh = realm.where(Warehouse.class).equalTo("lWId", w).findFirst();
            if (null != wh) {
                this.warehouse.setStrWName(wh.getStrWName());
                this.warehouse.setStrWStreet1(wh.getStrWStreet1());
                this.warehouse.setStrWStreet2(wh.getStrWStreet2());
                this.warehouse.setStrWCity(wh.getStrWCity());
                this.warehouse.setStrWState(wh.getStrWState());
                this.warehouse.setStrWZip(wh.getStrWZip());
            }

            // "INSERT INTO HISTORY(H_C_ID, H_C_D_ID, H_C_W_ID, H_D_ID, H_W_ID, H_AMOUNT, H_DATA, H_DATE, H_INITIAL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, FALSe)"
            realm.beginTransaction();
            History h = realm.createObject(History.class);

            Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
            h.setHId(AppContext.getInstance().getNextHId());
            h.setiHCId(c);
            h.setsHCDId(cd);
            h.setsHCWId(cw);
            h.setsHDId(d);
            h.setsHWId(w);
            h.setfHAmount(Float.parseFloat(s_amount));

            StringBuffer hData = new StringBuffer(24);
            hData.append(this.warehouse.getStrWName());
            hData.append("    ");
            hData.append(this.district.getStrDName());
            h.setStrHData(hData.toString());
            h.settHDate(currentTimeStamp);
            h.setbHInitial(false);

            realm.commitTransaction();
        } finally {
            if (null != realm) {
                realm.close();
            }
        }
    }

    public void newOrder(int terminalId, RealmDisplay display, Object displayData, short w, short d, int c, int[] items, short[] quantities, short[] supplyW)
            throws Exception {
        if (quantities == null || quantities.length == 0) {
            TPCCLog.e(RealmStandard.class.getName(), "newOrder Tml[" + terminalId + "]: quantities is null");

        }

        if (items == null || items.length == 0) {
            TPCCLog.e(RealmStandard.class.getName(), "newOrder Tml[" + terminalId + "]: items is null");
        }

        if (supplyW == null || supplyW.length == 0) {
            TPCCLog.e(RealmStandard.class.getName(), "newOrder Tml[" + terminalId + "]: supplyW is null");
        }

        sortOrderItems(items, quantities, supplyW);

        Realm realm = null;
        District dist = null;
        try {
            realm = ra.getRealmInstance();

            // "SELECT W_TAX FROM WAREHOUSE WHERE W_ID = ?"
            Warehouse wh = realm.where(Warehouse.class).equalTo("lWId", w).findFirst();
            float warehouseTax = -1;
            if (null != wh) {
                warehouseTax = wh.getfWTax();
            }

            // "UPDATE DISTRICT SET D_NEXT_O_ID = D_NEXT_O_ID + 1 WHERE D_W_ID = ? AND D_ID = ?"
            RealmResults<District> dists = realm.where(District.class)
                    .equalTo("sDWId", w).equalTo("sDId", d).findAll();

            if (null != dists) {
                realm.beginTransaction();
                for (int i = 0; i < dists.size(); i++) {
                    dist = dists.get(i);
                    dist.setiDNxtOId(dist.getiDNxtOId() + 1);
                }
                realm.commitTransaction();
            }

            int orderNumber = -1;
            float districtTax = -1;
            short allLocal = 1;

            // "SELECT D_NEXT_O_ID - 1, D_TAX FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?"
            dist = realm.where(District.class)
                    .equalTo("sDWId", w).equalTo("sDId", d).findFirst();

            if (null != dist) {
                orderNumber = dist.getiDNxtOId() - 1;
                districtTax = dist.getfDTax();
            }

            // "SELECT C_LAST, C_DISCOUNT, C_CREDIT FROM CUSTOMER WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?"
            Customer cus = realm.where(Customer.class)
                    .equalTo("sCWId", w).equalTo("sCDId", d).equalTo("iCId", c).findFirst();


            for (int i = 0; i < supplyW.length; i++) {
                if (supplyW[i] != w) {
                    allLocal = 0;
                    break;
                }
            }

            // "INSERT INTO ORDERS VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, NULL, ?, ?, NULL, FALSE)"
            try {
                realm.beginTransaction();
                Orders o = realm.createObject(Orders.class);

                o.setiOId(orderNumber);
                o.setsODId(d);
                o.setsOWId(w);
                o.setCompositeKey(o.getCompositeKey(o));
                o.setiOCId(c);
                Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
                o.settOEntryD(currentTimeStamp);
                if (null != items) {
                    o.setsOOlCnt((short) items.length);
                } else {
                    o.setsOOlCnt((short) 0);
                }
                o.setsOAllLocal(allLocal);
                o.setbOInitial(false);
                realm.commitTransaction();
            } catch (Exception e) {
                throw new RealmInsertException("newOrder: Tml[" + terminalId + "] - insert Orders[" + orderNumber + "-"  + d + "-" + w + "] fail ");
            }

            // "INSERT INTO NEWORDERS VALUES(?, ?, ?, FALSE, TRUE)"

            try {
                realm.beginTransaction();
                NewOrders no = realm.createObject(NewOrders.class);
                no.setiNoOId(orderNumber);
                no.setsNoDId(d);
                no.setsNoWId(w);
                no.setCompositeKey(no.getCompositeKey(no));
                no.setbNoInitial(false);
                no.setbNoLive(true);
                realm.commitTransaction();
            } catch (Exception e) {
                throw new RealmInsertException("newOrder: Tml[" + terminalId + "] - insert NewOrders[" + orderNumber + "-" + d + "-" + w + "] fail ");
            }

            int length = 0;
            if (null != items) {
                length = items.length;
            }

            RealmResults<Stock> stocks = null;
            Stock stock = null;
            int stockQuantity;
            String stockData = null;
            String stockDistInfo = null;
            Class<?> stockClazz = Class.forName("com.jingpu.android.apersistance.realm.model.Stock");
            Method method = null;
            String methodName = null;
            OrderLine ol = null;
            OrderLine qryOl = null;
            Item item = null;
            String itemPrice = null;
            String itemName = null;
            String itemData = null;

            for (int i = 0; i < length; i++) {
                // "SELECT I_PRICE, I_NAME, I_DATA FROM ITEM WHERE I_ID = ?"
                item = realm.where(Item.class).equalTo("lIId", items[i]).findFirst();

                itemPrice = null;
                itemName = null;
                itemData = null;
                if (null != item) {
                    itemPrice = String.valueOf(item.getfIPrice());
                    itemName = item.getStrIName();
                    itemData = item.getStrIData();
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
                stock = realm.where(Stock.class)
                        .equalTo("iSIId", items[i]).equalTo("sSWId", w).findFirst();

                stockQuantity = -1;
                stockData = null;
                stockDistInfo = null;

                if (null != stock) {
                    stockQuantity = stock.getiSQuantity();
                    stockData = stock.getStrSData();
                    methodName = d < 10 ? "getStrSDist0" + d : "getStrSDist" + d;
                    method = stockClazz.getMethod(methodName);
                    stockDistInfo = (String)method.invoke(stock);
                }

                // "UPDATE STOCK SET S_ORDER_CNT = S_ORDER_CNT + 1, S_YTD = S_YTD + ?, S_REMOTE_CNT = S_REMOTE_CNT + ?, S_QUANTITY = ?
                // WHERE S_I_ID = ? AND S_W_ID = ?"
                stocks = realm.where(Stock.class)
                        .equalTo("iSIId", items[i]).equalTo("sSWId", w).findAll();

                if (null != quantities){
                    if (stockQuantity - quantities[i] > 10) {
                        stockQuantity -= quantities[i];
                    } else {
                        stockQuantity = stockQuantity - quantities[i] + 91;
                    }
                }

                if (null != stocks) {
                    realm.beginTransaction();
                    for (int j=0; j<stocks.size(); j++) {
                        stock = stocks.get(j);
                        stock.setiSOrderCnt(stock.getiSOrderCnt() + 1);
                        stock.setfSYTD(stock.getfSYTD() + (null != quantities ? quantities[i] : 0));
                        stock.setiSRemoteCnt(stock.getiSRemoteCnt() + (null != supplyW ? (w == supplyW[i] ? 0 : 1) : 1));
                        stock.setiSQuantity(stockQuantity);
                    }
                    realm.commitTransaction();
                }

                if (itemPrice != null && stockDistInfo != null) {

                    // "INSERT INTO ORDERLINE(OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER, OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO, OL_DELIVERY_D, OL_DELIVERY_D_INITIAL, OL_INITIAL) VALUES (?, ?, ?, ?, ?, ?, ?, CAST (? AS DECIMAL(5,2)) * CAST (? AS SMALLINT), ?, NULL, NULL, FALSE)"
                    // CAST(expression AS data type)
                    // SELECT CAST('2000-10-31' AS DATE)
                    // SELECT CAST(1+2 AS CHAR)
                    // SELECT CAST('Surname' AS CHAR(5))

                    try {
                        realm.beginTransaction();
                        ol = realm.createObject(OrderLine.class);
                        ol.setsOlWId(w);
                        ol.setsOlDId(d);
                        ol.setiOlOId(orderNumber);
                        ol.setsOlNumber((short) (i + 1));
                        ol.setCompositeKey(ol.getCompositeKey(ol));
                        ol.setiOlIId(items[i]);
                        ol.setsOlSupplyWId(supplyW[i]);
                        ol.setsOlQuantity(quantities[i]);
                        ol.setfOlAmount(Float.parseFloat(itemPrice) * quantities[i]);
                        ol.setStrOlDistInfo(stockDistInfo);
                        ol.setbOlInitial(false);
                        realm.commitTransaction();
                    } catch (Exception e) {
                        throw new RealmInsertException("newOrder: Tml[" + terminalId + "] - insert OrderLine[" + orderNumber + "-" + d + "-" + w + "-" + (i + 1) + "] fail ");
                    }
                }
            }

            // "SELECT SUM(OL_AMOUNT) FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
            RealmResults<OrderLine> ols = realm.where(OrderLine.class)
                    .equalTo("sOlWId", w).equalTo("sOlDId", d).equalTo("iOlOId", orderNumber).findAll();

            float orderTotal = -1;
            if (null != ols && ols.size() > 0) {
                orderTotal = ols.sum("fOlAmount").floatValue();
            }
        } finally {
            if (null != realm) {
                realm.close();
            }
        }
    }

    public void scheduleDelivery(int terminalId, RealmDisplay display, Object displayData, short w, short carrier)
            throws Exception {
        Realm realm = null;
        try {
            realm = ra.getRealmInstance();
            // "INSERT INTO DELIVERY_REQUEST(DR_W_ID, DR_CARRIER_ID, DR_STATE) VALUES(?, ?, 'Q')"
            realm.beginTransaction();
            DeliveryRequest dr = realm.createObject(DeliveryRequest.class);
            dr.setsDrWId(w);
            dr.setsDrCarrierId(carrier);
            dr.setStrDrState("Q");
            realm.commitTransaction();

        } finally {
            if (null != realm) {
                realm.close();
            }
        }

        if (display != null) {
            display.displayScheduleDelivery(displayData, w, carrier);
        }
    }

    public void delivery(int terminalId) throws Exception {
        Realm realm = null;
        try {
            realm = ra.getRealmInstance();
            // "SELECT DR_ID, DR_W_ID, DR_CARRIER_ID FROM DELIVERY_REQUEST WHERE DR_STATE = 'Q' ORDER BY DR_QUEUED"
            RealmResults<DeliveryRequest> drs = realm.where(DeliveryRequest.class)
                    .equalTo("sDrState", "Q").findAllSorted("tDrQueued");

            int request = -1;
            short w = -1;
            short carrier = -1;
            DeliveryRequest dr = null;
            if (null != drs && drs.size() > 0) {
                dr = drs.get(0);
                request = (int)dr.getlDrId();
                w = dr.getsDrWId();
                carrier = dr.getsDrCarrierId();
            }

            // "UPDATE DELIVERY_REQUEST SET DR_STATE = ? WHERE DR_ID = ?"
            drs = realm.where(DeliveryRequest.class).equalTo("lDrId", request).findAll();
            if (null != drs) {
                realm.beginTransaction();
                for (int i=0; i<drs.size(); i++) {
                    dr = drs.get(i);
                    dr.setStrDrState("I");
                }
                realm.commitTransaction();
            }

            Timestamp currentTimeStamp = null;

            final List<DeliveryOrders> vDos = new ArrayList<DeliveryOrders>();
            DeliveryOrders dos = null;
            RealmResults<NewOrders> noList = null;
            RealmResults<Orders> oList = null;
            RealmResults<OrderLine> olList = null;
            RealmResults<Customer> cusList = null;
            Orders o = null;
            Customer cus = null;
            String strSQL = null;
            float sum = 0;
            int ocid = -1;

            for (short d = 1; d <= 10; d = (short)(d + 1)) {
                // "INSERT INTO DELIVERY_ORDERS(DO_DR_ID, DO_D_ID, DO_O_ID) VALUES (?, ?, ?)"
                dos = new DeliveryOrders();
                dos.setDoId(AppContext.getInstance().getNextDoId());
                dos.setiDoDrId(request);
                dos.setsDoDId(d);

                // "SELECT MIN(NO_O_ID) AS ORDER_TO_DELIVER FROM NEWORDERS WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_LIVE"
                noList = realm.where(NewOrders.class)
                        .equalTo("sNoWId", w).equalTo("sNoDId", d).equalTo("bNoLive", true).findAll();

                int order = -1;
                if (null != noList && noList.size() > 0) {
                    order = noList.min("iNoOId").intValue();
                } else {
                    vDos.add(dos);
                }

                // "UPDATE NEWORDERS SET NO_LIVE = FALSE WHERE NO_W_ID = ? AND NO_D_ID = ? AND NO_O_ID = ?"
                noList = realm.where(NewOrders.class)
                        .equalTo("sNoWId", w).equalTo("sNoDId", d).equalTo("iNoOId", order).findAll();
                if (null != noList) {
                    realm.beginTransaction();
                    for (int i=0; i<noList.size(); i++) {
                        noList.get(i).setbNoLive(false);
                    }
                    realm.commitTransaction();
                }

                // "UPDATE ORDERS SET O_CARRIER_ID = ? WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?"
                oList = realm.where(Orders.class)
                        .equalTo("sOWId", w).equalTo("sODId", d).equalTo("iOId", order).findAll();
                if (null != oList) {
                    realm.beginTransaction();
                    for (int i=0; i<oList.size(); i++) {
                        oList.get(i).setsOCarrierId(carrier);
                    }
                    realm.commitTransaction();
                }

                // "UPDATE ORDERLINE SET OL_DELIVERY_D = CURRENT TIMESTAMP WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?"
                olList = realm.where(OrderLine.class)
                        .equalTo("sOlWId", w).equalTo("sOlDId", d).equalTo("iOlOId", order).findAll();
                if (null != olList) {
                    currentTimeStamp = new Timestamp(System.currentTimeMillis());
                    realm.beginTransaction();
                    for (int i=0; i<olList.size(); i++) {
                        olList.get(i).settOlDeliveryD(currentTimeStamp);
                    }
                    realm.commitTransaction();
                }

                // "UPDATE CUSTOMER SET C_BALANCE = (SELECT SUM(OL_AMOUNT) FROM ORDERLINE WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?), C_DELIVERY_CNT = C_DELIVERY_CNT + 1 WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = (SELECT O_C_ID FROM ORDERS WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?)"
                sum = 0;
                olList = realm.where(OrderLine.class)
                        .equalTo("sOlWId", w).equalTo("sOlDId", d).equalTo("iOlOId", order).findAll();
                if (null != olList && olList.size() > 0) {
                    sum = olList.sum("fOlAmount").floatValue();
                }

                ocid = -1;
                o = realm.where(Orders.class)
                        .equalTo("sOWId", w).equalTo("sODId", d).equalTo("iOId", order).findFirst();
                if (null != o) {
                    ocid = o.getiOCId();
                }

                cusList = realm.where(Customer.class)
                        .equalTo("sCWId", w).equalTo("sCDId", d).equalTo("iCId", ocid).findAll();
                if (null != cusList) {
                    realm.beginTransaction();
                    for (int i=0; i<cusList.size(); i++) {
                        cus = cusList.get(i);
                        cus.setfCBalance(sum);
                        cus.setiCDeliveryCnt(cus.getiCDeliveryCnt() + 1);
                    }
                    realm.commitTransaction();
                }

                if (-1 != order) {
                    dos.setiDoOId(order);
                    vDos.add(dos);
                }
            }

            // "INSERT INTO DELIVERY_ORDERS(DO_DR_ID, DO_D_ID, DO_O_ID) VALUES (?, ?, ?)"
            realm.beginTransaction();
            realm.copyToRealm(vDos);
            realm.commitTransaction();

            // "UPDATE DELIVERY_REQUEST SET DR_STATE = 'C', DR_COMPLETED = CURRENT TIMESTAMP WHERE DR_ID = ?"
            drs = realm.where(DeliveryRequest.class)
                    .equalTo("lDrId", request).findAll();
            if (null != drs) {
                currentTimeStamp = new Timestamp(System.currentTimeMillis());
                realm.beginTransaction();
                for (int i=0; i<drs.size(); i++) {
                    dr = drs.get(i);
                    dr.setStrDrState("C");
                    dr.settDrCompleted(currentTimeStamp);
                }
                realm.commitTransaction();
            }
        } finally {
            if (null != realm) {
                realm.close();
            }
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

    private void clearCustomer(Customer cus) {
        if (null == cus) {
            return;
        }
        cus.setiCId(0);
        cus.setsCDId((short) 0);
        cus.setsCWId((short) 0);
        cus.setCompositeKey(null);
        cus.setStrCFst(null);
        cus.setStrCMid(null);
        cus.setStrCLst(null);
        cus.setStrCStreet1(null);
        cus.setStrCStreet2(null);
        cus.setStrCCity(null);
        cus.setStrCState(null);
        cus.setStrCZip(null);
        cus.setStrCPhone(null);
        cus.settCSince(null);
        cus.setfCCreditLim(0);
        cus.setfCDiscount(0);
        cus.setStrCCredit(null);
        cus.setfCYTDPayment(0);
        cus.setiCDeliveryCnt(0);
        cus.setiCPaymentCnt(0);
        cus.setStrCData(null);
    }

    private void clearDistrict(District dist) {
        if (null == dist) {
            return;
        }

        dist.setsDId((short) 0);
        dist.setsDWId((short) 0);
        dist.setCompositeKey(null);
        dist.setStrDName(null);
        dist.setStrDStreet1(null);
        dist.setStrDStreet2(null);
        dist.setStrDCity(null);
        dist.setStrDState(null);
        dist.setStrDZip(null);
        dist.setfDTax(0);
        dist.setfDYTD(0);
    }

    private void clearOrder(Orders order) {
        order.setiOId(0);
        order.setsODId((short)0);
        order.setsOWId((short)0);
        order.setCompositeKey(null);
        order.setiOCId(0);
        order.settOEntryD(null);
        order.setsOCarrierId((short)-1);
        order.setsOOlCnt((short)0);
        order.setsOAllLocal((short)0);
    }
}
