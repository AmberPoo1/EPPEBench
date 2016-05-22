package com.jingpu.android.apersistance.sqlite;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.sqlite.model.Customer;
import com.jingpu.android.apersistance.sqlite.model.District;
import com.jingpu.android.apersistance.sqlite.model.History;
import com.jingpu.android.apersistance.sqlite.model.Item;
import com.jingpu.android.apersistance.sqlite.model.NewOrders;
import com.jingpu.android.apersistance.sqlite.model.OrderLine;
import com.jingpu.android.apersistance.sqlite.model.Orders;
import com.jingpu.android.apersistance.sqlite.model.Stock;
import com.jingpu.android.apersistance.sqlite.model.Warehouse;
import com.jingpu.android.apersistance.util.TPCCLog;

import org.dacapo.derby.Load;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jing Pu on 2016/1/20.
 */
public class SqliteThreadInsert extends SqliteSimpleInsert implements Runnable {
    private final SqliteThreadInsert master;
    private Exception loadExceptions;
    private int threadCount;
    private short nextWarehouse = 1;

    public SqliteThreadInsert() {
        this.master = this;
    }

    private SqliteThreadInsert(SqliteThreadInsert master) {
        this.master = master;
    }

    public void setupLoad(short scale) throws SQLiteException { //Session session,
        super.setupLoad(scale);

        int cpuCount = Runtime.getRuntime().availableProcessors();

        setThreadCount(cpuCount);
    }

    public void setThreadCount(int trdCount) {
        if (this.scale == 1) {
            this.threadCount = 1;

            AppContext.getInstance().SetUIInfo("Parameters [cpuCount=" + trdCount
                    + "; scale=" + this.scale + "; loaderThreadNum=" + this.threadCount + "]");
            return;
        }
        if (this.scale < trdCount) {
            this.threadCount = this.scale;

            AppContext.getInstance().SetUIInfo("Parameters [cpuCount=" + trdCount
                    + "; scale=" + this.scale + "; loaderThreadNum=" + this.threadCount + "]");
            return;
        }

        this.threadCount = trdCount;

        AppContext.getInstance().SetUIInfo("Parameters [cpuCount=" + trdCount
                + "; scale=" + this.scale + "; loaderThreadNum=" + this.threadCount + "]");
    }

    public void populateAllTables() throws Exception {
        String log = null;
        SQLiteDatabase db = sa.getReadableDatabase();
        log = "**  Before populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[item=" + getTableCount(Item.TABLE, db)
                + ", warehouse=" + getTableCount(Warehouse.TABLE, db)
                + ", stock=" + getTableCount(Stock.TABLE, db)
                + ", district=" + getTableCount(District.TABLE, db)
                + ", customer=" + getTableCount(Customer.TABLE, db)
                + ", order=" + getTableCount(Orders.TABLE, db)
                + ", history=" + getTableCount(History.TABLE, db)
                + ", neworders=" + getTableCount(NewOrders.TABLE, db)
                + ", orderline=" + getTableCount(OrderLine.TABLE, db) + "]";
        AppContext.getInstance().SetUIInfo(log);

        if (this.threadCount == 1) {
            super.populateAllTables();
        } else {
            threadPopulate();
        }

        db = sa.getReadableDatabase();
        log = "**  After populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[item=" + getTableCount(Item.TABLE, db)
                + ", warehouse=" + getTableCount(Warehouse.TABLE, db)
                + ", stock=" + getTableCount(Stock.TABLE, db)
                + ", district=" + getTableCount(District.TABLE, db)
                + ", customer=" + getTableCount(Customer.TABLE, db)
                + ", order=" + getTableCount(Orders.TABLE, db)
                + ", history="+ getTableCount(History.TABLE, db)
                + ", neworders=" + getTableCount(NewOrders.TABLE, db)
                + ", orderline=" + getTableCount(OrderLine.TABLE, db) + "]";
        AppContext.getInstance().SetUIInfo(log);
    }

    private long getTableCount(String tableName, SQLiteDatabase db) {
        return DatabaseUtils.queryNumEntries(db, tableName);
    }

    private void setScale(short scale) {
        this.scale = scale;
    }

    private void threadPopulate() throws InterruptedException, Exception {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        SqliteThreadInsert ti = null;
        for (int t = 1; t < this.threadCount; t++) {
            ti = new  SqliteThreadInsert(this);
            ti.setSeed(this.seed * t / 17L);
            ti.setScale(this.scale);
            cachedThreadPool.execute(ti);
        }

        itemTable(1, Load.ITEM_COUNT); //10000
        run();

        // reject new incoming threads
        cachedThreadPool.shutdown();

        // and then wait for them to finish
        while (true) {
            if (cachedThreadPool.isTerminated()) {
                break;
            }
        }

        synchronized (this) {
            if (this.loadExceptions != null) {
                throw this.loadExceptions;
            }
        }
    }

    synchronized short getNextWarehouse() {
        short next = this.nextWarehouse++;
        if (next > this.scale) {
            return -1;
        }
        return next;
    }

    synchronized void addException(Exception sqle) {
        this.loadExceptions = sqle;
    }

    public void run() {
        // check operation of current thread
        if (null == this.sa) {
            try {
                setupAgent();
                setRandomGenerator();
            } catch (Exception e) {
                TPCCLog.e(SqliteThreadInsert.class.getName(), e);
                e.printStackTrace();
            }
        }

        short w;
        while ((w = master.getNextWarehouse()) != -1) {
            try {
                populateForOneWarehouse(w);
            }
            catch (Exception e) {
                master.addException(e);
                break;
            }
        }
    }
}

