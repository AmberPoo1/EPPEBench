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

import org.dacapo.harness.CommandLineArgs.Methodology;
import org.dacapo.parser.Config;

/**
 * @date $Date: 2009-12-24 11:19:36 +1100 (Thu, 24 Dec 2009) $
 * @id $Id: Callback.java 738 2009-12-24 00:19:36Z steveb-oss $
 */
public class Callback {

  /**
   * Support for timing methodologies that have timing and warmup runs.
   */
  protected enum Mode {
    WARMUP, TIMING
  };

  protected Mode mode;

  /**
   * The parsed command line arguments
   */
  protected final CommandLineArgs args;

  /**
   * Iterations of the current benchmark completed so far
   */
  protected int iterations;

  /**
   * Times for the last n iterations of the current benchmark
   */
  protected long[] times;

  /**
   * The start time of the most recent benchmark run.
   */
  protected long timer;

  /**
   * 
   */
  protected long elapsed;

  boolean verbose = false;

  /**
   * Create a new callback.
   * 
   * @param args The parsed command-line arguments.
   */
  public Callback(CommandLineArgs args) {
    this.args = args;
    if (args.getMethodology() == Methodology.CONVERGE) {
      times = new long[args.getWindow()];
    }
    verbose |= args.getDebug();
  }

  public void init(Config config) {
    if (verbose)
      TPCCLog.v(Callback.class.getName(), "Initializing callback");
    iterations = 0;

    switch (args.getMethodology()) {
    case ITERATE:
      if (args.getIterations() == 1)
        mode = Mode.TIMING;
      else
        mode = Mode.WARMUP;
      break;
    case CONVERGE:
      if (args.getWindow() == 0)
        mode = Mode.TIMING;
      else
        mode = Mode.WARMUP;
    }

    if (times != null)
      for (int i = 0; i < times.length; i++)
        times[i] = 0;
  }

  /**
   * This method governs the benchmark iteration process. The test harness will
   * run the benchmark repeatedly until this method returns 'false'.
   * 
   * The default methodologies consist of 0 or more 'warmup' iterations,
   * followed by a single timing iteration.
   * 
   * @return Whether to run another iteration.
   */
  public boolean runAgain() {
    if (verbose)
        TPCCLog.v(Callback.class.getName(), "runAgain");
    /* Always quit immediately after the timing iteration */
    if (!isWarmup())
      return false;

    iterations++;
    if (verbose)
      TPCCLog.v(Callback.class.getName(), "iterations = " + iterations);
    switch (args.getMethodology()) {
    case ITERATE:
      if (iterations == args.getIterations() - 1)
        mode = Mode.TIMING;
      if (verbose)
        TPCCLog.v(Callback.class.getName(), "mode = " + mode);
      return true;

    case CONVERGE:
      /* If we've exceeded the maximum iterations, exit */
      if (iterations >= args.getMaxIterations()) {
        TPCCLog.e(Callback.class.getName(), "Benchmark failed to converge.");
        return false;
      }

      /* Maintain the sliding window of execution times */
      times[(iterations - 1) % args.getWindow()] = elapsed;

      /* If we haven't filled the window, repeat immediately */
      if (iterations < args.getWindow())
        return true;

      /* Optionally report on progress towards convergence */
      if (iterations >= args.getWindow() && args.getVerbose()) {
        TPCCLog.v(Callback.class.getName(), String.format("Variation %4.2f%% achieved after %d iterations, target = %4.2f%%\n", coeff_of_var(times) * 100, iterations, args
                .getTargetVar() * 100));
      }

      /* Not yet converged, repeat in warmup mode */
      if (coeff_of_var(times) > args.getTargetVar())
        return true;

      /* If we've fallen through to here, we must have converged */
      mode = Mode.TIMING;
      return true;
    }

    // We should never fall through
    assert false;
    return false; // Keep javac happy
  }

  public boolean isWarmup() {
    return mode == Mode.WARMUP;
  }

  /**
   * Start the timer and announce the begining of an iteration
   */
  public void start(String benchmark) {
    start(benchmark, mode == Mode.WARMUP);
  };

  @Deprecated
  public void startWarmup(String benchmark) {
    start(benchmark, true);
  };

  protected void start(String benchmark, boolean warmup) {
    timer = System.currentTimeMillis();
    TPCCLog.v(Callback.class.getName(), "===== APersistance " + getBuildVersion() + " " + benchmark + " starting ");
    TPCCLog.v(Callback.class.getName(), (warmup ? ("warmup " + (iterations + 1) + " ") : "") + "=====");
    System.err.flush();
  }

  /* Stop the timer */
  public void stop() {
    stop(mode == Mode.WARMUP);
  }

  @Deprecated
  public void stopWarmup() {
    stop(true);
  }

  public void stop(boolean warmup) {
    elapsed = System.currentTimeMillis() - timer;
  }

  /* Announce completion of the benchmark (pass or fail) */
  public void complete(String benchmark, boolean valid) {
    complete(benchmark, valid, mode == Mode.WARMUP);
  };

  @Deprecated
  public void completeWarmup(String benchmark, boolean valid) {
    complete(benchmark, valid, true);
  };

  protected void complete(String benchmark, boolean valid, boolean warmup) {
    StringBuffer sb = new StringBuffer();
    sb.append("===== APersistance " + getBuildVersion() + " " + benchmark);
    if (valid) {
      sb.append(warmup ? (" completed warmup " + (iterations + 1) + " ") : " PASSED ");
      sb.append("in " + elapsed + " msec ");
    } else {
      sb.append(" FAILED " + (warmup ? "warmup " : ""));
    }
    sb.append("=====");
    TPCCLog.v(Callback.class.getName(), sb.toString());
    //System.err.flush();
  }

  public static String getBuildVersion() {
    return "1.0.0";
  }

  /**
   * Calculates coefficient of variation of a set of longs (standard deviation
   * divided by mean).
   *
   * @param times Array of input values
   * @return Coefficient of variation
   */
  public static double coeff_of_var(long[] times) {
    double n = times.length;
    double sum = 0.0;
    double sum2 = 0.0;

    for (int i = 0; i < times.length; i++) {
      double x = times[i];
      sum += x;
      sum2 += x * x;
    }

    double mean = sum / n;
    double sigma = Math.sqrt(1.0 / n * sum2 - mean * mean);

    return sigma / mean;
  }

}
