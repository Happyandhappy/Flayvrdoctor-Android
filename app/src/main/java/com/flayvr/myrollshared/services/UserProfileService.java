package com.flayvr.myrollshared.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.utils.IntentActions;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserProfileService extends IntentService
{

    public static final long DAYS_BACK_TO_CALC_USER_TYPE = 0x134fd9000L;
    public static final int ITEMS_TO_CHECK_HOME = 500;
    public static final int MAX_DISTANCE_BETWEEN_AREAS = 20000;
    public static final int MAX_DISTANCE_BETWEEN_AREAS_FOR_WORK_PLACE = 2000;
    private static final String TAG = UserProfileService.class.getSimpleName();
    private static final List WORK_DAYS;
    private static final int WORK_END_HOUR = 17;
    private static final int WORK_START_HOUR = 9;

    public UserProfileService()
    {
        super(UserProfileService.class.getSimpleName());
    }

    private Location findHome(Collection collection)
    {
        HashMap hashmap;
        Iterator iterator;
        Location location1 = null;
        if(collection.size() != 0) {
            hashmap = new HashMap();
            iterator = collection.iterator();
            boolean flag;
            while(iterator.hasNext())
            {
                Location location3 = ((MediaItem)iterator.next()).getLocation();
                if(location3 != null)
                {
                    Iterator iterator2 = hashmap.keySet().iterator();
                    flag = false;
                    while(iterator2.hasNext())
                    {
                        Location location4 = (Location)iterator2.next();
                        boolean flag1;
                        if(location4.distanceTo(location3) < 20000F)
                            flag1 = true;
                        else
                            flag1 = false;
                        if(flag1) {
                            hashmap.put(location4, Integer.valueOf(1 + ((Integer)hashmap.get(location4)).intValue()));
                            flag = true;
                            break;
                        }
                    }
                    if(!flag)
                        hashmap.put(location3, Integer.valueOf(1));
                }
            }
            Location location = User.UNDEIFINED_LOCATION;
            Iterator iterator1 = hashmap.keySet().iterator();
            int i = 0;
            location1 = location;
            while(iterator1.hasNext())
            {
                Location location2 = (Location)iterator1.next();
                int j;
                if(((Integer)hashmap.get(location2)).intValue() > i)
                {
                    j = ((Integer)hashmap.get(location2)).intValue();
                } else
                {
                    location2 = location1;
                    j = i;
                }
                location1 = location2;
                i = j;
            }
        }
        return location1;
    }

    private Location findWork(Collection collection)
    {
        int i;
        HashMap hashmap;
        SimpleDateFormat simpledateformat;
        Iterator iterator;
        i = 0;
        hashmap = new HashMap();
        simpledateformat = new SimpleDateFormat("dd/MM/yyyy");
        iterator = collection.iterator();
        boolean flag;
        while(iterator.hasNext())
        {
            MediaItem mediaitem = (MediaItem)iterator.next();
            Location location2 = mediaitem.getLocation();
            if(location2 != null)
            {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mediaitem.getDate());
                int k = calendar.get(Calendar.DAY_OF_WEEK);
                int l = calendar.get(Calendar.HOUR_OF_DAY);
                if(!(!WORK_DAYS.contains(Integer.valueOf(k)) || l < 9 || l > 17))
                {
                    Iterator iterator2 = hashmap.keySet().iterator();
                    flag = false;
                    while(iterator2.hasNext())
                    {
                        Location location3 = (Location)iterator2.next();
                        boolean flag1;
                        if(location3.distanceTo(location2) < 2000F)
                            flag1 = true;
                        else
                            flag1 = false;
                        if(flag1) {
                            ((Set)hashmap.get(location3)).add(simpledateformat.format(mediaitem.getDate()));
                            flag = true;
                            break;
                        }
                    }
                    if(!flag)
                    {
                        HashSet hashset;
                        hashset = new HashSet();
                        hashset.add(simpledateformat.format(mediaitem.getDate()));
                        hashmap.put(location2, hashset);
                    }
                }
            }
        }
        Iterator iterator1 = hashmap.keySet().iterator();
        Location location = null;
        while(iterator1.hasNext()) 
        {
            Location location1 = (Location)iterator1.next();
            Log.d(TAG, (new StringBuilder()).append("possible work location, with ").append(((Set)hashmap.get(location1)).size()).append(" occurrences: ").append(location1.getLatitude()).append(" , ").append(location1.getLongitude()).toString());
            int j;
            if(((Set)hashmap.get(location1)).size() > i)
            {
                j = ((Set)hashmap.get(location1)).size();
            } else
            {
                location1 = location;
                j = i;
            }
            location = location1;
            i = j;
        }
        if(i >= 5)
            return location;
        else
            return null;
    }

    private com.flayvr.myrollshared.data.Folder.UserType getFolderUserType(Folder folder)
    {
        MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        String s = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date.columnName;
        SQLiteDatabase sqlitedatabase = mediaitemdao.getDatabase();
        String s1 = mediaitemdao.getTablename();
        String as[] = new String[3];
        as[0] = (new StringBuilder()).append("MAX(").append(s).append(")").toString();
        as[1] = (new StringBuilder()).append("MIN(").append(s).append(")").toString();
        as[2] = "COUNT(*)";
        String s2 = (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Date.columnName).append(" > ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is NULL").toString();
        String as1[] = new String[2];
        as1[0] = Long.toString(folder.getId().longValue());
        as1[1] = Long.toString((new Date()).getTime() - 0x134fd9000L);
        Cursor cursor = sqlitedatabase.query(s1, as, s2, as1, null, null, null);
        if(cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            Date date = new Date(cursor.getLong(0));
            Date date1 = new Date(cursor.getLong(1));
            int i = cursor.getInt(2);
            cursor.close();
            int j = (int)((date.getTime() - date1.getTime()) / 0x5265c00L);
            double d;
            if(j > 0)
            {
                d = (1.0D * (double)i) / (double)j;
            } else
            {
                d = 0.0D;
            }
            if(d < 1.0D)
            {
                return com.flayvr.myrollshared.data.Folder.UserType.FEW;
            }
            if(d >= 1.0D && d < 2D)
            {
                return com.flayvr.myrollshared.data.Folder.UserType.NORMAL;
            } else
            {
                return com.flayvr.myrollshared.data.Folder.UserType.LOT;
            }
        } else
        {
            return com.flayvr.myrollshared.data.Folder.UserType.FEW;
        }
    }

    public static List getIntentFilter()
    {
        ArrayList arraylist = new ArrayList();
        arraylist.add(IntentActions.ACTION_GALLERY_BUILDER_NO_CHANGE);
        arraylist.add(IntentActions.ACTION_NEW_MEDIA);
        return arraylist;
    }

    public Collection getPhotosForHome()
    {
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder();
        querybuilder.where(com.flayvr.myrollshared.data.FolderDao.Properties.IsCamera.eq(Boolean.valueOf(true)), new WhereCondition[0]);
        List list = querybuilder.build().forCurrentThread().list();
        HashSet hashset = new HashSet();
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            Folder folder = (Folder)iterator.next();
            if(folder.getIsCamera() != null && folder.getIsCamera().booleanValue())
            {
                hashset.add(folder.getId());
            }
        } while(true);
        QueryBuilder querybuilder1 = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder();
        if(hashset.size() > 0)
        {
            querybuilder1.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.in(hashset), new WhereCondition[0]);
        }
        querybuilder1.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder1.orderDesc(aproperty);
        querybuilder1.limit(500);
        return querybuilder1.build().list();
    }

    protected void onHandleIntent(Intent intent)
    {
        boolean flag1;
        Location location;
        Object aobj[];
        int i;
        QueryBuilder querybuilder;
        Iterator iterator;
        User user;
        Location location1;
        int j;
        LinkedList linkedlist1;
        Intent intent1;
        long l;
        Location location2;
        User user1;
        User user2;
        Iterator iterator1;
        Location location3;
        Folder folder;
        com.flayvr.myrollshared.data.Folder.UserType usertype;
        boolean flag2;
        boolean flag = false;
        Log.d(TAG, (new StringBuilder()).append("start user profile service ").append(intent.getAction()).toString());
        Date date = new Date();
        LinkedList linkedlist = new LinkedList();
        FolderDao folderdao = DBManager.getInstance().getDaoSession().getFolderDao();
        if(!IntentActions.ACTION_GALLERY_BUILDER_NO_CHANGE.equals(intent.getAction()))
        {
            flag1 = IntentActions.ACTION_NEW_MEDIA.equals(intent.getAction());
            location = null;
            if(flag1)
            {
                aobj = (Object[])(Object[])intent.getExtras().get(IntentActions.EXTRA_FOLDERS_WITH_ADDED);
                location = null;
                if(aobj != null)
                {
                    i = aobj.length;
                    location = null;
                    if(i > 0)
                    {
                        querybuilder = folderdao.queryBuilder();
                        querybuilder.where(com.flayvr.myrollshared.data.FolderDao.Properties.Id.in(aobj), new WhereCondition[0]);
                        iterator = querybuilder.build().list().iterator();
                        while(iterator.hasNext())
                        {
                            folder = (Folder)iterator.next();
                            usertype = getFolderUserType(folder);
                            if(folder.getUserTypeEnum() != usertype)
                            {
                                folder.setUserTypeEnum(usertype);
                                linkedlist.add(folder);
                                Log.d(TAG, (new StringBuilder()).append(folder.getName()).append(": ").append(usertype).toString());
                            }
                            if(folder.getIsCamera().booleanValue())
                            {
                                flag2 = true;
                            } else
                            {
                                flag2 = flag;
                            }
                            flag = flag2;
                        }
                        user = User.getMyRollUser();
                        if(!flag)
                        {
                            location3 = user.getHomeLocation();
                            location = null;
                            if(location3 == null) {
                                location1 = findHome(getPhotosForHome());
                                if (user.getHomeLocation() == null) {
                                    location = location1;
                                } else {
                                    location = null;
                                    if (location1 != null) {
                                        location = null;
                                        if (location1.distanceTo(user.getHomeLocation()) > 100F)
                                            location = location1;
                                    }
                                }
                            }
                        } else {
                            location1 = findHome(getPhotosForHome());
                            if (user.getHomeLocation() == null) {
                                location = location1;
                            } else {
                                location = null;
                                if (location1 != null) {
                                    location = null;
                                    if (location1.distanceTo(user.getHomeLocation()) > 100F)
                                        location = location1;
                                }
                            }
                        }
                    }
                }
            }
        }else {
            Iterator iterator2 = folderdao.loadAll().iterator();
            while (iterator2.hasNext()) {
                Folder folder1 = (Folder) iterator2.next();
                com.flayvr.myrollshared.data.Folder.UserType usertype1 = getFolderUserType(folder1);
                if (folder1.getUserTypeEnum() != usertype1) {
                    folder1.setUserTypeEnum(usertype1);
                    linkedlist.add(folder1);
                    Log.d(TAG, (new StringBuilder()).append(folder1.getName()).append(": ").append(usertype1).toString());
                }
            }
            Location location4 = findHome(getPhotosForHome());
            if (User.getMyRollUser().getHomeLocation() == location4)
                location4 = null;
            location = location4;
        }
        linkedlist1 = new LinkedList();
        if(linkedlist.size() > 0)
        {
            folderdao.updateInTx(linkedlist);
            for(iterator1 = linkedlist.iterator(); iterator1.hasNext(); linkedlist1.add(((Folder)iterator1.next()).getId())) { }
        }
        if(location != null)
        {
            user2 = User.getMyRollUser();
            user2.setHomeLocation(location);
            DBManager.getInstance().getDaoSession().getUserDao().update(user2);
        }
        if(linkedlist1.size() > 0 || location != null)
        {
            intent1 = new Intent(IntentActions.ACTION_USER_INFO_FETCHED);
            intent1.putExtra(IntentActions.EXTRA_FOLDERS_CHANGED, ((java.io.Serializable) (linkedlist1.toArray())));
            intent1.putExtra(IntentActions.EXTRA_HOME_CHANGED, location);
            sendBroadcast(intent1);
        }
        l = (new Date()).getTime() - date.getTime();
        Log.i(TAG, (new StringBuilder()).append("timing: done ").append(l).toString());
        location2 = findWork(getPhotosForHome());
        user1 = User.getMyRollUser();
        if(location2 != null && user1.getWorkLocation() != location2)
        {
            Log.d(TAG, (new StringBuilder()).append("setting work to ").append(location2.getLatitude()).append(" , ").append(location2.getLongitude()).toString());
            user1.setHomeLocation(location2);
            DBManager.getInstance().getDaoSession().getUserDao().update(user1);
        }
    }

    static 
    {
        Integer ainteger[] = new Integer[4];
        ainteger[0] = Integer.valueOf(2);
        ainteger[1] = Integer.valueOf(3);
        ainteger[2] = Integer.valueOf(4);
        ainteger[3] = Integer.valueOf(5);
        WORK_DAYS = Arrays.asList(ainteger);
    }
}
