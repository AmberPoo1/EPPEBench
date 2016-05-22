package com.jingpu.android.apersistance.activeandroid;


import android.database.sqlite.SQLiteException;

import com.activeandroid.query.Select;
import com.jingpu.android.apersistance.activeandroid.model.C;

import org.dacapo.derby.OERandom;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by Jing Pu on 2016/1/3.
 */
public class ActiveAndroidSubmitter {
    public static final int STOCK_LEVEL = 0;
    public static final int ORDER_STATUS_BY_NAME = 1;
    public static final int ORDER_STATUS_BY_ID = 2;
    public static final int PAYMENT_BY_NAME = 3;
    public static final int PAYMENT_BY_ID = 4;
    public static final int DELIVERY_SCHEDULE = 5;
    public static final int NEW_ORDER = 6;
    public static final int NEW_ORDER_ROLLBACK = 7;
    public static final int[] TX_CUM_PROB = { 40, 64, 80, 338, 510, 550, 995, 1000 };
    public static final String[] TX_NAME = { "Stock level", "Order status by name", "Order status by ID", "Payment by name", "Payment by ID", "Delivery schedule", "New order", "New order rollback" };
    private final ActiveAndroidDisplay display;
    private final ActiveAndroidOperations ops;
    private final OERandom rand;
    private final short maxW;
    protected int terminalId;
    public final int[] transactionCount;

    protected static final int EXCEPTION_PAYMENT = 0;
    protected static final int EXCEPTION_ORDERSTATUS = 1;
    protected static final int EXCEPTION_CONSTRAINT = 2;
    protected static final int EXCEPTION_GENERAL = 3;
    public static final String[] EX_NAME = { "Payment by name no match", "Order status by name no match", "Record existed", "General exception"};
    protected final int[] exceptionCount;

    public static OERandom getRuntimeRandom(ActiveAndroidAgent ga) throws SQLiteException { // Connection conn
        OERandom rand = new OERandom(-1);

        // "SELECT CLOAD FROM C"
        C cObj = new Select().from(C.class).executeSingle();

        if (null == cObj) {
            throw new NullPointerException();
        }
        int cload = cObj.getCLoad();

        int c;
        int delta;
        do {
            c = rand.randomInt(0, 255);
            delta = Math.abs(cload - c);
        } while ((delta == 96) || (delta == 112) ||
                (delta < 65) || (delta > 119));

        rand = new OERandom(c);

        return rand;
    }

    public static ActiveAndroidSubmitter stockLevelOnly(ActiveAndroidDisplay display, ActiveAndroidOperations ops, OERandom rand, short maxW) {
        return new ActiveAndroidSubmitter(display, ops, rand, maxW) {
            protected int mixType(int chooseType) {
                return ActiveAndroidSubmitter.STOCK_LEVEL; //0
            }
        };
    }

    public static ActiveAndroidSubmitter orderStatusByIdOnly(ActiveAndroidDisplay display, ActiveAndroidOperations ops, OERandom rand, short maxW) {
        return new ActiveAndroidSubmitter(display, ops, rand, maxW) {
            protected int mixType(int chooseType) {
                return ActiveAndroidSubmitter.ORDER_STATUS_BY_ID; //2
            }
        };
    }

    public static ActiveAndroidSubmitter orderStatusByNameOnly(ActiveAndroidDisplay display, ActiveAndroidOperations ops, OERandom rand, short maxW) {
        return new ActiveAndroidSubmitter(display, ops, rand, maxW) {
            protected int mixType(int chooseType) {
                return ActiveAndroidSubmitter.ORDER_STATUS_BY_NAME; //1
            }
        };
    }

    public static ActiveAndroidSubmitter paymentByIdOnly(ActiveAndroidDisplay display, ActiveAndroidOperations ops, OERandom rand, short maxW) {
        return new ActiveAndroidSubmitter(display, ops, rand, maxW) {
            protected int mixType(int chooseType) {
                return ActiveAndroidSubmitter.PAYMENT_BY_ID; //4
            }
        };
    }

    public static ActiveAndroidSubmitter paymentByNameOnly(ActiveAndroidDisplay display, ActiveAndroidOperations ops, OERandom rand, short maxW) {
        return new ActiveAndroidSubmitter(display, ops, rand, maxW) {
            protected int mixType(int chooseType) {
                return ActiveAndroidSubmitter.PAYMENT_BY_NAME; //3
            }
        };
    }

    public static ActiveAndroidSubmitter newOrderOnly(ActiveAndroidDisplay display, ActiveAndroidOperations ops, OERandom rand, short maxW) {
        return new ActiveAndroidSubmitter(display, ops, rand, maxW) {
            protected int mixType(int chooseType) {
                return ActiveAndroidSubmitter.NEW_ORDER; //6
            }
        };
    }

    public ActiveAndroidSubmitter(ActiveAndroidDisplay display, ActiveAndroidOperations ops, OERandom rand, short maxW) {
        this.display = display;
        this.ops = ops;
        this.rand = rand;
        this.maxW = maxW;

        this.transactionCount = new int[NEW_ORDER_ROLLBACK+1];
        this.exceptionCount = new int[EXCEPTION_GENERAL+1];
    }

    public void clearTransactionCount() {
        Arrays.fill(this.transactionCount, 0);
    }

    public long runTransactions(Object displayData, int count) throws Exception {
        long startms = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            runTransaction(displayData);
        }
        long endms = System.currentTimeMillis();

        return endms - startms;
    }

    public void runTransaction(Object displayData) throws Exception {
        int chooseType = this.rand.randomInt(1, 100);

        int type = mixType(chooseType);
        switch (type) {
            case ActiveAndroidSubmitter.DELIVERY_SCHEDULE: //5
                runScheduleDelivery(displayData);
                break;
            case ActiveAndroidSubmitter.NEW_ORDER: //6
                runNewOrder(displayData, false);
                break;
            case ActiveAndroidSubmitter.NEW_ORDER_ROLLBACK: //7
                runNewOrder(displayData, true);
                break;
            case ActiveAndroidSubmitter.ORDER_STATUS_BY_ID: //2
                runOrderStatus(displayData, false);
                break;
            case ActiveAndroidSubmitter.ORDER_STATUS_BY_NAME: //1
                runOrderStatus(displayData, true);
                break;
            case ActiveAndroidSubmitter.PAYMENT_BY_ID: //4
                runPayment(displayData, false);
                break;
            case ActiveAndroidSubmitter.PAYMENT_BY_NAME: //3
                runPayment(displayData, true);
                break;
            case ActiveAndroidSubmitter.STOCK_LEVEL:  //0
                runStockLevel(displayData);
        }
        this.transactionCount[type] += 1;
    }

    protected int mixType(int chooseType) {
        if (chooseType <= 43) {
            boolean byName = this.rand.randomInt(1, 100) <= 60;
            return byName ? ActiveAndroidSubmitter.PAYMENT_BY_NAME : ActiveAndroidSubmitter.PAYMENT_BY_ID; //3 : 4
        }
        if (chooseType <= 47) {
            boolean byName = this.rand.randomInt(1, 100) <= 60;
            return byName ? ActiveAndroidSubmitter.ORDER_STATUS_BY_NAME : ActiveAndroidSubmitter.ORDER_STATUS_BY_ID; //1 : 2
        }
        if (chooseType <= 51) {
            return ActiveAndroidSubmitter.DELIVERY_SCHEDULE; //5
        }
        if (chooseType <= 55) {
            return ActiveAndroidSubmitter.STOCK_LEVEL; //0
        }
        boolean rollback = this.rand.randomInt(1, 100) == 1;
        return rollback ? ActiveAndroidSubmitter.NEW_ORDER_ROLLBACK : ActiveAndroidSubmitter.NEW_ORDER; //7 : 6
    }

    protected void runNewOrder(Object displayData, boolean forRollback) throws Exception {
        short homeWarehouse = warehouse();

        int orderItemCount = this.rand.randomInt(5, 15);

        int[] items = new int[orderItemCount];
        short[] quantities = new short[orderItemCount];
        short[] supplyW = new short[orderItemCount];
        for (int i = 0; i < orderItemCount; i++) {
            items[i] = this.rand.NURand8191();
            if ((this.maxW == 1) || (this.rand.randomInt(1, 100) > 1)) {
                supplyW[i] = homeWarehouse;
            }
            else {
                short sw = warehouse();
                while (sw == homeWarehouse) {
                    sw = warehouse();
                }
                supplyW[i] = sw;
            }
            supplyW[i] = (this.rand.randomInt(1, 100) > 1 ? homeWarehouse : warehouse());

            quantities[i] = ((short)this.rand.randomInt(1, 10));
        }
        if (forRollback) {
            items[(orderItemCount - 1)] = 2334432;
        }
        this.ops.newOrder(this.terminalId, this.display, displayData, homeWarehouse, this.rand.district(), this.rand.NURand1023(), items, quantities, supplyW);
    }

    protected void runScheduleDelivery(Object displayData) {
    }

    protected void runPayment(Object displayData, boolean byName) throws Exception {
        if (byName) {
            this.ops.payment(this.terminalId, this.display, displayData, warehouse(), this.rand.district(), warehouse(), this.rand.district(), this.rand.randomCLast(), this.rand.payment().toString());
        } else {
            this.ops.payment(this.terminalId, this.display, displayData, warehouse(), this.rand.district(), warehouse(), this.rand.district(), this.rand.NURand1023(), this.rand.payment().toString());
        }
    }

    private final short warehouse() {
        if (this.maxW == 1) {
            return 1;
        }
        return (short)this.rand.randomInt(1, this.maxW);
    }

    protected void runStockLevel(Object displayData) throws Exception {
        this.ops.stockLevel(this.terminalId, this.display, displayData, warehouse(), this.rand.district(), this.rand.threshold());
    }

    protected void runOrderStatus(Object displayData, boolean byName) throws Exception {
        if (byName) {
            this.ops.orderStatus(this.terminalId, this.display, displayData, warehouse(), this.rand.district(), this.rand.randomCLast());
        } else {
            this.ops.orderStatus(this.terminalId, this.display, displayData, warehouse(), this.rand.district(), this.rand.NURand1023());
        }
    }

    public void printReport(PrintStream out) {
        int total = 0;
        for (int i = 0; i < this.transactionCount.length; i++) {
            total += this.transactionCount[i];
        }
        out.println("Total Transactions: " + total);

        int noTotal = this.transactionCount[NEW_ORDER] + this.transactionCount[NEW_ORDER_ROLLBACK]; //6,7

        int pyCount = this.transactionCount[PAYMENT_BY_NAME] + this.transactionCount[PAYMENT_BY_ID]; //3,4

        int osCount = this.transactionCount[ORDER_STATUS_BY_NAME] + this.transactionCount[ORDER_STATUS_BY_ID]; //1,2
        if (noTotal != 0) {
            out.println(transactionCount("New Order         ", noTotal, total));
        }
        if (pyCount != 0) {
            out.println(transactionCount("Payment           ", pyCount, total));
            out.println(transactionCount("    By Name       ", this.transactionCount[PAYMENT_BY_NAME], total)); //3
            out.println(transactionCount("    By Identifier ", this.transactionCount[PAYMENT_BY_ID], total)); //4
        }
        if (osCount != 0) {
            out.println(transactionCount("Order Status      ", osCount, total));
            out.println(transactionCount("    By Name       ", this.transactionCount[ORDER_STATUS_BY_NAME], total)); //1
            out.println(transactionCount("    By Identifier ", this.transactionCount[ORDER_STATUS_BY_ID], total)); //2
        }
        if (this.transactionCount[STOCK_LEVEL] != 0) { //0
            out.println(transactionCount("Stock Level       ", this.transactionCount[STOCK_LEVEL], total)); //0
        }
        if (this.transactionCount[DELIVERY_SCHEDULE] != 0) { //5
            out.println(transactionCount("Schedule Delivery ", this.transactionCount[DELIVERY_SCHEDULE], total)); //5
        }
    }

    private String transactionCount(String name, int count, int total) {
        return name + " : " + percent(count, total) + "(" + count + ")";
    }

    private String percent(int count, int total) {
        BigDecimal c = new BigDecimal(count * 100L);
        BigDecimal t = new BigDecimal(total);

        BigDecimal p = c.divide(t, 2, BigDecimal.ROUND_DOWN); //1

        return p.toString().concat("%");
    }

    public int[] getTransactionCount() {
        return this.transactionCount;
    }

    public int[] getExceptionCount() {
        return this.exceptionCount;
    }
}
