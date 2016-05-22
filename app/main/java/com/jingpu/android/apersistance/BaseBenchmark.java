package com.jingpu.android.apersistance;

import android.content.Context;

import com.jingpu.android.apersistance.activeandroid.ActiveAndroidAORM;
import com.jingpu.android.apersistance.greendao.GreenDaoAORM;
import com.jingpu.android.apersistance.ormlite.OrmliteAORM;
import com.jingpu.android.apersistance.realm.RealmAORM;
import com.jingpu.android.apersistance.sqlite.SqliteAORM;
import com.jingpu.android.apersistance.sugarorm.SugarOrmAORM;
import com.jingpu.android.apersistance.util.TPCCLog;

import org.dacapo.harness.Benchmark;
import org.dacapo.harness.Callback;
import org.dacapo.harness.CommandLineArgs;
import org.dacapo.parser.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jing Pu on 2015/9/17.
 */
public class BaseBenchmark {
    Config config = null;
    String bm = null;
    int totalTrans = 0;
    short scale = 0;
    String size = "small";
    int terminalNum = 0;
    int phaseInterval = 0;
    int aORMTrans = 0;

    /*
    *  Constants
    */
    public final static String LOG_END_TAG = "<End>";
    public final static String DATABASE_NAME = "aptest.db";

    // Benchmarks
    // DaCapo
    public final static String BM_DACAPO_SQLITE = "DaCapoSqlite";
    public final static String BM_DACAPO_ORMLITE = "DaCapoOrmlite";
    public final static String BM_DACAPO_GREENDAO = "DaCapoGreendao";
    public final static String BM_DACAPO_ACTIVEANDROID = "DaCapoActiveAndroid";
    public final static String BM_DACAPO_SUGARORM = "DaCapoSugarOrm";
    public final static String BM_DACAPO_REALM = "DaCapoRealm";
    // AORM Sqlite
    public final static String BM_AORM_SQLITE_INI = "AORMSqliteIni";
    public final static String BM_AORM_SQLITE_I = "AORMSqliteInsert";
    public final static String BM_AORM_SQLITE_U = "AORMSqliteUpdate";
    public final static String BM_AORM_SQLITE_S = "AORMSqliteSelect";
    public final static String BM_AORM_SQLITE_D = "AORMSqliteDelete";
    // AORM Ormlite
    public final static String BM_AORM_ORMLITE_INI = "AORMOrmliteIni";
    public final static String BM_AORM_ORMLITE_I = "AORMOrmliteInsert";
    public final static String BM_AORM_ORMLITE_U = "AORMOrmliteUpdate";
    public final static String BM_AORM_ORMLITE_S = "AORMOrmliteSelect";
    public final static String BM_AORM_ORMLITE_D = "AORMOrmliteDelete";
    // AORM Greendao
    public final static String BM_AORM_GREENDAO_INI = "AORMGreendaoIni";
    public final static String BM_AORM_GREENDAO_I = "AORMGreendaoInsert";
    public final static String BM_AORM_GREENDAO_U = "AORMGreendaoUpdate";
    public final static String BM_AORM_GREENDAO_S = "AORMGreendaoSelect";
    public final static String BM_AORM_GREENDAO_D = "AORMGreendaoDelete";
    // AORM ActiveAndroid
    public final static String BM_AORM_ACTIVEANDROID_INI = "AORMActiveAndroidIni";
    public final static String BM_AORM_ACTIVEANDROID_I = "AORMActiveAndroidInsert";
    public final static String BM_AORM_ACTIVEANDROID_U = "AORMActiveAndroidUpdate";
    public final static String BM_AORM_ACTIVEANDROID_S = "AORMActiveAndroidSelect";
    public final static String BM_AORM_ACTIVEANDROID_D = "AORMActiveAndroidDelete";
    // AORM Sugar Orm
    public final static String BM_AORM_SUGARORM_INI = "AORMSugarOrmIni";
    public final static String BM_AORM_SUGARORM_I = "AORMSugarOrmInsert";
    public final static String BM_AORM_SUGARORM_U = "AORMSugarOrmUpdate";
    public final static String BM_AORM_SUGARORM_S = "AORMSugarOrmSelect";
    public final static String BM_AORM_SUGARORM_D = "AORMSugarOrmDelete";
    // AORM Realm
    public final static String BM_AORM_REALM_INI = "AORMRealmIni";
    public final static String BM_AORM_REALM_I = "AORMRealmInsert";
    public final static String BM_AORM_REALM_U = "AORMRealmUpdate";
    public final static String BM_AORM_REALM_S = "AORMRealmSelect";
    public final static String BM_AORM_REALM_D = "AORMRealmDelete";

    // time format:
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    CommandLineArgs commandLineArgs = null;

    private void parseDaCapoCfgFile(){
        bm = AppContext.getInstance().getBenchmark();
        totalTrans = AppContext.getInstance().getTotalTrans();
        scale = AppContext.getInstance().getScale();
        terminalNum = AppContext.getInstance().getTerminals();
        phaseInterval = AppContext.getInstance().getPhaseInterval();
        aORMTrans = AppContext.getInstance().getAORMTrans();

        InputStream ins = null;
        try {
            Context context = AppContext.getInstance().getApplicationContext();
            int id = -1;

            switch (bm) {
                case BM_DACAPO_SQLITE:
                    id = R.raw.sqlite;
                    break;
                case BM_DACAPO_ORMLITE:
                    id = R.raw.ormlite;
                    break;
                case BM_DACAPO_GREENDAO:
                    id = R.raw.greendao;
                    break;
                case BM_DACAPO_ACTIVEANDROID:
                    id = R.raw.activeandroid;
                    break;
                case BM_DACAPO_SUGARORM:
                    id = R.raw.sugarorm;
                    break;
                case BM_DACAPO_REALM:
                    id = R.raw.realm;
                    break;
                default:
                    break;
            }

            if (-1 == id) {
                TPCCLog.e(BaseBenchmark.class.getName(),"Unknown APersistance config file.");
                System.exit(20);
            }

            ins = context.getResources().openRawResource(id);
            if (ins == null) {
                TPCCLog.e(BaseBenchmark.class.getName(),"Unknown benchmark. ");
                System.exit(20);
            }
            config = Config.parse(ins);

        } catch (Exception e) {
            TPCCLog.e(BaseBenchmark.class.getName(), e);
            e.printStackTrace();
        }
    }

    private boolean isValidSize(String size) {
        return size != null && config.getSizes().contains(size);
    }

    private boolean isValidThreadCount(String size) {
        return config.getThreadLimit(size) == 0 || config.getThreadCount(size) <= config.getThreadLimit(size);
    }

    private void dump(boolean verbose) {
        if (verbose) {
            TPCCLog.v(BaseBenchmark.class.getName(), "Class name: " + config.className);

            TPCCLog.v(BaseBenchmark.class.getName(), "Configurations:");
            config.describe(System.err, size);
        }
    }

    private static void rmdir(File dir) {
        String[] files = dir.list();
        if (files != null) {
            for (int f = 0; f < files.length; f++) {
                File file = new File(dir, files[f]);
                if (file.isDirectory())
                    rmdir(file);
                if (!file.delete())
                    TPCCLog.v(BaseBenchmark.class.getName(), "Could not delete " + files[f]);
            }
        }
    }

    public static void makeCleanScratch(File scratch) {
        rmdir(scratch);
        scratch.mkdir();
    }

    private void bmInfo(String size) {
        config.describe(System.err, size);
    }

    private void runDaCapoBenchmark() {
        try {
            commandLineArgs = new CommandLineArgs(this.bm);

            int factor = 0;
            int limit = config.getThreadLimit(size);

            if (0 < factor && config.getThreadModel() == Config.ThreadModel.PER_CPU)
                config.setThreadFactor(size, factor);

            if (!isValidSize(size)) {
                TPCCLog.v(BaseBenchmark.class.getName(), "No configuration size, " + size + ", for benchmark " + bm + ".");
            } else if (factor != 0 && config.getThreadModel() != Config.ThreadModel.PER_CPU) {
                TPCCLog.v(BaseBenchmark.class.getName(), "Can only set the thread factor for per_cpu configurable benchmarks");
            } else if (!isValidThreadCount(size) && (config.getThreadCountOverride() > 0 || factor > 0)) {
                TPCCLog.v(BaseBenchmark.class.getName(), "The specified number of threads (" + config.getThreadCount(size) + ") is outside the range [1,"
                        + (limit == 0 ? "unlimited" : "" + limit) + "]");
            } else if (commandLineArgs.getInformation()) {
                bmInfo(size);
            } else {
                if (!isValidThreadCount(size)) {
                    TPCCLog.e(BaseBenchmark.class.getName(), "The derived number of threads (" + config.getThreadCount(size) + ") is outside the range [1,"
                            + (limit == 0 ? "unlimited" : "" + limit) + "]; rescaling to match thread limit.");
                    config.setThreadCountOverride(config.getThreadLimit(size));
                }

                dump(commandLineArgs.getVerbose());

                exeDaCapoBenchmark(null, bm);
            }
        } catch (Exception e) {
            TPCCLog.e(BaseBenchmark.class.getName(), e);
            e.printStackTrace();
        }
    }

    private void runAORMBenchmark() {
        try {
            switch (AppContext.getInstance().getBenchmark()) {
                case BM_AORM_SQLITE_INI:
                    new SqliteAORM(AppContext.getInstance()).initialize();
                    break;
                case BM_AORM_SQLITE_I:
                    new SqliteAORM(AppContext.getInstance()).insert();
                    break;
                case BM_AORM_SQLITE_U:
                    new SqliteAORM(AppContext.getInstance()).update();
                    break;
                case BM_AORM_SQLITE_S:
                    new SqliteAORM(AppContext.getInstance()).select();
                    break;
                case BM_AORM_SQLITE_D:
                    new SqliteAORM(AppContext.getInstance()).delete();
                    break;
                case BM_AORM_ORMLITE_INI:
                    new OrmliteAORM(AppContext.getInstance()).initialize();
                    break;
                case BM_AORM_ORMLITE_I:
                    new OrmliteAORM(AppContext.getInstance()).insert();
                    break;
                case BM_AORM_ORMLITE_U:
                    new OrmliteAORM(AppContext.getInstance()).update();
                    break;
                case BM_AORM_ORMLITE_S:
                    new OrmliteAORM(AppContext.getInstance()).select();
                    break;
                case BM_AORM_ORMLITE_D:
                    new OrmliteAORM(AppContext.getInstance()).delete();
                    break;
                case BM_AORM_GREENDAO_INI:
                    new GreenDaoAORM(AppContext.getInstance()).initialize();
                    break;
                case BM_AORM_GREENDAO_I:
                    new GreenDaoAORM(AppContext.getInstance()).insert();
                    break;
                case BM_AORM_GREENDAO_U:
                    new GreenDaoAORM(AppContext.getInstance()).update();
                    break;
                case BM_AORM_GREENDAO_S:
                    new GreenDaoAORM(AppContext.getInstance()).select();
                    break;
                case BM_AORM_GREENDAO_D:
                    new GreenDaoAORM(AppContext.getInstance()).delete();
                    break;
                case BM_AORM_ACTIVEANDROID_INI:
                    new ActiveAndroidAORM(AppContext.getInstance()).initialize();
                    break;
                case BM_AORM_ACTIVEANDROID_I:
                    new ActiveAndroidAORM(AppContext.getInstance()).insert();
                    break;
                case BM_AORM_ACTIVEANDROID_U:
                    new ActiveAndroidAORM(AppContext.getInstance()).update();
                    break;
                case BM_AORM_ACTIVEANDROID_S:
                    new ActiveAndroidAORM(AppContext.getInstance()).select();
                    break;
                case BM_AORM_ACTIVEANDROID_D:
                    new ActiveAndroidAORM(AppContext.getInstance()).delete();
                    break;
                case BM_AORM_SUGARORM_INI:
                    new SugarOrmAORM(AppContext.getInstance()).initialize();
                    break;
                case BM_AORM_SUGARORM_I:
                    new SugarOrmAORM(AppContext.getInstance()).insert();
                    break;
                case BM_AORM_SUGARORM_U:
                    new SugarOrmAORM(AppContext.getInstance()).update();
                    break;
                case BM_AORM_SUGARORM_S:
                    new SugarOrmAORM(AppContext.getInstance()).select();
                    break;
                case BM_AORM_SUGARORM_D:
                    new SugarOrmAORM(AppContext.getInstance()).delete();
                    break;
                case BM_AORM_REALM_INI:
                    new RealmAORM(AppContext.getInstance()).initialize();
                    break;
                case BM_AORM_REALM_I:
                    new RealmAORM(AppContext.getInstance()).insert();
                    break;
                case BM_AORM_REALM_U:
                    new RealmAORM(AppContext.getInstance()).update();
                    break;
                case BM_AORM_REALM_S:
                    new RealmAORM(AppContext.getInstance()).select();
                    break;
                case BM_AORM_REALM_D:
                    new RealmAORM(AppContext.getInstance()).delete();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            TPCCLog.e(BaseBenchmark.class.getName(), e);
            e.printStackTrace();
        }
    }

    private void exeDaCapoBenchmark(File scratch, String bm) throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException, Exception {
        Constructor<?> cons = Class.forName(config.className).getConstructor(new Class[] { Config.class, File.class });
        Benchmark b = (Benchmark) cons.newInstance(new Object[] { config, scratch });

        boolean valid = true;
        Callback callback = commandLineArgs.getCallback();
        callback.init(config);

        do {
            valid = b.run(callback, commandLineArgs.getSize(), totalTrans, scale, terminalNum, true) && valid;
        } while (callback.runAgain());
        b.cleanup();

        if (!valid) {
            TPCCLog.v(BaseBenchmark.class.getName(), "Validation FAILED for " + bm + " " + commandLineArgs.getSize());
            if (!commandLineArgs.getIgnoreValidation())
                System.exit(-2);
        }
    }

    @Before
    public Integer setUp() {
        Date cDate = new Date();
        AppContext.getInstance().SetUIInfo("Benchmark begin - Current time: " + BaseBenchmark.dateFormat.format(cDate) + "; Current time millis: " + cDate.getTime()); // System.currentTimeMillis()

        if (AppContext.getInstance().getBenchmark().startsWith("DaCapo")) {
            parseDaCapoCfgFile();
            runDaCapoBenchmark();
        } else {
            runAORMBenchmark();
        }

        cDate = new Date();
        AppContext.getInstance().SetUIInfo("Benchmark end - Current time: " + BaseBenchmark.dateFormat.format(cDate) + "; Current time millis: " + cDate.getTime()); // System.currentTimeMillis()
        AppContext.getInstance().SetUIInfo(BaseBenchmark.LOG_END_TAG);

        return 0;
    }

    @After
    public void tearDown() {
        TPCCLog.i(BaseBenchmark.class.getName(), "@After - tearDown");
    }

    @Test
    public void testBenchmark() {
        TPCCLog.i(BaseBenchmark.class.getName(), "@Test - testEmptyCollection");
    }
}
