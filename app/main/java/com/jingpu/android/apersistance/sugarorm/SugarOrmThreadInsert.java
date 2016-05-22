package com.jingpu.android.apersistance.sugarorm;

import android.database.sqlite.SQLiteException;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.sugarorm.model.Customer;
import com.jingpu.android.apersistance.sugarorm.model.District;
import com.jingpu.android.apersistance.sugarorm.model.History;
import com.jingpu.android.apersistance.sugarorm.model.Item;
import com.jingpu.android.apersistance.sugarorm.model.NewOrders;
import com.jingpu.android.apersistance.sugarorm.model.OrderLine;
import com.jingpu.android.apersistance.sugarorm.model.Orders;
import com.jingpu.android.apersistance.sugarorm.model.Stock;
import com.jingpu.android.apersistance.sugarorm.model.Warehouse;
import com.jingpu.android.apersistance.util.TPCCLog;

import org.dacapo.derby.Load;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jing Pu on 2016/1/20.
 */
public class SugarOrmThreadInsert extends SugarOrmSimpleInsert implements Runnable {
    private final SugarOrmThreadInsert master;
    private Exception loadExceptions;
    private int threadCount;
    private short nextWarehouse = 1;

    public SugarOrmThreadInsert() {
        this.master = this;
    }

    private SugarOrmThreadInsert(SugarOrmThreadInsert master) {
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

        String log = "**  Before populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[item=" + Item.count(Item.class)
                + ",warehouse=" + Warehouse.count(Warehouse.class)
                + ", stock=" + Stock.count(Stock.class)
                + ", district=" + District.count(District.class)
                + ", customer=" + Customer.count(Customer.class)
                + ", order=" + Orders.count(Orders.class)
                + ", history="+ History.count(History.class)
                + ", neworders=" + NewOrders.count(NewOrders.class)
                + ", orderline=" + OrderLine.count(OrderLine.class) + "]";

        AppContext.getInstance().SetUIInfo(log);

        if (this.threadCount == 1) {
            super.populateAllTables();
        } else {
            threadPopulate();
        }

        log = "**  After populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[item=" + Item.count(Item.class)
                + ", warehouse=" + Warehouse.count(Warehouse.class)
                + ", stock=" + Stock.count(Stock.class)
                + ", district=" + District.count(District.class)
                + ", customer=" + Customer.count(Customer.class)
                + ", order=" + Orders.count(Orders.class)
                + ", history="+ History.count(History.class)
                + ", neworders=" + NewOrders.count(NewOrders.class)
                + ", orderline=" + OrderLine.count(OrderLine.class) + "]";
        AppContext.getInstance().SetUIInfo(log);
    }

    private void setScale(short scale) {
        this.scale = scale;
    }

    private void threadPopulate() throws InterruptedException, Exception {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        SugarOrmThreadInsert ti = null;
        for (int t = 1; t < this.threadCount; t++) {
            ti = new  SugarOrmThreadInsert(this);
            ti.setSeed(this.seed * t / 17L);
            ti.setScale(this.scale);
            cachedThreadPool.execute(ti);
        }

        // main thread operation and session has been initialized in TPCC
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
        // multi-thread need to create session in current session
        if (null == this.soa) {
            try {
                setupAgent();
                setRandomGenerator();
            } catch (Exception e) {
                TPCCLog.e(SugarOrmThreadInsert.class.getName(), e);
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


