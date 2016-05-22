package com.jingpu.android.apersistance.greendao.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.jingpu.android.apersistance.greendao.model.Customer;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CUSTOMER".
*/
public class CustomerDao extends AbstractDao<Customer, String> {

    public static final String TABLENAME = "CUSTOMER";

    /**
     * Properties of entity Customer.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property CCompo = new Property(0, String.class, "cCompo", true, "C_COMPO");
        public final static Property CId = new Property(1, int.class, "cId", false, "C_ID");
        public final static Property CDId = new Property(2, short.class, "cDId", false, "C_D_ID");
        public final static Property CWId = new Property(3, short.class, "cWId", false, "C_W_ID");
        public final static Property CFirst = new Property(4, String.class, "cFirst", false, "C_FIRST");
        public final static Property CMiddle = new Property(5, String.class, "cMiddle", false, "C_MIDDLE");
        public final static Property CLast = new Property(6, String.class, "cLast", false, "C_LAST");
        public final static Property CStreet1 = new Property(7, String.class, "cStreet1", false, "C_STREET1");
        public final static Property CStreet2 = new Property(8, String.class, "cStreet2", false, "C_STREET2");
        public final static Property CCity = new Property(9, String.class, "cCity", false, "C_CITY");
        public final static Property CState = new Property(10, String.class, "cState", false, "C_STATE");
        public final static Property CZip = new Property(11, String.class, "cZip", false, "C_ZIP");
        public final static Property CPhone = new Property(12, String.class, "cPhone", false, "C_PHONE");
        public final static Property CSince = new Property(13, java.util.Date.class, "cSince", false, "C_SINCE");
        public final static Property CCredit = new Property(14, String.class, "cCredit", false, "C_CREDIT");
        public final static Property CCreditLim = new Property(15, float.class, "cCreditLim", false, "C_CREDIT_LIM");
        public final static Property CDiscount = new Property(16, float.class, "cDiscount", false, "C_DISCOUNT");
        public final static Property CBalance = new Property(17, float.class, "cBalance", false, "C_BALANCE");
        public final static Property CYtdPayment = new Property(18, float.class, "cYtdPayment", false, "C_YTD_PAYMENT");
        public final static Property CPaymentCnt = new Property(19, int.class, "cPaymentCnt", false, "C_PAYMENT_CNT");
        public final static Property CDeliveryCnt = new Property(20, int.class, "cDeliveryCnt", false, "C_DELIVERY_CNT");
        public final static Property CData = new Property(21, String.class, "cData", false, "C_DATA");
        public final static Property CDataInitial = new Property(22, String.class, "cDataInitial", false, "C_DATA_INITIAL");
    };


    public CustomerDao(DaoConfig config) {
        super(config);
    }
    
    public CustomerDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CUSTOMER\" (" + //
                "\"C_COMPO\" TEXT PRIMARY KEY NOT NULL ," + // 0: cCompo
                "\"C_ID\" INTEGER NOT NULL ," + // 1: cId
                "\"C_D_ID\" INTEGER NOT NULL ," + // 2: cDId
                "\"C_W_ID\" INTEGER NOT NULL ," + // 3: cWId
                "\"C_FIRST\" TEXT NOT NULL ," + // 4: cFirst
                "\"C_MIDDLE\" TEXT NOT NULL ," + // 5: cMiddle
                "\"C_LAST\" TEXT NOT NULL ," + // 6: cLast
                "\"C_STREET1\" TEXT NOT NULL ," + // 7: cStreet1
                "\"C_STREET2\" TEXT NOT NULL ," + // 8: cStreet2
                "\"C_CITY\" TEXT NOT NULL ," + // 9: cCity
                "\"C_STATE\" TEXT NOT NULL ," + // 10: cState
                "\"C_ZIP\" TEXT NOT NULL ," + // 11: cZip
                "\"C_PHONE\" TEXT NOT NULL ," + // 12: cPhone
                "\"C_SINCE\" INTEGER NOT NULL ," + // 13: cSince
                "\"C_CREDIT\" TEXT NOT NULL ," + // 14: cCredit
                "\"C_CREDIT_LIM\" REAL NOT NULL ," + // 15: cCreditLim
                "\"C_DISCOUNT\" REAL NOT NULL ," + // 16: cDiscount
                "\"C_BALANCE\" REAL NOT NULL ," + // 17: cBalance
                "\"C_YTD_PAYMENT\" REAL NOT NULL ," + // 18: cYtdPayment
                "\"C_PAYMENT_CNT\" INTEGER NOT NULL ," + // 19: cPaymentCnt
                "\"C_DELIVERY_CNT\" INTEGER NOT NULL ," + // 20: cDeliveryCnt
                "\"C_DATA\" TEXT NOT NULL ," + // 21: cData
                "\"C_DATA_INITIAL\" TEXT NOT NULL );"); // 22: cDataInitial
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CUSTOMER\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Customer entity) {
        stmt.clearBindings();
 
        String cCompo = entity.getCCompo();
        if (cCompo != null) {
            stmt.bindString(1, cCompo);
        }
        stmt.bindLong(2, entity.getCId());
        stmt.bindLong(3, entity.getCDId());
        stmt.bindLong(4, entity.getCWId());
        stmt.bindString(5, entity.getCFirst());
        stmt.bindString(6, entity.getCMiddle());
        stmt.bindString(7, entity.getCLast());
        stmt.bindString(8, entity.getCStreet1());
        stmt.bindString(9, entity.getCStreet2());
        stmt.bindString(10, entity.getCCity());
        stmt.bindString(11, entity.getCState());
        stmt.bindString(12, entity.getCZip());
        stmt.bindString(13, entity.getCPhone());
        stmt.bindLong(14, entity.getCSince().getTime());
        stmt.bindString(15, entity.getCCredit());
        stmt.bindDouble(16, entity.getCCreditLim());
        stmt.bindDouble(17, entity.getCDiscount());
        stmt.bindDouble(18, entity.getCBalance());
        stmt.bindDouble(19, entity.getCYtdPayment());
        stmt.bindLong(20, entity.getCPaymentCnt());
        stmt.bindLong(21, entity.getCDeliveryCnt());
        stmt.bindString(22, entity.getCData());
        stmt.bindString(23, entity.getCDataInitial());
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Customer readEntity(Cursor cursor, int offset) {
        Customer entity = new Customer( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // cCompo
            cursor.getInt(offset + 1), // cId
            cursor.getShort(offset + 2), // cDId
            cursor.getShort(offset + 3), // cWId
            cursor.getString(offset + 4), // cFirst
            cursor.getString(offset + 5), // cMiddle
            cursor.getString(offset + 6), // cLast
            cursor.getString(offset + 7), // cStreet1
            cursor.getString(offset + 8), // cStreet2
            cursor.getString(offset + 9), // cCity
            cursor.getString(offset + 10), // cState
            cursor.getString(offset + 11), // cZip
            cursor.getString(offset + 12), // cPhone
            new java.util.Date(cursor.getLong(offset + 13)), // cSince
            cursor.getString(offset + 14), // cCredit
            cursor.getFloat(offset + 15), // cCreditLim
            cursor.getFloat(offset + 16), // cDiscount
            cursor.getFloat(offset + 17), // cBalance
            cursor.getFloat(offset + 18), // cYtdPayment
            cursor.getInt(offset + 19), // cPaymentCnt
            cursor.getInt(offset + 20), // cDeliveryCnt
            cursor.getString(offset + 21), // cData
            cursor.getString(offset + 22) // cDataInitial
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Customer entity, int offset) {
        entity.setCCompo(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setCId(cursor.getInt(offset + 1));
        entity.setCDId(cursor.getShort(offset + 2));
        entity.setCWId(cursor.getShort(offset + 3));
        entity.setCFirst(cursor.getString(offset + 4));
        entity.setCMiddle(cursor.getString(offset + 5));
        entity.setCLast(cursor.getString(offset + 6));
        entity.setCStreet1(cursor.getString(offset + 7));
        entity.setCStreet2(cursor.getString(offset + 8));
        entity.setCCity(cursor.getString(offset + 9));
        entity.setCState(cursor.getString(offset + 10));
        entity.setCZip(cursor.getString(offset + 11));
        entity.setCPhone(cursor.getString(offset + 12));
        entity.setCSince(new java.util.Date(cursor.getLong(offset + 13)));
        entity.setCCredit(cursor.getString(offset + 14));
        entity.setCCreditLim(cursor.getFloat(offset + 15));
        entity.setCDiscount(cursor.getFloat(offset + 16));
        entity.setCBalance(cursor.getFloat(offset + 17));
        entity.setCYtdPayment(cursor.getFloat(offset + 18));
        entity.setCPaymentCnt(cursor.getInt(offset + 19));
        entity.setCDeliveryCnt(cursor.getInt(offset + 20));
        entity.setCData(cursor.getString(offset + 21));
        entity.setCDataInitial(cursor.getString(offset + 22));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Customer entity, long rowId) {
        return entity.getCCompo();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Customer entity) {
        if(entity != null) {
            return entity.getCCompo();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
