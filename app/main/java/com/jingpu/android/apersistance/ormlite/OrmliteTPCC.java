package com.jingpu.android.apersistance.ormlite;

import android.database.sqlite.SQLiteException;

import com.j256.ormlite.stmt.ColumnArg;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.ormlite.model.Customer;
import com.jingpu.android.apersistance.ormlite.model.District;
import com.jingpu.android.apersistance.ormlite.model.History;
import com.jingpu.android.apersistance.ormlite.model.NewOrders;
import com.jingpu.android.apersistance.ormlite.model.OrderLine;
import com.jingpu.android.apersistance.ormlite.model.Orders;
import com.jingpu.android.apersistance.ormlite.model.Stock;
import com.jingpu.android.apersistance.ormlite.model.Warehouse;
import com.jingpu.android.apersistance.util.TPCCLoad;
import com.jingpu.android.apersistance.util.TPCCLog;
import com.jingpu.android.apersistance.util.TPCCReporter;

import org.dacapo.derby.OERandom;
import org.dacapo.parser.Config;

import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jing Pu on 2015/10/23.
 */
public class OrmliteTPCC {
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
    private OrmliteAgent[] agents;
    private OrmliteSubmitter[] submitters;
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

    private OrmliteAgent agent;

    private boolean firstIteration;

    private long preIterationTime = 0;
    private long iterationTime = 0;
    private long postIterationTime = 0;
    //private long resetToInitialDataTime = 0;

    private String createSuffix = CREATE_SUFFIX;

    // A random seed for initializing the database and the OLTP terminals.
    final static long SEED = 897523978813691l;
    final static int SEED_STEP = 100000;

    public static OrmliteTPCC make(Config config, File scratch, Boolean verbose, Boolean preserve) throws Exception {
        return new OrmliteTPCC(config, scratch, verbose, preserve);
    }

    public OrmliteTPCC(Config config, File scratch, boolean verbose, boolean preserve) throws Exception {
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
        //connections = new Connection[numberOfTerminals];
        agents = new OrmliteAgent[numberOfTerminals];
        submitters = new OrmliteSubmitter[numberOfTerminals];
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

        OrmliteTPCCSubmitter.setSeed(SEED);

        preIterationDB();

        // make sure we have the same seeds each run
		for (int i = 0; i < rands.length; i++) {
        	rands[i] = new OERandom(SEED_STEP * i, SEED + SEED_STEP * i);
		}

        // create a Submitter for each thread
		for (int i = 0; i < submitters.length; i++) {
        	agents[i] = makeDbAgent();

        	OrmliteOperations ops = new OrmliteStandard(agents[i]);

        	submitters[i] = new OrmliteTPCCSubmitter(reporter, ops, rands[i], scale, i);
		}

        preIterationTime = System.currentTimeMillis() - start;
    }

    public void iteration(String size) throws Exception {
        long start = System.currentTimeMillis();

        AppContext.getInstance().SetUIInfo("Parameters [actualTerminalNum running transactions=" + submitters.length + "]");

        // we can't change size after the initial prepare(size)
        assert this.size.equalsIgnoreCase(size);

        // run all the submitters
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < submitters.length; i++) {
            submitters[i].clearTransactionCount();

            final OrmliteSubmitter submitter = submitters[i];
            final int count = transactionsPerTerminal[i];

            // run the thread: transactions
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        submitter.runTransactions(null, count);
                    } catch (Exception e) {
                        TPCCLog.e(OrmliteTPCC.class.getName(), e);
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
            TPCCLog.v(OrmliteTPCC.class.getName(), "Time to perform pre-iteration phase: " + preIterationTime + " msec");
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
        int[] transactions = new int[OrmliteSubmitter.NEW_ORDER_ROLLBACK + 1];
        int[] exceptions = new int[OrmliteSubmitter.EXCEPTION_GENERAL + 1];

        String dots = "..............................";
        StringBuffer sb = new StringBuffer();
        sb.append("\n== Ormlite benchmark result ==");
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
                sb.append(String.format("\t Terminal[" + i + "]" + OrmliteTPCCSubmitter.TX_NAME[j] + " " + dots.substring(OrmliteTPCCSubmitter.TX_NAME[j].length()) + "%6d%n", subTx[j]));
            }

            for (int j=0; j<subEx.length; j++) {
                exceptions[j] += subEx[j];
                totalEx += subEx[j];

                sb.append(String.format("\t Terminal[" + i + "]" + OrmliteTPCCSubmitter.EX_NAME[j] + " " + dots.substring(OrmliteTPCCSubmitter.EX_NAME[j].length()) + "%6d%n", subEx[j]));
            }

            sb.append("\n----------------------\n");
        }

        sb.append("\nCompleted " + totalTx + " transactions\n");
        sb.append(dots).append("\n");
        for (int i = 0; i < transactions.length; i++) {
            sb.append(String.format("\t" + OrmliteTPCCSubmitter.TX_NAME[i] + " " + dots.substring(OrmliteTPCCSubmitter.TX_NAME[i].length()) + "%6d (%4.1f%%)%n", transactions[i],
                    100 * ((float) transactions[i] / totalTx)));
        }

        sb.append(dots).append("\n");
        sb.append("\nOccurred " + totalEx + " transaction exceptions\n");
        for (int i = 0; i < exceptions.length; i++) {
            sb.append(String.format("\t" + OrmliteTPCCSubmitter.EX_NAME[i] + " " + dots.substring(OrmliteTPCCSubmitter.EX_NAME[i].length()) + "%6d (%4.1f%%)%n", exceptions[i],
                    100 * ((float) exceptions[i] / totalEx)));
        }

        TPCCLog.v(OrmliteTPCC.class.getName(), sb.toString());
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

        TPCCLoad loader = new OrmliteThreadInsert();
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
        OrmliteDBHelper dbHelper = getDbAgent().getDbHelper();

        // delete both objects but make sure that if either one fails, the transaction is rolled back
        // and both objects are "restored" to the database

        // "DELETE FROM DELIVERY_REQUEST"
        dbHelper.getDeliveryRequestDao().deleteBuilder().delete();

        // "DELETE FROM DELIVERY_ORDERS"
        dbHelper.getDeliveryOrdersDao().deleteBuilder().delete();

        // "DELETE FROM HISTORY WHERE H_INITIAL = FALSE"
        DeleteBuilder<History, Long> hdb = dbHelper.getHistroyDao().deleteBuilder();
        hdb.where().eq(History.COL_H_INITIAL, false);
        hdb.delete();

        // "DELETE FROM NEWORDERS WHERE NO_INITIAL = FALSE"
        DeleteBuilder<NewOrders, String> nodb = dbHelper.getNewOrdersDao().deleteBuilder();
        nodb.where().eq(NewOrders.COL_NO_INITIAL, false);
        nodb.delete();

        // "DELETE FROM ORDERLINE WHERE OL_INITIAL = FALSE"
        DeleteBuilder<OrderLine, String> oldb = dbHelper.getOrderLineDao().deleteBuilder();
        oldb.where().eq(OrderLine.COL_OL_INITIAL, false);
        oldb.delete();

        // "DELETE FROM ORDERS WHERE O_INITIAL = FALSE"
        DeleteBuilder<Orders, String> odb = dbHelper.getOrdersDao().deleteBuilder();
        odb.where().eq(Orders.COL_O_INITIAL, false);
        odb.delete();

        // although below seems a little inefficient we put the conditions in for
        // the
        // following reason: it keeps the commit set size low and therefore there is
        // less heap pressure
        // we also perform regular commits for the same reason

        // "UPDATE CUSTOMER SET C_DATA = C_DATA_INITIAL, C_BALANCE = -10.0, C_YTD_PAYMENT = 10.0, C_PAYMENT_CNT = 1, C_DELIVERY_CNT = 0 WHERE C_DATA <> C_DATA_INITIAL OR C_BALANCE <> -10.0 OR C_YTD_PAYMENT <> 10.0 OR C_PAYMENT_CNT <> 1 OR C_DELIVERY_CNT <> 0"
        UpdateBuilder<Customer, String> cub = dbHelper.getCustomerDao().updateBuilder();
        Where<Customer, String> cWhere = cub.where();
        cWhere.or(cWhere.ne(Customer.COL_C_DATA, new ColumnArg(Customer.COL_C_DATA_INITIAL)), cWhere.ne(Customer.COL_C_BALANCE, -10.0), cWhere.ne(Customer.COL_C_YTD_PAYMENT, 10.0), cWhere.ne(Customer.COL_C_PAYMENT_CNT, 1), cWhere.ne(Customer.COL_C_DELIVERY_CNT, 0));
        cub.updateColumnExpression(Customer.COL_C_DATA, Customer.COL_C_DATA_INITIAL);
        cub.updateColumnValue(Customer.COL_C_BALANCE, -10.0);
        cub.updateColumnValue(Customer.COL_C_YTD_PAYMENT, 10.0);
        cub.updateColumnValue(Customer.COL_C_PAYMENT_CNT, 1);
        cub.updateColumnValue(Customer.COL_C_DELIVERY_CNT, 0);
        cub.update();

        // "UPDATE DISTRICT SET D_YTD = 30000.0, D_NEXT_O_ID = 3001 WHERE D_YTD <> 30000.0 OR D_NEXT_O_ID <> 3001"
        UpdateBuilder<District, String> dub = dbHelper.getDistrictDao().updateBuilder();
        Where<District, String> dWhere = dub.where();
        dWhere.or(dWhere.ne(District.COL_D_YTD, 30000.0), dWhere.ne(District.COL_D_NEXT_O_ID, 3001));
        dub.updateColumnValue(District.COL_D_YTD, 30000.0);
        dub.updateColumnValue(District.COL_D_NEXT_O_ID, 3001);
        dub.update();

        // "UPDATE WAREHOUSE SET W_YTD = 300000.0 WHERE W_YTD <> 300000.0"
        UpdateBuilder<Warehouse, Long> wub = dbHelper.getWarehouseDao().updateBuilder();
        wub.where().ne(Warehouse.COL_W_YTD, 300000.0);
        wub.updateColumnValue(Warehouse.COL_W_YTD, 300000.0);
        wub.update();

        // "UPDATE STOCK SET S_QUANTITY = S_QUANTITY_INITIAL, S_ORDER_CNT = 0, S_YTD = 0, S_REMOTE_CNT = 0
        // WHERE S_QUANTITY <> S_QUANTITY_INITIAL OR S_ORDER_CNT <> 0 OR S_YTD <> 0 OR S_REMOTE_CNT <> 0"
        UpdateBuilder<Stock, String> sub = dbHelper.getStockDao().updateBuilder();
        Where<Stock, String> sWhere = sub.where();
        sWhere.or(sWhere.ne(Stock.COL_S_QUANTITY, new ColumnArg(Stock.COL_S_QUANTITY_INITIAL)), sWhere.ne(Stock.COL_S_ORDER_CNT, 0), sWhere.ne(Stock.COL_S_YTD, 0), sWhere.ne(Stock.COL_S_REMOTE_CNT, 0));
        sub.updateColumnExpression(Stock.COL_S_QUANTITY, Stock.COL_S_QUANTITY_INITIAL);
        sub.updateColumnValue(Stock.COL_S_ORDER_CNT, 0);
        sub.updateColumnValue(Stock.COL_S_YTD, 0);
        sub.updateColumnValue(Stock.COL_S_REMOTE_CNT, 0);
        sub.update();

        // "UPDATE ORDERS SET O_CARRIER_ID = O_CARRIER_ID_INITIAL WHERE O_CARRIER_ID <> O_CARRIER_ID_INITIAL"
        UpdateBuilder<Orders, String> oub = dbHelper.getOrdersDao().updateBuilder();
        oub.where().ne(Orders.COL_O_CARRIER_ID, new ColumnArg(Orders.COL_O_CARRIER_ID_INITIAL));
        oub.updateColumnExpression(Orders.COL_O_CARRIER_ID, Orders.COL_O_CARRIER_ID_INITIAL);
        oub.update();

        // "UPDATE ORDERLINE SET OL_DELIVERY_D = OL_DELIVERY_D_INITIAL WHERE OL_DELIVERY_D <> OL_DELIVERY_D_INITIAL"
        UpdateBuilder<OrderLine, String> olub = dbHelper.getOrderLineDao().updateBuilder();
        olub.where().ne(OrderLine.COL_OL_DELIVERY_D, new ColumnArg(OrderLine.COL_OL_DELIVERY_D_INITIAL));
        olub.updateColumnExpression(OrderLine.COL_OL_DELIVERY_D, OrderLine.COL_OL_DELIVERY_D_INITIAL);
        olub.update();
    }

    private OrmliteAgent getDbAgent() throws Exception {
        if (null == this.agent){
            this.agent = makeDbAgent();
        }

        return this.agent;
    }

    private OrmliteAgent makeDbAgent() throws Exception {
        return new OrmliteAgent(AppContext.getInstance());
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
