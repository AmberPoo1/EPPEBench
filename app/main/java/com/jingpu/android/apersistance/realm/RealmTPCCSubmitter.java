package com.jingpu.android.apersistance.realm;

import com.jingpu.android.apersistance.util.OrderStatusException;
import com.jingpu.android.apersistance.util.PaymentException;
import com.jingpu.android.apersistance.util.RealmInsertException;
import com.jingpu.android.apersistance.util.TPCCLog;
import com.jingpu.android.apersistance.util.TPCCReporter;

import org.dacapo.derby.OERandom;

/**
 * Created by Jing Pu on 2016/1/26.
 */
public class RealmTPCCSubmitter extends RealmSubmitter {
    // percentage of transactions that we will tolerate failing
    // before giving up as a failed run.
    private final static int MAXIMUM_FAILURE_PERCENTAGE = 10;

    private static long globalSeed = 0;

    private OERandom rand;
    private TPCCReporter reporter;

    public static void setSeed(long seed) {
        globalSeed = seed;
    }

    private synchronized static long getNextSeed() {
        long result = globalSeed;
        globalSeed += RealmTPCC.SEED_STEP;
        return result;
    }

    public RealmTPCCSubmitter(TPCCReporter reporter, RealmOperations ops, OERandom rand, short maxW, int terminalId) {
        super(null, ops, rand, maxW);
        this.rand = rand;
        this.reporter = reporter;
        this.terminalId = terminalId;
    }

    @Override
    public long runTransactions(final Object displayData, final int count) throws Exception {
        for (int i = 0; i < count; i++) {
            rand.setSeed(getNextSeed());

            int txType = getTransactionType();
            boolean success = false;
            while (!success) {
                try {
                    success = runTransaction(txType, displayData);
                } catch (RealmInsertException e) {
                    exceptionCount[EXCEPTION_CONSTRAINT]++;
                } catch (OrderStatusException e) {
                    exceptionCount[EXCEPTION_ORDERSTATUS]++;
                } catch (PaymentException e) {
                    exceptionCount[EXCEPTION_PAYMENT]++;
                } catch (Exception e) {
                    exceptionCount[EXCEPTION_GENERAL]++;
                    TPCCLog.e(RealmTPCCSubmitter.class.getName(), e);
                }
            }

            transactionCount[txType]++;
            reporter.done();
        }

        // timing is done elsewhere
        return 0;
    }

    private int getTransactionType() {
        int value = rand.randomInt(1, 1000);
        for (int type = 0; type < TX_CUM_PROB.length; type++) {
            if (value <= TX_CUM_PROB[type])
                return type;
        }
        return -1; // unreachable
    }

    private boolean runTransaction(final int txType, final Object displayData) throws Exception {
        switch (txType) {
            case RealmSubmitter.STOCK_LEVEL:
                runStockLevel(displayData);
                break;
            case RealmSubmitter.ORDER_STATUS_BY_NAME:
                runOrderStatus(displayData, true);
                break;
            case RealmSubmitter.ORDER_STATUS_BY_ID:
                runOrderStatus(displayData, false);
                break;
            case RealmSubmitter.PAYMENT_BY_NAME:
                runPayment(displayData, true);
                break;
            case RealmSubmitter.PAYMENT_BY_ID:
                runPayment(displayData, false);
                break;
            case RealmSubmitter.DELIVERY_SCHEDULE:
                runScheduleDelivery(displayData);
                break;
            case RealmSubmitter.NEW_ORDER:
                runNewOrder(displayData, false);
                break;
            case RealmSubmitter.NEW_ORDER_ROLLBACK:
                runNewOrder(displayData, true);
                break;
        }
        return true;
    }

    private static synchronized void doneTx() {
    }
}
