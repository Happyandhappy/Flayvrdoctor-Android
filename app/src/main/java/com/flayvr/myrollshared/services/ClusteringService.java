package com.flayvr.myrollshared.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.oldclasses.FlayvrsDBManager;
import com.flayvr.myrollshared.oldclasses.MediaGroupDate;
import com.flayvr.myrollshared.utils.IntentActions;
import com.flayvr.myrollshared.utils.SharedPreferencesManager;
import com.kissmetrics.sdk.KISSmetricsAPI;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;
import java.util.*;

public class ClusteringService extends IntentService
{

    public static final int MAX_DISTANCE_BETWEEN_GROUPS = 20000;
    public static final int MAX_DISTANCE_BETWEEN_MEDIA_ITEMS = 5000;
    public static final int MAX_DISTANCE_FROM_HOME = 50000;
    public static final int MAX_ITEMS_IN_FLAYVR = 80;
    public static final int MAX_PHOTOS_IN_FIRST_DAY_FLAYVR = 40;
    public static final int MAX_PHOTOS_IN_MERGED_FLAYVR = 100;
    public static final int MAX_PHOTOS_IN_SECOND_DAY_FLAYVR = 20;
    public static final long MAX_TIME_BETWEEN_GROUPS = 0x5265c00L;
    public static final long MAX_TIME_BETWEEN_GROUPS_IN_THE_SAME_DAY = 0x2932e00L;
    public static final long MAX_TIME_BETWEEN_MEDIA_ITEMS_FEW = 0xdbba00L;
    public static final long MAX_TIME_BETWEEN_MEDIA_ITEMS_IN_THE_SAME_LOCATION_FEW = 0x1499700L;
    public static final long MAX_TIME_BETWEEN_MEDIA_ITEMS_IN_THE_SAME_LOCATION_LOT = 0xdbba00L;
    public static final long MAX_TIME_BETWEEN_MEDIA_ITEMS_IN_THE_SAME_LOCATION_NORMAL = 0x112a880L;
    public static final long MAX_TIME_BETWEEN_MEDIA_ITEMS_LOT = 0xa4cb80L;
    public static final long MAX_TIME_BETWEEN_MEDIA_ITEMS_NORMAL = 0xa4cb80L;
    public static final long MAX_TIME_BETWEEN_MEDIA_ITEMS_WHATSAPP = 0x927c0L;
    public static final int MAX_VIDEOS_IN_FLAYVR = 80;
    public static final int MIN_ITEMS_IN_FLAYVR = 3;
    public static final int MIN_PHOTOS_IN_FLAYVR_FEW = 3;
    public static final int MIN_PHOTOS_IN_FLAYVR_LOT = 5;
    public static final int MIN_PHOTOS_IN_FLAYVR_NORMAL = 4;
    private static final String TAG = ClusteringService.class.getSimpleName();
    Query itemQuery;

    public ClusteringService()
    {
        super(ClusteringService.class.getSimpleName());
    }

    public static List cleanDeletedMoments(Collection collection)
    {
        DBManager dbmanager = DBManager.getInstance();
        QueryBuilder querybuilder = dbmanager.getDaoSession().getMomentsItemsDao().queryBuilder();
        querybuilder.where(com.flayvr.myrollshared.data.MomentsItemsDao.Properties.ItemId.eq(null), new WhereCondition[0]);
        Query query = querybuilder.build().forCurrentThread();
        HashSet hashset = new HashSet();
        HashSet hashset1 = new HashSet();
        for(Iterator iterator = collection.iterator(); iterator.hasNext();)
        {
            query.setParameter(0, ((MediaItem)iterator.next()).getId());
            Iterator iterator2 = query.list().iterator();
            while(iterator2.hasNext()) 
            {
                MomentsItems momentsitems = (MomentsItems)iterator2.next();
                hashset.add(momentsitems);
                hashset1.add(momentsitems.getMoment());
            }
        }

        dbmanager.getDaoSession().getMomentsItemsDao().deleteInTx(hashset);
        LinkedList linkedlist = new LinkedList();
        Iterator iterator1 = hashset1.iterator();
        do
        {
            if(!iterator1.hasNext())
            {
                break;
            }
            Moment moment = (Moment)iterator1.next();
            if(moment != null)
            {
                moment.resetMomentItems();
                if(moment.getMomentItems() == null || moment.getPhotos().size() < 3)
                {
                    if(moment.getMomentItems() != null && moment.getMomentItems().size() >= 1)
                    {
                        dbmanager.getDaoSession().getMomentsItemsDao().deleteInTx(moment.getMomentItems());
                    }
                    moment.delete();
                    linkedlist.add(moment.getId());
                }
            }
        } while(true);
        return linkedlist;
    }

    private Moment createMomentForItems(List list, Folder folder)
    {
        Moment moment = new Moment();
        ArrayList arraylist = new ArrayList(list.size());
        Date date = ((MediaItem)list.get(0)).getDate();
        Date date1 = ((MediaItem)list.get(0)).getDate();
        Iterator iterator = list.iterator();
        Date date2 = date;
        Date date3 = date1;
        while(iterator.hasNext()) 
        {
            MediaItem mediaitem = (MediaItem)iterator.next();
            MomentsItems momentsitems = new MomentsItems();
            momentsitems.setItem(mediaitem);
            momentsitems.setTempMoment(moment);
            arraylist.add(momentsitems);
            Date date4;
            if(!date2.before(mediaitem.getDate()))
                date2 = mediaitem.getDate();
            if(date3.after(mediaitem.getDate()))
                date4 = date3;
            else
                date4 = mediaitem.getDate();
            date3 = date4;
        }
        moment.setTempItems(arraylist);
        moment.setIsFavorite(Boolean.valueOf(false));
        moment.setIsHidden(Boolean.valueOf(false));
        moment.setIsTrip(Boolean.valueOf(false));
        moment.setFolder(folder);
        moment.setStartDate(date2);
        moment.setEndDate(date3);
        moment.setWatchCount(Integer.valueOf(0));
        return moment;
    }

    private void dbMigrate(Folder folder, LinkedList linkedlist, Set set)
    {
        if(SharedPreferencesManager.isMigratedDB33(folder))
            return;
        List list = FlayvrsDBManager.getInstance().getFolderMoments(folder.getId().toString());
        Iterator iterator = linkedlist.iterator();
        Iterator iterator1 = list.iterator();
        Moment moment;
        DBMoment dbmoment;
        HashSet hashset;
        DBMoment dbmoment1;
        Moment moment1;
        Iterator iterator2;
        Moment moment2;
        DBMoment dbmoment2;
        Moment moment3;
        Moment moment4;
        int j;
        if(iterator.hasNext())
            moment = (Moment)iterator.next();
        else
            moment = null;
        if(iterator1.hasNext())
            dbmoment = (DBMoment)iterator1.next();
        else
            dbmoment = null;
        hashset = new HashSet();
        dbmoment1 = dbmoment;
        moment1 = moment;
        while(moment1 != null && dbmoment1 != null) 
        {
            List list1 = moment1.getTempItems();
            Set set1 = dbmoment1.getOriginalItems();
            Iterator iterator3 = list1.iterator();
            int i = 0;
            while(iterator3.hasNext()) 
            {
                MomentsItems momentsitems = (MomentsItems)iterator3.next();
                if(set1 != null && set1.contains(momentsitems.getItem().getAndroidId()))
                {
                    j = i + 1;
                } else
                {
                    j = i;
                }
                i = j;
            }
            if(i > list1.size() / 2)
            {
                if(dbmoment1.getIsMerged() != null && dbmoment1.getIsMerged().booleanValue())
                {
                    hashset.add(moment1);
                } else
                {
                    mergeGroups(moment1, dbmoment1, set);
                }
                if(iterator.hasNext())
                {
                    moment3 = (Moment)iterator.next();
                } else
                {
                    moment3 = null;
                }
                if(iterator1.hasNext())
                {
                    dbmoment2 = (DBMoment)iterator1.next();
                } else
                {
                    dbmoment2 = null;
                }
            } else
            if(moment1.getEndDate().after(dbmoment1.getDate().getLastDate()))
            {
                if(iterator.hasNext())
                {
                    moment4 = (Moment)iterator.next();
                } else
                {
                    moment4 = null;
                }
                moment3 = moment4;
                dbmoment2 = dbmoment1;
            } else
            {
                if(iterator1.hasNext())
                {
                    dbmoment2 = (DBMoment)iterator1.next();
                } else
                {
                    dbmoment2 = null;
                }
                moment3 = moment1;
            }
            dbmoment1 = dbmoment2;
            moment1 = moment3;
        }
        for(iterator2 = hashset.iterator(); iterator2.hasNext(); set.removeAll(moment2.getTempItems()))
        {
            moment2 = (Moment)iterator2.next();
            linkedlist.remove(moment2);
        }

        SharedPreferencesManager.setMigratedDB33(folder);
    }

    private Location getFirstLocationForMoment(Moment moment)
    {
        for(ListIterator listiterator = moment.getTempItems().listIterator(moment.getTempItems().size()); listiterator.hasPrevious();)
        {
            Location location = ((MomentsItems)listiterator.previous()).getItem().getLocation();
            if(location != null)
            {
                return location;
            }
        }

        return null;
    }

    public static List getIntentFilter()
    {
        ArrayList arraylist = new ArrayList();
        arraylist.add(IntentActions.ACTION_USER_INFO_FETCHED);
        arraylist.add(IntentActions.ACTION_GALLERY_BUILDER_NO_CHANGE);
        arraylist.add(IntentActions.ACTION_NEW_MEDIA);
        return arraylist;
    }

    private Location getLastLocationForMoment(Moment moment)
    {
        for(Iterator iterator = moment.getTempItems().iterator(); iterator.hasNext();)
        {
            Location location = ((MomentsItems)iterator.next()).getItem().getLocation();
            if(location != null)
            {
                return location;
            }
        }

        return null;
    }

    private long getMaxTimeBetweenMediaItems(Folder folder)
    {
        long l = 0xdbba00L;
        if(folder.getName() == null || !folder.getName().toLowerCase().contains("whatsapp")) {
            switch(folder.getUserTypeEnum())
            {
                default:
                    return l;
                case NORMAL: // '\002'
                    return 0xa4cb80L;
                case LOT: // '\003'
                    return 0xa4cb80L;
                case FEW: // '\001'
                    break;
            }
        }else
            l = 0x927c0L;
        return l;
    }

    private long getMaxTimeBetweenMediaItemsInTheSameLocation(Folder folder)
    {
        switch(folder.getUserTypeEnum())
        {
        case FEW: // '\001'
        default:
            return 0x1499700L;

        case NORMAL: // '\002'
            return 0x112a880L;

        case LOT: // '\003'
            return 0xdbba00L;
        }
    }

    private Query getMediaItemQuery()
    {
        if(itemQuery == null)
        {
            QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder();
            querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.AndroidId.eq(null), new WhereCondition[0]);
            querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
            itemQuery = querybuilder.build();
        }
        return itemQuery;
    }

    private int getMinPhotosInFlayvr(Folder folder)
    {
        switch(folder.getUserTypeEnum())
        {
        case FEW: // '\001'
        default:
            return 3;
        case NORMAL: // '\002'
            return 4;
        case LOT: // '\003'
            return 5;
        }
    }

    private Moment getNextMediaGroup(Folder folder, ListIterator listiterator)
    {
        LinkedList linkedlist = new LinkedList();
        MediaItem mediaitem = (MediaItem)listiterator.next();
        int i = 0;
        MediaItem mediaitem1;
        MediaItem mediaitem2;
        for(mediaitem1 = mediaitem; listiterator.hasNext(); mediaitem1 = mediaitem2)
        {
            mediaitem1.setWasClustered(Boolean.valueOf(true));
            mediaitem2 = (MediaItem)listiterator.next();
            boolean flag = inTheSameGroup(mediaitem1, mediaitem2, folder);
            linkedlist.add(mediaitem1);
            if(mediaitem1.getType().intValue() == 1)
            {
                i++;
            }
            if(!flag || linkedlist.size() > 80)
            {
                listiterator.previous();
                if(i >= getMinPhotosInFlayvr(folder))
                {
                    return createMomentForItems(linkedlist, folder);
                } else
                {
                    return new TempMoment(linkedlist);
                }
            }
        }

        mediaitem1.setWasClustered(Boolean.valueOf(true));
        linkedlist.add(mediaitem1);
        if(mediaitem1.getType().intValue() == 1)
        {
            i++;
        }
        if(i >= getMinPhotosInFlayvr(folder))
        {
            return createMomentForItems(linkedlist, folder);
        }
        if(linkedlist.size() > 0)
        {
            return new TempMoment(linkedlist);
        } else
        {
            return null;
        }
    }

    private boolean inTheSameGroup(MediaItem mediaitem, MediaItem mediaitem1, Folder folder)
    {
        long l = mediaitem.getDate().getTime() - mediaitem1.getDate().getTime();
        if(l >= getMaxTimeBetweenMediaItems(folder))
        {
            Location location = mediaitem1.getLocation();
            if(location == null)
            {
                return false;
            }
            Location location1 = mediaitem.getLocation();
            if(location1 == null)
            {
                return false;
            }
            if(location.distanceTo(location1) >= 5000F || l >= getMaxTimeBetweenMediaItemsInTheSameLocation(folder))
            {
                return false;
            }
        }
        return true;
    }

    private void mergeGroups(Moment moment, DBMoment dbmoment, Set set)
    {
        moment.setTitle(dbmoment.getTitle());
        moment.setLocation(dbmoment.getLocation());
        moment.setIsMuted(dbmoment.getIsMuted());
        moment.setWatchCount(dbmoment.getWatchCount());
        if(dbmoment.getIsTrip() != null && dbmoment.getIsTrip().booleanValue())
        {
            moment.setIsTrip(dbmoment.getIsTrip());
        }
        if(dbmoment.getIsFavorite() != null && dbmoment.getIsFavorite().booleanValue())
        {
            moment.setIsFavorite(dbmoment.getIsFavorite());
        }
        if(dbmoment.getIsHidden() != null && dbmoment.getIsHidden().booleanValue())
        {
            moment.setIsHidden(dbmoment.getIsHidden());
        }
        if(dbmoment.getIsTitleFromCalendar() != null && dbmoment.getIsTitleFromCalendar().booleanValue())
        {
            moment.setIsTitleFromCalendar(dbmoment.getIsTitleFromCalendar());
        }
        Long long1 = dbmoment.getCoverId();
        if(long1 != null)
        {
            Query query1 = getMediaItemQuery();
            query1.setParameter(0, long1);
            MediaItem mediaitem1 = (MediaItem)query1.unique();
            if(mediaitem1 != null)
            {
                moment.setCover(mediaitem1);
            }
        }
        List list = dbmoment.getMomentsItems();
        HashSet hashset = new HashSet();
        for(Iterator iterator = list.iterator(); iterator.hasNext(); hashset.add(((DBMomentsItems)iterator.next()).getItem().getId())) { }
        HashSet hashset1 = new HashSet(dbmoment.getOriginalItems());
        hashset1.removeAll(hashset);
        HashSet hashset2 = new HashSet();
        if(hashset1.size() > 0)
        {
            Iterator iterator2 = moment.getTempItems().iterator();
            do
            {
                if(!iterator2.hasNext())
                {
                    break;
                }
                MomentsItems momentsitems1 = (MomentsItems)iterator2.next();
                if(hashset1.contains(momentsitems1.getItem().getAndroidId()))
                {
                    hashset2.add(momentsitems1);
                }
            } while(true);
            moment.getTempItems().removeAll(hashset2);
            set.removeAll(hashset2);
        }
        HashSet hashset3 = new HashSet(hashset);
        hashset3.removeAll(dbmoment.getOriginalItems());
        if(hashset3.size() > 0)
        {
            Query query = getMediaItemQuery();
            Iterator iterator1 = hashset3.iterator();
            do
            {
                if(!iterator1.hasNext())
                {
                    break;
                }
                query.setParameter(0, (Long)iterator1.next());
                MediaItem mediaitem = (MediaItem)query.unique();
                if(mediaitem != null)
                {
                    MomentsItems momentsitems = new MomentsItems();
                    momentsitems.setItem(mediaitem);
                    momentsitems.setTempMoment(moment);
                    moment.getTempItems().add(momentsitems);
                    set.add(momentsitems);
                }
            } while(true);
        }
    }

    private boolean mergeMoments(Moment moment, Moment moment1, boolean flag, Location location)
    {
        if(moment1 != null && !flag && moment.getIsTrip().booleanValue() && moment.getTempItems().size() <= 100)
        {
            long l = moment.getStartDate().getTime() - moment1.getEndDate().getTime();
            boolean flag1;
            if(l < 0x5265c00L)
            {
                flag1 = true;
            } else
            {
                flag1 = false;
            }
            if(flag1 && (l < 0x2932e00L || moment.getTempItems().size() < 40 && moment1.getTempItems().size() < 20) && isInTheSamePlaceInTrip(location, getLastLocationForMoment(moment1)))
            {
                Log.d(TAG, "merge!");
                mergeTripsGroups(moment, moment1);
                return true;
            }
        }
        return false;
    }

    private void mergeOldMoments(Folder folder, LinkedList linkedlist, Set set, LinkedList linkedlist1, List list)
    {
        Iterator iterator = linkedlist1.iterator();
        Moment moment;
        Iterator iterator1;
        Moment moment1;
        if(iterator.hasNext())
        {
            moment = (Moment)iterator.next();
        } else
        {
            moment = null;
        }
        iterator1 = list.iterator();
        if(iterator1.hasNext())
        {
            moment1 = (Moment)iterator1.next();
        } else
        {
            moment1 = null;
        }
        do
        {
            for(Moment moment2 = moment1; moment != null && moment2 != null;)
            {
                MediaItem mediaitem = ((MomentsItems)moment.getTempItems().get(0)).getItem();
                MediaItem mediaitem1 = ((MomentsItems)moment.getTempItems().get(-1 + moment.getTempItems().size())).getItem();
                if(moment2.getMomentItems().size() == 0)
                {
                    Moment moment6;
                    if(iterator1.hasNext())
                    {
                        moment6 = (Moment)iterator1.next();
                    } else
                    {
                        moment6 = null;
                    }
                    moment2 = moment6;
                } else
                {
                    MediaItem mediaitem2 = ((MomentsItems)moment2.getMomentItems().get(0)).getItem();
                    MediaItem mediaitem3 = ((MomentsItems)moment2.getMomentItems().get(-1 + moment2.getMomentItems().size())).getItem();
                    Moment moment3;
                    if(mediaitem.getDate().before(mediaitem2.getDate()))
                    {
                        if(inTheSameGroup(mediaitem2, mediaitem1, folder))
                        {
                            MomentsItems momentsitems1;
                            for(Iterator iterator3 = moment.getTempItems().iterator(); iterator3.hasNext(); momentsitems1.getItem().setWasClustered(Boolean.valueOf(true)))
                            {
                                momentsitems1 = (MomentsItems)iterator3.next();
                                momentsitems1.setMomentId(moment2.getId());
                            }

                            set.addAll(moment.getTempItems());
                            if(!(moment instanceof TempMoment))
                            {
                                linkedlist.remove(moment);
                            }
                            moment2.getMomentItems().addAll(0, moment.getTempItems());
                            moment2.setStartDate(mediaitem.getDate());
                            linkedlist.add(moment2);
                        }
                        Moment moment5;
                        if(iterator.hasNext())
                        {
                            moment5 = (Moment)iterator.next();
                        } else
                        {
                            moment5 = null;
                        }
                        moment = moment5;
                        moment3 = moment2;
                    } else
                    {
                        if(inTheSameGroup(mediaitem, mediaitem3, folder))
                        {
                            MomentsItems momentsitems;
                            for(Iterator iterator2 = moment.getTempItems().iterator(); iterator2.hasNext(); momentsitems.getItem().setWasClustered(Boolean.valueOf(true)))
                            {
                                momentsitems = (MomentsItems)iterator2.next();
                                momentsitems.setMomentId(moment2.getId());
                            }

                            set.addAll(moment.getTempItems());
                            if(!(moment instanceof TempMoment))
                            {
                                linkedlist.remove(moment);
                            }
                            moment2.getMomentItems().addAll(moment2.getMomentItems().size(), moment.getTempItems());
                            moment2.setEndDate(mediaitem1.getDate());
                            linkedlist.add(moment2);
                            Moment moment4;
                            if(iterator.hasNext())
                            {
                                moment4 = (Moment)iterator.next();
                            } else
                            {
                                moment4 = null;
                            }
                            moment = moment4;
                        }
                        if(iterator1.hasNext())
                        {
                            moment3 = (Moment)iterator1.next();
                        } else
                        {
                            moment3 = null;
                        }
                    }
                    moment2 = moment3;
                }
            }

            return;
        } while(true);
    }

    private PackagingResult packageFolder(Folder folder, List list, Location location)
    {
        Moment moment1;
        boolean flag;
        PackagingResult packagingresult;
        String s;
        StringBuilder stringbuilder;
        String s1;
        Moment moment2;
        Moment moment;
        String s2;
        StringBuilder stringbuilder1;
        String s3;
        ListIterator listiterator = list.listIterator();
        LinkedList linkedlist = new LinkedList();
        HashSet hashset = new HashSet();
        LinkedList linkedlist1 = new LinkedList();
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getMomentDao().queryBuilder();
        querybuilder.where(com.flayvr.myrollshared.data.MomentDao.Properties.FolderId.eq(folder.getId()), new WhereCondition[0]);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MomentDao.Properties.StartDate;
        querybuilder.orderDesc(aproperty);
        List list1 = querybuilder.list();
        for(moment = null; listiterator.hasNext() && moment == null;)
        {
            Moment moment3 = getNextMediaGroup(folder, listiterator);
            if(moment3 instanceof TempMoment)
            {
                for(Iterator iterator = moment3.getTempItems().iterator(); iterator.hasNext(); ((MomentsItems)iterator.next()).getItem().setWasClustered(Boolean.valueOf(false))) { }
                linkedlist1.add(moment3);
                moment = null;
            } else
                moment = moment3;
        }
        moment1 = moment;
        flag = false;
        while(listiterator.hasNext()) 
        {
            moment2 = getNextMediaGroup(folder, listiterator);
            if(moment2 instanceof TempMoment)
            {
                linkedlist1.add(moment2);
                flag = true;
            } else
            if(mergeMoments(moment1, moment2, flag, resolveMomentLocation(location, moment1)))
            {
                linkedlist.remove(moment2);
            } else
            {
                s2 = TAG;
                stringbuilder1 = (new StringBuilder()).append(folder.getName()).append(" : ").append(moment1.getTempItems().size());
                if(moment1.getIsTrip().booleanValue())
                {
                    s3 = " TRIP";
                } else
                {
                    s3 = "";
                }
                Log.d(s2, stringbuilder1.append(s3).toString());
                linkedlist.add(moment1);
                hashset.addAll(moment1.getTempItems());
                linkedlist1.add(moment2);
                moment1 = moment2;
                flag = false;
            }
        }
        if(moment1 != null)
        {
            resolveMomentLocation(location, moment1);
            s = TAG;
            stringbuilder = (new StringBuilder()).append(folder.getName()).append(" : ").append(moment1.getTempItems().size());
            if(moment1.getIsTrip().booleanValue())
            {
                s1 = " TRIP";
            } else
            {
                s1 = "";
            }
            Log.d(s, stringbuilder.append(s1).toString());
            linkedlist.add(moment1);
            hashset.addAll(moment1.getTempItems());
            linkedlist1.add(moment1);
        }
        mergeOldMoments(folder, linkedlist, hashset, linkedlist1, list1);
        dbMigrate(folder, linkedlist, hashset);
        packagingresult = new PackagingResult();
        packagingresult.moments = linkedlist;
        packagingresult.momentItems = hashset;
        return packagingresult;
    }

    private Location resolveMomentLocation(Location location, Moment moment)
    {
        Location location1 = getFirstLocationForMoment(moment);
        boolean flag = false;
        if(location1 != null)
        {
            flag = false;
            if(location != null)
            {
                Location location2 = User.UNDEIFINED_LOCATION;
                flag = false;
                if(location != location2)
                {
                    flag = false;
                    if((double)location.distanceTo(location1) > 50000D)
                        flag = true;
                }
            }
        }
        moment.setIsTrip(Boolean.valueOf(flag));
        return location1;
    }

    protected boolean isInTheSamePlaceInTrip(Location location, Location location1)
    {
        boolean flag = false;
        if(location1 != null)
        {
            flag = false;
            if(location.distanceTo(location1) < 20000F)
            {
                flag = true;
            }
        }
        return flag;
    }

    protected void mergeTripsGroups(Moment moment, Moment moment1)
    {
        List list = moment1.getTempItems();
        for(Iterator iterator = list.iterator(); iterator.hasNext(); ((MomentsItems)iterator.next()).setTempMoment(moment)) { }
        moment.getTempItems().addAll(list);
        moment.setIsTrip(Boolean.valueOf(true));
        moment.setEndDate(moment1.getEndDate());
    }

    protected void onHandleIntent(Intent intent)
    {
        Log.i(TAG, (new StringBuilder()).append("start media grouping ").append(intent.getAction()).toString());
        Date date = new Date();
        Location location = User.getMyRollUser().getHomeLocation();
        Log.d(TAG, (new StringBuilder()).append("home: ").append(location).toString());
        if(location == null)
        {
            return;
        }
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder();
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.eq(null), new WhereCondition[0]);
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasClustered.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty).limit(1000).offset(0);
        Query query = querybuilder.build();
        FolderDao folderdao = DBManager.getInstance().getDaoSession().getFolderDao();
        LinkedList linkedlist = new LinkedList();
        LinkedList linkedlist1 = new LinkedList();
        LinkedList linkedlist2 = new LinkedList();
        Iterator iterator = folderdao.loadAll().iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            Folder folder = (Folder)iterator.next();
            if(folder.getUserTypeEnum() != null)
            {
                query.setParameter(0, folder.getId());
                LinkedList linkedlist3 = new LinkedList();
                int j = 0;
                for(int k = 1000; k == '\u03E8';)
                {
                    query.setOffset(j);
                    List list = query.list();
                    linkedlist3.addAll(list);
                    k = list.size();
                    j += 1000;
                }

                if(linkedlist3.size() > 0)
                {
                    PackagingResult packagingresult = packageFolder(folder, linkedlist3, location);
                    linkedlist1.addAll(packagingresult.moments);
                    linkedlist2.addAll(packagingresult.momentItems);
                    linkedlist.addAll(linkedlist3);
                }
            }
        } while(true);
        DaoSession daosession = DBManager.getInstance().getDaoSession();
        if(linkedlist1.size() > 0)
        {
            daosession.getMomentDao().insertOrReplaceInTx(linkedlist1);
        }
        if(linkedlist.size() > 0)
        {
            daosession.getMediaItemDao().updateInTx(linkedlist);
        }
        if(linkedlist2.size() > 0)
        {
            Iterator iterator1 = linkedlist2.iterator();
            do
            {
                if(!iterator1.hasNext())
                {
                    break;
                }
                MomentsItems momentsitems = (MomentsItems)iterator1.next();
                if(momentsitems.getTempMoment() != null)
                {
                    momentsitems.setMomentId(momentsitems.getTempMoment().getId());
                }
            } while(true);
            daosession.getMomentsItemsDao().insertOrReplaceInTx(linkedlist2);
        }
        if(linkedlist1.size() > 0)
        {
            long al[] = new long[linkedlist1.size()];
            for(int i = 0; i < al.length; i++)
            {
                al[i] = ((Moment)linkedlist1.get(i)).getId().longValue();
            }

            Intent intent1 = new Intent(IntentActions.ACTION_NEW_MOMENTS);
            intent1.putExtra(IntentActions.EXTRA_MOMENTS_ADDED, al);
            intent1.putExtra(IntentActions.EXTRA_MOMENTS_REMOVED, new long[0]);
            sendBroadcast(intent1);
        }
        sendBroadcast(new Intent(IntentActions.ACTION_CLUSTERING_FINISHED));
        long l = (new Date()).getTime() - date.getTime();
        Log.i(TAG, (new StringBuilder()).append("timing: done ").append(l).toString());
        try
        {
            HashMap hashmap = new HashMap();
            hashmap.put("total moments", (new StringBuilder()).append(DaoHelper.getMomentsCount(1)).append("").toString());
            hashmap.put("total picasa moments", (new StringBuilder()).append(DaoHelper.getMomentsCount(2)).append("").toString());
            KISSmetricsAPI.sharedAPI().set(hashmap);
            return;
        }
        catch(Exception exception)
        {
            Log.w(TAG, exception);
            return;
        }
    }





    private class TempMoment extends Moment
    {

        private final List momentItems = new LinkedList();

        public List getTempItems()
        {
            return momentItems;
        }

        public TempMoment(List list)
        {
            super();
            MomentsItems momentsitems;
            for(Iterator iterator = list.iterator(); iterator.hasNext(); momentItems.add(momentsitems))
            {
                MediaItem mediaitem = (MediaItem)iterator.next();
                momentsitems = new MomentsItems();
                momentsitems.setItem(mediaitem);
            }

        }
    }

    private class PackagingResult
    {

        Set momentItems;
        LinkedList moments;

        private PackagingResult()
        {
            super();
        }
    }

}
