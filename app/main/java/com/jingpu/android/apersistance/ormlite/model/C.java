package com.jingpu.android.apersistance.ormlite.model;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Jing Pu on 2015/10/9.
 */
@DatabaseTable(tableName = "C")
public class C {

	// Labels Table Column names
	public static final String COL_C_ID = "C_ID";
	public static final String COL_CLOAD = "CLOAD";

	// property help us to keep data
    @DatabaseField(id = true, columnName="C_ID")
    private Long cId;

	@DatabaseField(columnName="CLOAD", dataType = DataType.INTEGER)
	private int cLoad;

	public C() {

	}
    public Long getCId() {
        return cId;
    }

    public void setCId(Long cId) {
        this.cId = cId;
    }

	public int getCLoad() {
		return cLoad;
	}

	public void setCLoad(int cLoad) {
		this.cLoad = cLoad;
	}

}
