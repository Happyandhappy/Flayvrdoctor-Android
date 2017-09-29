package com.flayvr.myrollshared.services;

import android.app.IntentService;
import android.content.*;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.settings.SharedSettings;
import com.flayvr.myrollshared.utils.IntentActions;
import com.flayvr.myrollshared.utils.SharedPreferencesManager;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;
import java.io.IOException;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

public class MomentsEnrichmentService extends IntentService
{

    private static final String CAL_PROJECTION[] = {
        "_id", "name"
    };
    private static final String EVENT_PROJECTION[] = {
        "title", "dtstart", "dtend", "calendar_id"
    };
    private static final long MAX_EVENT_LENGTH = 0x5265c00L;
    private static final long MIN_EVENT_LENGTH = 0x36ee80L;
    private static final String TAG = MomentsEnrichmentService.class.getSimpleName();
    private static final long TIME_BETWEEN_ENRICHMENT_RUNNING = 0x240c8400L;

    public MomentsEnrichmentService()
    {
        super(MomentsEnrichmentService.class.getSimpleName());
    }

    private String findTitleFromCalendar(Moment moment)
    {
        if(android.os.Build.VERSION.SDK_INT < 14)
            return null;
        String s;
        Cursor cursor;
        long l;
        Set set;
        Map map;
        try
        {
            map = getCalendars();
            if(map == null || map.size() == 0)
            {
                return null;
            }
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("(");
            stringbuilder.append(StringUtils.join(Collections.nCopies(map.size(), "calendar_id= ?").iterator(), " OR "));
            String as[] = new String[3 + map.size()];
            Iterator iterator = map.keySet().iterator();
            for(int i = 0; iterator.hasNext(); i++)
            {
                as[i] = ((Long)iterator.next()).toString();
            }

            stringbuilder.append(") AND ");
            stringbuilder.append("(rrule IS NULL) AND ");
            stringbuilder.append("(allDay != ? ) AND ");
            as[-3 + as.length] = "1";
            stringbuilder.append("(dtstart <= ? ) AND ");
            as[-2 + as.length] = (new StringBuilder()).append(moment.getStartDate().getTime()).append("").toString();
            stringbuilder.append("(dtend >= ?)");
            as[-1 + as.length] = (new StringBuilder()).append(moment.getEndDate().getTime()).append("").toString();
            cursor = FlayvrApplication.getAppContext().getContentResolver().query(android.provider.CalendarContract.Events.CONTENT_URI, EVENT_PROJECTION, stringbuilder.toString(), as, null);
            if(cursor == null)
            {
                return null;
            }
            s = null;
            l = 0L;
            set = SharedSettings.calendarTitlesBlackList;
            while(cursor.moveToNext()) {
                String s1;
                long l1;
                long l2;
                s1 = cursor.getString(0);
                l1 = cursor.getLong(1);
                l2 = cursor.getLong(2);
                Long.valueOf(cursor.getLong(3));
                if (!StringUtils.isBlank(s1)) {
                    boolean flag = false;
                    Iterator iterator1 = set.iterator();
                    while (iterator1.hasNext()) {
                        if (s1.matches((String) iterator1.next())) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        String s2;
                        long l4;
                        long l5;
                        if (l2 - l1 >= 0x36ee80L && l2 - l1 <= 0x5265c00L && l2 - l1 > l) {
                            l4 = l2 - l1;
                            s2 = s1;
                        } else {
                            long l3 = l;
                            s2 = s;
                            l4 = l3;
                        }
                        l5 = l4;
                        s = s2;
                        l = l5;
                    }
                }
            }
            cursor.close();
            if(s == null)
                return null;
            return s;
        } catch(IllegalArgumentException illegalargumentexception) {
            return null;
        } catch(IllegalStateException illegalstateexception) {
            return null;
        } catch(Exception e) {
            return null;
        }
    }

    private Map getCalendars()
    {
        ContentResolver contentresolver = FlayvrApplication.getAppContext().getContentResolver();
        android.net.Uri uri = android.provider.CalendarContract.Calendars.CONTENT_URI;
        String as[] = {
            "700"
        };
        try {
            Cursor cursor = contentresolver.query(uri, CAL_PROJECTION, "calendar_access_level = ?", as, null);
            HashMap hashmap = new HashMap();
            if (cursor == null) {
                return hashmap;
            }
            long l;
            String s;
            for (; cursor.moveToNext(); hashmap.put(Long.valueOf(l), s)) {
                l = cursor.getLong(0);
                s = cursor.getString(1);
            }
            cursor.close();
            return hashmap;
        }catch(Exception e){
            return null;
        }
    }

    public static String getCityFromAdress(List list)
    {
        String s = ((Address)list.get(0)).getLocality();
        if(s == null)
        {
            if((s = ((Address)list.get(0)).getAddressLine(1)) != null)
            {
                return s.replaceAll("^\\d{5}|\\d{5}$", "");
            }
        }
        return s;
    }

    private MediaItem getFirstMediaItemWithLocation(Moment moment)
    {
        for(Iterator iterator = (new LinkedList(moment.getMomentItems())).iterator(); iterator.hasNext();)
        {
            MomentsItems momentsitems = (MomentsItems)iterator.next();
            if(momentsitems.getItem().getLatitude().doubleValue() != 0.0D)
            {
                return momentsitems.getItem();
            }
        }

        return null;
    }

    public static List getIntentFilter()
    {
        ArrayList arraylist = new ArrayList();
        arraylist.add(IntentActions.ACTION_CLUSTERING_FINISHED);
        arraylist.add(IntentActions.ACTION_NEW_MOMENTS);
        return arraylist;
    }

    private MediaItem getLastMediaItemWithLocation(Moment moment)
    {
        for(ListIterator listiterator = moment.getMomentItems().listIterator(moment.getMomentItems().size()); listiterator.hasPrevious();)
        {
            MediaItem mediaitem = ((MomentsItems)listiterator.previous()).getItem();
            if(mediaitem.getLocation() != null)
            {
                return mediaitem;
            }
        }

        return null;
    }

    private String getLocation(Moment moment, Geocoder geocoder)
    {
        MediaItem mediaitem;
        try {
            if (moment.getIsTrip().booleanValue()) {
                return handleMergedFlayvr(moment, geocoder);
            }
            mediaitem = getFirstMediaItemWithLocation(moment);
            if (mediaitem == null) {
                return null;
            }
            String s;
            List list = geocoder.getFromLocation(mediaitem.getLatitude().doubleValue(), mediaitem.getLongitude().doubleValue(), 1);
            if (!isValidAddress(list)) {
                return null;
            }
            s = getCityFromAdress(list);
            return s;
        }catch(IOException ioexception){
            return null;
        }
    }

    private String handleMergedFlayvr(Moment moment, Geocoder geocoder)
    {
        MediaItem mediaitem;
        MediaItem mediaitem1;
        List list;
        List list1;
        List list2;
        boolean flag;
        boolean flag1;
        Address address;
        Address address1;
        String s1;
        String s2;
        String s = null;
        try
        {
            mediaitem = getFirstMediaItemWithLocation(moment);
            mediaitem1 = getLastMediaItemWithLocation(moment);
            if(mediaitem == null) {
                if (mediaitem1 == null)
                    return s;
            }
            if(mediaitem != null)
                list = geocoder.getFromLocation(mediaitem.getLatitude().doubleValue(), mediaitem.getLongitude().doubleValue(), 1);
            else
                list = null;
            if(mediaitem1 == null)
                list2 = null;
            else{
                list1 = geocoder.getFromLocation(mediaitem1.getLatitude().doubleValue(), mediaitem1.getLongitude().doubleValue(), 1);
                list2 = list1;
            }
            if(isValidAddress(list) && !isValidAddress(list2))
                return getCityFromAdress(list);
            if(isValidAddress(list2) && !isValidAddress(list))
                return getCityFromAdress(list2);
            flag = isValidAddress(list);
            s = null;
            if(flag)
            {
                flag1 = isValidAddress(list2);
                s = null;
                if(flag1)
                {
                    s = getCityFromAdress(list);
                    if(s == null || !s.equals(getCityFromAdress(list2)))
                    {
                        address = (Address)list.get(0);
                        address1 = (Address)list2.get(0);
                        s = address.getAdminArea();
                        s1 = address1.getAdminArea();
                        if(s == null || !s.equals(s1))
                        {
                            s = address.getCountryName();
                            s2 = address1.getCountryName();
                            if(s == null || !s.equals(s2))
                            {
                                return (new StringBuilder()).append(s).append(", ").append(s2).toString();
                            }
                        }
                    }
                }
            }
            return s;
        }catch(IllegalArgumentException illegalargumentexception){
            return null;
        }catch(IOException e){
            return null;
        }
    }

    public static boolean isValidAddress(List list)
    {
        return list != null && list.size() > 0;
    }

    private void setLocation(Geocoder geocoder, Moment moment)
    {
        String s;
        if(moment.getLocation() == null)
        {
            if((s = getLocation(moment, geocoder)) != null)
            {
                moment.setLocation(s);
                moment.update();
                return;
            }
        }
    }

    private void setTitle(Moment moment)
    {
        String s;
        if(moment.getTitle() == null)
        {
            if((s = findTitleFromCalendar(moment)) != null)
            {
                moment.setTitle(s);
                moment.setIsTitleFromCalendar(Boolean.valueOf(true));
                moment.update();
                return;
            }
        }
    }

    protected void onHandleIntent(Intent intent)
    {
        Log.d(TAG, "start moments enrichment");
        Date date = new Date();
        if(IntentActions.ACTION_NEW_MOMENTS.equals(intent.getAction()))
        {
            long al[] = intent.getExtras().getLongArray(IntentActions.EXTRA_MOMENTS_ADDED);
            if(al != null && al.length > 0)
            {
                Long along[] = new Long[al.length];
                for(int i = 0; i < al.length; i++)
                {
                    along[i] = Long.valueOf(al[i]);
                }

                MomentDao momentdao1 = DBManager.getInstance().getDaoSession().getMomentDao();
                LinkedList linkedlist = new LinkedList();
                for(int j = 0; j <= (-1 + along.length) / 900; j++)
                {
                    int k = j * 900;
                    int i1 = 900 * (j + 1);
                    if(i1 > along.length)
                    {
                        i1 = along.length;
                    }
                    Long along1[] = (Long[])Arrays.copyOfRange(along, k, i1);
                    QueryBuilder querybuilder2 = momentdao1.queryBuilder();
                    querybuilder2.where(com.flayvr.myrollshared.data.MomentDao.Properties.Id.in((Object[])along1), new WhereCondition[0]);
                    linkedlist.addAll(querybuilder2.build().list());
                }

                for(Iterator iterator2 = linkedlist.iterator(); iterator2.hasNext(); setTitle((Moment)iterator2.next())) { }
                Geocoder geocoder1 = new Geocoder(this);
                for(Iterator iterator3 = linkedlist.iterator(); iterator3.hasNext(); setLocation(geocoder1, (Moment)iterator3.next())) { }
            }
        } else
        if(IntentActions.ACTION_CLUSTERING_FINISHED.equals(intent.getAction()) && (SharedPreferencesManager.getLastEnrichmentRunDate() == null || System.currentTimeMillis() > 0x240c8400L + SharedPreferencesManager.getLastEnrichmentRunDate().getTime()))
        {
            MomentDao momentdao = DBManager.getInstance().getDaoSession().getMomentDao();
            QueryBuilder querybuilder = momentdao.queryBuilder();
            querybuilder.where(com.flayvr.myrollshared.data.MomentDao.Properties.Title.isNull(), new WhereCondition[0]);
            querybuilder.where(com.flayvr.myrollshared.data.MomentDao.Properties.IsTitleFromCalendar.isNull(), new WhereCondition[0]);
            for(Iterator iterator = querybuilder.build().list().iterator(); iterator.hasNext(); setTitle((Moment)iterator.next())) { }
            QueryBuilder querybuilder1 = momentdao.queryBuilder();
            querybuilder1.where(com.flayvr.myrollshared.data.MomentDao.Properties.Location.isNull(), new WhereCondition[0]);
            List list = querybuilder1.build().list();
            Geocoder geocoder = new Geocoder(this);
            for(Iterator iterator1 = list.iterator(); iterator1.hasNext(); setLocation(geocoder, (Moment)iterator1.next())) { }
            SharedPreferencesManager.setLastEnrichmentRunDate(new Date());
        }
        Log.d(TAG, "timing: moments enrichment finished");
        long l = (new Date()).getTime() - date.getTime();
        Log.i(TAG, (new StringBuilder()).append("timing: done ").append(l).toString());
    }

}
