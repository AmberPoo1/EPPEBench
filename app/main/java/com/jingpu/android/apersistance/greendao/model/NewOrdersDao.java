package com.jingpu.android.apersistance.greendao.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.jingpu.android.apersistance.greendao.model.NewOrders;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NEWORDERS".
*/
public class NewOrdersDao extends AbstractDao<NewOrders, String> {

    public static final String TABLENAME = "NEWORDERS";

    /**
     * Properties of entity NewOrders.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property NoCompo = new Property(0, String.class, "noCompo", true, "NO_COMPO");
        public final static Property NoOId = new Property(1, int.class, "noOId", false, "NO_O_ID");
        public final static Property NoDId = new Property(2, short.class, "noDId", false, "NO_D_ID");
        public final static Property NoWId = new Property(3, short.class, "noWId", false, "NO_W_ID");
        public final static Property NoInitial = new Property(4, Boolean.class, "noInitial", false, "NO_INITIAL");
        public final static Property NoLive = new Property(5, Boolean.class, "noLive", false, "NO_LIVE");
    };


    public NewOrdersDao(DaoConfig config) {
        super(config);
    }
    
    public NewOrdersDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NEWORDERS\" (" + //
                "\"NO_COMPO\" TEXT PRIMARY KEY NOT NULL ," + // 0: noCompo
                "\"NO_O_ID\" INTEGER NOT NULL ," + // 1: noOId
                "\"NO_D_ID\" INTEGER NOT NULL ," + // 2: noDId
                "\"NO_W_ID\" INTEGER NOT NULL ," + // 3: noWId
                "\"NO_INITIAL\" INTEGER," + // 4: noInitial
                "\"NO_LIVE\" INTEGER);"); // 5: noLive
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NEWORDERS\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, NewOrders entity) {
        stmt.clearBindings();
 
        String noCompo = entity.getNoCompo();
        if (noCompo != null) {
            stmt.bindString(1, noCompo);
        }
        stmt.bindLong(2, entity.getNoOId());
        stmt.bindLong(3, entity.getNoDId());
        stmt.bindLong(4, entity.getNoWId());
 
        Boolean noInitial = entity.getNoInitial();
        if (noInitial != null) {
            stmt.bindLong(5, noInitial ? 1L: 0L);
        }
 
        Boolean noLive = entity.getNoLive();
        if (noLive != null) {
            stmt.bindLong(6, noLive ? 1L: 0L);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public NewOrders readEntity(Cursor cursor, int offset) {
        NewOrders entity = new NewOrders( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // noCompo
            cursor.getInt(offset + 1), // noOId
            cursor.getShort(offset + 2), // noDId
            cursor.getShort(offset + 3), // noWId
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0, // noInitial
            cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0 // noLive
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, NewOrders entity, int offset) {
        entity.setNoCompo(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setNoOId(cursor.getInt(offset + 1));
        entity.setNoDId(cursor.getShort(offset + 2));
        entity.setNoWId(cursor.getShort(offset + 3));
        entity.setNoInitial(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
        entity.setNoLive(cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(NewOrders entity, long rowId) {
        return entity.getNoCompo();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(NewOrders entity) {
        if(entity != null) {
            return entity.getNoCompo();
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