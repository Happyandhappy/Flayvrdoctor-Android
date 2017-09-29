package com.flayvr.myrollshared.events;

import com.anjlab.android.iab.v3.TransactionDetails;

public class IABOnPurchaseEvent
{

    private final String text;
    private final TransactionDetails transactionDetails;

    public IABOnPurchaseEvent(String s, TransactionDetails transactiondetails)
    {
        text = s;
        transactionDetails = transactiondetails;
    }

    public String getText()
    {
        return text;
    }

    public TransactionDetails getTransactionDetails()
    {
        return transactionDetails;
    }
}
