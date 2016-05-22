package com.jingpu.android.apersistance.realm;

import android.content.Context;

import com.jingpu.android.apersistance.AppContext;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmException;


/**
 * Created by Jing Pu on 2016/1/21.
 */
public class RealmAgent {

    private RealmConfiguration realmConfig;

    public RealmAgent(Context context) {
        if (null == realmConfig) {
            realmConfig = new RealmConfiguration.Builder(AppContext.getInstance()).build();
        }
    }

    public Realm getRealmInstance() {
        //Realm.setDefaultConfiguration(realmConfig);
        //return Realm.getDefaultInstance();
        return Realm.getInstance(realmConfig);
    }

    public void createSchemaAndConstraints() throws RealmException {

    }
}
