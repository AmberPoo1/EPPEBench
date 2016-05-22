/*
 * Copyright (c) 2006, 2009 The Australian National University.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0.
 * You may obtain the license at
 * 
 *    http://www.opensource.org/licenses/apache2.0.php
 */
package org.dacapo.harness;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.jingpu.android.apersistance.AppContext;
import com.jingpu.android.apersistance.BaseBenchmark;
import com.jingpu.android.apersistance.util.TPCCLog;
import com.orm.SugarContext;

import org.dacapo.parser.Config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Each DaCapo benchmark is represented by an instance of this abstract class.
 * It defines the methods that the benchmark harness calls during the running of
 * the benchmark.
 *
 * @date $Date: 2009-12-24 11:19:36 +1100 (Thu, 24 Dec 2009) $
 * @id $Id: Benchmark.java 738 2009-12-24 00:19:36Z steveb-oss $
 */
public abstract class Benchmark {

    /**
     * I/O buffer size for unzipping
     */
    private static final int BUFFER_SIZE = 2048;

    /*
     * Class variables
     */

    /**
     * Verbose output.
     */
    private static boolean verbose = false;

    /**
     * Display output from the benchmark ?
     */
    private static boolean silent = true;

    /**
     * Perform digest operations on standard output and standard error
     */
    private static boolean validateOutput = true;

    /**
     * Perform System.gc() just prior to each iteration
     */
    private static boolean preIterationGC = false;

    /**
     * Perform validation
     */
    private static boolean validate = true;

    /**
     * Don't clean up output files
     */
    private static boolean preserve = false;

    /**
     * Output file for writing digests
     */
    private static PrintWriter valRepFile = null;

    /**
     *
     */
    private static boolean validationReport = false;

    /**
     * Parsed version of the configuration file for this benchmark
     */
    protected final Config config;

    /**
     * Classloader used to run the benchmark
     */
    protected ClassLoader loader;

    /**
     * Saved classloader across iterations
     */
    private ClassLoader savedClassLoader;

    /**
     * Keep track of the number of times we have been iterated.
     */
    protected int iteration = 0;

    protected Method method;

    /**
     * Run a benchmark. This is final because individual benchmarks should not
     * interfere with the flow of control.
     *
     * @param callback     The user-specified timing callback
     * @param size         The size (as given on the command line)
     * @param uiTotalTrans The number of total transactions (as given on the UI)
     * @param uiScale      The scale (as given on the UI)
     * @param uiTerminals  The number of terminals
     * @param useUIParam   switch of using UI parameters or not
     * @return Whether the run was valid or not.
     * @throws Exception Whatever exception the target application dies with
     */
    public final boolean run(Callback callback, String size, int uiTotalTrans, short uiScale, int uiTerminals, boolean useUIParam) throws Exception {
        Date cDate = null;
        iteration++;
        if (iteration == 1) {
            cDate = new Date();
            AppContext.getInstance().SetUIInfo("Prepare begin - Current time: " + BaseBenchmark.dateFormat.format(cDate) + "; Current time millis: " + cDate.getTime()); //System.currentTimeMillis()
            prepare(size, uiTotalTrans, uiScale, uiTerminals, useUIParam);
            cDate = new Date();
            AppContext.getInstance().SetUIInfo("Prepare end - Current time: " + BaseBenchmark.dateFormat.format(cDate) + "; Current time millis: " + cDate.getTime()); //System.currentTimeMillis()
        }

        cDate = new Date();
        AppContext.getInstance().SetUIInfo("PreIteration begin - Current time: " + BaseBenchmark.dateFormat.format(cDate) + "; Current time millis: " + cDate.getTime()); //System.currentTimeMillis()

        // delete existing database
        if (AppContext.getInstance().getBenchmark().equals(BaseBenchmark.BM_DACAPO_REALM)) {
            RealmConfiguration config = new RealmConfiguration.Builder(AppContext.getInstance())
                    .deleteRealmIfMigrationNeeded().build();
            Realm.deleteRealm(config);
        } else {
            AppContext.getInstance().deleteDatabase(BaseBenchmark.DATABASE_NAME);
        }

        // Initialize activeandroid db environment
        if (AppContext.getInstance().getBenchmark().equals(BaseBenchmark.BM_DACAPO_ACTIVEANDROID)) {
            Configuration dbConfiguration = new Configuration.Builder(AppContext.getInstance())
                    .setDatabaseName(BaseBenchmark.DATABASE_NAME).create();
            ActiveAndroid.initialize(dbConfiguration);
        } else if (AppContext.getInstance().getBenchmark().equals(BaseBenchmark.BM_DACAPO_SUGARORM)) {
            SugarContext.init(AppContext.getInstance());
        }

        preIteration(size);

        if (preIterationGC) {
            System.gc();
        }

        cDate = new Date();
        AppContext.getInstance().SetUIInfo("PreIteration end - Current time: " + BaseBenchmark.dateFormat.format(cDate) + "; Current time millis: " + cDate.getTime()); // System.currentTimeMillis()

        // current thread stop between phases
        if (AppContext.getInstance().getPhaseInterval() > 0) {
            AppContext.getInstance().SetUIInfo("** Phase I stop[ " + AppContext.getInstance().getPhaseInterval() + " S ] begin ... ");
            Thread.sleep(AppContext.getInstance().getPhaseInterval() * 1000);
            AppContext.getInstance().SetUIInfo("** Phase I stop end");
        }

        cDate = new Date();
        AppContext.getInstance().SetUIInfo("Iterate begin - Current time: " + BaseBenchmark.dateFormat.format(cDate) + "; Current time millis: " + cDate.getTime()); //System.currentTimeMillis()

        callback.start(config.name);

        startIteration();

        try {
            iterate(size);
        } finally {
            stopIteration();
        }

        callback.stop();

        boolean valid = validate(size);
        callback.complete(config.name, valid);
        cDate = new Date();
        AppContext.getInstance().SetUIInfo("Iterate end - Current time: " + BaseBenchmark.dateFormat.format(cDate) + "; Current time millis: " + cDate.getTime()); //System.currentTimeMillis()

        // current thread stop between phases
        if (AppContext.getInstance().getPhaseInterval() > 0) {
            AppContext.getInstance().SetUIInfo("** Phase II stop[ " + AppContext.getInstance().getPhaseInterval() + " S ] begin ... ");
            Thread.sleep(AppContext.getInstance().getPhaseInterval() * 1000);
            AppContext.getInstance().SetUIInfo("** Phase II stop end");
        }

        cDate = new Date();
        AppContext.getInstance().SetUIInfo("PostIteration begin - Current time: " + BaseBenchmark.dateFormat.format(cDate) + "; Current time millis: " + cDate.getTime()); //System.currentTimeMillis()
        postIteration(size);
        cDate = new Date();
        AppContext.getInstance().SetUIInfo("PostIteration end - Current time: " + BaseBenchmark.dateFormat.format(cDate) + "; Current time millis: "  + cDate.getTime()); // System.currentTimeMillis()
        AppContext.getInstance().SetUIInfo("** LogFilePath= " + TPCCLog.getLogFilePath());
        return valid;
    }

    public Benchmark(Config config, File scratch, boolean silent) throws Exception {
        // TODO this is very ugly
        Benchmark.silent = silent;

        this.config = config;
        initialize();
    }

    /**
     * When an instance of a Benchmark is created, it is expected to prepare its
     * scratch directory, unloading files from the jar file if required.
     *
     * @param scratch Scratch directory
     */
    public Benchmark(Config config, File scratch) throws Exception {
        this(config, scratch, true);
    }

    protected void initialize() throws Exception {
        prepare();
    }

    /**
     * Perform pre-benchmark preparation. By default it unpacks the zip file
     * <code>data/<i>name</i>.zip</code> into the scratch directory.
     */
    protected void prepare() throws Exception {
    }

    /**
     * One-off preparation performed once we know the benchmark size.
     * <p/>
     * By default, does nothing.
     *
     * @param size         The size (as defined in the per-benchmark configuration file).
     * @param uiTotalTrans The number of total transactions (as given on the UI)
     * @param uiScale      The scale (as given on the UI)
     * @param uiTerminals  The number of terminals
     * @param useUIParam   switch of using UI parameters or not
     */
    protected void prepare(String size, int uiTotalTrans, short uiScale, int uiTerminals, boolean useUIParam) throws Exception {
    }

    /**
     * Benchmark-specific per-iteration setup, outside the timing loop.
     * <p/>
     * Needs to take care of any *required* cleanup when the -preserve flag us
     * used.
     *
     * @param size Size as specified by the "-s" command line flag
     */
    public void preIteration(String size) throws Exception {
        if (verbose) {
            String[] args = config.preprocessArgs(size, null); //scratch
            StringBuffer sb = new StringBuffer();
            sb.append("Benchmark parameters: ");
            for (int i = 0; i < args.length; i++)
                sb.append(args[i] + " ");
            TPCCLog.v(Benchmark.class.toString(), sb.toString());
        }

        /*
         * Allow those benchmarks that can't tolerate overwriting prior output to
         * run in the face of the '-preserve' flag.
         */
        if (preserve && iteration > 1)
            postIterationCleanup(size);
    }

    /**
     * Per-iteration setup, inside the timing loop. Nothing comes between this and
     * the call to 'iterate' - its purpose is to start collection of the input and
     * output streams. stopIteration() should be its inverse.
     */
    public final void startIteration() {
        if (verbose) {
            TPCCLog.v(Benchmark.class.getName(), "startIteration()");
        }

        useBenchmarkClassLoader();
    }

    /**
     * An actual iteration of the benchmark. This is what is timed.
     */
    public abstract void iterate(String size) throws Exception;

    /**
     * Post-iteration tear-down, inside the timing loop. Restores standard output
     * and error, and saves the digest of the iteration output. This is inside the
     * timing loop so as not to process any output from the timing harness.
     */
    public final void stopIteration() {
        revertClassLoader();

        if (verbose) {
            TPCCLog.v(Benchmark.class.toString(), "stopIteration()");
        }
    }

    /**
     * TODO
     */
    protected void useBenchmarkClassLoader() {
    }

    /**
     * TODO
     */
    protected void revertClassLoader() {
    }

    /**
     * Perform validation of output. By default process the conditions specified
     * in the config file.
     *
     * @param size Size of the benchmark run.
     * @return true if the output was correct
     */
    public boolean validate(String size) {
        return true;
    }

    /**
     * Per-iteration cleanup, outside the timing loop. By default it deletes the
     * named output files.
     *
     * @param size Argument to the benchmark iteration.
     */
    public void postIteration(String size) throws Exception {
        if (!preserve) {
            postIterationCleanup(size);
        }
    }

    /**
     * Perform post-iteration cleanup.
     *
     * @param size
     */
    protected void postIterationCleanup(String size) {

    }

    /**
     * Perform post-benchmark cleanup, deleting output files etc. By default it
     * deletes a subdirectory of the scratch directory with the same name as the
     * benchmark.
     */
    public void cleanup() {

    }

    /*************************************************************************************
     * Utility methods
     */

    /**
     * Copy a file to the specified directory
     *
     * @param inputFile File to copy
     * @param outputDir Destination directory
     */
    public static void copyFileTo(File inputFile, File outputDir) throws IOException {
        copyFile(inputFile, new File(outputDir, inputFile.getName()));
    }

    /**
     * Copy a file, specifying input and output file names.
     *
     * @param inputFile  Name of the input file.
     * @param outputFile Name of the output file
     * @throws IOException Any exception thrown by the java.io functions used to
     *                     perform the copy.
     */
    public static void copyFile(File inputFile, File outputFile) throws IOException {
        FileInputStream input = new FileInputStream(inputFile);
        FileOutputStream output = new FileOutputStream(outputFile);
        while (true) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read = input.read(buffer);
            if (read == -1)
                break;
            output.write(buffer, 0, read);
        }
        input.close();
        output.flush();
        output.close();
    }

    /**
     * Translate a resource name into a URL.
     *
     * @param fn
     * @return
     */
    public static URL getURL(String fn) {
        ClassLoader cl = Benchmark.class.getClassLoader();
        URL resource = cl.getResource(fn);
        if (verbose)
            TPCCLog.v(Benchmark.class.toString(), "Util.getURL: returns " + resource);
        return resource;
    }

    private static String fileIn(File scratch, String name) {
        return (new File(scratch, name)).getPath();
    }

    /**
     * Unpack a zip archive into the specified directory.
     *
     * @param name        Name of the zip file
     * @param destination Directory to unpack into.
     * @throws IOException
     */
    public static void unpackZipFile(String name, File destination) throws IOException, FileNotFoundException {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(name));
        unpackZipStream(inputStream, destination);
    }

    /**
     * Unpack a zip file resource into the specified directory. The directory
     * structure of the zip archive is preserved.
     *
     * @param name
     * @param destination
     * @throws IOException
     */
    public static void unpackZipFileResource(String name, File destination) throws IOException, FileNotFoundException, DacapoException {
        URL resource = getURL(name);
        if (resource == null)
            throw new DacapoException("No such zip file: \"" + name + "\"");

        BufferedInputStream inputStream = new BufferedInputStream(resource.openStream());
        unpackZipStream(inputStream, destination);
    }

    public static void extractFileResource(String name, File destination) throws IOException, FileNotFoundException, DacapoException {
        if (verbose)
            TPCCLog.v(Benchmark.class.getName(), "Extracting file " + name + " into " + destination.getCanonicalPath());
        // find the file in the root directory of Benchmark.class
        // (get parent class loader of Benchmark.class first, then search the parent class loader for the resource)
        URL resource = getURL(name);
        if (resource == null)
            throw new DacapoException("No such file: \"" + name + "\"");
        BufferedInputStream inputStream = new BufferedInputStream(resource.openStream());
        fileFromInputStream(inputStream, new File(destination, name));
    }

    /**
     * @param inputStream
     * @param destination
     * @throws IOException
     */
    private static void unpackZipStream(BufferedInputStream inputStream, File destination) throws IOException {
        ZipInputStream input = new ZipInputStream(inputStream);
        ZipEntry entry;
        while ((entry = input.getNextEntry()) != null) {
            if (verbose)
                TPCCLog.v(Benchmark.class.toString(), "Unpacking " + entry.getName());
            File file = new File(destination, entry.getName());
            if (entry.isDirectory()) {
                if (!file.exists())
                    file.mkdir();
            } else {
                fileFromInputStream(input, file);
            }
        }
        input.close();
    }

    private static void fileFromInputStream(InputStream input, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE);
        int count;
        byte data[] = new byte[BUFFER_SIZE];
        while ((count = input.read(data, 0, BUFFER_SIZE)) != -1) {
            dest.write(data, 0, count);
        }
        dest.flush();
        dest.close();
    }

    public static void deleteTree(File tree) {
        if (verbose)
            TPCCLog.v(Benchmark.class.getName(), "Deleting " + tree.getName());
        if (!tree.isDirectory())
            tree.delete();
        else {
            File[] files = tree.listFiles();
            for (int i = 0; i < files.length; i++)
                deleteTree(files[i]);
            tree.delete();
        }
    }

    public static void deleteFile(File file) {
        if (verbose)
            TPCCLog.v(Benchmark.class.toString(), "Deleting " + file.getName());
        if (file.exists() && !file.isDirectory())
            file.delete();
    }

    public static void deleteFiles(File dir, final String pattern) {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.matches(pattern);
            }
        };
        File[] files = dir.listFiles(filter);
        for (int i = 0; i < files.length; i++) {
            deleteFile(files[i]);
        }
    }

    public static int lineCount(String file) throws IOException {
        return lineCount(new File(file));
    }

    public static int lineCount(File file) throws IOException {
        int lines = 0;
        BufferedReader in = new BufferedReader(new FileReader(file));
        while (in.readLine() != null)
            lines++;
        in.close();
        return lines;
    }

    public static long byteCount(String file) throws IOException {
        return byteCount(new File(file));
    }

    public static long byteCount(File file) throws IOException {
        return file.length();
    }

    public static void setCommandLineOptions(CommandLineArgs line) {
        silent = line.getSilent();
        preserve = line.getPreserve();
        validate = line.getValidate();
        validateOutput = line.getValidateOutput();
        preIterationGC = line.getPreIterationGC();
        if (line.getValidationReport() != null)
            Benchmark.enableValidationReport(line.getValidationReport());
    }

    private static void enableValidationReport(String filename) {
        try {
            validationReport = true;
            // Append to an output file
            valRepFile = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // getter methods
    public static boolean getVerbose() {
        return verbose;
    }

    public static boolean getValidateOutput() {
        return validateOutput;
    }

    public static boolean getValidate() {
        return validate;
    }

    public static boolean getPreserve() {
        return preserve;
    }

    protected int getIteration() {
        return iteration;
    }

    public static boolean getSilent() {
        return silent;
    }
}
