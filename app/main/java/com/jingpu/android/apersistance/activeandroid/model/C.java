package com.jingpu.android.apersistance.activeandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Jing Pu on 2016/1/3.
 */
@Table(name = "C", id = "clientId")
public class C extends Model {

	// Labels Table Column names
	public static final String COL_C_ID = "Id"; //C_ID
	public static final String COL_CLOAD = "CLOAD";

	// property help us to keep data
    @Column(name="Id") //, unique = true
    private Long id;

	@Column(name="CLOAD")
	private int cLoad;

	public C() {
		super();
	}

    public Long getCId() {
        return id;
    }

    public void setCId(Long cId) {
        this.id = cId;
    }

    public int getCLoad() {
		return cLoad;
	}

	public void setCLoad(int cLoad) {
		this.cLoad = cLoad;
	}
}
