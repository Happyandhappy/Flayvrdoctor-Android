package com.flayvr.myrollshared.learning;

import android.location.Location;
import com.flayvr.myrollshared.data.*;
import java.util.*;

public class PhotoFeatures
{

    public static final List BORING_FOLDERS_NAMES = Arrays.asList(new String[] {
        "WhatsApp Images", "Messenger", "Viber Images", "Twitter", "QQ_Images", "NAVER_LINE", "Snapchat", "VK", "Tumblr"
    });
    public static final int MAX_DISTANCE_FROM_WORK = 1000;
    public static final int MAX_PHOTOS_IN_FOLDER_TO_BE_CONSIDERED_BORING = 5;
    public static final List SCREENSHOTS_FOLDERS_NAMES = Arrays.asList(new String[] {
        "Screenshots", "ScreenCapture", "Screenshot"
    });
    public static final List TEMP_FOLDERS_NAMES = Arrays.asList(new String[] {
        "Temp", "Tmp", "cache"
    });

    public PhotoFeatures()
    {
    }

    public static int getNumberOfPhotosInMoments(MediaItem mediaitem)
    {
        List list = mediaitem.getMoments();
        int i = 0;
        if(list != null)
        {
            int j = list.size();
            i = 0;
            if(j > 0)
            {
                i = ((Moment)list.get(0)).getAllItems().size();
            }
        }
        return i;
    }

    public static TimeOfDay getTimeOfDay(MediaItem mediaitem)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mediaitem.getDate());
        int i = calendar.get(Calendar.HOUR_OF_DAY);
        if(i >= 5 && i <= 7)
        {
            return TimeOfDay.EARLY_MORNING;
        }
        if(i >= 5 && i <= 7)
        {
            return TimeOfDay.MORNING;
        }
        if(i >= 8 && i <= 10)
        {
            return TimeOfDay.NOON;
        }
        if(i >= 11 && i <= 16)
        {
            return TimeOfDay.AFTERNOON;
        }
        if(i >= 17 && i <= 20)
        {
            return TimeOfDay.EVENING;
        }
        if(i >= 21 || i <= 1)
        {
            return TimeOfDay.NIGHT;
        } else
        {
            return TimeOfDay.LATE_NIGHT;
        }
    }

    public static boolean isFromWhatsapp(MediaItem mediaitem)
    {
        if(mediaitem.getFolder() != null && mediaitem.getFolder().getName() != null)
        {
            return mediaitem.getFolder().getName().toLowerCase().contains("whatsapp");
        } else
        {
            return false;
        }
    }

    public static boolean isInBoringFolder(MediaItem mediaitem)
    {
        boolean flag;
label0:
        {
            Folder folder = mediaitem.getFolder();
            flag = false;
            if(folder == null)
            {
                break label0;
            }
            String s = mediaitem.getFolder().getName();
            flag = false;
            if(s == null)
            {
                break label0;
            }
            if(!BORING_FOLDERS_NAMES.contains(mediaitem.getFolder().getName()))
            {
                flag = false;
                if(mediaitem.getFolder().getNotDeletedMediaItemCount().longValue() >= 5)
                {
                    break label0;
                }
            }
            flag = true;
        }
        return flag;
    }

    public static boolean isInHiddenMoment(MediaItem mediaitem)
    {
label0:
        {
            List list = mediaitem.getMoments();
            if(list == null)
            {
                break label0;
            }
            Iterator iterator = list.iterator();
            Moment moment;
            do
            {
                if(!iterator.hasNext())
                {
                    break label0;
                }
                moment = (Moment)iterator.next();
            } while(moment == null || moment.getIsHidden() == null || !moment.getIsHidden().booleanValue());
            return true;
        }
        return false;
    }

    public static boolean isInTemporaryPhoto(MediaItem mediaitem)
    {
        if(mediaitem.getFolder() != null && mediaitem.getFolder().getName() != null)
        {
            return TEMP_FOLDERS_NAMES.contains(mediaitem.getFolder().getName());
        } else
        {
            return false;
        }
    }

    public static boolean isOfGoodEnoughScore(MediaItem mediaitem)
    {
        ClassifierThreshold classifierthreshold = DaoHelper.getThreshold(mediaitem.getSource().intValue());
        Double double1 = mediaitem.getScore();
        boolean flag = false;
        if(double1 != null)
        {
            flag = false;
            if(classifierthreshold != null)
            {
                flag = false;
                if(mediaitem.getScore().doubleValue() > classifierthreshold.getGoodEnoughScore().doubleValue())
                {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public static boolean isScreenshot(MediaItem mediaitem)
    {
        if(mediaitem.getFolder() != null && mediaitem.getFolder().getName() != null)
        {
            return SCREENSHOTS_FOLDERS_NAMES.contains(mediaitem.getFolder().getName());
        } else
        {
            return false;
        }
    }

    public static boolean isTakenAtWork(MediaItem mediaitem, User user)
    {
        return mediaitem.getLocation() != null && user.getWorkLocation() != null && mediaitem.getLocation().distanceTo(user.getWorkLocation()) <= 1000F;
    }


    private enum TimeOfDay
    {
        EARLY_MORNING("EARLY_MORNING", 0),
        MORNING("MORNING", 1),
        NOON("NOON", 2),
        AFTERNOON("AFTERNOON", 3),
        EVENING("EVENING", 4),
        NIGHT("NIGHT", 5),
        LATE_NIGHT("LATE_NIGHT", 6);

        private TimeOfDay(String s, int i)
        {
        }
    }

}
