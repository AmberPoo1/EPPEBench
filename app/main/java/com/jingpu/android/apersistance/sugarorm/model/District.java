package com.jingpu.android.apersistance.sugarorm.model;


import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

/**
 * Created by Jing Pu on 2016/01/16.
 */

@Table(name = "DISTRICT")
public class District extends SugarRecord {

	// property help us to keep data
	// sugar orm default id
	private Long id;

    @Column(name="D_COMPO", unique = true)
    private String compositeKey;

    @Column(name="D_ID", notNull = true)
    private short sDId; //id = true

    @Column(name="D_W_ID", notNull = true)
    private short sDWId; //id = true

    @Column(name="D_NAME", notNull = true)
	private String strDName;

    @Column(name="D_STREET_1", notNull = true)
	private String strDStreet1;

    @Column(name="D_STREET_2", notNull = true)
	private String strDStreet2;

    @Column(name="D_CITY", notNull = true)
	private String strDCity;

    @Column(name="D_STATE", notNull = true)
    private String strDState;

    @Column(name="D_ZIP", notNull = true)
	private String strDZip;

    @Column(name="D_TAX", notNull = true)
	private float fDTax;

    @Column(name="D_YTD", notNull = true)
	private float fDYTD;

    @Column(name="D_NEXT_O_ID", notNull = true)
	private int iDNxtOId;

	public District(){
	}

	public void clear(){
        this.setCompositeKey(null);
		this.sDId = 0;
        this.sDWId = 0;
		this.strDName = null;
		this.strDStreet1 = null;
		this.strDStreet2 = null;
		this.strDCity = null;
		this.strDState = null;
		this.strDZip = null;
		this.fDTax = 0;
		this.fDYTD = 0;
	}

	public void setCompositeKey(String compositeKey) {
		this.compositeKey = compositeKey;
	}

	public String getCompositeKey() {
		return this.compositeKey;
	}

	public static String getCompositeKey(District obj) {
		return Short.toString(obj.getDWId()) + "-" + Short.toString(obj.getDId());
	}

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public short getDId() {
		return sDId;
	}

	public void setDId(short sDId) {
		this.sDId = sDId;
	}

    public short getDWId() {
        return sDWId;
    }

    public void setDWId(short sDWId) {
        this.sDWId = sDWId;
    }

	public String getDName() {
		return strDName;
	}

	public void setDName(String strDName) {
		this.strDName = strDName;
	}

	public String getDStreet1() {
		return strDStreet1;
	}

	public void setDStreet1(String strDStreet1) {
		this.strDStreet1 = strDStreet1;
	}

	public String getDStreet2() {
		return strDStreet2;
	}

	public void setDStreet2(String strDStreet2) {
		this.strDStreet2 = strDStreet2;
	}

	public String getDCity() {
		return strDCity;
	}

	public void setDCity(String strDCity) {
		this.strDCity = strDCity;
	}

	public String getDState() {
		return strDState;
	}

	public void setDState(String strDState) {
		this.strDState = strDState;
	}

	public String getDZip() {
		return strDZip;
	}

	public void setDZip(String strDZip) {
		this.strDZip = strDZip;
	}

	public float getDTax() {
		return fDTax;
	}

	public void setDTax(float bgDTax) {
		this.fDTax = fDTax;
	}

	public float getDYTD() {
		return fDYTD;
	}

	public void setDYTD(float bgDYTD) {
		this.fDYTD = bgDYTD;
	}

	public int getDNxtOId() {
		return iDNxtOId;
	}

	public void setDNxtOId(int iDNxtOId) {
		this.iDNxtOId = iDNxtOId;
	}
}
