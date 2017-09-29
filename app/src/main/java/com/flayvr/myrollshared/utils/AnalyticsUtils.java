package com.flayvr.myrollshared.utils;

import android.util.Log;
import com.kissmetrics.sdk.KISSmetricsAPI;
import java.util.*;

public class AnalyticsUtils
{

    private static Set events = new HashSet();

    public AnalyticsUtils()
    {
    }

    public static void trackEventWithKISS(String s)
    {
        /*try
        {
            KISSmetricsAPI.sharedAPI().record((new StringBuilder()).append(s).append(" android").toString());
        }
        catch(Exception exception)
        {
            Log.e("KISS analytics", exception.getMessage(), exception);
        }
        if(!s.equals("opened app") && !s.equals("exited app"))
        {
            ServerUtils.createAppEventAsync(s, new String[0]);
        }*/
    }

    public static void trackEventWithKISS(String s, HashMap hashmap)
    {
        //trackEventWithKISS(s, hashmap, true);
    }

    public static void trackEventWithKISS(String s, HashMap hashmap, boolean flag)
    {
        /*Iterator iterator = hashmap.keySet().iterator();
        while(iterator.hasNext())
        {
            String s1 = (String)iterator.next();
            if(hashmap.get(s1) == null)
            {
                hashmap.put(s1, "null");
            }
        }
        try
        {
            KISSmetricsAPI.sharedAPI().record((new StringBuilder()).append(s).append(" android").toString(), hashmap);
        }
        catch(Exception exception)
        {
            Log.e("KISS analytics", exception.getMessage(), exception);
        }
        try
        {
            if(!s.equals("opened app") && !s.equals("exited app"))
            {
                String as[] = new String[hashmap.size()];
                hashmap.values().toArray(as);
                ServerUtils.createAppEventAsync(s, as);
            }
        }
        catch(Exception exception1)
        {
            Log.e("KISS analytics", exception1.getMessage(), exception1);
        }*/
    }

    public static void trackEventWithKISSOnce(String s)
    {
        /*if(!events.contains(s))
        {
            events.add(s);
            trackEventWithKISS(s);
        }*/
    }

    public static void trackEventWithKISSOnce(String s, HashMap hashmap)
    {
        /*if(!events.contains(s))
        {
            events.add(s);
            trackEventWithKISS(s, hashmap, true);
        }*/
    }

}
