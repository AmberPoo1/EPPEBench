package org.dacapo.harness;

import org.dacapo.parser.Config;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by Jing Pu on 2015/10/23.
 */
public class OrmLite extends Benchmark {

    private Object tpcc;
    private Method makeTPCC;
    private Method prepareTPCC;
    private Method preIterationTPCC;
    private Method iterationTPCC;
    private Method postIterationTPCC;

    public OrmLite(Config config, File scratch) throws Exception {
        super(config, scratch, false);
    }

    @Override
    protected void prepare() throws Exception {
        useBenchmarkClassLoader();
        try {
            Class<?> tpccClazz  = Class.forName("com.jingpu.android.apersistance.ormlite.OrmliteTPCC"); //,true,loader
            this.makeTPCC = tpccClazz.getMethod("make", Config.class, File.class, Boolean.class, Boolean.class);
            this.prepareTPCC = tpccClazz.getMethod("prepare", String.class, int.class, short.class, int.class, boolean.class);
            this.preIterationTPCC = tpccClazz.getMethod("preIteration", String.class);
            this.iterationTPCC = tpccClazz.getMethod("iteration", String.class);
            this.postIterationTPCC = tpccClazz.getMethod("postIteration", String.class);

            // construct the benchmark
            this.tpcc = this.makeTPCC.invoke(null, config, null, getVerbose(), getPreserve()); // scratch
        } finally {
            revertClassLoader();
        }
    }


    /**
     * The benchmark run
     */
    @Override
    public void prepare(String size, int uiTotalTrans, short uiScale, int uiTerminals, boolean useUIParam) throws Exception {

        useBenchmarkClassLoader();
        try {
            this.prepareTPCC.invoke(this.tpcc, size, uiTotalTrans, uiScale, uiTerminals, useUIParam);
        } finally {
            revertClassLoader();
        }
    }

    @Override
    public void preIteration(String size) throws Exception {

        useBenchmarkClassLoader();
        try {
            this.preIterationTPCC.invoke(this.tpcc, size);
        } finally {
            revertClassLoader();
        }
    }

    @Override
    public void iterate(String size) throws Exception {
        useBenchmarkClassLoader();
        try {
            this.iterationTPCC.invoke(this.tpcc, size);
        } finally {
            revertClassLoader();
        }
    }

    @Override
    public void postIteration(String size) throws Exception {
        useBenchmarkClassLoader();
        try {
            this.postIterationTPCC.invoke(this.tpcc, size);
        } finally {
            revertClassLoader();
            super.postIteration(size);
        }
    }

    @Override
    public void cleanup() {
        this.tpcc = null;
        this.makeTPCC = null;
        this.prepareTPCC = null;
        this.preIterationTPCC = null;
        this.iterationTPCC = null;
        this.postIterationTPCC = null;

        super.cleanup();
    }
}
