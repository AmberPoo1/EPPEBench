package com.jingpu.android.apersistance.ormlite;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jingpu.android.apersistance.ormlite.model.*;

import java.sql.SQLException;

/**
 * Created by Jing Pu on 2015/10/9.
 */
public class OrmliteAgent {

    private OrmliteDBHelper dbHelper;

    public OrmliteAgent(Context context) {
        dbHelper = OpenHelperManager.getHelper(context, OrmliteDBHelper.class);
    }

    public OrmliteDBHelper getDbHelper() {
        return dbHelper;
    }

    protected  void releaseHelper() throws SQLException {
        if (dbHelper != null && dbHelper.isOpen()) {
            OpenHelperManager.releaseHelper();
        }

        dbHelper = null;
    }

    public void createSchemaAndConstraints() throws SQLException {
        dropTables();
        ConnectionSource connectionSource = dbHelper.getConnectionSource();

        TableUtils.createTable(connectionSource, Customer.class);
        TableUtils.createTable(connectionSource, DeliveryOrders.class);
        TableUtils.createTable(connectionSource, DeliveryRequest.class);
        TableUtils.createTable(connectionSource, District.class);
        TableUtils.createTable(connectionSource, History.class);
        TableUtils.createTable(connectionSource, Item.class);
        TableUtils.createTable(connectionSource, NewOrders.class);
        TableUtils.createTable(connectionSource, OrderLine.class);
        TableUtils.createTable(connectionSource, Orders.class);
        TableUtils.createTable(connectionSource, Stock.class);
        TableUtils.createTable(connectionSource, Warehouse.class);
    }

    public void dropTables() throws SQLException {
        ConnectionSource connectionSource = dbHelper.getConnectionSource();

        TableUtils.dropTable(connectionSource, Customer.class, true);
        TableUtils.dropTable(connectionSource, DeliveryOrders.class, true);
        TableUtils.dropTable(connectionSource, DeliveryRequest.class, true);
        TableUtils.dropTable(connectionSource, District.class, true);
        TableUtils.dropTable(connectionSource, History.class, true);
        TableUtils.dropTable(connectionSource, Item.class, true);
        TableUtils.dropTable(connectionSource, NewOrders.class, true);
        TableUtils.dropTable(connectionSource, OrderLine.class, true);
        TableUtils.dropTable(connectionSource, Orders.class, true);
        TableUtils.dropTable(connectionSource, Stock.class, true);
        TableUtils.dropTable(connectionSource, Warehouse.class, true);
    }
}
