package com.jingpu.android.apersistance.realm;

import com.jingpu.android.apersistance.realm.model.Customer;
import com.jingpu.android.apersistance.realm.model.District;
import com.jingpu.android.apersistance.realm.model.OrderLine;
import com.jingpu.android.apersistance.realm.model.Orders;
import com.jingpu.android.apersistance.realm.model.Warehouse;

/**
 * Created by Jing Pu on 2016/1/26.
 */
public interface RealmDisplay {
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
