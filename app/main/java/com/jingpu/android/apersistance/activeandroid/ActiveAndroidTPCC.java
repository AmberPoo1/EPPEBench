package com.jingpu.android.apersistance.activeandroid;

import android.database.sqlite.SQLiteException;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Update;
import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.activeandroid.model.Customer;
import com.jingpu.android.apersistance.activeandroid.model.DeliveryOrders;
import com.jingpu.android.apersistance.activeandroid.model.DeliveryRequest;
import com.jingpu.android.apersistance.activeandroid.model.District;
import com.jingpu.android.apersistance.activeandroid.model.History;
import com.jingpu.android.apersistance.activeandroid.model.NewOrders;
import com.jingpu.android.apersistance.activeandroid.model.OrderLine;
import com.jingpu.android.apersistance.activeandroid.model.Orders;
import com.jingpu.android.apersistance.activeandroid.model.Stock;
import com.jingpu.android.apersistance.activeandroid.model.Warehouse;
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
 * Created by Jing Pu on 2016/1/2.
 */
public class ActiveAndroidTPCC {

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
    private ActiveAndroidAgent[] agents;
    private ActiveAndroidSubmitter[] submitters;
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

    private ActiveAndroidAgent agent;

    private boolean firstIteration;

    private long preIterationTime = 0;
    private long iterationTime = 0;
    private long postIterationTime = 0;

    private String createSuffix = CREATE_SUFFIX;

    // A random seed for initializing the database and the OLTP terminals.
    final static long SEED = 897523978813691l;
    final static int SEED_STEP = 100000;

    public static ActiveAndroidTPCC make(Config config, File scratch, Boolean verbose, Boolean preserve) throws Exception {
        return new ActiveAndroidTPCC(config, scratch, verbose, preserve);
    }

    public ActiveAndroidTPCC(Config config, File scratch, boolean verbose, boolean preserve) throws Exception {
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
        agents = new ActiveAndroidAgent[numberOfTerminals];
        submitters = new ActiveAndroidSubmitter[numberOfTerminals];
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

        ActiveAndroidTPCCSubmitter.setSeed(SEED);

        preIterationDB();

        // make sure we have the same seeds each run
		for (int i = 0; i < rands.length; i++) {
        	rands[i] = new OERandom(SEED_STEP * i, SEED + SEED_STEP * i);
		}

        // create a Submitter for each thread
		for (int i = 0; i < submitters.length; i++) {
        	agents[i] = makeDbAgent();

        	ActiveAndroidOperations ops = new ActiveAndroidStandard(agents[i]);

        	submitters[i] = new ActiveAndroidTPCCSubmitter(reporter, ops, rands[i], scale, i);
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

            final ActiveAndroidSubmitter submitter = submitters[i];
            final int count = transactionsPerTerminal[i];

            // run the thread: transactions
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        submitter.runTransactions(null, count);
                    } catch (Exception e) {
                        TPCCLog.e(ActiveAndroidTPCC.class.getName(), e);
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
            TPCCLog.v(ActiveAndroidTPCC.class.getName(), "Time to perform pre-iteration phase: " + preIterationTime + " msec");
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
        int[] transactions = new int[ActiveAndroidSubmitter.NEW_ORDER_ROLLBACK + 1];
        int[] exceptions = new int[ActiveAndroidSubmitter.EXCEPTION_GENERAL + 1];

        String dots = "..............................";
        StringBuffer sb = new StringBuffer();
        sb.append("\n== ActiveAndroid benchmark result ==");
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

                sb.append(String.format("\t Terminal[" + i + "]" + ActiveAndroidTPCCSubmitter.TX_NAME[j] + " " + dots.substring(ActiveAndroidTPCCSubmitter.TX_NAME[j].length()) + "%6d%n", subTx[j]));
            }

            for (int j=0; j<subEx.length; j++) {
                exceptions[j] += subEx[j];
                totalEx += subEx[j];

                sb.append(String.format("\t Terminal[" + i + "]" + ActiveAndroidTPCCSubmitter.EX_NAME[j] + " " + dots.substring(ActiveAndroidTPCCSubmitter.EX_NAME[j].length()) + "%6d%n", subEx[j]));
            }

            sb.append("\n----------------------\n");
        }

        sb.append("\nCompleted " + totalTx + " transactions\n");
        sb.append(dots).append("\n");

        for (int i = 0; i < transactions.length; i++) {
            sb.append(String.format("\t" + ActiveAndroidTPCCSubmitter.TX_NAME[i] + " " + dots.substring(ActiveAndroidTPCCSubmitter.TX_NAME[i].length()) + "%6d (%4.1f%%)%n", transactions[i],
                    100 * ((float) transactions[i] / totalTx)));
        }

        sb.append(dots).append("\n");
        sb.append("\nOccurred " + totalEx + " transaction exceptions\n");
        for (int i = 0; i < exceptions.length; i++) {
            sb.append(String.format("\t" + ActiveAndroidTPCCSubmitter.EX_NAME[i] + " " + dots.substring(ActiveAndroidTPCCSubmitter.EX_NAME[i].length()) + "%6d (%4.1f%%)%n", exceptions[i],
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

        TPCCLoad loader = new ActiveAndroidThreadInsert();
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

        // "DELETE FROM DELIVERY_REQUEST"
        new Delete().from(DeliveryRequest.class).execute();

        // "DELETE FROM DELIVERY_ORDERS"
        new Delete().from(DeliveryOrders.class).execute();

        // "DELETE FROM HISTORY WHERE H_INITIAL = FALSE"
        new Delete().from(History.class).where("H_INITIAL = FALSE").execute();

        // "DELETE FROM NEWORDERS WHERE NO_INITIAL = FALSE"
        new Delete().from(NewOrders.class).where("NO_INITIAL = FALSE").execute();

        // "DELETE FROM ORDERLINE WHERE OL_INITIAL = FALSE"
        new Delete().from(OrderLine.class).where("OL_INITIAL = FALSE").execute();

        // "DELETE FROM ORDERS WHERE O_INITIAL = FALSE"
        new Delete().from(Orders.class).where("O_INITIAL = FALSE").execute();

        // although below seems a little inefficient we put the conditions in for
        // the
        // following reason: it keeps the commit set size low and therefore there is
        // less heap pressure
        // we also perform regular commits for the same reason


        // "UPDATE CUSTOMER SET C_DATA = C_DATA_INITIAL, C_BALANCE = -10.0, C_YTD_PAYMENT = 10.0, C_PAYMENT_CNT = 1, C_DELIVERY_CNT = 0 WHERE C_DATA <> C_DATA_INITIAL OR C_BALANCE <> -10.0 OR C_YTD_PAYMENT <> 10.0 OR C_PAYMENT_CNT <> 1 OR C_DELIVERY_CNT <> 0"
        new Update(Customer.class).set("C_DATA = C_DATA_INITIAL, C_BALANCE = -10.0, C_YTD_PAYMENT = 10.0, C_PAYMENT_CNT = 1, C_DELIVERY_CNT = 0")
                .where("C_DATA <> C_DATA_INITIAL OR C_BALANCE <> -10.0 OR C_YTD_PAYMENT <> 10.0 OR C_PAYMENT_CNT <> 1 OR C_DELIVERY_CNT <> 0").execute();

        // "UPDATE DISTRICT SET D_YTD = 30000.0, D_NEXT_O_ID = 3001 WHERE D_YTD <> 30000.0 OR D_NEXT_O_ID <> 3001"
        new Update(District.class).set("D_YTD = 30000.0, D_NEXT_O_ID = 3001")
                .where("D_YTD <> 30000.0 OR D_NEXT_O_ID <> 3001").execute();

        // "UPDATE WAREHOUSE SET W_YTD = 300000.0 WHERE W_YTD <> 300000.0"
        new Update(Warehouse.class).set("W_YTD = 300000.0")
                .where("W_YTD <> 300000.0").execute();

        // "UPDATE STOCK SET S_QUANTITY = S_QUANTITY_INITIAL, S_ORDER_CNT = 0, S_YTD = 0, S_REMOTE_CNT = 0 WHERE S_QUANTITY <> S_QUANTITY_INITIAL OR S_ORDER_CNT <> 0 OR S_YTD <> 0 OR S_REMOTE_CNT <> 0"
        new Update(Stock.class).set("S_QUANTITY = S_QUANTITY_INITIAL, S_ORDER_CNT = 0, S_YTD = 0, S_REMOTE_CNT = 0")
                .where("S_QUANTITY <> S_QUANTITY_INITIAL OR S_ORDER_CNT <> 0 OR S_YTD <> 0 OR S_REMOTE_CNT <> 0").execute();

        // "UPDATE ORDERS SET O_CARRIER_ID = O_CARRIER_ID_INITIAL WHERE O_CARRIER_ID <> O_CARRIER_ID_INITIAL "
        new Update(Orders.class).set("O_CARRIER_ID = O_CARRIER_ID_INITIAL")
                .where("O_CARRIER_ID <> O_CARRIER_ID_INITIAL").execute();

        // "UPDATE ORDERLINE SET OL_DELIVERY_D = OL_DELIVERY_D_INITIAL WHERE OL_DELIVERY_D <> OL_DELIVERY_D_INITIAL"
        new Update(OrderLine.class).set("OL_DELIVERY_D = OL_DELIVERY_D_INITIAL")
                .where("OL_DELIVERY_D <> OL_DELIVERY_D_INITIAL").execute();
    }

    private ActiveAndroidAgent getDbAgent() throws Exception {
        if (null == this.agent){
            this.agent = makeDbAgent();
        }

        return this.agent;
    }

    private ActiveAndroidAgent makeDbAgent() throws Exception {
        return new ActiveAndroidAgent(AppContext.getInstance());
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
