package com.jingpu.android.apersistance.sugarorm.model;


import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;


/**
 * Created by Jing Pu on 2016/01/3.
 */
@Table(name = "STOCK")
public class Stock extends SugarRecord {

	// property help us to keep data
	// sugar orm default id
	private Long id;

    @Column(name="S_COMPO", unique = true)
    private String compositeKey;

    @Column(name="S_W_ID", notNull = true)
    private short sSWId; // Id

    @Column(name="S_I_ID", notNull = true)
    private int iSIId; // Id

    @Column(name="S_QUANTITY", notNull = true)
	private int iSQuantity;

	@Column(name="S_DIST_01", notNull = true)
	private String strSDist01;

	@Column(name="S_DIST_02", notNull = true)
	private String strSDist02;

	@Column(name="S_DIST_03", notNull = true)
	private String strSDist03;

	@Column(name="S_DIST_04", notNull = true)
	private String strSDist04;

	@Column(name="S_DIST_05", notNull = true)
	private String strSDist05;

	@Column(name="S_DIST_06", notNull = true)
	private String strSDist06;

	@Column(name="S_DIST_07", notNull = true)
	private String strSDist07;

	@Column(name="S_DIST_08", notNull = true)
	private String strSDist08;

	@Column(name="S_DIST_09", notNull = true)
	private String strSDist09;

	@Column(name="S_DIST_10", notNull = true)
	private String strSDist10;

	@Column(name="S_YTD", notNull = true)
	private float fSYTD;

	@Column(name="S_ORDER_CNT", notNull = true)
	private int iSOrderCnt;

	@Column(name="S_REMOTE_CNT", notNull = true)
	private int iSRemoteCnt;

	@Column(name="S_DATA", notNull = true)
	private String strSData;

	@Column(name="S_QUANTITY_INITIAL", notNull = true)
	private int iSQtyInit;

    public Stock(){
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
