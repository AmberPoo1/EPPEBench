package com.jingpu.android.apersistance.greendao;

import android.database.sqlite.SQLiteException;

import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.greendao.model.Customer;
import com.jingpu.android.apersistance.greendao.model.CustomerDao;
import com.jingpu.android.apersistance.greendao.model.DaoSession;
import com.jingpu.android.apersistance.greendao.model.District;
import com.jingpu.android.apersistance.greendao.model.DistrictDao;
import com.jingpu.android.apersistance.greendao.model.HistoryDao;
import com.jingpu.android.apersistance.greendao.model.NewOrdersDao;
import com.jingpu.android.apersistance.greendao.model.OrderLine;
import com.jingpu.android.apersistance.greendao.model.OrderLineDao;
import com.jingpu.android.apersistance.greendao.model.Orders;
import com.jingpu.android.apersistance.greendao.model.OrdersDao;
import com.jingpu.android.apersistance.greendao.model.Stock;
import com.jingpu.android.apersistance.greendao.model.StockDao;
import com.jingpu.android.apersistance.greendao.model.Warehouse;
import com.jingpu.android.apersistance.greendao.model.WarehouseDao;
import com.jingpu.android.apersistance.util.TPCCLoad;
import com.jingpu.android.apersistance.util.TPCCLog;
import com.jingpu.android.apersistance.util.TPCCReporter;

import org.dacapo.derby.OERandom;
import org.dacapo.parser.Config;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by Jing Pu on 2015/11/18.
 */
public class GreenDaoTPCC {
    private final static String CREATE_SUFFIX = "";

    // default configuration for external testing of derby
    // database scale (see TPC-C documentation) number of terminals (clients) that
    // run transactions
    private final static int DEF_NUM_OF_TERMINALS = 2;

    // database scale (see TPC-C documentation)
    private final static short DEF_SCALE = 1;

    // number of transactions each terminal (client) runs
    private final static int DEF_TRANSACTIONS_PER_TERMINAL = 100;

    // Basic configurable items

    // this loaderThreads seems determininstic and should be
    // to the number of CPU cores
    private short scale = DEF_SCALE;
    private int totalTransactions = DEF_NUM_OF_TERMINALS * DEF_TRANSACTIONS_PER_TERMINAL;
    private int numberOfTerminals = DEF_NUM_OF_TERMINALS;
    private int[] transactionsPerTerminal = {DEF_TRANSACTIONS_PER_TERMINAL, DEF_TRANSACTIONS_PER_TERMINAL};
    private boolean generate = false; // by default we use the pre-generated
    // database
    private boolean cleanupInIteration = false; // by default perform clean up in
    // preiteration phase
    private boolean reportPreIterationTimes = false;

    // OLTP runners
    private GreenDaoAgent[] agents;
    private GreenDaoSubmitter[] submitters;
    private TPCCReporter reporter = new TPCCReporter();
    private OERandom[] rands;

    private Config config;
    private File scratch;
    private boolean verbose;
    private boolean preserve;
    private String size;
    private int uiTotalTrans;
    private short uiScale;
    private int uiTerminals;
    private boolean useUIParam;

    private GreenDaoAgent agent;

    private boolean firstIteration;

    private long preIterationTime = 0;
    private long iterationTime = 0;
    private long postIterationTime = 0;

    private String createSuffix = CREATE_SUFFIX;

    // A random seed for initializing the database and the OLTP terminals.
    final static long SEED = 897523978813691l;
    final static int SEED_STEP = 100000;

    public static GreenDaoTPCC make(Config config, File scratch, Boolean verbose, Boolean preserve) throws Exception {
        return new GreenDaoTPCC(config, scratch, verbose, preserve);
    }

    public GreenDaoTPCC(Config config, File scratch, boolean verbose, boolean preserve) throws Exception {
        this.config = config;
        this.scratch = scratch;
        this.verbose = verbose;
        this.preserve = preserve;
    }

    public void prepare(String size, int uiTotalTrans, short uiScale, int uiTerminals, boolean useUIParam) throws Exception {

        this.firstIteration = true;
        this.size = size;
        this.uiTotalTrans = uiTotalTrans;
        this.uiScale = uiScale;
        this.uiTerminals = uiTerminals;
        this.useUIParam = useUIParam;

        configure();

        // make a seeded random number generator for each submitter
        rands = new OERandom[numberOfTerminals];

        // create a set of Submitter each with a Standard operations implementation
        agents = new GreenDaoAgent[numberOfTerminals];
        submitters = new GreenDaoSubmitter[numberOfTerminals];
        transactionsPerTerminal = new int[numberOfTerminals];

        // set up the transactions for each terminal
        final int iterationsPerClient = totalTransactions / numberOfTerminals;
        final int oddIterations = totalTransactions - (iterationsPerClient * numberOfTerminals);

		for (int i = 0; i < numberOfTerminals; i++)
        	transactionsPerTerminal[i] = iterationsPerClient + (i < oddIterations ? 1 : 0);
    }

    private void preIterationDB() throws Exception {

        // delete the database if it exists
        if (preserve && !firstIteration)
            deleteDatabase();

        if (firstIteration) {

            // create the database
            createSchemaAndConstraints();

            // generate data
            loadData();

            // close last connection returning database to a stable state
            closeDbAgent();

        } else if (!cleanupInIteration) {
            resetToInitialData();
        }
    }

    public void preIteration(String size) throws Exception {
        long start = System.currentTimeMillis();

        // we can't change size after the initial prepare(size)
        assert this.size.equalsIgnoreCase(size);

        reporter.reset();

        GreenDaoTPCCSubmitter.setSeed(SEED);

        preIterationDB();

        // make sure we have the same seeds each run
		for (int i = 0; i < rands.length; i++) {
        	rands[i] = new OERandom(SEED_STEP * i, SEED + SEED_STEP * i);
		}

        // create a Submitter for each thread
		for (int i = 0; i < submitters.length; i++) {
        	agents[i] = makeDbAgent();

        	GreenDaoOperations ops = new GreenDaoStandard(agents[i]);

        	submitters[i] = new GreenDaoTPCCSubmitter(reporter, ops, rands[i], scale, i);
		}

        preIterationTime = System.currentTimeMillis() - start;
    }

    public void iteration(String size) throws Exception {
        long start = System.currentTimeMillis();
        AppContext.getInstance().SetUIInfo("Parameters [actualTerminalNum running transactions=" + submitters.length + "]");

        // we can't change size after the initial prepare(size)
        assert this.size.equalsIgnoreCase(size);

        // run all the submitters.
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < submitters.length; i++) {
            submitters[i].clearTransactionCount();

            final GreenDaoSubmitter submitter = submitters[i];
            final int count = transactionsPerTerminal[i];

            // run the thread: transactions
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        submitter.runTransactions(null, count);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        // reject new incoming threads
        cachedThreadPool.shutdown();

        // and then wait for them to finish
        while (true) {
            if (cachedThreadPool.isTerminated()) {
                break;
            }
        }

        iterationTime = System.currentTimeMillis() - start;

        // done running the submitters
        report(System.out);
    }

    public void postIteration(String size) throws Exception {
        long start = System.currentTimeMillis();

        if (verbose && (firstIteration)) {
            TPCCLog.v(GreenDaoTPCC.class.getName(), "Time to perform pre-iteration phase: " + preIterationTime + " msec");
        }

        firstIteration = false;

        // we can't change size after the initial prepare(size)
        assert this.size.equalsIgnoreCase(size);

		for (int i = 0; i < submitters.length; i++) {
        	submitters[i] = null;
        	agents[i] = null;
		}

        if (!preserve)
            deleteDatabase();

        postIterationTime = System.currentTimeMillis() - start;
    }

    public void cleanup() throws Exception {
        if (!preserve)
            deleteDatabase();
    }

    /**
     * helper function for recursively deleting the database directory
     * @return
     * @throws SQLiteException
     */
    private boolean deleteDatabase() throws SQLiteException {
        return true;
    }

    private void report(PrintStream os) {

        int totalTx = 0;
        int totalEx = 0;
        int[] transactions = new int[GreenDaoSubmitter.NEW_ORDER_ROLLBACK + 1];
        int[] exceptions = new int[GreenDaoSubmitter.EXCEPTION_GENERAL + 1];

        String dots = "..............................";
        StringBuffer sb = new StringBuffer();
        sb.append("\n== GreenDao benchmark result ==");
        sb.append("\nTotal running time: " + (preIterationTime + iterationTime + postIterationTime) + "\n");
        sb.append("\n- Pre iteration time: " + preIterationTime  + "\n");
        sb.append("\n- Iteration time: " + iterationTime  + "\n");
        sb.append("\n- Post iteration time: " + postIterationTime + "\n");

        sb.append(dots).append("\n");
        sb.append("\nTerminal transaction/exception statistics \n");

        for (int i = 0; i < submitters.length; i++) {
            int[] subTx = submitters[i].getTransactionCount();
            int[] subEx = submitters[i].getExceptionCount();

            for (int j = 0; j < subTx.length; j++) {
                transactions[j] += subTx[j];
                totalTx += subTx[j];

                sb.append(String.format("\t Terminal[" + i + "]" + GreenDaoTPCCSubmitter.TX_NAME[j] + " " + dots.substring(GreenDaoTPCCSubmitter.TX_NAME[j].length()) + "%6d%n", subTx[j]));
            }

            for (int j=0; j<subEx.length; j++) {
                exceptions[j] += subEx[j];
                totalEx += subEx[j];

                sb.append(String.format("\t Terminal[" + i + "]" + GreenDaoTPCCSubmitter.EX_NAME[j] + " " + dots.substring(GreenDaoTPCCSubmitter.EX_NAME[j].length()) + "%6d%n", subEx[j]));
            }

            sb.append("\n----------------------\n");
        }

        sb.append("\nCompleted " + totalTx + " transactions\n");
        sb.append(dots).append("\n");

        for (int i = 0; i < transactions.length; i++) {
            sb.append(String.format("\t" + GreenDaoTPCCSubmitter.TX_NAME[i] + " " + dots.substring(GreenDaoTPCCSubmitter.TX_NAME[i].length()) + "%6d (%4.1f%%)%n", transactions[i],
                    100 * ((float) transactions[i] / totalTx)));
        }

        sb.append(dots).append("\n");
        sb.append("\nOccurred " + totalEx + " transaction exceptions\n");
        for (int i = 0; i < exceptions.length; i++) {
            sb.append(String.format("\t" + GreenDaoTPCCSubmitter.EX_NAME[i] + " " + dots.substring(GreenDaoTPCCSubmitter.EX_NAME[i].length()) + "%6d (%4.1f%%)%n", exceptions[i],
                    100 * ((float) exceptions[i] / totalEx)));
        }

        AppContext.getInstance().SetUIInfo(sb.toString());
    }

    private void createSchemaAndConstraints() throws Exception {
        // create schema
        getDbAgent().createSchemaAndConstraints();
    }

    private void loadData() throws Exception {

        // Use simple insert statements to insert data.
        // currently only this form of load is present, once we have
        // different implementations, the loading mechanism will need
        // to be configurable taking an option from the command line
        // arguments.

        TPCCLoad loader = new GreenDaoThreadInsert();
        loader.setSeed(SEED);
        loader.setupLoad(scale);

        loader.populateAllTables();

        // Way to populate data is extensible. Any other implementation
        // of org.apache.derbyTesting.system.oe.client.Load can be used
        // to load data. configurable using the oe.load.insert property
        // that is defined in oe.properties
        // One extension would be to have an implementation that
        // uses bulkinsert vti to load data.

        return;
    }

    private void resetToInitialData() throws Exception {

        // there are no initial delivery requests or orders so remove all
        // residual entries
        // delete both objects but make sure that if either one fails, the transaction is rolled back
        // and both objects are "restored" to the database

        DaoSession ds = getDbAgent().getDaoSession();

        // "DELETE FROM DELIVERY_REQUEST"
        ds.getDeliveryRequestDao().deleteAll();

        // "DELETE FROM DELIVERY_ORDERS"
        ds.getDeliveryOrdersDao().deleteAll();

        // "DELETE FROM HISTORY WHERE H_INITIAL = FALSE"
        ds.getHistoryDao().queryBuilder().where(HistoryDao.Properties.HInitial.eq(false)).buildDelete();

        // "DELETE FROM NEWORDERS WHERE NO_INITIAL = FALSE"
        ds.getNewOrdersDao().queryBuilder().where(NewOrdersDao.Properties.NoInitial.eq(false)).buildDelete();

        // "DELETE FROM ORDERLINE WHERE OL_INITIAL = FALSE"
        ds.getOrderLineDao().queryBuilder().where(OrderLineDao.Properties.OlInitial.eq(false)).buildDelete();

        // "DELETE FROM ORDERS WHERE O_INITIAL = FALSE"
        ds.getOrdersDao().queryBuilder().where(OrdersDao.Properties.OInitial.eq(false)).buildDelete();

        // although below seems a little inefficient we put the conditions in for
        // the
        // following reason: it keeps the commit set size low and therefore there is
        // less heap pressure
        // we also perform regular commits for the same reason

        // "UPDATE CUSTOMER SET C_DATA = C_DATA_INITIAL, C_BALANCE = -10.0, C_YTD_PAYMENT = 10.0, C_PAYMENT_CNT = 1, C_DELIVERY_CNT = 0 WHERE C_DATA <> C_DATA_INITIAL OR C_BALANCE <> -10.0 OR C_YTD_PAYMENT <> 10.0 OR C_PAYMENT_CNT <> 1 OR C_DELIVERY_CNT <> 0"
        CustomerDao cusDao = ds.getCustomerDao();
        QueryBuilder qb = cusDao.queryBuilder();
        qb.where(qb.or(new WhereCondition.StringCondition("C_DATA <> C_DATA_INITIAL"),
                CustomerDao.Properties.CBalance.notEq(-10.0),
                CustomerDao.Properties.CYtdPayment.notEq(10.0),
                CustomerDao.Properties.CPaymentCnt.notEq(1),
                CustomerDao.Properties.CDeliveryCnt.notEq(0)));
        List<Customer> cusList = qb.list();
        for (Customer cus : cusList) {
            cus.setCData(cus.getCDataInitial());
            cus.setCBalance(-10.0f);
            cus.setCYtdPayment(10.0f);
            cus.setCPaymentCnt(1);
            cus.setCDeliveryCnt(0);
        }
        cusDao.updateInTx(cusList);

        // "UPDATE DISTRICT SET D_YTD = 30000.0, D_NEXT_O_ID = 3001 WHERE D_YTD <> 30000.0 OR D_NEXT_O_ID <> 3001"
        DistrictDao distDao = ds.getDistrictDao();
        qb = distDao.queryBuilder();

        qb.where(qb.or(DistrictDao.Properties.DYtd.notEq(30000.0),
                DistrictDao.Properties.DNextOId.notEq(3001)));
        List<District> dists = qb.list();
        for (District dist : dists) {
            dist.setDYtd(30000.0f);
            dist.setDNextOId(3001);
        }
        distDao.updateInTx(dists);

        // "UPDATE WAREHOUSE SET W_YTD = 300000.0 WHERE W_YTD <> 300000.0"
        WarehouseDao wDao = ds.getWarehouseDao();
        qb = wDao.queryBuilder();
        qb.where(WarehouseDao.Properties.WYtd.notEq(300000.0));
        List<Warehouse> ws = qb.list();
        for (Warehouse w: ws) {
            w.setWYtd(300000.0f);
        }
        wDao.updateInTx(ws);

        // "UPDATE STOCK SET S_QUANTITY = S_QUANTITY_INITIAL, S_ORDER_CNT = 0, S_YTD = 0, S_REMOTE_CNT = 0 WHERE S_QUANTITY <> S_QUANTITY_INITIAL OR S_ORDER_CNT <> 0 OR S_YTD <> 0 OR S_REMOTE_CNT <> 0"
        StockDao sDao = ds.getStockDao();
        qb = sDao.queryBuilder();
        qb.where(qb.or(new WhereCondition.StringCondition("S_QUANTITY <> S_QUANTITY_INITIAL"),
                StockDao.Properties.SOrderCnt.notEq(0),
                StockDao.Properties.SYtd.notEq(0),
                StockDao.Properties.SRemoteCnt.notEq(0)));
        List<Stock> ss = qb.list();
        for (Stock s : ss) {
            s.setSQuantity(s.getSQuantityInitial());
            s.setSOrderCnt(0);
            s.setSYtd(0);
            s.setSRemoteCnt(0);
        }
        sDao.updateInTx(ss);

        // "UPDATE ORDERS SET O_CARRIER_ID = O_CARRIER_ID_INITIAL WHERE O_CARRIER_ID <> O_CARRIER_ID_INITIAL"
        OrdersDao oDao = ds.getOrdersDao();
        qb = oDao.queryBuilder();
        qb.where(OrdersDao.Properties.OCarrierId.notEq(OrdersDao.Properties.OCarrierIdInitial));
        List<Orders> os = qb.list();
        for (Orders o : os) {
            o.setOCarrierId(o.getOCarrierIdInitial());
        }
        oDao.updateInTx(os);

        // "UPDATE ORDERLINE SET OL_DELIVERY_D = OL_DELIVERY_D_INITIAL WHERE OL_DELIVERY_D <> OL_DELIVERY_D_INITIAL"
        OrderLineDao olDao = ds.getOrderLineDao();
        qb = olDao.queryBuilder();
        qb.where(new WhereCondition.StringCondition("OL_DELIVERY_D <> OL_DELIVERY_DINITIAL"));
        List<OrderLine> ols = qb.list();
        for (OrderLine ol : ols) {
            ol.setOlDeliveryD(ol.getOlDeliveryDInitial());
        }
        olDao.updateInTx(ols);
    }

    private GreenDaoAgent getDbAgent() throws Exception {
        if (null == this.agent){
            this.agent = makeDbAgent();
        }

        return this.agent;
    }

    private GreenDaoAgent makeDbAgent() throws Exception {
        return new GreenDaoAgent(AppContext.getInstance());
    }

    private void closeDbAgent() throws Exception {

    }

    private void configure() {
        String[] args = config.preprocessArgs(size, scratch);

        for (int i = 0; i < args.length; i++) {
    
            if ("--numberOfTerminals".equalsIgnoreCase(args[i])) {
                this.numberOfTerminals = Integer.parseInt(args[++i]);
            } else
            if ("--total-transactions".equalsIgnoreCase(args[i])) {
                this.totalTransactions = Integer.parseInt(args[++i]);
            } else if ("--scale".equalsIgnoreCase(args[i])) {
                this.scale = Short.parseShort(args[++i]);
            } else if ("--generate".equalsIgnoreCase(args[i])) {
                this.generate = true;
            } else if ("--report-pre-iteration-times".equalsIgnoreCase(args[i])) {
                this.reportPreIterationTimes = true;
            } else if ("--cleanup-in-iteration".equalsIgnoreCase(args[i])) {
                this.cleanupInIteration = true;
            } else if ("--create-suffix".equalsIgnoreCase(args[i])) {
                this.createSuffix = args[++i];
            }
        }

        if (useUIParam) {
            this.totalTransactions = this.uiTotalTrans;
            this.scale = this.uiScale;
            this.numberOfTerminals = this.uiTerminals;
        }
    }

}
