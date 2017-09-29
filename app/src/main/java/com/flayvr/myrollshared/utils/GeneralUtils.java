package com.flayvr.myrollshared.utils;

import android.content.*;
import android.content.res.Resources;
import android.graphics.Point;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.Toast;
import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.learning.PhotoFeatures;
import de.greenrobot.dao.query.QueryBuilder;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class GeneralUtils
{

    private static DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private static Set screenshotFoldersIds;
    private static Set whatsappFoldersIds;

    public GeneralUtils()
    {
    }

    public static String convertStreamToString(InputStream inputstream)
    {
        BufferedReader bufferedreader;
        StringBuilder stringbuilder;
        bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
        stringbuilder = new StringBuilder();
        try {
            while (true) {
                String s = bufferedreader.readLine();
                if (s == null)
                    break;
                stringbuilder.append((new StringBuilder()).append(s).append("\n").toString());
            }
        }catch(Exception e){}
        return stringbuilder.toString();
    }

    public static float dpFromPx(Context context, float f)
    {
        return f / context.getResources().getDisplayMetrics().density;
    }

    public static float getBatteryLevel(Context context)
    {
        Intent intent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int i = intent.getIntExtra("level", -1);
        int j = intent.getIntExtra("scale", -1);
        if(i == -1 || j == -1)
        {
            return 50F;
        } else
        {
            return 100F * ((float)i / (float)j);
        }
    }

    public static long getDuplicateSizeInBytes(List list)
    {
        Iterator iterator;
        long l;
        DuplicatesSet duplicatesset;
        Iterator iterator1;
        long l1;
        long l3;
        long l4;
        iterator = list.iterator();
        l = 0L;
        while(iterator.hasNext()) {
            duplicatesset = (DuplicatesSet) iterator.next();
            iterator1 = duplicatesset.getDuplicatesSetPhotos().iterator();
            l1 = 0L;
            while (iterator1.hasNext()) {
                DuplicatesSetsToPhotos duplicatessetstophotos = (DuplicatesSetsToPhotos) iterator1.next();
                if (duplicatessetstophotos.getPhoto() == null || duplicatessetstophotos.getPhoto().getId() == duplicatesset.getBestPhotoOfSet().getId()) {
                    long l2 = l1;
                    l3 = l;
                    l4 = l2;
                } else {
                    long l6 = duplicatessetstophotos.getPhoto().getFileSizeBytesSafe().longValue();
                    l += l6;
                    if (l6 <= l1) {
                        long l2 = l1;
                        l3 = l;
                        l4 = l2;
                    } else {
                        l3 = l;
                        l4 = l6;
                    }
                }
                long l5 = l4;
                l = l3;
                l1 = l5;
            }
        }
        return l;
    }

    public static Set getScreenshotFoldersIds()
    {
        if(screenshotFoldersIds != null)
        {
            return screenshotFoldersIds;
        }
        List list = DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder().list();
        if(list.size() > 0)
        {
            screenshotFoldersIds = new HashSet();
            Iterator iterator = list.iterator();
            do
            {
                if(!iterator.hasNext())
                {
                    break;
                }
                Folder folder = (Folder)iterator.next();
                String s = folder.getName();
                if(PhotoFeatures.SCREENSHOTS_FOLDERS_NAMES.contains(s))
                {
                    screenshotFoldersIds.add(folder.getId());
                }
            } while(true);
        } else
        {
            return new HashSet();
        }
        return screenshotFoldersIds;
    }

    public static int[] getSize(Context context)
    {
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int i;
        int j;
        if(android.os.Build.VERSION.SDK_INT > 11)
        {
            Point point = new Point();
            display.getSize(point);
            i = point.x;
            j = point.y;
        } else
        {
            i = display.getWidth();
            j = display.getHeight();
        }
        return (new int[] {
            i, j
        });
    }

    public static Set getWhatsappFolderIds()
    {
        if(whatsappFoldersIds != null)
        {
            return whatsappFoldersIds;
        }
        List list = DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder().list();
        if(list.size() > 0)
        {
            whatsappFoldersIds = new HashSet();
            Iterator iterator = list.iterator();
            do
            {
                if(!iterator.hasNext())
                {
                    break;
                }
                Folder folder = (Folder)iterator.next();
                if(folder.getName().toLowerCase().contains("whatsapp"))
                {
                    whatsappFoldersIds.add(folder.getId());
                }
            } while(true);
        } else
        {
            return new HashSet();
        }
        return whatsappFoldersIds;
    }

    public static String humanReadableByteCount(long l, boolean flag)
    {
        if(l < (long)1024)
        {
            return (new StringBuilder()).append(l).append(" B").toString();
        } else
        {
            int i = (int)(Math.log(l) / Math.log(1024));
            String s = (new StringBuilder()).append("kMGTPE".charAt(i - 1)).append("").toString();
            Object aobj[] = new Object[2];
            aobj[0] = Double.valueOf((double)l / Math.pow(1024, i));
            aobj[1] = s;
            return String.format("%.2f %sB", aobj);
        }
    }

    public static String humanReadableByteCountForDuplicates(List list)
    {
        return humanReadableByteCount(getDuplicateSizeInBytes(list), true);
    }

    public static String humanReadableByteCountForItems(Collection collection)
    {
        Iterator iterator = collection.iterator();
        long l;
        for(l = 0L; iterator.hasNext(); l += ((MediaItem)iterator.next()).getFileSizeBytesSafe().longValue()) { }
        return humanReadableByteCount(l, true);
    }

    public static String humanReadableByteCountIntro(long l)
    {
        return Formatter.formatShortFileSize(FlayvrApplication.getAppContext(), l);
    }

    public static String humanReadableNumber(long l)
    {
        return decimalFormat.format(l);
    }

    public static float pxFromDp(Context context, float f)
    {
        return f * context.getResources().getDisplayMetrics().density;
    }

    public static String readableFileSize(long l)
    {
        if(l <= 0L)
        {
            return "0";
        } else
        {
            String as[] = {
                "B", "KB", "MB", "GB", "TB"
            };
            int i = (int)(Math.log10(l) / Math.log10(1024D));
            return (new StringBuilder()).append((new DecimalFormat("#,##0.#")).format((double)l / Math.pow(1024D, i))).append(" ").append(as[i]).toString();
        }
    }

    public static boolean sendFeedback(Context context, String s, String s1)
    {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("message/rfc822");
        intent.putExtra("android.intent.extra.SUBJECT", s);
        intent.putExtra("android.intent.extra.EMAIL", new String[] {
            s1
        });
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append((new StringBuilder()).append("App version: ").append(AndroidUtils.getAppVersion()).append("\n").toString());
        stringbuilder.append((new StringBuilder()).append("Android version: ").append(AndroidUtils.getAndroidVersion()).append("\n").toString());
        stringbuilder.append((new StringBuilder()).append("Device: ").append(AndroidUtils.getDeviceType()).append("\n").toString());
        stringbuilder.append("==================\n\n");
        intent.putExtra("android.intent.extra.TEXT", stringbuilder.toString());
        try
        {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.send_mail_chooser_title)));
        }
        catch(ActivityNotFoundException activitynotfoundexception)
        {
            Toast.makeText(context, R.string.send_mail_chooser_error, Toast.LENGTH_SHORT).show();
            return true;
        }
        return true;
    }

    public static void sendViewToBack(View view)
    {
        ViewGroup viewgroup = (ViewGroup)view.getParent();
        if(viewgroup != null)
        {
            viewgroup.removeView(view);
            viewgroup.addView(view, 0);
        }
    }
}
