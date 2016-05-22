package com.jingpu.android.apersistance.realm;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.realm.model.C;
import com.jingpu.android.apersistance.realm.model.Customer;
import com.jingpu.android.apersistance.realm.model.District;
import com.jingpu.android.apersistance.realm.model.History;
import com.jingpu.android.apersistance.realm.model.Item;
import com.jingpu.android.apersistance.realm.model.NewOrders;
import com.jingpu.android.apersistance.realm.model.OrderLine;
import com.jingpu.android.apersistance.realm.model.Orders;
import com.jingpu.android.apersistance.realm.model.Stock;
import com.jingpu.android.apersistance.realm.model.Warehouse;
import com.jingpu.android.apersistance.util.TPCCLoad;

import org.dacapo.derby.Load;
import org.dacapo.derby.OERandom;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

/**
 * Created by Jing Pu on 2016/1/26.
 */
public class RealmSimpleInsert implements TPCCLoad {

    RealmAgent ra = null;
    short scale = 1;
    long seed = System.currentTimeMillis();
    OERandom random;

    public void setupLoad(short scale) throws RealmException { //Session session,
        this.scale = scale;
        setupAgent();

        Realm realm = null;
        try {
            realm = ra.getRealmInstance();

	        // "DELETE FROM C"
	        RealmResults<C> results = realm.where(C.class).findAll();
	        realm.beginTransaction();
	        results.clear();
	
	        // "INSERT INTO C VALUES(" + loadRandomFactor + ")"
	        random = new OERandom(-1, seed);
	        int loadRandomFactor = random.randomInt(0, 255);
	        C c = realm.createObject(C.class);
            c.setCId(1);
	        c.setiCLoad(loadRandomFactor);
	        realm.commitTransaction();
        } finally {
            if (null != realm) {
                realm.close();
            }
        }

        setRandomGenerator();
    }

    public void setupAgent() throws RealmException {
        ra = new RealmAgent(AppContext.getInstance());
        if (null == ra) {
            throw new NullPointerException();
        }
    }

    void setRandomGenerator() throws RealmException {
        if (null == ra) {
            throw new NullPointerException();
        }

        Realm realm = null;
        try {
            realm = ra.getRealmInstance();

            // "SELECT CLOAD FROM C"
            C cObj = realm.where(C.class).findFirst();

            if (null == cObj) {
                throw new NullPointerException();
            }

            int loadRandomFactor = cObj.getiCLoad();
            this.random = new OERandom(loadRandomFactor, this.seed);
        } finally {
            if (null != realm) {
                realm.close();
            }
        }
    }

    public void populateAllTables() throws Exception {

        itemTable(1, Load.ITEM_COUNT); //10000

        for (short w = 1; w <= this.scale; w = (short)(w + 1)) {
            populateForOneWarehouse(w);
        }
    }

    void populateForOneWarehouse(short w) throws RealmException {
        warehouseTable(w);

        stockTable(1, Load.STOCK_COUNT_W, w); //10000

        for (short d = 1; d <= Load.DISTRICT_COUNT_W; d = (short)(d + 1)) { //4
            districtTable(w, d);
            customerTable(w, d);
            orderTable(w, d);
        }
    }

    public void itemTable(final int itemStart, final int itemEnd) throws RealmException {
        if (null == ra) {
            throw new NullPointerException();
        }

        // "INSERT INTO ITEM(I_ID,I_IM_ID,I_NAME,I_PRICE,I_DATA) VALUES (?, ?, ?, ?, ?)"
        List<Item> items = new ArrayList<Item>();
        Item item = null;
        for (int i = itemStart; i <= itemEnd; i++) {
            item = new Item();
            item.setlIId(i);
            item.setiIImId(random.randomInt(1, 10000));
            item.setStrIName(random.randomAString14_24());
            item.setfIPrice(Float.parseFloat(random.randomDecimalString(100, 9999, 2)));
            item.setStrIData(random.randomData());
            items.add(item);
        }

        Realm realm = null;
        try {
            realm = ra.getRealmInstance();
            realm.beginTransaction();
            realm.copyToRealm(items);
            realm.commitTransaction();;
        } finally {
            if (null != realm) {
                realm.close();
            }
        }
    }

    public void warehouseTable(short w) throws RealmException {
        if (null == ra) {
            throw new NullPointerException();
        }

        Realm realm = null;
        try {
            realm = ra.getRealmInstance();
            // "INSERT INTO WAREHOUSE VALUES (?, ?, ?, ?, ?, ?, ?, ?, 300000.00)"
            realm.beginTransaction();
            Warehouse wh = realm.createObject(Warehouse.class);
            wh.setlWId(w);
            wh.setStrWName(this.random.randomAString(6, 10));
            wh.setStrWStreet1(this.random.randomAString10_20());
            wh.setStrWStreet2(this.random.randomAString10_20());
            wh.setStrWCity(this.random.randomAString10_20());
            wh.setStrWState(this.random.randomState());
            wh.setStrWZip(this.random.randomZIP());
            wh.setfWTax(Float.parseFloat(this.random.randomDecimalString(0, 2000, 4)));
            wh.setfWYtd(300000.00f);
            realm.commitTransaction();
        } finally {
            if (null != realm) {
                realm.close();
            }
        }
    }

    public void stockTable(final int itemStart, final int itemEnd, final short w) throws RealmException {
        if (null == ra) {
            throw new NullPointerException();
        }

        // "INSERT INTO STOCK (S_I_ID, S_W_ID, S_QUANTITY,S_DIST_01, S_DIST_02, S_DIST_03,S_DIST_04,S_DIST_05,
        // S_DIST_06,S_DIST_07,S_DIST_08,S_DIST_09,S_DIST_10,
        // S_ORDER_CNT, S_REMOTE_CNT, S_YTD, S_DATA, S_QUANTITY_INITIAL ) VALUES
        // (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0, 0, ?, ?)"

        int quantity = 0;
        Stock stock = null;
        List<Stock> stocks = new ArrayList<Stock>();
        for (int i = itemStart; i <= itemEnd; i++) {
            quantity = random.randomInt(10, 100);
            stock = new Stock();
            stock.setiSIId(i);
            stock.setsSWId(w);
            stock.setCompositeKey(stock.getCompositeKey(stock));
            stock.setiSQuantity(quantity);
            stock.setStrSDist01(random.randomAString24());
            stock.setStrSDist02(random.randomAString24());
            stock.setStrSDist03(random.randomAString24());
            stock.setStrSDist04(random.randomAString24());
            stock.setStrSDist05(random.randomAString24());
            stock.setStrSDist06(random.randomAString24());
            stock.setStrSDist07(random.randomAString24());
            stock.setStrSDist08(random.randomAString24());
            stock.setStrSDist09(random.randomAString24());
            stock.setStrSDist10(random.randomAString24());
            stock.setiSOrderCnt(0);
            stock.setiSRemoteCnt(0);
            stock.setfSYTD(0);
            stock.setStrSData(random.randomData());
            stock.setiSQtyInit(quantity);
            stocks.add(stock);
        }

        Realm realm = null;
        try {
            realm = ra.getRealmInstance();
            realm.beginTransaction();
            realm.copyToRealm(stocks);
            realm.commitTransaction();
        } finally {
            if (null != realm) {
                realm.close();
            }
        }
    }

    public void districtTable(short w, short d) throws RealmException {
        if (null == ra) {
            throw new NullPointerException();
        }

        Realm realm = null;
        try {
            //"INSERT INTO DISTRICT (D_ID, D_W_ID, D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP,
            // D_TAX, D_YTD, D_NEXT_O_ID)  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, 30000.00, 3001)");
            realm = ra.getRealmInstance();
            realm.beginTransaction();

            District district = realm.createObject(District.class);
            district.setsDId(d);
            district.setsDWId(w);
            district.setCompositeKey(district.getCompositeKey(district));
            district.setStrDName(this.random.randomAString(6, 10));
            district.setStrDStreet1(random.randomAString10_20());
            district.setStrDStreet2(random.randomAString10_20());
            district.setStrDCity(this.random.randomAString10_20());
            district.setStrDState(this.random.randomState());
            district.setStrDZip(this.random.randomZIP());
            district.setfDTax(Float.parseFloat(this.random.randomDecimalString(0, 2000, 4)));
            district.setfDYTD(30000.00f);
            district.setiDNxtOId(3001);
            realm.commitTransaction();
        } finally {
            if (null != realm) {
                realm.close();
            }
        }
    }

    public void customerTable(final short w, final short d) throws RealmException {
        if (null == ra) {
            throw new NullPointerException();
        }

        List<Customer> customers = new ArrayList<Customer>();
        List<History> histories  = new ArrayList<History>();

        String str = null;
        Timestamp currentTimeStamp = null;
        Customer customer = null;
        History history = null;

        for (int c = 1; c <= Load.CUSTOMER_COUNT_W / Load.DISTRICT_COUNT_W; c++) { //1000
            currentTimeStamp = new Timestamp(System.currentTimeMillis());
            // "INSERT INTO CUSTOMER (C_ID, C_D_ID, C_W_ID, C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2,  C_CITY,
            // C_STATE, C_ZIP, C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT,
            // C_PAYMENT_CNT, C_DELIVERY_CNT, C_DATA, C_DATA_INITIAL)
            // VALUES (?, ?, ?, ?, 'OE', ?, ?, ?, ?, ?, ?, ?,
            // CURRENT TIMESTAMP ,?, 50000.00, ?, -10.0, 10.0, 1, 0, ?, ?)"

            customer = new Customer();
            customer.setiCId(c);
            customer.setsCDId(d);
            customer.setsCWId(w);
            customer.setCompositeKey(customer.getCompositeKey(customer));
            customer.setStrCFst(random.randomAString8_16());
            customer.setStrCMid("OE");
            customer.setStrCLst(random.randomCLastPopulate(c));
            customer.setStrCStreet1(random.randomAString10_20());
            customer.setStrCStreet2(random.randomAString10_20());
            customer.setStrCCity(random.randomAString10_20());
            customer.setStrCState(random.randomState());
            customer.setStrCZip(random.randomZIP());
            customer.setStrCPhone(random.randomNString(16, 16));
            customer.settCSince(currentTimeStamp);
            customer.setStrCCredit(Math.random() < 0.1D ? "BC" : "GC");
            customer.setfCCreditLim(50000.00f);
            customer.setfCDiscount(Float.parseFloat(random.randomDecimalString(0, 5000, 4)));
            customer.setfCBalance(-10.0f);
            customer.setfCYTDPayment(10.0f);
            customer.setiCPaymentCnt(1);
            customer.setiCDeliveryCnt(0);

            str = random.randomAString300_500();
            if (str.length() > 255) {
                str = str.substring(255);
            }
            customer.setStrCData(str);
            customer.setStrCDataInit(str);
            customers.add(customer);

            // "INSERT INTO HISTORY (H_C_ID, H_C_D_ID, H_C_W_ID, H_D_ID, H_W_ID, H_DATE, H_AMOUNT, H_DATA, H_INITIAL)
            // VALUES (?, ?, ?, ?, ?, CURRENT TIMESTAMP, 10.00, ?, TRUE)"
            history = new History();
            history.setHId(AppContext.getInstance().getNextHId());
            history.setiHCId(c);
            history.setsHCDId(d);
            history.setsHCWId(w);
            history.setsHDId(d);
            history.setsHWId(w);
            history.settHDate(currentTimeStamp);
            history.setfHAmount(10.00f);
            history.setStrHData(random.randomAString(12, 24));
            history.setbHInitial(true);
            histories.add(history);
        }

        Realm realm = null;
        try {
            realm = ra.getRealmInstance();
            realm.beginTransaction();
            realm.copyToRealm(customers);
            realm.copyToRealm(histories);
            realm.commitTransaction();
        } finally {
            if (null != realm) {
                realm.close();
            }
        }
    }

    public void orderTable(final short w, final short d) throws RealmException {
        if (null == ra) {
            throw new NullPointerException();
        }

        Timestamp o_entry_d = null;
        short o_carrier_id;

        Orders order = null;
        OrderLine ol = null;
        NewOrders no = null;
        List<Orders> orders = new ArrayList<Orders>();
        List<OrderLine> ols = new ArrayList<OrderLine>();
        List<NewOrders> nos = new ArrayList<NewOrders>();

        int[] cid = random.randomIntPerm(Load.CUSTOMER_COUNT_W / Load.DISTRICT_COUNT_W); //1000

        for (int o_id = 1; o_id <= cid.length; o_id++) {
            // "INSERT INTO ORDERS (O_ID, O_D_ID, O_W_ID, O_C_ID, O_ENTRY_D, O_CARRIER_ID,
            // O_OL_CNT, O_ALL_LOCAL, O_CARRIER_ID_INITIAL, O_INITIAL)
            // VALUES (?, ?, ?, ?, ?, ?, ?, 1, ?, TRUE)"

            order = new Orders();
            order.setiOId(o_id);
            order.setsODId(d);
            order.setsOWId(w);
            order.setCompositeKey(order.getCompositeKey(order));
            order.setiOCId(cid[(o_id - 1)]);
            o_entry_d = new Timestamp(System.currentTimeMillis());
            order.settOEntryD(o_entry_d);

            if (o_id <= Load.NEWORDERS_BREAKPOINT) { //700
                o_carrier_id = (short) random.randomInt(1, Load.CARRIER_COUNT); //10
                order.setsOCarrierId(o_carrier_id);
                order.setsOCarIdIni(o_carrier_id);
            }

            int o_ol_cnt = random.randomInt(5, 15);
            order.setsOOlCnt((short) o_ol_cnt);
            order.setsOAllLocal((short) 1);
            order.setbOInitial(true);
            orders.add(order);

            for (int ol_number = 1; ol_number <= o_ol_cnt; ol_number++) {
                // "INSERT INTO ORDERLINE (OL_O_ID, OL_D_ID, OL_W_ID, OL_NUMBER, OL_I_ID,
                // OL_SUPPLY_W_ID, OL_DELIVERY_D, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO,
                // VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE)"
                ol = new OrderLine();

                ol.setiOlOId(o_id);
                ol.setsOlDId(d);
                ol.setsOlWId(w);
                ol.setsOlNumber((short) ol_number);
                ol.setCompositeKey(ol.getCompositeKey(ol));
                ol.setiOlIId(random.randomInt(1, 10));
                ol.setsOlSupplyWId(w);

                if (o_id <= Load.NEWORDERS_BREAKPOINT) { //700
                    ol.settOlDeliveryD(o_entry_d);
                    ol.settOlDlvDIni(o_entry_d);
                    ol.setfOlAmount(0);
                } else {
                    ol.setfOlAmount(Float.parseFloat(random.randomDecimalString(1, 999999, 2)));
                }

                ol.setsOlQuantity((short) 5);
                ol.setStrOlDistInfo(random.randomAString24());
                ol.setbOlInitial(true);
                ols.add(ol);
            }

            if (o_id > Load.NEWORDERS_BREAKPOINT) { //700
                // "INSERT INTO NEWORDERS (NO_O_ID, NO_D_ID, NO_W_ID, NO_INITIAL, NO_LIVE)
                // VALUES (?, ?, ?, TRUE, TRUE)"
                no = new NewOrders();

                no.setiNoOId(o_id);
                no.setsNoDId(d);
                no.setsNoWId(w);
                no.setCompositeKey(no.getCompositeKey(no));
                no.setbNoInitial(true);
                no.setbNoLive(true);
                nos.add(no);
            }
        }

        Realm realm = null;
        try {
            realm = ra.getRealmInstance();
            realm.beginTransaction();
            realm.copyToRealm(orders);
            realm.copyToRealm(ols);
            realm.copyToRealm(nos);
            realm.commitTransaction();
        } finally {
            if (null != realm) {
                realm.close();
            }
        }
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void setThreadCount(int threadCount) {
    }

}
