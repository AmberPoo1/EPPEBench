package com.jingpu.android.apersistance.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.jingpu.android.apersistance.R;
import com.jingpu.android.apersistance.ormlite.model.*;

import java.sql.SQLException;

/**
 * Created by Jing Pu on 2015/10/23.
 */
public class OrmliteDBHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME="aptest.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<C, Long> cDao;
    private Dao<Customer, String> customerDao;
    private Dao<DeliveryOrders, Long> deliveryOrdersDao;
    private Dao<DeliveryRequest, Long> deliveryRequestDao;
    private Dao<District, String> districtDao;
    private Dao<History, Long> historyDao;
    private Dao<Item, Long> itemDao;
    private Dao<NewOrders, String> newOrdersDao;
    private Dao<OrderLine, String> orderLineDao;
    private Dao<Orders, String> ordersDao;
    private Dao<Stock, String> stockDao;
    private Dao<Warehouse, Long> warehouseDao;
    private Dao<Category, Long> categoryDao;
    private Dao<CItem, Long> cItemDao;


    public OrmliteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {

    }

    // Create the getDao methods of all database tables to access those from android code.
    // Insert, delete, read, update everything will be happened through DAOs

    public Dao<C, Long> getCDao() throws SQLException {
        if (cDao == null) {
            cDao = getDao(C.class);
        }
        return cDao;
    }

    public Dao<Customer, String> getCustomerDao() throws SQLException {
        if (customerDao == null) {
            customerDao = getDao(Customer.class);
        }
        return customerDao;
    }

    public Dao<DeliveryOrders, Long> getDeliveryOrdersDao() throws SQLException {
        if (deliveryOrdersDao == null) {
            deliveryOrdersDao = getDao(DeliveryOrders.class);
        }
        return deliveryOrdersDao;
    }

    public Dao<DeliveryRequest, Long> getDeliveryRequestDao() throws SQLException {
        if (deliveryRequestDao == null) {
            deliveryRequestDao = getDao(DeliveryRequest.class);
        }
        return deliveryRequestDao;
    }

    public Dao<District, String> getDistrictDao() throws SQLException {
        if (districtDao == null) {
            districtDao = getDao(District.class);
        }
        return districtDao;
    }

    public Dao<History, Long> getHistroyDao() throws SQLException {
        if (historyDao == null) {
            historyDao = getDao(History.class);
        }
        return historyDao;
    }

    public Dao<Item, Long> getItemDao() throws SQLException {
        if (itemDao == null) {
            itemDao = getDao(Item.class);
        }
        return itemDao;
    }

    public Dao<NewOrders, String> getNewOrdersDao() throws SQLException {
        if (newOrdersDao == null) {
            newOrdersDao = getDao(NewOrders.class);
        }
        return newOrdersDao;
    }

    public Dao<OrderLine, String> getOrderLineDao() throws SQLException {
        if (orderLineDao == null) {
            orderLineDao = getDao(OrderLine.class);
        }
        return orderLineDao;
    }

    public Dao<Orders, String> getOrdersDao() throws SQLException {
        if (ordersDao == null) {
            ordersDao = getDao(Orders.class);
        }
        return ordersDao;
    }

    public Dao<Stock, String> getStockDao() throws SQLException {
        if (stockDao == null) {
            stockDao = getDao(Stock.class);
        }
        return stockDao;
    }

    public Dao<Warehouse, Long> getWarehouseDao() throws SQLException {
        if (warehouseDao == null) {
            warehouseDao = getDao(Warehouse.class);
        }
        return warehouseDao;
    }

    public Dao<Category, Long> getCategoryDao() throws SQLException {
        if (categoryDao == null) {
            categoryDao = getDao(Category.class);
        }
        return categoryDao;
    }

    public Dao<CItem, Long> getCItemDao() throws SQLException {
        if (cItemDao == null) {
            cItemDao = getDao(CItem.class);
        }
        return cItemDao;
    }
}

