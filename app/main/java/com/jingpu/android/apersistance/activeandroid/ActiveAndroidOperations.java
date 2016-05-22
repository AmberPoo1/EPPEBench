package com.jingpu.android.apersistance.activeandroid;


/**
 * Created by Jing Pu on 2016/1/3.
 */
public interface ActiveAndroidOperations {
    public void stockLevel(int terminalId, ActiveAndroidDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2, int paramInt)
            throws Exception;

    public void orderStatus(int terminalId, ActiveAndroidDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2, String paramString)
            throws Exception;

    public void orderStatus(int terminalId, ActiveAndroidDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2, int paramInt)
            throws Exception;

    public void payment(int terminalId, ActiveAndroidDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2, short paramShort3, short paramShort4, String paramString1, String paramString2)
            throws Exception;

    public void payment(int terminalId, ActiveAndroidDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2, short paramShort3, short paramShort4, int paramInt, String paramString)
            throws Exception;

    public void newOrder(int terminalId, ActiveAndroidDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2, int paramInt, int[] paramArrayOfInt, short[] paramArrayOfShort1, short[] paramArrayOfShort2)
            throws Exception;

    public void scheduleDelivery(int terminalId, ActiveAndroidDisplay paramDisplay, Object paramObject, short paramShort1, short paramShort2)
            throws Exception;

    public void delivery(int terminalId)
            throws Exception;
}
