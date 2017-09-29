package com.flayvr.myrollshared.managers;

import android.content.Context;
import android.content.Intent;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.flayvr.myrollshared.events.IABOnBillingError;
import com.flayvr.myrollshared.events.IABOnPurchaseEvent;
import de.greenrobot.event.EventBus;
import java.util.Iterator;
import java.util.List;

public abstract class IABManager
    implements com.anjlab.android.iab.v3.BillingProcessor.IBillingHandler
{

    protected BillingProcessor bp;

    protected IABManager(Context context, String s)
    {
        bp = new BillingProcessor(context, s, this);
    }

    public abstract List getSubscriptionIds();

    public boolean isSubscribed()
    {
        bp.loadOwnedPurchasesFromGoogle();
        for(Iterator iterator = bp.listOwnedSubscriptions().iterator(); iterator.hasNext();)
        {
            String s = (String)iterator.next();
            if(getSubscriptionIds().contains(s))
            {
                return true;
            }
        }

        return false;
    }

    public boolean onActivityResult(int i, int j, Intent intent)
    {
        return bp.handleActivityResult(i, j, intent);
    }

    public void onBillingError(int i, Throwable throwable)
    {
        EventBus.getDefault().post(new IABOnBillingError(i, throwable));
    }

    public void onBillingInitialized()
    {
        bp.loadOwnedPurchasesFromGoogle();
        if(!isSubscribed())
        {
            onUserUnsubscribed();
        }
    }

    public void onProductPurchased(String s, TransactionDetails transactiondetails)
    {
        EventBus.getDefault().post(new IABOnPurchaseEvent(s, transactiondetails));
    }

    public void onPurchaseHistoryRestored()
    {
    }

    public abstract void onUserUnsubscribed();
}
