package com.jingpu.android.apersistance.greendao;

import com.jingpu.android.apersistance.greendao.model.Customer;
import com.jingpu.android.apersistance.greendao.model.District;
import com.jingpu.android.apersistance.greendao.model.OrderLine;
import com.jingpu.android.apersistance.greendao.model.Orders;
import com.jingpu.android.apersistance.greendao.model.Warehouse;

/**
 * Created by Jing Pu on 2015/11/17.
 */
public interface GreenDaoDisplay {
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

