/*
 * Copyright (c) 2006, 2009 The Australian National University.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0.
 * You may obtain the license at
 * 
 *    http://www.opensource.org/licenses/apache2.0.php
 */
package org.dacapo.harness;

import com.jingpu.android.apersistance.util.TPCCLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Command line arguments for a dacapo benchmark run.
 * 
 * Encapsulated in an object so that it can be passed to user-written callbacks.
 * 
 * @date $Date: 2009-12-24 11:19:36 +1100 (Thu, 24 Dec 2009) $
 * @id $Id: CommandLineArgs.java 738 2009-12-24 00:19:36Z steveb-oss $
 */
public class CommandLineArgs {

  public enum Methodology {
    ITERATE, CONVERGE;
  }

  public final static int EXIT_OK = 0;
  public final static int EXIT_MISSING_CALLBACK = 2;
  public final static int EXIT_BAD_CALLBACK = 3;
  public final static int EXIT_BAD_COMMANDLINE = 4;
  public final static int EXIT_UNKNOWN_BENCHMARK = 9;
  public final static int EXIT_MISSING_BENCHMARKS = 10;

  public static final String RELEASE_NOTES = "RELEASE_NOTES.txt";
  public static final String DEFAULT_SIZE = "default";
  public static final String DEFAULT_SCRATCH_DIRECTORY = "scratch";
  public static final String DEFAULT_MAX_ITERATIONS = "20";
  public static final String DEFAULT_WINDOW_SIZE = "3";
  public static final String DEFAULT_VARIANCE = "3.0";
  public static final String DEFAULT_ITERATIONS = "1";
  public static final String DEFAULT_THREAD_COUNT = "0"; // 0 represents
                                                         // unspecified
  public static final String DEFAULT_THREAD_FACTOR = "0"; // 0 represents
                                                          // unspecified

  private static final String OPT_CALLBACK = "callback";
  private static final String OPT_HELP = "help";
  private static final String OPT_RELEASE_NOTES = "release-notes";
  private static final String OPT_LIST_BENCHMARKS = "list-benchmarks";
  private static final String OPT_INFORMATION = "information";
  private static final String OPT_SIZE = "size";
  private static final String OPT_SCRATCH_DIRECTORY = "scratch-directory";
  private static final String OPT_CONVERGE = "converge";
  private static final String OPT_MAX_ITERATIONS = "max-iterations";
  private static final String OPT_VARIANCE = "variance";
  private static final String OPT_WINDOW = "window";
  private static final String OPT_ITERATIONS = "iterations";
  private static final String OPT_DEBUG = "debug";
  private static final String OPT_IGNORE_VALIDATION = "ignore-validation";
  private static final String OPT_NO_DIGEST_OUTPUT = "no-digest-output";
  private static final String OPT_NO_VALIDATION = "no-validation";
  private static final String OPT_PRESERVE = "preserve";
  private static final String OPT_VALIDATION_REPORT = "validation-report";
  private static final String OPT_CONFIG = "config";
  private static final String OPT_VERBOSE = "verbose";
  private static final String OPT_NO_PRE_ITERATION_GC = "no-pre-iteration-gc";
  private static final String OPT_THREAD_COUNT = "thread-count";
  private static final String OPT_THREAD_FACTOR = "thread-factor";

  private Callback callback = null;
  private List<String> benchmarks = new ArrayList<String>();

  public CommandLineArgs(String benchmark) throws Exception {
      // configure the callback
    defineCallback();

    benchmarks.add((String) benchmark);
  }



  /**
   * Print the release notes to System.out
   */
  static void printReleaseNotes() throws IOException {
    BufferedReader releaseNotes = new BufferedReader(new InputStreamReader(CommandLineArgs.class.getClassLoader().getResourceAsStream(RELEASE_NOTES)));

    String line;
    while ((line = releaseNotes.readLine()) != null) {
      TPCCLog.v(CommandLineArgs.class.getName(), line);
    }
  }

  public Iterable<String> benchmarks() {
    return benchmarks;
  }

  // Getter methods
  public boolean getVerbose() {
    return false;
  }

  public Methodology getMethodology() {
    return Methodology.ITERATE;
  }

  public double getTargetVar() {
    return Double.parseDouble(DEFAULT_VARIANCE) / 100.0;
  }

  public int getWindow() {
    return Integer.parseInt(DEFAULT_WINDOW_SIZE);
  }

  public int getMaxIterations() {
    return Integer.parseInt(DEFAULT_MAX_ITERATIONS);
  }

  public boolean getIgnoreValidation() {
    return false;
  }

  public int getIterations() {
    return Integer.parseInt(DEFAULT_ITERATIONS);
  }

  public String getSize() {
    return DEFAULT_SIZE;
  }

  public String getScratchDir() {
    return DEFAULT_SCRATCH_DIRECTORY;
  }

  public Callback getCallback() {
    return callback;
  }

  public String getCnfOverride() {
    return null;
  }

  public boolean getInformation() {
    return false;
  }

  public boolean getSilent() {
    return !getVerbose();
  }

  public boolean getDebug() {
    return false;
  }

  public boolean getPreserve() {
    return false;
  }

  public boolean getValidateOutput() {
    return true; // validateOutput;
  }

  public boolean getValidate() {
    return true;
  }

  public String getValidationReport() {
    return null;
  }

  public boolean getPreIterationGC() {
    return true;
  }

  public String getThreadCount() {
    return DEFAULT_THREAD_COUNT;
  }

  public String getThreadFactor() {
    return DEFAULT_THREAD_FACTOR;
  }

  // *****************************************************************
  private void defineCallback() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    // define the callback class (or set the default if none specified

    // set the default callback class if no callback is defined
    if (getCallback() == null) {
      callback = new Callback(this);
    }
  }
}
