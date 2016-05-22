package com.jingpu.android.apersistance.ormlite.model;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Jing Pu on 2015/10/6.
 */

@DatabaseTable(tableName = "NEWORDERS")
public class NewOrders {

	// Labels Table Column names
    public static final String COL_C_COMPO = "NO_COMPO";
	public static final String COL_NO_O_ID = "NO_O_ID";
	public static final String COL_NO_D_ID = "NO_D_ID";
	public static final String COL_NO_W_ID = "NO_W_ID";
	public static final String COL_NO_INITIAL = "NO_INITIAL";
	public static final String COL_NO_LIVE = "NO_LIVE";

	// property help us to keep data

    @DatabaseField(id = true, columnName="NO_COMPO")
    private String compositeKey;

    @DatabaseField(columnName="NO_O_ID", canBeNull = false)
    private int iNoOId; // Id

    @DatabaseField(columnName="NO_D_ID", canBeNull = false)
    private short sNoDId; // Id

    @DatabaseField(columnName="NO_W_ID", canBeNull = false)
    private short sNoWId; //Id

    @DatabaseField(columnName="NO_INITIAL", dataType = DataType.BOOLEAN)
	private boolean bNoInitial;

    @DatabaseField(columnName="NO_LIVE", dataType = DataType.BOOLEAN)
	private boolean bNoLive;

	public NewOrders(){

	}

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public String getCompositeKey() {
        return this.compositeKey;
    }

    public static String getCompositeKey(NewOrders obj) {
        return Integer.toString(obj.getNoOId()) + "-"  + Short.toString(obj.getNoDId()) + "-"  + Short.toString(obj.getNoWId());
    }

    public int getNoOId() {
        return iNoOId;
    }

    public void setNoOId(int iNoOId) {
        this.iNoOId = iNoOId;
    }

    public short getNoDId() {
        return sNoDId;
    }

    public void setNoDId(short sNoDId) {
        this.sNoDId = sNoDId;
    }

    public short getNoWId() {
        return sNoWId;
    }

    public void setNoWId(short sNoWId) {
        this.sNoWId = sNoWId;
    }

    public boolean isNoInitial() {
		return bNoInitial;
	}

	public void setNoInitial(boolean bNoInitial) {
		this.bNoInitial = bNoInitial;
	}

	public boolean isNoLive() {
		return bNoLive;
	}

	public void setNoLive(boolean bNoLive) {
		this.bNoLive = bNoLive;
	}

}
