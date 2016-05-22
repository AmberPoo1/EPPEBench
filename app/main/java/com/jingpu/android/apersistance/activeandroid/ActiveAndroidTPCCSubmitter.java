package com.jingpu.android.apersistance.activeandroid;

import android.database.sqlite.SQLiteConstraintException;

import com.jingpu.android.apersistance.util.OrderStatusException;
import com.jingpu.android.apersistance.util.PaymentException;
import com.jingpu.android.apersistance.util.TPCCLog;
import com.jingpu.android.apersistance.util.TPCCReporter;

import org.dacapo.derby.OERandom;

/**
 * Created by Jing Pu on 2016/1/3.
 */
public class ActiveAndroidTPCCSubmitter extends ActiveAndroidSubmitter {
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
        globalSeed += ActiveAndroidTPCC.SEED_STEP;
        return result;
    }

    public ActiveAndroidTPCCSubmitter(TPCCReporter reporter, ActiveAndroidOperations ops, OERandom rand, short maxW, int terminalId) {
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
                } catch (SQLiteConstraintException e) {
                    exceptionCount[EXCEPTION_CONSTRAINT]++;
                } catch (OrderStatusException e) {
                    exceptionCount[EXCEPTION_ORDERSTATUS]++;
                } catch (PaymentException e) {
                    exceptionCount[EXCEPTION_PAYMENT]++;
                } catch (Exception e) {
                    exceptionCount[EXCEPTION_GENERAL]++;
                    TPCCLog.e(ActiveAndroidTPCCSubmitter.class.getName(), e);
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
            case ActiveAndroidSubmitter.STOCK_LEVEL:
                runStockLevel(displayData);
                break;
            case ActiveAndroidSubmitter.ORDER_STATUS_BY_NAME:
                runOrderStatus(displayData, true);
                break;
            case ActiveAndroidSubmitter.ORDER_STATUS_BY_ID:
                runOrderStatus(displayData, false);
                break;
            case ActiveAndroidSubmitter.PAYMENT_BY_NAME:
                runPayment(displayData, true);
                break;
            case ActiveAndroidSubmitter.PAYMENT_BY_ID:
                runPayment(displayData, false);
                break;
            case ActiveAndroidSubmitter.DELIVERY_SCHEDULE:
                runScheduleDelivery(displayData);
                break;
            case ActiveAndroidSubmitter.NEW_ORDER:
                runNewOrder(displayData, false);
                break;
            case ActiveAndroidSubmitter.NEW_ORDER_ROLLBACK:
                runNewOrder(displayData, true);
                break;
        }
        return true;
    }

    // Jing Pu 5/14/2016 if transaction test, need to uncomment this method
    /*
    private boolean runTransaction(final int txType, final Object displayData) throws Exception {
        String strTxType = "";
        switch (txType) {
            case ActiveAndroidSubmitter.STOCK_LEVEL:
                strTxType = "Stock level";
                break;
            case ActiveAndroidSubmitter.ORDER_STATUS_BY_NAME:
                strTxType = "Order status by name";
                break;
            case ActiveAndroidSubmitter.ORDER_STATUS_BY_ID:
                strTxType = "Order status by ID";
                break;
            case ActiveAndroidSubmitter.PAYMENT_BY_NAME:
                strTxType = "Payment by name";
                break;
            case ActiveAndroidSubmitter.PAYMENT_BY_ID:
                strTxType = "Payment by ID";
                break;
            case ActiveAndroidSubmitter.DELIVERY_SCHEDULE:
                strTxType = "Delivery schedule";
                break;
            case ActiveAndroidSubmitter.NEW_ORDER:
                strTxType = "New order";
                break;
            case ActiveAndroidSubmitter.NEW_ORDER_ROLLBACK:
                strTxType = "New order rollback";
                break;
        }

        transStartTime = new Date().getTime();
        TPCCLog.v(ActiveAndroidTPCCSubmitter.class.getName(), "ActiveAndroid Transaction[" + strTxType
                + "(" + txType + ")" + "] start ms = " + transStartTime);

        switch (txType) {
            case ActiveAndroidSubmitter.STOCK_LEVEL:
                runStockLevel(displayData);
                break;
            case ActiveAndroidSubmitter.ORDER_STATUS_BY_NAME:
                runOrderStatus(displayData, true);
                break;
            case ActiveAndroidSubmitter.ORDER_STATUS_BY_ID:
                runOrderStatus(displayData, false);
                break;
            case ActiveAndroidSubmitter.PAYMENT_BY_NAME:
                runPayment(displayData, true);
                break;
            case ActiveAndroidSubmitter.PAYMENT_BY_ID:
                runPayment(displayData, false);
                break;
            case ActiveAndroidSubmitter.DELIVERY_SCHEDULE:
                runScheduleDelivery(displayData);
                break;
            case ActiveAndroidSubmitter.NEW_ORDER:
                runNewOrder(displayData, false);
                break;
            case ActiveAndroidSubmitter.NEW_ORDER_ROLLBACK:
                runNewOrder(displayData, true);
                break;
        }

        transEndTime = new Date().getTime();
        TPCCLog.v(ActiveAndroidTPCCSubmitter.class.getName(), "ActiveAndroid Transaction[" + strTxType
                + "(" + txType + ")" + "] end ms = " + transEndTime
                + ", duration = " + (transEndTime - transStartTime));

        return true;
    }
    */

    private static synchronized void doneTx() {
    }
}
