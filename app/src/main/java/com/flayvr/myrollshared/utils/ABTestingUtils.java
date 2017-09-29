package com.flayvr.myrollshared.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.flayvr.myrollshared.application.AppConfiguration;
import com.flayvr.myrollshared.application.FlayvrApplication;

public class ABTestingUtils
{

    private static final String STICKY_NOTIFICATIONS_KEY = "STICKY_NOTIFICATIONS";
    private static final String SUBSCRIPTION_KEY = "SUBSCRIPTION";
    private static ABTestingUtils instance;
    private STICKY_NOTIFICATION stickyNotification;
    private SUBSCRIPTION subscription;

    private ABTestingUtils()
    {
    }

    public static ABTestingUtils getInstance()
    {
        if(instance == null)
        {
            instance = new ABTestingUtils();
        }
        return instance;
    }

    private void storeABTestingType(String s, String s1)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putString(s, s1);
        editor.commit();
    }

    public boolean didInitializeSubscription()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).contains("SUBSCRIPTION");
    }

    public Enum getABTestingType(Class class1, String s)
    {
        String s1 = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getString(s, null);
        if(s1 == null)
        {
            double d = Math.random();
            double d1 = 0.0D;
            int i = 0;
            Enum enum2 = null;
            do
            {
label0:
                {
                    if(i < ((Enum[])class1.getEnumConstants()).length)
                    {
                        enum2 = ((Enum[])class1.getEnumConstants())[i];
                        d1 += ((ABTestingEnum)enum2).getValue();
                        if(d1 <= d)
                        {
                            break label0;
                        }
                    }
                    storeABTestingType(s, enum2.name());
                    return enum2;
                }
                i++;
            } while(true);
        } else {
            try
            {
                Enum enum1 = Enum.valueOf(class1, s1);
                return enum1;
            }
            catch(Exception exception)
            {
                Enum enum2 = ((Enum[])class1.getEnumConstants())[0];
                storeABTestingType(s, enum2.name());
                return enum2;
            }
        }
    }

    public STICKY_NOTIFICATION getStickyNotification()
    {
        if(stickyNotification == null)
        {
            stickyNotification = (STICKY_NOTIFICATION)getABTestingType(STICKY_NOTIFICATION.class, "STICKY_NOTIFICATIONS");
        }
        return stickyNotification;
    }

    public SUBSCRIPTION getSubscription(boolean flag)
    {
        if(subscription == null)
        {
            if(!flag)
            {
                if(!FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).contains("SUBSCRIPTION"))
                {
                    subscription = SUBSCRIPTION.DISABLE;
                    storeABTestingType("SUBSCRIPTION", subscription.name());
                } else
                {
                    subscription = (SUBSCRIPTION)getABTestingType(SUBSCRIPTION.class, "SUBSCRIPTION");
                }
            } else
            {
                subscription = (SUBSCRIPTION)getABTestingType(SUBSCRIPTION.class, "SUBSCRIPTION");
            }
        }
        return subscription;
    }

    interface ABTestingEnum
    {
        public abstract double getValue();
    }

    private enum STICKY_NOTIFICATION implements ABTestingEnum
    {
        ENABLE("ENABLE", 0, 0.0D),
        DISABLE("DISABLE", 1, 1.0D);

        private final double value;

        public double getValue()
        {
            return value;
        }

        private STICKY_NOTIFICATION(String s, int i, double d)
        {
            value = d;
        }
    }

    private enum SUBSCRIPTION implements ABTestingEnum
    {
        ENABLE("ENABLE", 0, 1.0D),
        DISABLE("DISABLE", 1, 0.0D);
        private final double value;

        public double getValue()
        {
            return value;
        }

        private SUBSCRIPTION(String s, int i, double d)
        {
            value = d;
        }
    }
}
