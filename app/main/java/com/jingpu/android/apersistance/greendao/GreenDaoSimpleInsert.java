package com.jingpu.android.apersistance.greendao;

import android.database.sqlite.SQLiteException;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.greendao.model.C;
import com.jingpu.android.apersistance.greendao.model.CDao;
import com.jingpu.android.apersistance.greendao.model.Customer;
import com.jingpu.android.apersistance.greendao.model.DaoSession;
import com.jingpu.android.apersistance.greendao.model.District;
import com.jingpu.android.apersistance.greendao.model.History;
import com.jingpu.android.apersistance.greendao.model.Item;
import com.jingpu.android.apersistance.greendao.model.NewOrders;
import com.jingpu.android.apersistance.greendao.model.OrderLine;
import com.jingpu.android.apersistance.greendao.model.Orders;
import com.jingpu.android.apersistance.greendao.model.Stock;
import com.jingpu.android.apersistance.greendao.model.Warehouse;
import com.jingpu.android.apersistance.util.TPCCLoad;

import org.dacapo.derby.Load;
import org.dacapo.derby.OERandom;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jing Pu on 2015/12/2.
 */
public class GreenDaoSimpleInsert implements TPCCLoad {
    GreenDaoAgent ga = null;
    short scale = 1;
    long seed = System.currentTimeMillis();
    OERandom random;

    public void setupLoad(short scale) throws SQLiteException { //Session session,
        this.scale = scale;
        setupAgent();

        DaoSession ds = ga.getDaoSession();

        // "DELETE FROM C"
        CDao cDao = ds.getCDao();
        cDao.deleteAll();

        // "INSERT INTO C VALUES(" + loadRandomFactor + ")"
        random = new OERandom(-1, seed);
        int loadRandomFactor = random.randomInt(0, 255);
        C c = new C();
        c.setCLoad(loadRandomFactor);
        cDao.insert(c);

        setRandomGenerator();
    }

    public void setupAgent() throws SQLiteException { //Session session, short scale
        ga = new GreenDaoAgent(AppContext.getInstance());
        if (null == ga) {
            throw new NullPointerException();
        }
    }

    void setRandomGenerator() throws SQLiteException {
        if (null == ga) {
            throw new NullPointerException();
        }

        DaoSession ds = ga.getDaoSession();

        // "SELECT CLOAD FROM C"
        List<C> cList = ds.getCDao().loadAll();

        if (null == cList) {
            throw new NullPointerException();
        }

        C cObj = cList.get(0);

        if (null == cObj) {
            throw new NullPointerException();
        }

        int loadRandomFactor = cObj.getCLoad();
        this.random = new OERandom(loadRandomFactor, this.seed);

    }

    public void populateAllTables() throws Exception {

        itemTable(1, Load.ITEM_COUNT); //10000

        for (short w = 1; w <= this.scale; w = (short)(w + 1)) {
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

    public void itemTable(final int itemStart, final int itemEnd) throws SQLiteException {
        if (null == ga) {
            throw new NullPointerException();
        }

        // "INSERT INTO ITEM(I_ID,I_IM_ID,I_NAME,I_PRICE,I_DATA) VALUES (?, ?, ?, ?, ?)"
        List<Item> items = new ArrayList<Item>();
        Item item = null;
        for (int i = itemStart; i <= itemEnd; i++) {
            item = new Item();
            item.setId((long)i); //IId
            item.setIImId(random.randomInt(1, 10000));
            item.setIName(random.randomAString14_24());
            item.setIPrice(Float.parseFloat(random.randomDecimalString(100, 9999, 2)));
            item.setIData(random.randomData());
            items.add(item);
        }

        DaoSession ds = ga.getDaoSession();
        ds.getItemDao().insertInTx(items);
    }

    public void warehouseTable(short w) throws SQLiteException {
        if (null == ga) {
            throw new NullPointerException();
        }

        // "INSERT INTO WAREHOUSE VALUES (?, ?, ?, ?, ?, ?, ?, ?, 300000.00)"
        Warehouse wh = new Warehouse();
        wh.setId((long)w); // WId
        wh.setWName(this.random.randomAString(6, 10));
        wh.setWStreet1(this.random.randomAString10_20());
        wh.setWStreet2(this.random.randomAString10_20());
        wh.setWCity(this.random.randomAString10_20());
        wh.setWState(this.random.randomState());
        wh.setWZip(this.random.randomZIP());
        wh.setWTax(Float.parseFloat(this.random.randomDecimalString(0, 2000, 4)));
        wh.setWYtd(300000.00f);

        DaoSession ds = ga.getDaoSession();
        ds.getWarehouseDao().insert(wh);
    }

    public void stockTable(final int itemStart, final int itemEnd, final short w) throws SQLiteException {
        if (null == ga) {
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
            stock.setSIId(i);
            stock.setSWId(w);
            stock.setSCompo(stock.getSCompo(stock));
            stock.setSQuantity(quantity);
            stock.setSDist01(random.randomAString24());
            stock.setSDist02(random.randomAString24());
            stock.setSDist03(random.randomAString24());
            stock.setSDist04(random.randomAString24());
            stock.setSDist05(random.randomAString24());
            stock.setSDist06(random.randomAString24());
            stock.setSDist07(random.randomAString24());
            stock.setSDist08(random.randomAString24());
            stock.setSDist09(random.randomAString24());
            stock.setSDist10(random.randomAString24());
            stock.setSOrderCnt(0);
            stock.setSRemoteCnt(0);
            stock.setSYtd(0);
            stock.setSData(random.randomData());
            stock.setSQuantityInitial(quantity);
            stocks.add(stock);
        }

        DaoSession ds = ga.getDaoSession();
        ds.getStockDao().insertInTx(stocks);
    }

    public void districtTable(short w, short d) throws SQLiteException {
        if (null == ga) {
            throw new NullPointerException();
        }

        //"INSERT INTO DISTRICT (D_ID, D_W_ID, D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP,
        // D_TAX, D_YTD, D_NEXT_O_ID)  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, 30000.00, 3001)");
        District district = new District();
        district.setDId(d);
        district.setDWId(w);
        district.setDCompo(district.getDCompo(district));
        district.setDName(this.random.randomAString(6, 10));
        district.setDStreet1(random.randomAString10_20());
        district.setDStreet2(random.randomAString10_20());
        district.setDCity(this.random.randomAString10_20());
        district.setDState(this.random.randomState());
        district.setDZip(this.random.randomZIP());
        district.setDTax(Float.parseFloat(this.random.randomDecimalString(0, 2000, 4)));
        district.setDYtd(30000.00f);
        district.setDNextOId(3001);

        DaoSession ds = ga.getDaoSession();
        ds.getDistrictDao().insert(district);
    }

    public void customerTable(final short w, final short d) throws SQLiteException {
        if (null == ga) {
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
            customer.setCId(c);
            customer.setCDId(d);
            customer.setCWId(w);
            customer.setCCompo(customer.getCCompo(customer));
            customer.setCFirst(random.randomAString8_16());
            customer.setCMiddle("OE");
            customer.setCLast(random.randomCLastPopulate(c));
            customer.setCStreet1(random.randomAString10_20());
            customer.setCStreet2(random.randomAString10_20());
            customer.setCCity(random.randomAString10_20());
            customer.setCState(random.randomState());
            customer.setCZip(random.randomZIP());
            customer.setCPhone(random.randomNString(16, 16));
            customer.setCSince(currentTimeStamp);
            customer.setCCredit(Math.random() < 0.1D ? "BC" : "GC");
            customer.setCCreditLim(50000.00f);
            customer.setCDiscount(Float.parseFloat(random.randomDecimalString(0, 5000, 4)));
            customer.setCBalance(-10.0f);
            customer.setCYtdPayment(10.0f);
            customer.setCPaymentCnt(1);
            customer.setCDeliveryCnt(0);

            str = random.randomAString300_500();
            if (str.length() > 255) {
                str = str.substring(255);
            }
            customer.setCData(str);
            customer.setCDataInitial(str);
            customers.add(customer);

            // "INSERT INTO HISTORY (H_C_ID, H_C_D_ID, H_C_W_ID, H_D_ID, H_W_ID, H_DATE, H_AMOUNT, H_DATA, H_INITIAL)
            // VALUES (?, ?, ?, ?, ?, CURRENT TIMESTAMP, 10.00, ?, TRUE)"
            history = new History();
            history.setHCId(c);
            history.setHCDId(d);
            history.setHCWId(w);
            history.setHDId(d);
            history.setHWId(w);
            history.setHDate(currentTimeStamp);
            history.setHAmount(10.00f);
            history.setHData(random.randomAString(12, 24));
            history.setHInitial(true);
            histories.add(history);
        }

        DaoSession ds = ga.getDaoSession();
        ds.getCustomerDao().insertInTx(customers);
        ds.getHistoryDao().insertInTx(histories);
    }

    public void orderTable(final short w, final short d) throws SQLiteException {
        if (null == ga) {
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
            order.setOId(o_id);
            order.setODId(d);
            order.setOWId(w);
            order.setOCompo(order.getOCompo(order));
            order.setOCId(cid[(o_id - 1)]);
            o_entry_d = new Timestamp(System.currentTimeMillis());
            order.setOEntryD(o_entry_d.toString());

            if (o_id <= Load.NEWORDERS_BREAKPOINT) { //700
                o_carrier_id = (short) random.randomInt(1, Load.CARRIER_COUNT); //10
                order.setOCarrierId(o_carrier_id);
                order.setOCarrierIdInitial(o_carrier_id);
            }

            int o_ol_cnt = random.randomInt(5, 15);
            order.setOOlCnt((short) o_ol_cnt);
            order.setOAllLocal((short) 1);
            order.setOInitial(true);
            orders.add(order);

            for (int ol_number = 1; ol_number <= o_ol_cnt; ol_number++) {
                // "INSERT INTO ORDERLINE (OL_O_ID, OL_D_ID, OL_W_ID, OL_NUMBER, OL_I_ID,
                // OL_SUPPLY_W_ID, OL_DELIVERY_D, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO,
                // OL_DELIVERY_D_INITIAL, OL_INITIAL)
                // VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE)"
                ol = new OrderLine();

                ol.setOlOId(o_id);
                ol.setOlDId(d);
                ol.setOlWId(w);
                ol.setOlNumber((short) ol_number);
                ol.setOlCompo(ol.getOlCompo(ol));
                ol.setOlIId(random.randomInt(1, 10));
                ol.setOlSupplyWId(w);

                if (o_id <= Load.NEWORDERS_BREAKPOINT) { //700
                    ol.setOlDeliveryD(o_entry_d);
                    ol.setOlDeliveryDInitial(o_entry_d);
                    ol.setOlAmount(0);
                } else {
                    ol.setOlAmount(Float.parseFloat(random.randomDecimalString(1, 999999, 2)));
                }

                ol.setOlQuantity((short) 5);
                ol.setOlDistInfo(random.randomAString24());
                ol.setOlInitial(true);
                ols.add(ol);
            }

            if (o_id > Load.NEWORDERS_BREAKPOINT) { //700
                // "INSERT INTO NEWORDERS (NO_O_ID, NO_D_ID, NO_W_ID, NO_INITIAL, NO_LIVE)
                // VALUES (?, ?, ?, TRUE, TRUE)"
                no = new NewOrders();

                no.setNoOId(o_id);
                no.setNoDId(d);
                no.setNoWId(w);
                no.setNoCompo(no.getNoCompo(no));
                no.setNoInitial(true);
                no.setNoLive(true);
                nos.add(no);
            }
        }

        DaoSession ds = ga.getDaoSession();

        ds.getOrdersDao().insertInTx(orders);
        ds.getOrderLineDao().insertInTx(ols);
        ds.getNewOrdersDao().insertInTx(nos);
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void setThreadCount(int threadCount) {
    }

}
