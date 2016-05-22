package com.jingpu.android.apersistance;

import android.app.Application;

import com.orm.SugarContext;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Jing Pu on 2015/9/16.
 */
public class AppContext extends Application {

    private static AppContext instance;

    private String benchmark = null;
    int totalTrans = 0;
    short scale = 0;
    int terminals = 0;
    int phaseInterval = 0;
    int aORMTrans = 0;
    Date startRunDate = null;
    StringBuffer sbInfo = new StringBuffer();

    AtomicLong hPkValue;
    AtomicLong doPkValue;

    public static AppContext getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public void onTerminate() {
        super.onTerminate();
        if (benchmark.equals("sugarorm")) {
            SugarContext.terminate();
        }
    }

    public String getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(String benchmark) {
        this.benchmark = benchmark;
    }

    public int getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(int totalTrans) {
        this.totalTrans = totalTrans;
    }

    public short getScale() {
        return scale;
    }

    public void setScale(short scale) {
        this.scale = scale;
    }

    public int getTerminals() {
        return terminals;
    }

    public void setTerminals(int terminals) {
        this.terminals = terminals;
    }

    public int getPhaseInterval() {
        return phaseInterval;
    }

    public void setPhaseInterval(int phaseInterval) {
        this.phaseInterval = phaseInterval;
    }

    public int getAORMTrans() {
        return aORMTrans;
    }

    public void setAORMTrans(int aORMTrans) {
        this.aORMTrans = aORMTrans;
    }

    public Date getStartRunDate() {
        return startRunDate;
    }

    public void setStartRunDate(Date startRunDate) {
        this.startRunDate = startRunDate;
    }

    public synchronized void SetUIInfo(String info) {
        sbInfo.append("\n=============================\n");
        sbInfo.append(info);
    }

    public synchronized String getUIInfo() {
        String info = sbInfo.toString();
        sbInfo.setLength(0);
        return info;
    }

    public synchronized Long getNextHId() {
        if (null == hPkValue) {
            hPkValue = new AtomicLong(1);
        }
        return hPkValue.getAndIncrement();
    }

    public synchronized Long getNextDoId() {
        if (null == doPkValue) {
            doPkValue = new AtomicLong(1);
        }
        return doPkValue.getAndIncrement();
    }
}
