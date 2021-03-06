package com.jingpu.android.apersistance.greendao.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.jingpu.android.apersistance.greendao.model.Category;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CATEGORY".
*/
public class CategoryDao extends AbstractDao<Category, Long> {

    public static final String TABLENAME = "CATEGORY";

    /**
     * Properties of entity Category.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property StrCTitle = new Property(1, String.class, "strCTitle", false, "STR_CTITLE");
        public final static Property ICPages = new Property(2, int.class, "iCPages", false, "I_CPAGES");
        public final static Property ICSubCats = new Property(3, int.class, "iCSubCats", false, "I_CSUB_CATS");
        public final static Property ICFiles = new Property(4, int.class, "iCFiles", false, "I_CFILES");
    };


    public CategoryDao(DaoConfig config) {
        super(config);
    }
    
    public CategoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CATEGORY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"STR_CTITLE\" TEXT NOT NULL ," + // 1: strCTitle
                "\"I_CPAGES\" INTEGER NOT NULL ," + // 2: iCPages
                "\"I_CSUB_CATS\" INTEGER NOT NULL ," + // 3: iCSubCats
                "\"I_CFILES\" INTEGER NOT NULL );"); // 4: iCFiles
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CATEGORY\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Category entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getStrCTitle());
        stmt.bindLong(3, entity.getICPages());
        stmt.bindLong(4, entity.getICSubCats());
        stmt.bindLong(5, entity.getICFiles());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Category readEntity(Cursor cursor, int offset) {
        Category entity = new Category( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // strCTitle
            cursor.getInt(offset + 2), // iCPages
            cursor.getInt(offset + 3), // iCSubCats
            cursor.getInt(offset + 4) // iCFiles
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Category entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setStrCTitle(cursor.getString(offset + 1));
        entity.setICPages(cursor.getInt(offset + 2));
        entity.setICSubCats(cursor.getInt(offset + 3));
        entity.setICFiles(cursor.getInt(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Category entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Category entity) {
        if(entity != null) {
            return entity.getId();
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
