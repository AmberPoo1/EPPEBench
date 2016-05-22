package com.jingpu.android.apersistance.sqlite.model;

/**
 * 
 * @author Jing Pu
 *
 */
public class District {

	public static final String TABLE = "DISTRICT";

	// Labels Table Column names
    public static final String COL_D_COMPO = "D_COMPO";
	public static final String COL_D_ID = "D_ID";
	public static final String COL_D_W_ID = "D_W_ID";
	public static final String COL_D_NAME = "D_NAME";
	public static final String COL_D_STREET_1 = "D_STREET_1";
	public static final String COL_D_STREET_2 = "D_STREET_2";
	public static final String COL_D_CITY = "D_CITY";
	public static final String COL_D_STATE = "D_STATE";
	public static final String COL_D_ZIP = "D_ZIP";
	public static final String COL_D_TAX = "D_TAX";
	public static final String COL_D_YTD = "D_YTD";
	public static final String COL_D_NEXT_O_ID = "D_NEXT_O_ID";

	// property help us to keep data
    //Column(name="D_COMPO")
    private String compositeKey;

	//Id
	//Column(name="D_ID")
	private short sDId;
	
	//Id
	//Column(name="D_W_ID")
	private short sDWId;

	//Column(name="D_NAME")
	private String strDName;
	
	//Column(name="D_STREET_1")
	private String strDStreet1;
	
	//Column(name="D_STREET_2")
	private String strDStreet2;
	
	//Column(name="D_CITY")
	private String strDCity;
	
	//Column(name="D_STATE")
	private String strDState;
	
	//Column(name="D_ZIP")
	private String strDZip;
	
	//Column(name="D_TAX")
	private float fDTax;
	
	//Column(name="D_YTD")
	private float fDYTD;
	
	//Column(name="D_NEXT_O_ID")
	private int iDNxtOId;

	public District(){
		
	}

	public void clear(){
        this.setCompositeKey(null);
		this.sDWId = 0;
		this.sDId = 0;
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

	public void setDTax(float fDTax) {
		this.fDTax = fDTax;
	}

	public float getDYTD() {
		return fDYTD;
	}

	public void setDYTD(float fDYTD) {
		this.fDYTD = fDYTD;
	}

	public int getDNxtOId() {
		return iDNxtOId;
	}

	public void setDNxtOId(int iDNxtOId) {
		this.iDNxtOId = iDNxtOId;
	}
}
