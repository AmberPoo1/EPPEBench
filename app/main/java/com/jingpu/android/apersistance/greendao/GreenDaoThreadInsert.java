package com.jingpu.android.apersistance.greendao;

import android.database.sqlite.SQLiteException;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.greendao.model.DaoSession;
import com.jingpu.android.apersistance.util.TPCCLog;

import org.dacapo.derby.Load;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jing Pu on 2016/1/20.
 */
public class GreenDaoThreadInsert extends GreenDaoSimpleInsert implements Runnable {
    private final GreenDaoThreadInsert master;
    private Exception loadExceptions;
    private int threadCount;
    private short nextWarehouse = 1;

    public GreenDaoThreadInsert() {
        this.master = this;
    }

    private GreenDaoThreadInsert(GreenDaoThreadInsert master) {
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

        DaoSession ds = ga.getDaoSession();
        String log = "**  Before populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
            + ". Table count[item=" + ds.getItemDao().count()
            + ", warehouse=" + ds.getWarehouseDao().count()
            + ", stock=" + ds.getStockDao().count()
            + ", district=" + ds.getDistrictDao().count()
            + ", customer=" + ds.getCustomerDao().count()
            + ", order=" + ds.getOrdersDao().count()
            + ", history="+ ds.getHistoryDao().count()
            + ", neworders=" + ds.getNewOrdersDao().count()
            + ", orderline=" + ds.getOrderLineDao().count() + "]";

        AppContext.getInstance().SetUIInfo(log);

        if (this.threadCount == 1) {
            super.populateAllTables();
        } else {
            threadPopulate();
        }

        log = "**  After populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
            + ". Table count[item=" + ds.getItemDao().count()
            + ", warehouse=" + ds.getWarehouseDao().count()
            + ", stock=" + ds.getStockDao().count()
            + ", district=" + ds.getDistrictDao().count()
            + ", customer=" + ds.getCustomerDao().count()
            + ", order=" + ds.getOrdersDao().count()
            + ", history="+ ds.getHistoryDao().count()
            + ", neworders=" + ds.getNewOrdersDao().count()
            + ", orderline=" + ds.getOrderLineDao().count() + "]";

        AppContext.getInstance().SetUIInfo(log);
    }

    private void setScale(short scale) {
        this.scale = scale;
    }

    private void threadPopulate() throws InterruptedException, Exception {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        GreenDaoThreadInsert ti = null;
        for (int t = 1; t < this.threadCount; t++) {
            ti = new GreenDaoThreadInsert(this);
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
        if (null == this.ga) {
            try {
                setupAgent();
                setRandomGenerator();
            } catch (Exception e) {
                TPCCLog.e(GreenDaoThreadInsert.class.getName(), e);
                e.printStackTrace();
            }
        }

        short w;
        while ((w = master.getNextWarehouse()) != -1) {
            try {
                populateForOneWarehouse(w);
            } catch (Exception e) {
                master.addException(e);
                break;
            }
        }
    }
}
