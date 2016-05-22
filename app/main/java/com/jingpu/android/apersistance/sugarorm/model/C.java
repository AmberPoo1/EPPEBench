package com.jingpu.android.apersistance.sugarorm.model;


import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

/**
 * Created by Jing Pu on 2016/1/16.
 */

@Table(name = "C")
public class C extends SugarRecord {

	// property help us to keep data
    private Long id; //"C_ID"

	@Column(name="CLOAD")
	private int cLoad;

	public C() {
	}

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public int getCLoad() {
		return cLoad;
	}

	public void setCLoad(int cLoad) {
		this.cLoad = cLoad;
	}
}
