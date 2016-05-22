package com.jingpu.android.apersistance.ormlite;

import com.jingpu.android.apersistance.ormlite.model.Customer;
import com.jingpu.android.apersistance.ormlite.model.District;
import com.jingpu.android.apersistance.ormlite.model.OrderLine;
import com.jingpu.android.apersistance.ormlite.model.Orders;
import com.jingpu.android.apersistance.ormlite.model.Warehouse;

/**
 * Created by Jing Pu on 2015/10/23.
 */
public interface OrmliteDisplay {
    public void displayStockLevel(Object paramObject, short paramShort1, short paramShort2, int paramInt1, int paramInt2)
            throws Exception;

    public void displayOrderStatus(Object paramObject, boolean paramBoolean, Customer paramCustomer, Orders paramOrder, OrderLine[] paramArrayOfOrderLine)
            throws Exception;

    public void displayPayment(Object paramObject, String paramString, boolean paramBoolean, Warehouse paramWarehouse, District paramDistrict, Customer paramCustomer)
            throws Exception;

    public void displayNewOrder(Object paramObject, Warehouse paramWarehouse, District paramDistrict, Customer paramCustomer, Orders paramOrder)
            throws Exception;

    public void displayScheduleDelivery(Object paramObject, short paramShort1, short paramShort2)
            throws Exception;
}

