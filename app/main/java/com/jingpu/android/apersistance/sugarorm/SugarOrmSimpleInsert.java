package com.jingpu.android.apersistance.sugarorm;

import android.database.sqlite.SQLiteException;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.sugarorm.model.C;
import com.jingpu.android.apersistance.sugarorm.model.Customer;
import com.jingpu.android.apersistance.sugarorm.model.District;
import com.jingpu.android.apersistance.sugarorm.model.History;
import com.jingpu.android.apersistance.sugarorm.model.Item;
import com.jingpu.android.apersistance.sugarorm.model.NewOrders;
import com.jingpu.android.apersistance.sugarorm.model.OrderLine;
import com.jingpu.android.apersistance.sugarorm.model.Orders;
import com.jingpu.android.apersistance.sugarorm.model.Stock;
import com.jingpu.android.apersistance.sugarorm.model.Warehouse;
import com.jingpu.android.apersistance.util.TPCCLoad;

import org.dacapo.derby.Load;
import org.dacapo.derby.OERandom;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jing Pu on 2016/1/18.
 */
public class SugarOrmSimpleInsert implements TPCCLoad {
    SugarOrmAgent soa = null;
    short scale = 1;
    long seed = System.currentTimeMillis();
    OERandom random;

    public void setupLoad(short scale) throws SQLiteException { //Session session,
        this.scale = scale;
        setupAgent();

        // no need to create C table
        // "INSERT INTO C VALUES(" + loadRandomFactor + ")"
        random = new OERandom(-1, seed);
        int loadRandomFactor = random.randomInt(0, 255);

        C c = new C();
        c.setCLoad(loadRandomFactor);
        c.save();

        setRandomGenerator();
    }

    public void setupAgent() throws SQLiteException { //Session session, short scale
        soa = new SugarOrmAgent(AppContext.getInstance());

        if (null == soa) {
            throw new NullPointerException();
        }
    }

    void setRandomGenerator() throws SQLiteException {

        // "SELECT CLOAD FROM C"
        C c = C.first(C.class);

        if (null == c) {
            throw new NullPointerException();
        }

        int loadRandomFactor = c.getCLoad();
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

        // "INSERT INTO ITEM(I_ID,I_IM_ID,I_NAME,I_PRICE,I_DATA) VALUES (?, ?, ?, ?, ?)"
        List<Item> iList = new ArrayList<Item>();
        Item item = null;
        for (int i = itemStart; i <= itemEnd; i++) {
            item = new Item();
            item.setId((long)i); // IId
            item.setIImId(random.randomInt(1, 10000));
            item.setIName(random.randomAString14_24());
            item.setIPrice(Float.parseFloat(random.randomDecimalString(100, 9999, 2)));
            item.setIData(random.randomData());
            iList.add(item);
        }
        Item.saveInTx(iList);
    }

    public void warehouseTable(short w) throws SQLiteException {

        // "INSERT INTO WAREHOUSE VALUES (?, ?, ?, ?, ?, ?, ?, ?, 300000.00)"
        Warehouse wh = new Warehouse();
        wh.setId((long)w); //WId
        wh.setWName(this.random.randomAString(6, 10));
        wh.setWStreet1(this.random.randomAString10_20());
        wh.setWStreet2(this.random.randomAString10_20());
        wh.setWCity(this.random.randomAString10_20());
        wh.setWState(this.random.randomState());
        wh.setWZip(this.random.randomZIP());
        wh.setWTax(Float.parseFloat(this.random.randomDecimalString(0, 2000, 4)));
        wh.setWYtd(300000.00f);
        wh.save();
    }

    public void stockTable(final int itemStart, final int itemEnd, final short w) throws SQLiteException {

        // "INSERT INTO STOCK (S_I_ID, S_W_ID, S_QUANTITY,S_DIST_01, S_DIST_02, S_DIST_03,S_DIST_04,S_DIST_05,
        // S_DIST_06,S_DIST_07,S_DIST_08,S_DIST_09,S_DIST_10,
        // S_ORDER_CNT, S_REMOTE_CNT, S_YTD, S_DATA, S_QUANTITY_INITIAL ) VALUES
        // (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0, 0, ?, ?)"
        List<Stock> skList = new ArrayList<Stock>();
        int quantity = 0;
        Stock stock = null;
        for (int i = itemStart; i <= itemEnd; i++) {
            quantity = random.randomInt(10, 100);
            stock = new Stock();
            stock.setSIId(i);
            stock.setSWId(w);
            stock.setCompositeKey(stock.getCompositeKey(stock));
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
            stock.setSYTD(0);
            stock.setSData(random.randomData());
            stock.setSQuantity(quantity);
            skList.add(stock);
        }
        Stock.saveInTx(skList);
    }

    public void districtTable(short w, short d) throws SQLiteException {

        //"INSERT INTO DISTRICT (D_ID, D_W_ID, D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP,
        // D_TAX, D_YTD, D_NEXT_O_ID)  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, 30000.00, 3001)");
        District district = new District();
        district.setDId(d);
        district.setDWId(w);
        district.setCompositeKey(district.getCompositeKey(district));
        district.setDName(this.random.randomAString(6, 10));
        district.setDStreet1(random.randomAString10_20());
        district.setDStreet2(random.randomAString10_20());
        district.setDCity(this.random.randomAString10_20());
        district.setDState(this.random.randomState());
        district.setDZip(this.random.randomZIP());
        district.setDTax(Float.parseFloat(this.random.randomDecimalString(0, 2000, 4)));
        district.setDYTD(30000.00f);
        district.setDNxtOId(3001);
        district.save();
    }

    public void customerTable(final short w, final short d) throws SQLiteException {
        String str = null;
        Timestamp currentTimeStamp = null;
        Customer customer = null;
        History history = null;
        List<Customer> cusList = new ArrayList<Customer>();
        List<History> hisList = new ArrayList<History>();

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
            customer.setCompositeKey(customer.getCompositeKey(customer));
            customer.setCFst(random.randomAString8_16());
            customer.setCMid("OE");
            customer.setCLst(random.randomCLastPopulate(c));
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
            customer.setCYTDPayment(10.0f);
            customer.setCPaymentCnt(1);
            customer.setCDeliveryCnt(0);

            str = random.randomAString300_500();
            if (str.length() > 255) {
                str = str.substring(255);
            }
            customer.setCData(str);
            customer.setCDataInit(str);
            cusList.add(customer);

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
            hisList.add(history);
        }

        Customer.saveInTx(cusList);
        History.saveInTx(hisList);
    }

    public void orderTable(final short w, final short d) throws SQLiteException {
        Timestamp o_entry_d = null;
        short o_carrier_id;

        Orders order = null;
        OrderLine ol = null;
        NewOrders no = null;
        List<Orders> oList = new ArrayList<Orders>();
        List<OrderLine> olList = new ArrayList<OrderLine>();
        List<NewOrders> noList = new ArrayList<NewOrders>();

        int[] cid = random.randomIntPerm(Load.CUSTOMER_COUNT_W / Load.DISTRICT_COUNT_W); //1000


        for (int o_id = 1; o_id <= cid.length; o_id++) {
            // "INSERT INTO ORDERS (O_ID, O_D_ID, O_W_ID, O_C_ID, O_ENTRY_D, O_CARRIER_ID,
            // O_OL_CNT, O_ALL_LOCAL, O_CARRIER_ID_INITIAL, O_INITIAL)
            // VALUES (?, ?, ?, ?, ?, ?, ?, 1, ?, TRUE)"

            order = new Orders();
            order.setOId(o_id);
            order.setODId(d);
            order.setOWId(w);
            order.setCompositeKey(order.getCompositeKey(order));
            order.setOCId(cid[(o_id - 1)]);
            o_entry_d = new Timestamp(System.currentTimeMillis());
            order.setOEntryD(o_entry_d);

            if (o_id <= Load.NEWORDERS_BREAKPOINT) { //700
                o_carrier_id = (short) random.randomInt(1, Load.CARRIER_COUNT); //10
                order.setOCarrierId(o_carrier_id);
                order.setOCarIdIni(o_carrier_id);
            }

            int o_ol_cnt = random.randomInt(5, 15);
            order.setOOlCnt((short) o_ol_cnt);
            order.setOAllLocal((short) 1);
            order.setOInitial(true);
            oList.add(order);

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
                ol.setCompositeKey(ol.getCompositeKey(ol));
                ol.setOlIId(random.randomInt(1, 10));
                ol.setOlSupplyWId(w);

                if (o_id <= Load.NEWORDERS_BREAKPOINT) { //700
                    ol.setOlDeliveryD(o_entry_d);
                    ol.setOlDlvDIni(o_entry_d);
                    ol.setOlAmount(0);
                } else {
                    ol.setOlAmount(Float.parseFloat(random.randomDecimalString(1, 999999, 2)));
                }

                ol.setOlQuantity((short) 5);
                ol.setOlDistInfo(random.randomAString24());
                ol.setOlInitial(true);
                olList.add(ol);
            }

            if (o_id > Load.NEWORDERS_BREAKPOINT) { //700
                // "INSERT INTO NEWORDERS (NO_O_ID, NO_D_ID, NO_W_ID, NO_INITIAL, NO_LIVE)
                // VALUES (?, ?, ?, TRUE, TRUE)"
                no = new NewOrders();

                no.setNoOId(o_id);
                no.setNoDId(d);
                no.setNoWId(w);
                no.setCompositeKey(no.getCompositeKey(no));
                no.setNoInitial(true);
                no.setNoLive(true);
                noList.add(no);
            }
        }

        Orders.saveInTx(oList);
        OrderLine.saveInTx(olList);
        NewOrders.saveInTx(noList);
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void setThreadCount(int threadCount) {
    }
}
