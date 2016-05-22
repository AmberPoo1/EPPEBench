package com.jingpu.android.apersistance.greendao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

/**
 *  Generates entities and DAOs for greenDAO ORM, the project apersistance
 *
 *  Run it as a Java application (not Android).
 * Created by Jing Pu on 2015/9/16.
 */
public class ApTestGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.jingpu.android.apersistance.greendao.model");

        addC(schema);
        addWarehouse(schema);
        addDistrict(schema);
        addCustomer(schema);
        addHistory(schema);
        addNewOrders(schema);
        addOrders(schema);
        addOrderLine(schema);
        addItem(schema);
        addStock(schema);
        addDeliveryRequest(schema);
        addDeliveryOrders(schema);
        addCategory(schema);
        //addCItem(schema);

        String PROJECT_DIR = System.getProperty("user.dir").replace("\\", "/");
        //new DaoGenerator().generateAll(schema, PROJECT_DIR + "/app/src/main/java/com/jingpu/android/apersistance/greendao1");
        new DaoGenerator().generateAll(schema, PROJECT_DIR + "/app/greendaomodel");
        //new DaoGenerator().generateAll(schema, "../app/src/main/java/com/jingpu/android/apersistance/greendao1");
    }

    private static void addCategory(Schema schema) {
        Entity category = schema.addEntity("Category");
        category.setTableName("CATEGORY");
        // primary key "iCid"
        category.addIdProperty();

        //category.addLongProperty("iCid").notNull().unique();
        category.addStringProperty("strCTitle").notNull();
        category.addIntProperty("iCPages").notNull();
        category.addIntProperty("iCSubCats").notNull();
        category.addIntProperty("iCFiles").notNull();

        addCItem(schema, category);
    }

    private static void addCItem(Schema schema, Entity category) {
        Entity cItem = schema.addEntity("CItem");
        Property cIdProperty = cItem.addLongProperty("cId").getProperty();
        cItem.addToOne(category, cIdProperty);

        cItem.setTableName("CITEM");
        // Jing Pu 2016/03/16 modified v2.0
        // primary key "iIId"
        cItem.addIdProperty();

        //cItem.addIntProperty("iIId").notNull().unique();
        cItem.addIntProperty("iImId").notNull();
        cItem.addStringProperty("iName").notNull();
        cItem.addFloatProperty("iPrice").notNull();
        cItem.addStringProperty("iData").notNull();
    }

    private static void addC(Schema schema){
        // "CREATE TABLE C(CLOAD INT);"
        Entity c = schema.addEntity("C");
        c.setTableName("C");
        c.addIdProperty();
        c.addIntProperty("cLoad");
    }

    private static void addWarehouse(Schema schema) {
        /*
        CREATE TABLE WAREHOUSE (W_ID SMALLINT NOT NULL,W_NAME VARCHAR(10) NOT NULL,W_STREET_1 VARCHAR(20) NOT NULL,W_STREET_2 VARCHAR(20) NOT NULL,
        W_CITY VARCHAR(20) NOT NULL,W_STATE CHAR(2) NOT NULL,W_ZIP CHAR(9) NOT NULL,W_TAX DECIMAL(4,4) NOT NULL,W_YTD DECIMAL(12,2) NOT NULL);

        ALTER TABLE WAREHOUSE ADD CONSTRAINT WAREHOUSE_PK PRIMARY KEY (W_ID);
        */

        Entity warehouse = schema.addEntity("Warehouse");
        warehouse.setTableName("WAREHOUSE");
        // primary key:"wId"
        warehouse.addIdProperty(); //wId

        //warehouse.addShortProperty("wId").unique().notNull();
        warehouse.addStringProperty("wName").notNull();
        warehouse.addStringProperty("wStreet1").notNull();
        warehouse.addStringProperty("wStreet2").notNull();
        warehouse.addStringProperty("wCity").notNull();
        warehouse.addStringProperty("wState").notNull();
        warehouse.addStringProperty("wZip").notNull();
        warehouse.addFloatProperty("wTax").notNull();
        warehouse.addFloatProperty("wYtd").notNull();
    }

    private static void addDistrict(Schema schema) {
        /*
        CREATE TABLE DISTRICT(D_ID SMALLINT NOT NULL,D_W_ID SMALLINT NOT NULL,D_NAME VARCHAR(10) NOT NULL,D_STREET_1 VARCHAR(20) NOT NULL,
        D_STREET_2 VARCHAR(20) NOT NULL,D_CITY VARCHAR(20) NOT NULL,D_STATE CHAR(2) NOT NULL,D_ZIP CHAR(9) NOT NULL,
        D_TAX DECIMAL (4,4) NOT NULL,D_YTD DECIMAL (12,2) NOT NULL,
        D_NEXT_O_ID INTEGER NOT NULL);

        ALTER TABLE DISTRICT ADD CONSTRAINT DISTRICT_PK PRIMARY KEY (D_W_ID, D_ID);
        ALTER TABLE DISTRICT ADD CONSTRAINT D_W_FK FOREIGN KEY (D_W_ID) REFERENCES WAREHOUSE;
         */
        Entity district = schema.addEntity("District");
        district.setTableName("DISTRICT");
        //district.addIdProperty();
        district.addStringProperty("dCompo").primaryKey();
        district.addShortProperty("dId").notNull(); //Property dId =  .getProperty();
        district.addShortProperty("dWId").notNull(); //Property dWId =  .getProperty();
        district.addStringProperty("dName").notNull();
        district.addStringProperty("dStreet1").notNull();
        district.addStringProperty("dStreet2").notNull();
        district.addStringProperty("dCity").notNull();
        district.addStringProperty("dState").notNull();
        district.addStringProperty("dZip").notNull();
        district.addFloatProperty("dTax").notNull();
        district.addFloatProperty("dYtd").notNull();
        district.addIntProperty("dNextOId").notNull();
        /*
        // primary key
        Index indexUnique = new Index();
        indexUnique.addProperty(dId);
        indexUnique.addProperty(dWId);
        indexUnique.makeUnique();
        district.addIndex(indexUnique);
        */
    }

    private static void addCustomer(Schema schema) {
	    /*
        CREATE TABLE CUSTOMER(C_ID INTEGER NOT NULL,C_D_ID SMALLINT NOT NULL,C_W_ID SMALLINT NOT NULL,
        C_FIRST VARCHAR(16) NOT NULL,C_MIDDLE CHAR(2) NOT NULL,C_LAST VARCHAR(16) NOT NULL,
        C_STREET_1 VARCHAR(20) NOT NULL,C_STREET_2 VARCHAR(20) NOT NULL,C_CITY VARCHAR(20) NOT NULL,
        C_STATE CHAR(2) NOT NULL,C_ZIP CHAR(9) NOT NULL,C_PHONE CHAR(16) NOT NULL,
        C_SINCE TIMESTAMP NOT NULL,C_CREDIT CHAR(2) NOT NULL,C_CREDIT_LIM DECIMAL(12,2) NOT NULL,
        C_DISCOUNT DECIMAL(4,4) NOT NULL,C_BALANCE DECIMAL(12,2) NOT NULL,C_YTD_PAYMENT DECIMAL(12,2) NOT NULL,
        C_PAYMENT_CNT INTEGER NOT NULL,C_DELIVERY_CNT INTEGER NOT NULL,C_DATA VARCHAR(500) NOT NULL,
        C_DATA_INITIAL VARCHAR(500) NOT NULL);

        ALTER TABLE CUSTOMER ADD CONSTRAINT CUSTOMER_PK PRIMARY KEY(C_W_ID, C_D_ID, C_ID);

        ALTER TABLE CUSTOMER ADD CONSTRAINT C_D_FK_DISTRICT FOREIGN KEY (C_W_ID,C_D_ID) REFERENCES DISTRICT;

        CREATE INDEX CUSTOMER_LAST_NAME ON CUSTOMER(C_W_ID, C_D_ID, C_LAST);
	    */
        Entity customer = schema.addEntity("Customer");
        customer.setTableName("CUSTOMER");
        //customer.addIdProperty();
        customer.addStringProperty("cCompo").primaryKey();
        customer.addIntProperty("cId").notNull(); // Property cId = .getProperty();
        customer.addShortProperty("cDId").notNull(); //Property cDId = .getProperty();
        customer.addShortProperty("cWId").notNull(); //Property cWId = .getProperty();
        customer.addStringProperty("cFirst").notNull();
        customer.addStringProperty("cMiddle").notNull();
        customer.addStringProperty("cLast").notNull();
        customer.addStringProperty("cStreet1").notNull();
        customer.addStringProperty("cStreet2").notNull();
        customer.addStringProperty("cCity").notNull();
        customer.addStringProperty("cState").notNull();
        customer.addStringProperty("cZip").notNull();
        customer.addStringProperty("cPhone").notNull();
        customer.addDateProperty("cSince").notNull(); // timestamp
        customer.addStringProperty("cCredit").notNull();
        customer.addFloatProperty("cCreditLim").notNull();
        customer.addFloatProperty("cDiscount").notNull();
        customer.addFloatProperty("cBalance").notNull();
        customer.addFloatProperty("cYtdPayment").notNull();
        customer.addIntProperty("cPaymentCnt").notNull();
        customer.addIntProperty("cDeliveryCnt").notNull();
        customer.addStringProperty("cData").notNull();
        customer.addStringProperty("cDataInitial").notNull();

        /*
        // primary key
        Index indexUnique = new Index();
        indexUnique.addProperty(cId);
        indexUnique.addProperty(cDId);
        indexUnique.addProperty(cWId);
        indexUnique.makeUnique();
        customer.addIndex(indexUnique);
        */
    }

    private static void addHistory(Schema schema) {
	/*
	CREATE TABLE HISTORY(H_C_ID INTEGER NOT NULL, H_C_D_ID SMALLINT NOT NULL,H_C_W_ID SMALLINT NOT NULL,H_D_ID SMALLINT NOT NULL,
	H_W_ID SMALLINT NOT NULL,H_DATE TIMESTAMP NOT NULL,H_AMOUNT DECIMAL(6,2) NOT NULL,H_DATA VARCHAR(24) NOT NULL,
	H_INITIAL BOOLEAN);

	ALTER TABLE HISTORY ADD CONSTRAINT H_C_FK FOREIGN KEY (H_C_W_ID, H_C_D_ID, H_C_ID) REFERENCES CUSTOMER;
	ALTER TABLE HISTORY ADD CONSTRAINT H_D_FK FOREIGN KEY (H_W_ID, H_D_ID) REFERENCES DISTRICT;
	*/
        Entity history = schema.addEntity("History");
        history.setTableName("HISTORY");
        history.addIdProperty();
        history.addIntProperty("hCId").notNull();
        history.addShortProperty("hCDId").notNull();
        history.addShortProperty("hCWId").notNull();
        history.addShortProperty("hDId").notNull();
        history.addShortProperty("hWId").notNull();
        history.addDateProperty("hDate").notNull(); //timestamp
        history.addFloatProperty("hAmount").notNull();
        history.addStringProperty("hData").notNull();
        history.addBooleanProperty("hInitial");
    }

    private static void addNewOrders(Schema schema) {
        /*
        CREATE TABLE NEWORDERS(NO_O_ID INTEGER NOT NULL, NO_D_ID SMALLINT NOT NULL, NO_W_ID SMALLINT NOT NULL,NO_INITIAL BOOLEAN, NO_LIVE BOOLEAN);

        ALTER TABLE NEWORDERS ADD CONSTRAINT NEWORDERS_PK PRIMARY KEY(NO_W_ID, NO_D_ID, NO_O_ID);
        ALTER TABLE NEWORDERS ADD CONSTRAINT NO_O_FK FOREIGN KEY (NO_W_ID, NO_D_ID, NO_O_ID) REFERENCES ORDERS;
        */
        Entity newOrders = schema.addEntity("NewOrders");
        newOrders.setTableName("NEWORDERS");
        //newOrders.addIdProperty();
        newOrders.addStringProperty("noCompo").primaryKey();
        newOrders.addIntProperty("noOId").notNull(); //Property noOId = .getProperty();
        newOrders.addShortProperty("noDId").notNull(); //Property noDId = .getProperty();
        newOrders.addShortProperty("noWId").notNull(); //Property noWId = .getProperty();
        newOrders.addBooleanProperty("noInitial");
        newOrders.addBooleanProperty("noLive");

        /*
        // primary key
        Index indexUnique = new Index();
        indexUnique.addProperty(noOId);
        indexUnique.addProperty(noDId);
        indexUnique.addProperty(noWId);
        indexUnique.makeUnique();
        newOrders.addIndex(indexUnique);
        */
    }

    private static void addOrders(Schema schema) {
        /*
        CREATE TABLE ORDERS
        (O_ID INTEGER NOT NULL,O_D_ID SMALLINT NOT NULL,O_W_ID SMALLINT NOT NULL,
        O_C_ID INTEGER NOT NULL, O_ENTRY_D TIMESTAMP NOT NULL,O_CARRIER_ID SMALLINT,
        O_OL_CNT SMALLINT NOT NULL,O_ALL_LOCAL SMALLINT NOT NULL,O_CARRIER_ID_INITIAL SMALLINT,
        O_INITIAL BOOLEAN);

        ALTER TABLE ORDERS ADD CONSTRAINT ORDERS_PK PRIMARY KEY(O_W_ID, O_D_ID, O_ID);
        ALTER TABLE ORDERS ADD CONSTRAINT O_C_FK FOREIGN KEY (O_W_ID, O_D_ID, O_C_ID) REFERENCES CUSTOMER;
        */

        Entity orders = schema.addEntity("Orders");
        orders.setTableName("ORDERS");
        //orders.addIdProperty();
        orders.addStringProperty("oCompo").primaryKey();
        orders.addIntProperty("oId").notNull(); // Property oId = .getProperty();
        orders.addShortProperty("oDId").notNull(); //Property oDId = .getProperty();
        orders.addShortProperty("oWId").notNull(); //Property oWId = .getProperty();
        orders.addIntProperty("oCId").notNull();
        orders.addDateProperty("oEntryD").notNull(); // timestamp
        orders.addShortProperty("oCarrierId");
        orders.addShortProperty("oOlCnt").notNull();
        orders.addShortProperty("oAllLocal").notNull();
        orders.addShortProperty("oCarrierIdInitial");
        orders.addBooleanProperty("oInitial");

        /*
        // primary key
        Index indexUnique = new Index();
        indexUnique.addProperty(oId);
        indexUnique.addProperty(oDId);
        indexUnique.addProperty(oWId);
        indexUnique.makeUnique();
        orders.addIndex(indexUnique);
        */
    }

    private static void addOrderLine(Schema schema) {
        /*
        CREATE TABLE ORDERLINE(OL_O_ID INTEGER NOT NULL,OL_D_ID SMALLINT NOT NULL,OL_W_ID SMALLINT NOT NULL,OL_NUMBER SMALLINT NOT NULL,
        OL_I_ID INTEGER NOT NULL,OL_SUPPLY_W_ID SMALLINT NOT NULL, OL_DELIVERY_D TIMESTAMP,OL_QUANTITY SMALLINT NOT NULL,
        OL_AMOUNT DECIMAL(6,2) NOT NULL,OL_DIST_INFO CHAR(24) NOT NULL,OL_DELIVERY_D_INITIAL TIMESTAMP,OL_INITIAL BOOLEAN);

        ALTER TABLE ORDERLINE ADD CONSTRAINT ORDERLINE_PK PRIMARY KEY(OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER);
        ALTER TABLE ORDERLINE ADD CONSTRAINT OL_O_FK FOREIGN KEY (OL_W_ID, OL_D_ID, OL_O_ID) REFERENCES ORDERS;
        ALTER TABLE ORDERLINE ADD CONSTRAINT OL_S_FK FOREIGN KEY (OL_SUPPLY_W_ID, OL_I_ID) REFERENCES STOCK;
        */
        Entity orderLine = schema.addEntity("OrderLine");
        orderLine.setTableName("ORDERLINE");
        //orderLine.addIdProperty();
        orderLine.addStringProperty("olCompo").primaryKey();
        orderLine.addIntProperty("olOId").notNull(); //Property olOId = .getProperty();
        orderLine.addShortProperty("olDId").notNull(); //Property olDId = .getProperty();
        orderLine.addShortProperty("olWId").notNull(); //Property olWId = .getProperty();
        orderLine.addShortProperty("olNumber").notNull(); //Property olNumber = .getProperty();
        orderLine.addIntProperty("olIId").notNull();
        orderLine.addShortProperty("olSupplyWId").notNull();
        orderLine.addDateProperty("olDeliveryD"); // timestamp
        orderLine.addShortProperty("olQuantity").notNull();
        orderLine.addFloatProperty("olAmount").notNull();
        orderLine.addStringProperty("olDistInfo").notNull();
        orderLine.addDateProperty("olDeliveryDInitial"); // timestamp
        orderLine.addBooleanProperty("olInitial");

        /*
        // primary key
        Index indexUnique = new Index();
        indexUnique.addProperty(olOId);
        indexUnique.addProperty(olDId);
        indexUnique.addProperty(olWId);
        indexUnique.addProperty(olNumber);
        indexUnique.makeUnique();
        orderLine.addIndex(indexUnique);
        */
    }

    private static void addItem(Schema schema) {
        /*
        CREATE TABLE ITEM(I_ID INTEGER NOT NULL,I_IM_ID INTEGER NOT NULL,I_NAME VARCHAR(24) NOT NULL,
        I_PRICE DECIMAL(5,2) NOT NULL,I_DATA VARCHAR(50) NOT NULL);

        ALTER TABLE ITEM ADD CONSTRAINT ITEM_PK PRIMARY KEY (I_ID);
         */

        Entity item = schema.addEntity("Item");
        item.setTableName("ITEM");
        // primary key: "iId"
        item.addIdProperty();

        //item.addIntProperty("iId").notNull().unique();
        item.addIntProperty("iImId").notNull();
        item.addStringProperty("iName").notNull();
        item.addFloatProperty("iPrice").notNull();
        item.addStringProperty("iData").notNull();
    }

    private static void  addStock(Schema schema) {
        /*
        CREATE TABLE STOCK(S_I_ID INTEGER NOT NULL,S_W_ID SMALLINT NOT NULL,S_QUANTITY INTEGER NOT NULL,
        S_DIST_01 CHAR(24) NOT NULL,S_DIST_02 CHAR(24) NOT NULL,S_DIST_03 CHAR(24) NOT NULL,
        S_DIST_04 CHAR(24) NOT NULL,S_DIST_05 CHAR(24) NOT NULL,S_DIST_06 CHAR(24) NOT NULL,
        S_DIST_07 CHAR(24) NOT NULL,S_DIST_08 CHAR(24) NOT NULL,S_DIST_09 CHAR(24) NOT NULL,
        S_DIST_10 CHAR(24) NOT NULL,S_YTD DECIMAL(8) NOT NULL,S_ORDER_CNT INTEGER NOT NULL,
        S_REMOTE_CNT INTEGER NOT NULL,S_DATA VARCHAR(50) NOT NULL,S_QUANTITY_INITIAL INTEGER NOT NULL);

        ALTER TABLE STOCK ADD CONSTRAINT STOCK_PK PRIMARY KEY (S_W_ID, S_I_ID);
        ALTER TABLE STOCK ADD CONSTRAINT S_W_FK FOREIGN KEY (S_W_ID) REFERENCES WAREHOUSE;
        ALTER TABLE STOCK ADD CONSTRAINT S_I_FK FOREIGN KEY (S_I_ID) REFERENCES ITEM;
        */
        Entity stock = schema.addEntity("Stock");
        stock.setTableName("STOCK");
        //stock.addIdProperty();
        stock.addStringProperty("sCompo").primaryKey();
        stock.addIntProperty("sIId").notNull(); //Property sIId = .getProperty();
        stock.addShortProperty("sWId").notNull(); //Property sWId = .getProperty();
        stock.addIntProperty("sQuantity").notNull();
        stock.addStringProperty("sDist01").notNull();
        stock.addStringProperty("sDist02").notNull();
        stock.addStringProperty("sDist03").notNull();
        stock.addStringProperty("sDist04").notNull();
        stock.addStringProperty("sDist05").notNull();
        stock.addStringProperty("sDist06").notNull();
        stock.addStringProperty("sDist07").notNull();
        stock.addStringProperty("sDist08").notNull();
        stock.addStringProperty("sDist09").notNull();
        stock.addStringProperty("sDist10").notNull();
        stock.addFloatProperty("sYtd").notNull();
        stock.addIntProperty("sOrderCnt").notNull();
        stock.addIntProperty("sRemoteCnt").notNull();
        stock.addStringProperty("sData").notNull();
        stock.addIntProperty("sQuantityInitial").notNull();

        /*
        // primary key
        Index indexUnique = new Index();
        indexUnique.addProperty(sIId);
        indexUnique.addProperty(sWId);
        indexUnique.makeUnique();
        stock.addIndex(indexUnique);
        */
    }

    private static void addDeliveryRequest(Schema schema) {
        /*
        CREATE TABLE DELIVERY_REQUEST(DR_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,DR_W_ID SMALLINT NOT NULL,
        DR_CARRIER_ID SMALLINT NOT NULL,DR_QUEUED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        DR_COMPLETED TIMESTAMP,DR_STATE CHAR(1) CONSTRAINT DR_STATE_CHECK CHECK (DR_STATE IN ('Q', 'I', 'C', 'E')));

        CREATE INDEX DR_LOOKUP ON DELIVERY_REQUEST(DR_STATE, DR_QUEUED);
         */

        Entity deliveryRequest = schema.addEntity("DeliveryRequest");
        deliveryRequest.setTableName("DELIVERY_REQUEST");
        // Primary key:drId
        deliveryRequest.addIdProperty();

        //deliveryRequest.addIntProperty("drId").notNull().unique(); // generated
        deliveryRequest.addShortProperty("drWId").notNull();
        deliveryRequest.addShortProperty("drCarrierId").notNull();
        deliveryRequest.addDateProperty("drQueued").notNull(); //Property drQueued = .getProperty(); timestamp
        deliveryRequest.addDateProperty("drCompleted"); // timestamp
        deliveryRequest.addStringProperty("drState"); //Property drState = .getProperty();

        /*
        Index index = new Index();
        index.addProperty(drQueued);
        index.addProperty(drState);
        deliveryRequest.addIndex(index);
        */
    }

    private static void addDeliveryOrders(Schema schema) {
        /*
        CREATE TABLE DELIVERY_ORDERS
        (DO_DR_ID INTEGER NOT NULL CONSTRAINT DO_FK REFERENCES DELIVERY_REQUEST(DR_ID), // DR_ID
        DO_D_ID SMALLINT NOT NULL,
        DO_O_ID INTEGER);
         */

        Entity deliveryOrders = schema.addEntity("DeliveryOrders");
        deliveryOrders.setTableName("DELIVERY_ORDERS");
        deliveryOrders.addIdProperty();
        deliveryOrders.addIntProperty("doDrId").notNull();
        deliveryOrders.addShortProperty("doDId").notNull();
        deliveryOrders.addIntProperty("doOId");
    }
}
