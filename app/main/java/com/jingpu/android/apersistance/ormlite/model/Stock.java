package com.jingpu.android.apersistance.ormlite.model;



import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Jing Pu on 2015/10/6.
 */

@DatabaseTable(tableName = "STOCK")
public class Stock {

	// Labels Table Column names
	public static final String COL_S_COMPO = "S_COMPO";
	public static final String COL_S_I_ID = "S_I_ID";
	public static final String COL_S_W_ID = "S_W_ID";
	public static final String COL_S_QUANTITY = "S_QUANTITY";
	public static final String COL_S_DIST_01 = "S_DIST_01";
	public static final String COL_S_DIST_02 = "S_DIST_02";
	public static final String COL_S_DIST_03 = "S_DIST_03";
	public static final String COL_S_DIST_04 = "S_DIST_04";
	public static final String COL_S_DIST_05 = "S_DIST_05";
	public static final String COL_S_DIST_06 = "S_DIST_06";
	public static final String COL_S_DIST_07 = "S_DIST_07";
	public static final String COL_S_DIST_08 = "S_DIST_08";
	public static final String COL_S_DIST_09 = "S_DIST_09";
	public static final String COL_S_DIST_10 = "S_DIST_10";
	public static final String COL_S_YTD = "S_YTD";
	public static final String COL_S_ORDER_CNT = "S_ORDER_CNT";
	public static final String COL_S_REMOTE_CNT = "S_REMOTE_CNT";
	public static final String COL_S_DATA = "S_DATA";
	public static final String COL_S_QUANTITY_INITIAL = "S_QUANTITY_INITIAL";

	// property help us to keep data

    @DatabaseField(id=true, columnName="S_COMPO")
    private String compositeKey;

    @DatabaseField(columnName="S_W_ID", canBeNull = false)
	private short sSWId; // Id

    @DatabaseField(columnName="S_I_ID", canBeNull = false)
	private int iSIId; // Id

	@DatabaseField(columnName="S_QUANTITY", canBeNull = false)
	private int iSQuantity;

	@DatabaseField(columnName="S_DIST_01", canBeNull = false)
	private String strSDist01;

	@DatabaseField(columnName="S_DIST_02", canBeNull = false)
	private String strSDist02;

	@DatabaseField(columnName="S_DIST_03", canBeNull = false)
	private String strSDist03;

	@DatabaseField(columnName="S_DIST_04", canBeNull = false)
	private String strSDist04;

	@DatabaseField(columnName="S_DIST_05", canBeNull = false)
	private String strSDist05;

	@DatabaseField(columnName="S_DIST_06", canBeNull = false)
	private String strSDist06;

	@DatabaseField(columnName="S_DIST_07", canBeNull = false)
	private String strSDist07;

	@DatabaseField(columnName="S_DIST_08", canBeNull = false)
	private String strSDist08;

	@DatabaseField(columnName="S_DIST_09", canBeNull = false)
	private String strSDist09;

	@DatabaseField(columnName="S_DIST_10", canBeNull = false)
	private String strSDist10;

	@DatabaseField(columnName="S_YTD", canBeNull = false)
	private float fSYTD;

	@DatabaseField(columnName="S_ORDER_CNT", canBeNull = false)
	private int iSOrderCnt;

	@DatabaseField(columnName="S_REMOTE_CNT", canBeNull = false)
	private int iSRemoteCnt;

	@DatabaseField(columnName="S_DATA", canBeNull = false)
	private String strSData;

	@DatabaseField(columnName="S_QUANTITY_INITIAL", canBeNull = false)
	private int iSQtyInit;

    public Stock(){

    }

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public String getCompositeKey() {
        return this.compositeKey;
    }

    public static String getCompositeKey(Stock obj) {
        return Integer.toString(obj.getSWId()) + "-"  + Integer.toString(obj.getSIId());
    }

    public short getSWId() {
        return sSWId;
    }

    public void setSWId(short sSWId) {
        this.sSWId = sSWId;
    }

    public int getSIId() {
        return iSIId;
    }

    public void setSIId(int iSIId) {
        this.iSIId = iSIId;
    }

    public int getSQuantity() {
		return iSQuantity;
	}

	public void setSQuantity(int iSQuantity) {
		this.iSQuantity = iSQuantity;
	}

	public String getSDist01() {
		return strSDist01;
	}

	public void setSDist01(String strSDist01) {
		this.strSDist01 = strSDist01;
	}

	public String getSDist02() {
		return strSDist02;
	}

	public void setSDist02(String strSDist02) {
		this.strSDist02 = strSDist02;
	}

	public String getSDist03() {
		return strSDist03;
	}

	public void setSDist03(String strSDist03) {
		this.strSDist03 = strSDist03;
	}

	public String getSDist04() {
		return strSDist04;
	}

	public void setSDist04(String strSDist04) {
		this.strSDist04 = strSDist04;
	}

	public String getSDist05() {
		return strSDist05;
	}

	public void setSDist05(String strSDist05) {
		this.strSDist05 = strSDist05;
	}

	public String getSDist06() {
		return strSDist06;
	}

	public void setSDist06(String strSDist06) {
		this.strSDist06 = strSDist06;
	}

	public String getSDist07() {
		return strSDist07;
	}

	public void setSDist07(String strSDist07) {
		this.strSDist07 = strSDist07;
	}

	public String getSDist08() {
		return strSDist08;
	}

	public void setSDist08(String strSDist08) {
		this.strSDist08 = strSDist08;
	}

	public String getSDist09() {
		return strSDist09;
	}

    public void setSDist09(String strSDist09) {
		this.strSDist09 = strSDist09;
	}

	public String getSDist10() {
		return strSDist10;
	}

	public void setSDist10(String strSDist10) {
		this.strSDist10 = strSDist10;
	}

	public float getSYTD() {
		return fSYTD;
	}

	public void setSYTD(float fSYTD) {
		this.fSYTD = fSYTD;
	}

	public int getSOrderCnt() {
		return iSOrderCnt;
	}

	public void setSOrderCnt(int iSOrderCnt) {
		this.iSOrderCnt = iSOrderCnt;
	}

	public int getSRemoteCnt() {
		return iSRemoteCnt;
	}

	public void setSRemoteCnt(int iSRemoteCnt) {
		this.iSRemoteCnt = iSRemoteCnt;
	}

	public String getSData() {
		return strSData;
	}

	public void setSData(String strSData) {
		this.strSData = strSData;
	}

	public int getSQtyInit() {
		return iSQtyInit;
	}

	public void setSQtyInit(int iSQtyInit) {
		this.iSQtyInit = iSQtyInit;
	}
}
