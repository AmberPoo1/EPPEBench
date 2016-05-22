package com.jingpu.android.apersistance.realm;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.realm.model.Customer;
import com.jingpu.android.apersistance.realm.model.District;
import com.jingpu.android.apersistance.realm.model.History;
import com.jingpu.android.apersistance.realm.model.Item;
import com.jingpu.android.apersistance.realm.model.NewOrders;
import com.jingpu.android.apersistance.realm.model.OrderLine;
import com.jingpu.android.apersistance.realm.model.Orders;
import com.jingpu.android.apersistance.realm.model.Stock;
import com.jingpu.android.apersistance.realm.model.Warehouse;
import com.jingpu.android.apersistance.util.TPCCLog;

import org.dacapo.derby.Load;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.exceptions.RealmException;

/**
 * Created by Jing Pu on 2016/1/26.
 */
public class RealmThreadInsert extends RealmSimpleInsert implements Runnable {
    private final RealmThreadInsert master;
    private Exception loadExceptions;
    private int threadCount;
    private short nextWarehouse = 1;

    public RealmThreadInsert() {
        this.master = this;
    }

    private RealmThreadInsert(RealmThreadInsert master) {
        this.master = master;
    }

    public void setupLoad(short scale) throws RealmException { //Session session,
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

        if (null == ra) {
            throw new NullPointerException();
        }

        Realm realm = null;
        String log = null;
        try {
            realm = ra.getRealmInstance();
            log = "**  Before populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[item=" + realm.where(Item.class).count()
                + ", warehouse=" + realm.where(Warehouse.class).count()
                + ", stock=" + realm.where(Stock.class).count()
                + ", district=" + realm.where(District.class).count()
                + ", customer=" + realm.where(Customer.class).count()
                + ", order=" + realm.where(Orders.class).count()
                + ", history="+ realm.where(History.class).count()
                + ", neworders=" + realm.where(NewOrders.class).count()
                + ", orderline=" + realm.where(OrderLine.class).count() + "]";
        } finally {
            if (null != realm) {
                realm.close();
            }
        }

        AppContext.getInstance().SetUIInfo(log);

        if (this.threadCount == 1) {
            super.populateAllTables();
        } else {
            threadPopulate();
        }

        try {
            realm = ra.getRealmInstance();
            log = "**  After populateAllTables:" + (new Date()).getTime() //System.currentTimeMillis()
                + ". Table count[item=" + realm.where(Item.class).count()
                + ", warehouse=" + realm.where(Warehouse.class).count()
                + ", stock=" + realm.where(Stock.class).count()
                + ", district=" + realm.where(District.class).count()
                + ", customer=" + realm.where(Customer.class).count()
                + ", order=" + realm.where(Orders.class).count()
                + ", history="+ realm.where(History.class).count()
                + ", neworders=" + realm.where(NewOrders.class).count()
                + ", orderline=" + realm.where(OrderLine.class).count() + "]";
        } finally {
            realm.close();
        }

        AppContext.getInstance().SetUIInfo(log);
    }

    private void setScale(short scale) {
        this.scale = scale;
    }

    private void threadPopulate() throws InterruptedException, Exception {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        RealmThreadInsert ti = null;
        for (int t = 1; t < this.threadCount; t++) {
            ti = new RealmThreadInsert(this);
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
        if (null == this.ra) {
            try {
                setupAgent();
                setRandomGenerator();
            } catch (Exception e) {
                TPCCLog.e(RealmThreadInsert.class.getName(), e);
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
