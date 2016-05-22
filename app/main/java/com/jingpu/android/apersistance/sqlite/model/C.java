package com.jingpu.android.apersistance.sqlite.model;

/**
 * Created by Jing Pu on 2015/10/3.
 */
public class C {
	public static final String TABLE = "C";

	// Labels Table Column names
	public static final String COL_C_ID = "C_ID";
	public static final String COL_CLOAD = "CLOAD";

	// property help us to keep data
	//Column(name="C_ID")
    private long cId;

	//Column(name="CLOAD")
	private int cLoad;

    public long getCId() {
        return cId;
    }

    public void setCId(long cId) {
        this.cId = cId;
    }

    public int getCLoad() {
		return cLoad;
	}

	public void setCLoad(int cLoad) {
		this.cLoad = cLoad;
	}
}
