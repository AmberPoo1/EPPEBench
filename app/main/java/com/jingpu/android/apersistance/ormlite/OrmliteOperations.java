package com.jingpu.android.apersistance.ormlite;

/**
 * Created by Jing Pu on 2015/10/23.
 */
public interface OrmliteOperations {
    public void stockLevel(int TerminalId, OrmliteDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2, int paramInt)
            throws Exception;

    public void orderStatus(int TerminalId, OrmliteDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2, String paramString)
            throws Exception;

    public void orderStatus(int TerminalId, OrmliteDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2, int paramInt)
            throws Exception;

    public void payment(int TerminalId, OrmliteDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2, short paramShort3, short paramShort4, String paramString1, String paramString2)
            throws Exception;

    public void payment(int TerminalId, OrmliteDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2, short paramShort3, short paramShort4, int paramInt, String paramString)
            throws Exception;

    public void newOrder(int TerminalId, OrmliteDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2, int paramInt, int[] paramArrayOfInt, short[] paramArrayOfShort1, short[] paramArrayOfShort2)
            throws Exception;

    public void scheduleDelivery(int TerminalId, OrmliteDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2)
            throws Exception;

    public void delivery(int TerminalId)
            throws Exception;
}
