package com.jingpu.android.apersistance.ormlite;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.util.TPCCLog;

import org.dacapo.derby.Load;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jing Pu on 2016/1/20.
 */
public class OrmliteThreadInsert extends OrmliteSimpleInsert implements Runnable {
    private final OrmliteThreadInsert master;
    private Exception loadExceptions;
    private int threadCount;
    private short nextWarehouse = 1;

    public OrmliteThreadInsert() {
        this.master = this;
    }

    private OrmliteThreadInsert(OrmliteThreadInsert master) {
        this.master = master;
    }

    public void setupLoad(short scale) throws SQLException { //Session session,
        super.setupLoad(scale); //session,

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

        if (null == oa) {
            throw new NullPointerException();
        }

        String log = "**  Before populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[item=" + oa.getDbHelper().getItemDao().countOf()
                + ", warehouse=" + oa.getDbHelper().getWarehouseDao().countOf()
                + ", stock=" + oa.getDbHelper().getStockDao().countOf()
                + ", district=" + oa.getDbHelper().getDistrictDao().countOf()
                + ", customer=" + oa.getDbHelper().getCustomerDao().countOf()
                + ", order=" + oa.getDbHelper().getOrdersDao().countOf()
                + ", history="+ oa.getDbHelper().getHistroyDao().countOf()
                + ", neworders=" + oa.getDbHelper().getNewOrdersDao().countOf()
                + ", orderline=" + oa.getDbHelper().getOrderLineDao().countOf() + "]";
        AppContext.getInstance().SetUIInfo(log);

        if (this.threadCount == 1) {
            super.populateAllTables();
        } else {
            threadPopulate();
        }

        log = "**  After populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[item=" + oa.getDbHelper().getItemDao().countOf()
                + ", warehouse=" + oa.getDbHelper().getWarehouseDao().countOf()
                + ", stock=" + oa.getDbHelper().getStockDao().countOf()
                + ", district=" + oa.getDbHelper().getDistrictDao().countOf()
                + ", customer=" + oa.getDbHelper().getCustomerDao().countOf()
                + ", order=" + oa.getDbHelper().getOrdersDao().countOf()
                + ", history="+ oa.getDbHelper().getHistroyDao().countOf()
                + ", neworders=" + oa.getDbHelper().getNewOrdersDao().countOf()
                + ", orderline=" + oa.getDbHelper().getOrderLineDao().countOf() + "]";
        AppContext.getInstance().SetUIInfo(log);
    }

    private void setScale(short scale) {
        this.scale = scale;
    }

    private void threadPopulate() throws InterruptedException, Exception {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        OrmliteThreadInsert ti = null;
        for (int t = 1; t < this.threadCount; t++) {
            ti = new  OrmliteThreadInsert(this);
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
        if (null == this.oa) {
            try {
                setupAgent();
                setRandomGenerator();
            } catch (Exception e) {
                TPCCLog.e(OrmliteThreadInsert.class.getName(), e);
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


