package com.flayvr.myrollshared.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.services.GalleryBuilderService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import java.io.*;
import java.text.*;
import java.util.*;
import org.joda.time.DateTime;
import org.joda.time.Days;

public class AndroidUtils
{

    private static final List FORBIDDEN_COUNTRIES = Arrays.asList(new String[] {
        "CU", "IR", "SD", "SY", "KP"
    });
    private static final String TAG = "flayvr_android_utils";
    private static DateFormat defaultDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private static DateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public AndroidUtils()
    {
    }

    private static String capitalize(String s)
    {
        if(s == null || s.length() == 0)
        {
            s = "";
        } else
        {
            char c = s.charAt(0);
            if(!Character.isUpperCase(c))
            {
                return (new StringBuilder()).append(Character.toUpperCase(c)).append(s.substring(1)).toString();
            }
        }
        return s;
    }

    public static void copy(File file, File file1)
    {
        FileInputStream fileinputstream;
        FileOutputStream fileoutputstream;
        FileInputStream fileinputstream1;
        byte abyte0[];
        int i;
        try
        {
            fileinputstream = new FileInputStream(file);
            fileoutputstream = new FileOutputStream(file1);
            abyte0 = new byte[1024];
            while(true) {
                i = fileinputstream.read(abyte0);
                if (i <= 0)
                    break;
                fileoutputstream.write(abyte0, 0, i);
            }
            file1.setLastModified(file.lastModified());
            if(fileinputstream != null)
                fileinputstream.close();
            if(fileoutputstream != null)
                fileoutputstream.close();
        }
        catch(Throwable throwable)
        {
            fileoutputstream = null;
            fileinputstream1 = null;
        }
        finally
        {
            fileoutputstream = null;
            fileinputstream = null;
        }
    }

    public static String getAndroidVersion()
    {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getAppVersion()
    {
        String s;
        try
        {
            Context context = FlayvrApplication.getAppContext();
            s = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            return s;
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            return "";
        }
    }

    public static int getAppVersionCode()
    {
        int i;
        try
        {
            Context context = FlayvrApplication.getAppContext();
            i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            return i;
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            return -1;
        }
    }

    public static String getCountryCode()
    {
        Context context = FlayvrApplication.getAppContext();
        String s = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getSimCountryIso();
        if(s == null || s.equals(""))
        {
            s = context.getResources().getConfiguration().locale.getCountry();
        }
        if(s != null)
        {
            s = s.toUpperCase();
        }
        return s;
    }

    public static String getDateStr(Date date)
    {
        DateTime datetime = new DateTime(date);
        int i;
        Context context;
        try
        {
            i = Days.daysBetween(datetime.minusHours(5).toDateMidnight(), (new DateTime()).minusHours(5).toDateMidnight()).getDays();
        }
        catch(ArithmeticException arithmeticexception)
        {
            Log.w("flayvr_android_utils", (new StringBuilder()).append("error while calculating date str from ").append(datetime).append(" to ").append(new DateTime()).toString(), arithmeticexception);
            return "";
        }
        context = FlayvrApplication.getAppContext();
        if(i < 1)
        {
            PartOfDay partofday1 = getTime(datetime);
            switch(partofday1)
            {
            default:
                Log.w("flayvr_android_utils", (new StringBuilder()).append("wrong part of day ").append(datetime).toString());
                return "";

            case MORNING: // '\001'
                return context.getString(R.string.this_morning);

            case NOON: // '\002'
                return context.getString(R.string.this_noon);

            case AFTERNOON: // '\003'
                return context.getString(R.string.this_afternoon);

            case EVENING: // '\004'
                return context.getString(R.string.this_evening);

            case NIGHT: // '\005'
                return context.getString(R.string.this_night);
            }
        }
        if(i < 2)
        {
            PartOfDay partofday = getTime(datetime);
            switch(partofday)
            {
            default:
                Log.w("flayvr_android_utils", (new StringBuilder()).append("wrong part of day ").append(datetime).toString());
                return "";

            case MORNING: // '\001'
                return context.getString(R.string.yesterday_morning);

            case NOON: // '\002'
                return context.getString(R.string.yesterday_noon);

            case AFTERNOON: // '\003'
                return context.getString(R.string.yesterday_afternoon);

            case EVENING: // '\004'
                return context.getString(R.string.yesterday_evening);

            case NIGHT: // '\005'
                return context.getString(R.string.last_night);
            }
        }
        if(i < 7)
        {
            switch(datetime.getDayOfWeek())
            {
            default:
                Log.w("flayvr_android_utils", (new StringBuilder()).append("wrong day of hte week ").append(datetime).toString());
                return "";

            case 7: // '\007'
                return context.getString(R.string.last_sunday);

            case 1: // '\001'
                return context.getString(R.string.last_monday);

            case 2: // '\002'
                return context.getString(R.string.last_tuesday);

            case 3: // '\003'
                return context.getString(R.string.last_wednesday);

            case 4: // '\004'
                return context.getString(R.string.last_thursday);

            case 5: // '\005'
                return context.getString(R.string.last_friday);

            case 6: // '\006'
                return context.getString(R.string.last_saturday);
            }
        } else
        {
            return defaultDateFormat.format(date);
        }
    }

    public static String getDateStr(Date date, Date date1, boolean flag)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        if(calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == calendar1.get(Calendar.DAY_OF_YEAR))
        {
            if(flag)
                return getDateStr(date);
            else
                return defaultDateFormat.format(date);
        }
        if(calendar.get(Calendar.MONTH) != calendar1.get(Calendar.MONTH))
        {
            SimpleDateFormat simpledateformat = new SimpleDateFormat("MMM dd", Locale.getDefault());
            return (new StringBuilder()).append(simpledateformat.format(date)).append("-").append(simpledateformat.format(date1)).toString();
        } else
        {
            SimpleDateFormat simpledateformat1 = new SimpleDateFormat("MMM dd", Locale.getDefault());
            SimpleDateFormat simpledateformat2 = new SimpleDateFormat("dd", Locale.getDefault());
            return (new StringBuilder()).append(simpledateformat1.format(date)).append("-").append(simpledateformat2.format(date1)).toString();
        }
    }

    public static DateFormat getDefaultDateFormat()
    {
        return defaultDateFormat;
    }

    public static String getDeviceType()
    {
        String s = Build.MANUFACTURER;
        String s1 = Build.MODEL;
        if(s1.startsWith(s))
        {
            return capitalize(s1);
        } else
        {
            return (new StringBuilder()).append(capitalize(s)).append(" ").append(s1).toString();
        }
    }

    public static File getExternalCacheDir(Context context)
    {
        if(hasExternalCacheDir())
        {
            return context.getExternalCacheDir();
        } else
        {
            String s = (new StringBuilder()).append("/Android/data/").append(context.getPackageName()).append("/cache/").toString();
            return new File((new StringBuilder()).append(Environment.getExternalStorageDirectory().getPath()).append(s).toString());
        }
    }

    public static String getFileExtenstion(String s)
    {
        return s.substring(1 + s.lastIndexOf('.'));
    }

    public static DateFormat getServerDateFormat()
    {
        return serverDateFormat;
    }

    public static PartOfDay getTime(DateTime datetime)
    {
        int i = datetime.getHourOfDay();
        if(i >= 5 && i < 11)
        {
            return PartOfDay.MORNING;
        }
        if(i >= 11 && i < 15)
        {
            return PartOfDay.NOON;
        }
        if(i >= 15 && i < 18)
        {
            return PartOfDay.AFTERNOON;
        }
        if(i >= 18 && i < 22)
        {
            return PartOfDay.EVENING;
        }
        if(i >= 22 && i <= 24 || i >= 0 && i < 5)
        {
            return PartOfDay.NIGHT;
        } else
        {
            return PartOfDay.OTHER;
        }
    }

    public static boolean hasExternalCacheDir()
    {
        return android.os.Build.VERSION.SDK_INT >= 8;
    }

    public static boolean hasWear()
    {
        GoogleApiClient googleapiclient = (new com.google.android.gms.common.api.GoogleApiClient.Builder(FlayvrApplication.getAppContext())).addApi(Wearable.API).build();
        googleapiclient.connect();
        PendingResult pendingresult = Wearable.NodeApi.getConnectedNodes(googleapiclient);
        Wearable.NodeApi.addListener(googleapiclient, new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(Node node)
            {
                FlayvrApplication.setHasWear(true);
                if(!SharedPreferencesManager.didSendConnectedToWearEvent().booleanValue())
                {
                    SharedPreferencesManager.setSentConnectedToWearEvent();
                    AnalyticsUtils.trackEventWithKISSOnce("connected to wear");
                }
            }

            @Override
            public void onPeerDisconnected(Node node)
            {
                FlayvrApplication.setHasWear(false);
            }
        });
        return ((com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult)pendingresult.await()).getNodes().size() > 0;
    }

    public static void hideKeyboard(Activity activity)
    {
        InputMethodManager inputmethodmanager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view != null)
            inputmethodmanager.hideSoftInputFromWindow(view.getWindowToken(), 2);
    }

    public static boolean isAppVersionSupported()
    {
        boolean flag;
        try
        {
            flag = (new Date()).before((new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)).parse("10/05/2013"));
            return flag;
        }
        catch(ParseException parseexception)
        {
            return true;
        }
    }

    public static boolean isChargerConnected(Context context)
    {
        int i = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("plugged", -1);
        return i == 1 || i == 2;
    }

    public static boolean isExternalStorageRemovable()
    {
        if(android.os.Build.VERSION.SDK_INT >= 9)
        {
            return Environment.isExternalStorageRemovable();
        } else
        {
            return true;
        }
    }

    public static boolean isForbiddenCountry(Context context)
    {
        String s = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getSimCountryIso();
        if(s != null)
        {
            String s5 = s.toUpperCase();
            if(FORBIDDEN_COUNTRIES.contains(s5))
            {
                return true;
            }
        }
        String s1 = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkCountryIso();
        if(s1 != null)
        {
            String s4 = s1.toUpperCase();
            if(FORBIDDEN_COUNTRIES.contains(s4))
            {
                return true;
            }
        }
        String s2 = context.getResources().getConfiguration().locale.getCountry();
        if(s2 != null)
        {
            String s3 = s2.toUpperCase();
            if(FORBIDDEN_COUNTRIES.contains(s3))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isMyServiceRunning(Class class1, Context context)
    {
        for(Iterator iterator = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(0x7fffffff).iterator(); iterator.hasNext();)
        {
            android.app.ActivityManager.RunningServiceInfo runningserviceinfo = (android.app.ActivityManager.RunningServiceInfo)iterator.next();
            if(class1.getName().equals(runningserviceinfo.service.getClassName()))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isServiceRunning(Class class1)
    {
        for(Iterator iterator = ((ActivityManager)FlayvrApplication.getAppContext().getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(0x7fffffff).iterator(); iterator.hasNext();)
        {
            android.app.ActivityManager.RunningServiceInfo runningserviceinfo = (android.app.ActivityManager.RunningServiceInfo)iterator.next();
            if(class1.getName().equals(runningserviceinfo.service.getClassName()))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isWiFiConnected(Context context)
    {
        return ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(1).isConnected();
    }

    public static void move(MediaItem mediaitem, File file)
    {
        copy(new File(mediaitem.getPath()), file);
        GalleryBuilderService.deleteItems(Arrays.asList(new MediaItem[] {
            mediaitem
        }));
    }

    private enum PartOfDay
    {
        MORNING("MORNING", 0),
        NOON("NOON", 1),
        AFTERNOON("AFTERNOON", 2),
        EVENING("EVENING", 3),
        NIGHT("NIGHT", 4),
        OTHER("OTHER", 5);

        private PartOfDay(String s, int i)
        {
        }
    }
}
