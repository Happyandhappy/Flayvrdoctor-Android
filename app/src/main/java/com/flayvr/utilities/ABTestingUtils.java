package com.flayvr.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import com.flayvr.myrollshared.application.AppConfiguration;
import com.flayvr.myrollshared.application.FlayvrApplication;

public class ABTestingUtils
{

    private static final String RATE_US_TEXT_KEY = "RATE_US_TEXT";
    private static final String SEND_IMAGE_ANALYTICS_KEY = "SEND_IMAGE_ANALYTICS";
    private static final String TEST_BACKUP_OPTION_KEY = "TEST_BACKUP_OPTION";
    private static ABTestingUtils instance;
    private SEND_IMAGE_ANALYTICS sendImageAnalytics;
    private TEST_BACKUP_OPTION testBackupOption;

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

    public Enum getABTestingType(Class class1, String s)
    {
        String s1 = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getString(s, null);
        if(s1 == null)
        {
            double d = Math.random();
            double d1 = 0.0D;
            int i = 0;
            Enum enum2 = null;
            while(i < ((Enum[])class1.getEnumConstants()).length)
            {
                enum2 = ((Enum[])class1.getEnumConstants())[i];
                d1 += ((ABTestingEnum)enum2).getValue();
                if(d1 > d)
                    break;
                i++;
            }
            storeABTestingType(s, enum2.name());
            return enum2;
        } else {
            Enum enum1 = Enum.valueOf(class1, s1);
            return enum1;
        }
    }

    public SEND_IMAGE_ANALYTICS getSendImageAnalytics()
    {
        if(sendImageAnalytics == null)
        {
            sendImageAnalytics = (SEND_IMAGE_ANALYTICS)getABTestingType(ABTestingUtils.SEND_IMAGE_ANALYTICS.class, "SEND_IMAGE_ANALYTICS");
        }
        return sendImageAnalytics;
    }

    public TEST_BACKUP_OPTION getTestBackupOption()
    {
        if(testBackupOption == null)
        {
            testBackupOption = (TEST_BACKUP_OPTION)getABTestingType(ABTestingUtils.TEST_BACKUP_OPTION.class, "TEST_BACKUP_OPTION");
        }
        return testBackupOption;
    }

    interface ABTestingEnum
    {
        public abstract double getValue();
    }


    enum SEND_IMAGE_ANALYTICS implements ABTestingEnum
    {
        SEND("SEND", 0, 0.050000000000000003D),
        DONT_SEND("DONT_SEND", 1, 0.94999999999999996D);
        private final double value;

        public double getValue()
        {
            return value;
        }

        private SEND_IMAGE_ANALYTICS(String s, int i, double d)
        {
            value = d;
        }
    }


    enum TEST_BACKUP_OPTION implements ABTestingEnum {
        TEST_WITH_PRICE("TEST_WITH_PRICE", 0, 0.050000000000000003D),
        TEST_WITHOUT_PRICE("TEST_WITHOUT_PRICE", 1, 0.050000000000000003D),
        DONT_TEST("DONT_TEST", 2, 0.90000000000000002D);
        private final double value;

        public double getValue()
        {
            return value;
        }

        private TEST_BACKUP_OPTION(String s, int i, double d)
        {
            value = d;
        }
    }

}
