package com.jingpu.android.apersistance.activeandroid.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Jing Pu on 2016/01/3.
 */

@Table(name = "NEWORDERS")
public class NewOrders extends Model {

	// Labels Table Column names
    public static final String COL_NO_COMPO = "NO_COMPO";
	public static final String COL_NO_O_ID = "NO_O_ID";
	public static final String COL_NO_D_ID = "NO_D_ID";
	public static final String COL_NO_W_ID = "NO_W_ID";
	public static final String COL_NO_INITIAL = "NO_INITIAL";
	public static final String COL_NO_LIVE = "NO_LIVE";

	// property help us to keep data
    @Column(name="NO_COMPO", unique = true, onUniqueConflicts = {Column.ConflictAction.REPLACE})
    private String compositeKey;

    @Column(name="NO_O_ID", notNull = true)
    private int iNoOId; // Id

    @Column(name="NO_D_ID", notNull = true)
    private short sNoDId; // Id

    @Column(name="NO_W_ID", notNull = true)
    private short sNoWId; //Id

    @Column(name="NO_INITIAL")
	private boolean bNoInitial;

    @Column(name="NO_LIVE")
	private boolean bNoLive;

	public NewOrders(){
        super();
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
