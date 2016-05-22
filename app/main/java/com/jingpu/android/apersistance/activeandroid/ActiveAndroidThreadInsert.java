package com.jingpu.android.apersistance.activeandroid;

import android.database.sqlite.SQLiteException;

import com.activeandroid.query.Select;
import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.activeandroid.model.Customer;
import com.jingpu.android.apersistance.activeandroid.model.District;
import com.jingpu.android.apersistance.activeandroid.model.History;
import com.jingpu.android.apersistance.activeandroid.model.Item;
import com.jingpu.android.apersistance.activeandroid.model.NewOrders;
import com.jingpu.android.apersistance.activeandroid.model.OrderLine;
import com.jingpu.android.apersistance.activeandroid.model.Orders;
import com.jingpu.android.apersistance.activeandroid.model.Stock;
import com.jingpu.android.apersistance.activeandroid.model.Warehouse;
import com.jingpu.android.apersistance.util.TPCCLog;

import org.dacapo.derby.Load;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jing Pu on 2016/1/20.
 */
public class ActiveAndroidThreadInsert extends ActiveAndroidSimpleInsert implements Runnable {
    private final ActiveAndroidThreadInsert master;
    private Exception loadExceptions;
    private int threadCount;
    private short nextWarehouse = 1;

    public ActiveAndroidThreadInsert() {
        this.master = this;
    }

    private ActiveAndroidThreadInsert(ActiveAndroidThreadInsert master) {
        this.master = master;
    }

    public void setupLoad(short scale) throws SQLiteException {
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
                + ". Table count[item=" + new Select().from(Item.class).count()
                + ", warehouse=" + new Select().from(Warehouse.class).count()
                + ", stock=" + new Select().from(Stock.class).count()
                + ", district=" + new Select().from(District.class).count()
                + ", customer=" + new Select().from(Customer.class).count()
                + ", order=" + new Select().from(Orders.class).count()
                + ", history="+ new Select().from(History.class).count()
                + ", neworders=" + new Select().from(NewOrders.class).count()
                + ", orderline=" + new Select().from(OrderLine.class).count() + "]";
        AppContext.getInstance().SetUIInfo(log);

        if (this.threadCount == 1) {
            super.populateAllTables();
        } else {
            threadPopulate();
        }

        log = "**  After populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[item=" + new Select().from(Item.class).count()
                + ", warehouse=" + new Select().from(Warehouse.class).count()
                + ", stock=" + new Select().from(Stock.class).count()
                + ", district=" + new Select().from(District.class).count()
                + ", customer=" + new Select().from(Customer.class).count()
                + ", order=" + new Select().from(Orders.class).count()
                + ", history="+ new Select().from(History.class).count()
                + ", neworders=" + new Select().from(NewOrders.class).count()
                + ", orderline=" + new Select().from(OrderLine.class).count() + "]";
        AppContext.getInstance().SetUIInfo(log);
    }

    private void setScale(short scale) {
        this.scale = scale;
    }

    private void threadPopulate() throws InterruptedException, Exception {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        ActiveAndroidThreadInsert ti = null;
        for (int t = 1; t < this.threadCount; t++) {
            ti = new  ActiveAndroidThreadInsert(this);
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
        if (null == this.aaa) {
            try {
                setupAgent();
                setRandomGenerator();
            } catch (Exception e) {
                TPCCLog.e(ActiveAndroidThreadInsert.class.getName(), e);
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


