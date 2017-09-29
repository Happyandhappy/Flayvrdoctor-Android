package com.flayvr.myrollshared.managers;

import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.events.MomentChangedEvent;
import com.flayvr.myrollshared.oldclasses.FlayvrsDBManager;
import com.flayvr.myrollshared.oldclasses.ImageInteraction;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;
import java.util.*;

public class InteractionManager
{

    private static InteractionManager instance;
    private final InteractionDao interactionDao = DBManager.getInstance().getDaoSession().getInteractionDao();

    private InteractionManager()
    {
    }

    public static InteractionManager getInstance()
    {
        if(instance == null)
        {
            instance = new InteractionManager();
        }
        return instance;
    }

    private void post(InteractionType interactiontype, MediaItem mediaitem)
    {
        Log.d(getClass().getSimpleName(), interactiontype.toString());
        Double double1 = mediaitem.getInteractionScore();
        if(double1 == null)
        {
            double1 = Double.valueOf(0.0D);
        }
        mediaitem.setInteractionScore(Double.valueOf(double1.doubleValue() + interactiontype.getValue()));
        Interaction interaction = new Interaction();
        interaction.setInteractionDate(new Date());
        interaction.setInteractionType(Integer.valueOf(interactiontype.getInteractionTypeId()));
        interaction.setItemId(mediaitem.getId());
        interactionDao.insert(interaction);
    }

    private void post(InteractionType interactiontype, Collection collection)
    {
        for(Iterator iterator = collection.iterator(); iterator.hasNext(); post(interactiontype, (MediaItem)iterator.next())) { }
        DBManager.getInstance().getDaoSession().getMediaItemDao().updateInTx(collection);
    }

    private void post(InteractionType interactiontype, List list)
    {
        ArrayList arraylist = new ArrayList(list.size());
        MediaItem mediaitem;
        for(Iterator iterator = list.iterator(); iterator.hasNext(); arraylist.add(mediaitem))
        {
            mediaitem = ((MomentsItems)iterator.next()).getItem();
            post(interactiontype, mediaitem);
        }

        DBManager.getInstance().getDaoSession().getMediaItemDao().updateInTx(arraylist);
    }

    private void postAsyncDB(InteractionType interactiontype, List list)
    {
        final ArrayList itemstoUpdate = new ArrayList(list.size());
        MediaItem mediaitem;
        for(Iterator iterator = list.iterator(); iterator.hasNext(); itemstoUpdate.add(mediaitem))
        {
            mediaitem = ((MomentsItems)iterator.next()).getItem();
            post(interactiontype, mediaitem);
        }

        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                DBManager.getInstance().getDaoSession().getMediaItemDao().updateInTx(itemstoUpdate);
            }
        });
    }

    public void migrate()
    {
        DaoSession daosession = DBManager.getInstance().getDaoSession();
        QueryBuilder querybuilder = daosession.getMediaItemDao().queryBuilder();
        WhereCondition wherecondition = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.eq(Integer.valueOf(0));
        WhereCondition awherecondition[] = new WhereCondition[1];
        awherecondition[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.AndroidId.eq(Integer.valueOf(0));
        querybuilder.where(wherecondition, awherecondition);
        Query query = querybuilder.build();
        LinkedList linkedlist = new LinkedList();
        LinkedList linkedlist1 = new LinkedList();
        Iterator iterator = daosession.getFolderDao().loadAll().iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            Folder folder = (Folder)iterator.next();
            for(Iterator iterator1 = FlayvrsDBManager.getInstance().getFolderMoments(folder.getId().toString()).iterator(); iterator1.hasNext();)
            {
                Iterator iterator2 = ((DBMoment)iterator1.next()).getInteractions().entrySet().iterator();
                while(iterator2.hasNext()) 
                {
                    java.util.Map.Entry entry = (java.util.Map.Entry)iterator2.next();
                    Long long1 = (Long)entry.getKey();
                    List list = (List)entry.getValue();
                    query.setParameter(0, folder.getId());
                    query.setParameter(1, long1);
                    MediaItem mediaitem = (MediaItem)query.unique();
                    linkedlist1.add(mediaitem);
                    Double double1 = mediaitem.getInteractionScore();
                    if(double1 == null)
                    {
                        double1 = Double.valueOf(0.0D);
                    }
                    Iterator iterator3 = list.iterator();
                    while(iterator3.hasNext()) 
                    {
                        ImageInteraction imageinteraction = (ImageInteraction)iterator3.next();
                        if(ImageInteraction.EditOut.equals(imageinteraction))
                        {
                            double1 = migrateInteraction(linkedlist, mediaitem, double1, InteractionType.RemovedFromMoment);
                        } else
                        if(ImageInteraction.EditIn.equals(imageinteraction))
                        {
                            double1 = migrateInteraction(linkedlist, mediaitem, double1, InteractionType.AddedToMoment);
                        } else
                        if(ImageInteraction.OpenFullScreenFromMoment.equals(imageinteraction))
                        {
                            double1 = migrateInteraction(linkedlist, mediaitem, double1, InteractionType.OpenFromMoment);
                        }
                        mediaitem.setInteractionScore(double1);
                    }
                }
            }

            daosession.getMediaItemDao().updateInTx(linkedlist1);
            daosession.getInteractionDao().insertInTx(linkedlist);
        } while(true);
    }

    public Double migrateInteraction(List list, MediaItem mediaitem, Double double1, InteractionType interactiontype)
    {
        Interaction interaction = new Interaction();
        interaction.setInteractionDate(new Date());
        Double double2 = Double.valueOf(double1.doubleValue() + interactiontype.getValue());
        interaction.setInteractionType(Integer.valueOf(interactiontype.getInteractionTypeId()));
        interaction.setItemId(mediaitem.getId());
        list.add(interaction);
        return double2;
    }

    public void onEvent(MomentChangedEvent momentchangedevent)
    {
        Moment moment;
        List list;
        InteractionType interactiontype;
        moment = momentchangedevent.getMoment();
        list = moment.getMomentItems();
        if(momentchangedevent.getType() != com.flayvr.myrollshared.events.MomentChangedEvent.Type.Hidden) {
            if(momentchangedevent.getType() != com.flayvr.myrollshared.events.MomentChangedEvent.Type.Favorite)
                return;
            if(moment.getIsFavorite().booleanValue())
                interactiontype = InteractionType.ItemsMomentFavorited;
            else
                interactiontype = InteractionType.ItemsMomentUnFavorited;
        } else {
            if (moment.getIsHidden().booleanValue())
                interactiontype = InteractionType.ItemsMomentHidden;
            else
                interactiontype = InteractionType.ItemsMomentUnHidden;
        }
        postAsyncDB(interactiontype, list);
    }

    public void postAsync(final InteractionType type, final MediaItem photo)
    {
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                post(type, photo);
                photo.update();
            }
        });
    }

    public void postAsync(final InteractionType type, final Long id)
    {
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                MediaItem mediaitem = (MediaItem)DBManager.getInstance().getDaoSession().getMediaItemDao().load(id);
                post(type, mediaitem);
                mediaitem.update();
            }
        });
    }

    public void postAsync(final InteractionType type, final ArrayList ids)
    {
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                List list = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder().where(com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.in(ids), new WhereCondition[0]).list();
                post(type, list);
            }
        });
    }

    public void postAsync(final InteractionType type, final Collection photos)
    {
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                post(type, photos);
            }
        });
    }

    public void postAsync(final InteractionType type, final List allItems)
    {
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                post(type, allItems);
            }
        });
    }

    private enum InteractionType
    {
        OpenFromGallery("OpenFromGallery", 0, 1, 0.050000000000000003D),
        ShareFromGallerySingle("ShareFromGallerySingle", 1, 2, 0.20000000000000001D),
        ShareFromGalleryBatch("ShareFromGalleryBatch", 2, 3, 0.20000000000000001D),
        DeletedFromGallerySingle("DeletedFromGallerySingle", 3, 4, 0.0D),
        DeletedFromGalleryBatch("DeletedFromGalleryBatch", 4, 5, 0.0D),
        ItemsMomentHidden("ItemsMomentHidden", 5, 6, -0.10000000000000001D),
        ItemsMomentFavorited("ItemsMomentFavorited", 6, 7, 0.10000000000000001D),
        ItemsMomentUnHidden("ItemsMomentUnHidden", 7, 8, 0.10000000000000001D),
        ItemsMomentUnFavorited("ItemsMomentUnFavorited", 8, 9, -0.10000000000000001D),
        RemovedFromMoment("RemovedFromMoment", 9, 10, -0.10000000000000001D),
        AddedToMoment("AddedToMoment", 10, 11, 0.10000000000000001D),
        OpenFromMoment("OpenFromMoment", 11, 12, 0.050000000000000003D),
        ShareFromFullScreen("ShareFromFullScreen", 12, 13, 0.20000000000000001D),
        SetAsFromFullScreen("SetAsFromFullScreen", 13, 14, 0.20000000000000001D),
        DeleteFromFullScreen("DeleteFromFullScreen", 14, 15, 0.0D),
        ItemsMomentChangedTitle("ItemsMomentChangedTitle", 15, 16, 0.050000000000000003D),
        SetAsCoverInMoment("SetAsCoverInMoment", 16, 17, 0.20000000000000001D),
        ItemsMomentShared("ItemsMomentShared", 17, 18, 0.10000000000000001D);
        private int interactionTypeId;
        private final double value;

        public InteractionType findByTypeId(String s)
        {
            if(s != null)
            {
                InteractionType ainteractiontype[] = values();
                int i = ainteractiontype.length;
                for(int j = 0; j < i; j++)
                {
                    InteractionType interactiontype = ainteractiontype[j];
                    if(s.equals(Integer.valueOf(interactiontype.getInteractionTypeId())))
                    {
                        return interactiontype;
                    }
                }

            }
            return null;
        }

        public int getInteractionTypeId()
        {
            return interactionTypeId;
        }

        public double getValue()
        {
            return value;
        }

        private InteractionType(String s, int i, int j, double d)
        {
            interactionTypeId = j;
            value = d;
        }
    }
}
