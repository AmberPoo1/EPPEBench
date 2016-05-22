package com.jingpu.android.apersistance;

import com.jingpu.android.apersistance.util.TPCCLog;

import org.dacapo.derby.OERandom;

import java.util.Date;

/**
 * Created by Jing Pu on 2016/1/30.
 */
public abstract class SimpleBenchmark {

    private long seed = System.currentTimeMillis();
    protected OERandom random = null;

    protected void setupRandom() {
        this.random = new OERandom(-1, this.seed);
        int loadRandomFactor = this.random.randomInt(0, 255);
        this.random = new OERandom(loadRandomFactor, this.seed);
    }

    protected void reportBeforeRun() {
        AppContext.getInstance().SetUIInfo("Parameters[benchmark="
                + AppContext.getInstance().getBenchmark()
                + ", scale=" + AppContext.getInstance().getScale()
                + ", transactions per scale=" + AppContext.getInstance().getAORMTrans()
                + "].\n Begin Run - " + (new Date()).getTime()); //System.currentTimeMillis()
    }

    protected void reportAfterRun() {
        AppContext.getInstance().SetUIInfo("End Run - " + (new Date()).getTime()
                + "\n LogFilePath= " + TPCCLog.getLogFilePath());
    }

    public abstract void initialize() throws Exception;
    public abstract void insert() throws Exception;
    public abstract void update() throws Exception;
    public abstract void select() throws Exception;
    public abstract void delete() throws Exception;
}
