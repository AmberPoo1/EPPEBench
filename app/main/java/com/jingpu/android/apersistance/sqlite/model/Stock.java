package com.jingpu.android.apersistance.sqlite.model;

/**
 * Created by Jing Pu on 2015/10/6.
 */
public class Stock {

	public static final String TABLE = "STOCK";

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
    //Column(name="S_COMPO")
    private String compositeKey;
	
	//Id
	//Column(name="S_I_ID")
	private int iSIId;
	
	//Id
	//Column(name="S_W_ID")
	private short sSWId;
	
	//Column(name="S_QUANTITY")
	private int iSQuantity;
	
	//Column(name="S_DIST_01")
	private String strSDist01;
	
	//Column(name="S_DIST_02")
	private String strSDist02;
	
	//Column(name="S_DIST_03")
	private String strSDist03;
	
	//Column(name="S_DIST_04")
	private String strSDist04;
	
	//Column(name="S_DIST_05")
	private String strSDist05;
	
	//Column(name="S_DIST_06")
	private String strSDist06;
	
	//Column(name="S_DIST_07")
	private String strSDist07;
	
	//Column(name="S_DIST_08")
	private String strSDist08;
	
	//Column(name="S_DIST_09")
	private String strSDist09;
	
	//Column(name="S_DIST_10")
	private String strSDist10;
	
	//Column(name="S_YTD")
	private float fSYTD;
	
	//Column(name="S_ORDER_CNT")
	private int iSOrderCnt;
	
	//Column(name="S_REMOTE_CNT")
	private int iSRemoteCnt;
	
	//Column(name="S_DATA")
	private String strSData;
	
	//Column(name="S_QUANTITY_INITIAL")
	private int iSQtyInit;

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public String getCompositeKey() {
        return this.compositeKey;
    }

    public static String getCompositeKey(Stock obj) {
        return Integer.toString(obj.getSWId()) + "-"  + Integer.toString(obj.getSIId());
    }

	public int getSIId() {
		return iSIId;
	}

	public void setSIId(int iSIId) {
		this.iSIId = iSIId;
	}

	public short getSWId() {
		return sSWId;
	}

	public void setSWId(short sSWId) {
		this.sSWId = sSWId;
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
