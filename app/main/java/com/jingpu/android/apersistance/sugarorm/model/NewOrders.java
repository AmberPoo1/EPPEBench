package com.jingpu.android.apersistance.sugarorm.model;


import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

/**
 * Created by Jing Pu on 2016/01/16.
 */
@Table(name = "NEWORDERS")
public class NewOrders extends SugarRecord {

    // property help us to keep data
    // sugar orm default id
    private Long id;

    @Column(name="NO_COMPO", unique = true)
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
	}

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
