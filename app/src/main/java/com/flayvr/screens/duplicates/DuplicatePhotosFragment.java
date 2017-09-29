package com.flayvr.screens.duplicates;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import com.flayvr.application.PreferencesManager;
import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.fragments.MessageDialog;
import com.flayvr.myrollshared.services.DuplicatesService;
import com.flayvr.myrollshared.services.GalleryBuilderService;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.screens.SelectionPhotosFragment;
import com.flayvr.util.*;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

public class DuplicatePhotosFragment extends SelectionPhotosFragment
{

    private List duplicatesSets;
    private HashMap mediaItemsToDupSet;
    int numberOfGroupsCleaned;

    public DuplicatePhotosFragment()
    {
        duplicatesSets = new ArrayList();
        numberOfGroupsCleaned = 0;
    }

    private static String getSetParams(Set set)
    {
        return StringUtils.join(set, ",");
    }

    public static DuplicatePhotosFragment newInstance(int i)
    {
        DuplicatePhotosFragment duplicatephotosfragment = new DuplicatePhotosFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SOURCE", i);
        duplicatephotosfragment.setArguments(bundle);
        return duplicatephotosfragment;
    }

    public void addItemsData(Intent intent, MediaItem mediaitem)
    {
        DuplicatesSet duplicatesset;
        Iterator iterator;
        DuplicatesSet duplicatesset1;
        duplicatesset = null;
        iterator = duplicatesSets.iterator();
        while(iterator.hasNext())
        {
            duplicatesset1 = (DuplicatesSet)iterator.next();
            Iterator iterator2 = ((List)mediaItemsToDupSet.get(duplicatesset1.getId())).iterator();
            while(iterator2.hasNext())
            {
                duplicatesset1 = duplicatesset;
                if(((MediaItem)iterator2.next()).getId() == mediaitem.getId())
                    break;
            }
            duplicatesset = duplicatesset1;
        }
        if(duplicatesset != null)
        {
            List list = (List)mediaItemsToDupSet.get(duplicatesset.getId());
            long al[] = new long[list.size()];
            int i = 0;
            for(Iterator iterator1 = list.iterator(); iterator1.hasNext();)
            {
                al[i] = ((MediaItem)iterator1.next()).getId().longValue();
                i++;
            }

            intent.putExtra("ITEMS_SELECTED", al);
        }
    }

    public void checkCollection(Collection collection, Collection collection1)
    {
        DuplicatesService.deleteSetsForDeletedPhotos(collection, collection1);
    }

    public GalleryDoctorDuplicatesAdapter createAdapter()
    {
        return new GalleryDoctorDuplicatesAdapter(grid, duplicatesSets, mediaItemsToDupSet);
    }

    public void deleteCollection(final Collection items)
    {
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                GalleryBuilderService.deleteItems(items);
            }
        });
    }

    public void deleteCollection(final Collection items, Collection collection)
    {
        DuplicatesService.deleteSetsForDeletedPhotos(items, collection);
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                LinkedList linkedlist = new LinkedList();
                for(Iterator iterator = items.iterator(); iterator.hasNext(); linkedlist.add(((DuplicatesSetsToPhotos)iterator.next()).getPhoto())) { }
                GalleryBuilderService.deleteItems(linkedlist);
            }
        });
    }

    protected com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType getActionType()
    {
        return com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType.CLEAN_ALL_SIMILAR_SETS;
    }

    protected String getFragmentName()
    {
        return "similar photos";
    }

    protected void headerClick(int i, List list, int j)
    {
        final DuplicatesSet duplicatesSet = (DuplicatesSet)duplicatesSets.get(i);
        if(j == 1)
        {
            MessageDialog messagedialog = new MessageDialog();
            final HashSet deleteItems = new HashSet();
            final HashSet checkItems = new HashSet();
            final HashSet deleteSelected = new HashSet();
            HashSet hashset = ((GalleryDoctorDuplicatesAdapter)photoAdapter).getUnselectedItems();
            List list1 = duplicatesSet.getDuplicatesSetPhotos();
            long finalSpaceDeleted = 0L;
            Iterator iterator = list1.iterator();
            while(iterator.hasNext()) 
            {
                DuplicatesSetsToPhotos duplicatessetstophotos = (DuplicatesSetsToPhotos)iterator.next();
                if(!hashset.contains(duplicatessetstophotos.getPhoto()))
                {
                    MediaItem mediaitem = duplicatessetstophotos.getPhoto();
                    deleteItems.add(duplicatessetstophotos);
                    finalSpaceDeleted += mediaitem.getFileSizeBytesSafe().longValue();
                } else
                {
                    checkItems.add(duplicatessetstophotos);
                }
                deleteSelected.add(duplicatessetstophotos.getPhoto());
            }
            HashMap hashmap;
            if(deleteItems.size() == 0)
            {
                messagedialog.setMsg(getResources().getString(R.string.gallery_doctor_deletion_popup_text_keep_all));
                messagedialog.setTitle(getResources().getString(R.string.gallery_doctor_deletion_popup_heading_keep_all));
                messagedialog.setPositiveText(getResources().getString(R.string.gallery_doctor_deletion_popup_keep_all_action));
            } else
            {
                messagedialog.setPositiveText(getResources().getString(R.string.gallery_doctor_bad_deletion_popup_button));
                Resources resources = getResources();
                int k;
                if(source == 1)
                {
                    k = R.string.gallery_doctor_similar_deletion_popup_text_group;
                } else
                {
                    k = R.string.similar_deletion_popup_text_group_cloud;
                }
                messagedialog.setMsg(resources.getString(k));
                if(deleteItems.size() == 1)
                {
                    messagedialog.setTitle(getResources().getString(R.string.gallery_doctor_bad_deletion_popup_heading_single));
                } else
                {
                    String s = getResources().getString(R.string.gallery_doctor_bad_deletion_popup_heading);
                    Object aobj[] = new Object[1];
                    aobj[0] = Integer.valueOf(deleteItems.size());
                    messagedialog.setTitle(String.format(s, aobj));
                }
            }
            messagedialog.setNegativeText(getResources().getString(R.string.cancel_label));
            messagedialog.setNegetiveListener(new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    HashMap hashmap = new HashMap();
                    hashmap.put("total "+fragmentType+" photos in set to be deleted", deleteItems.size()+"");
                    hashmap.put("total "+fragmentType+" photos in set to be kept", checkItems.size()+"");
                    AnalyticsUtils.trackEventWithKISS("canceled deleting "+fragmentType+" photos set");
                }
            });
            final long final_finalSpaceDeleted = finalSpaceDeleted;
            messagedialog.setPositiveListener(new android.content.DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DuplicatePhotosFragment duplicatephotosfragment = DuplicatePhotosFragment.this;
                    duplicatephotosfragment.numberOfGroupsCleaned = 1 + duplicatephotosfragment.numberOfGroupsCleaned;
                    if(numberOfGroupsCleaned >= 5)
                    {
                        userMadeAction = true;
                    }
                    numberOfPhotosCleaned += deleteItems.size();
                    DuplicatesSetsToPhotos duplicatessetstophotos;
                    for(Iterator iterator = deleteItems.iterator(); iterator.hasNext(); sizeOfPhotosCleaned = sizeOfPhotosCleaned + duplicatessetstophotos.getPhoto().getFileSizeBytesSafe().longValue())
                    {
                        duplicatessetstophotos = (DuplicatesSetsToPhotos)iterator.next();
                    }

                    photoAdapter.removeUnselectedItems(deleteSelected);
                    checkCollection(checkItems, Collections.singleton(duplicatesSet));
                    deleteCollection(deleteItems, Collections.singleton(duplicatesSet));
                    refreshData();
                    updateActionText();
                    HashMap hashmap = new HashMap();
                    hashmap.put("total "+fragmentType+" photos in set deleted", deleteItems.size()+"");
                    hashmap.put("total photos deleted", deleteItems.size()+"");
                    hashmap.put("total "+fragmentType+" photos  in set kept", checkItems.size()+"");
                    hashmap.put("total space saved", (final_finalSpaceDeleted / 0x100000L)+"");
                    hashmap.put("total space saved for "+fragmentType+" photos", (final_finalSpaceDeleted / 0x100000L)+"");
                    AnalyticsUtils.trackEventWithKISS("accepted deleting similar photos set", hashmap, true);
                    GalleryDoctorAnalyticsSender.sendDoctorUserActionAsync(GalleryDoctorAnalyticsSender.DoctorActionType.CLEAN_SIMILAR_SET, deleteItems.size(), checkItems.size(), final_finalSpaceDeleted);
                    if(((GalleryDoctorDuplicatesAdapter)photoAdapter).getHeaderItemCount() == 0)
                    {
                        getActivity().finish();
                    }
                }
            });
            messagedialog.show(getFragmentManager(), "fsdf");
            hashmap = new HashMap();
            hashmap.put((new StringBuilder()).append("total ").append(fragmentType).append(" photos in set to be deleted").toString(), (new StringBuilder()).append(deleteItems.size()).append("").toString());
            hashmap.put((new StringBuilder()).append("total ").append(fragmentType).append(" photos  in set to be kept").toString(), (new StringBuilder()).append(checkItems.size()).append("").toString());
            AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("chose to delete ").append(fragmentType).append(" photo set").toString(), hashmap, true);
        } else
        if(j == 0)
        {
            HashSet hashset1 = new HashSet();
            for(Iterator iterator1 = list.iterator(); iterator1.hasNext(); hashset1.add((MediaItem)iterator1.next())) { }
            ((GalleryDoctorDuplicatesAdapter)photoAdapter).removeUnselectedItems(hashset1);
            checkCollection(duplicatesSet.getDuplicatesSetPhotos(), Collections.singleton(duplicatesSet));
            refreshData();
            updateActionText();
            HashMap hashmap1 = new HashMap();
            hashmap1.put("total similar photos in set  to be kept", (new StringBuilder()).append(hashset1.size()).append("").toString());
            AnalyticsUtils.trackEventWithKISS("accepted deleting similar photos set", hashmap1, true);
            GalleryDoctorAnalyticsSender.sendDoctorUserActionAsync(com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType.KEEP_SIMILAR_SET, 0, list.size(), 0L);
            if(((GalleryDoctorDuplicatesAdapter)photoAdapter).getHeaderItemCount() == 0)
            {
                getActivity().finish();
                return;
            }
        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        int i = reloadItems();
        HashMap hashmap = new HashMap();
        hashmap.put("total similar photos", (new StringBuilder()).append(i).append("").toString());
        hashmap.put("total similar sets of photos", (new StringBuilder()).append(duplicatesSets.size()).append("").toString());
        AnalyticsUtils.trackEventWithKISS("viewed similar photos", hashmap, true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        menuinflater.inflate(R.menu.menu_gallery_doctor_cards, menu);
        super.onCreateOptionsMenu(menu, menuinflater);
    }

    protected void refreshData()
    {
        reloadItems();
        ((GalleryDoctorDuplicatesAdapter)photoAdapter).reloadItems(duplicatesSets, mediaItemsToDupSet);
    }

    public int reloadItems()
    {
        Set set = PreferencesManager.getExcludeAllFolder();
        mediaItemsToDupSet = new LinkedHashMap();
        DaoSession daosession = DBManager.getInstance().getDaoSession();
        Cursor cursor = daosession.getDatabase().rawQuery((new StringBuilder()).append("SELECT ").append(com.flayvr.myrollshared.data.DuplicatesSetsToPhotosDao.Properties.DuplicatesSetId.columnName).append(", m.* ").append("FROM ").append("DUPLICATES_SETS_TO_PHOTOS").append(" d").append(" LEFT JOIN ").append("MEDIA_ITEM").append(" m on (m.").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.columnName).append(" = ").append("d.").append(com.flayvr.myrollshared.data.DuplicatesSetsToPhotosDao.Properties.PhotoId.columnName).append(") ").append(" WHERE m.").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ").append(source).append(" AND ").append("m.").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" NOT IN (").append(getSetParams(set)).append(")").append(" ORDER BY ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Date.columnName).append(" DESC ").toString(), null);
        int i;
        for(i = 0; cursor.moveToNext(); i++)
        {
            long l = cursor.getLong(0);
            DuplicatesSet duplicatesset = daosession.getDuplicatesSetDao().readEntity(cursor, 0);
            if(!mediaItemsToDupSet.containsKey(Long.valueOf(l)))
            {
                duplicatesset.__setDaoSession(DBManager.getInstance().getDaoSession());
                duplicatesSets.add(duplicatesset);
                mediaItemsToDupSet.put(Long.valueOf(l), new ArrayList());
            }
            MediaItem mediaitem = daosession.getMediaItemDao().readEntity(cursor, 1);
            mediaitem.__setDaoSession(DBManager.getInstance().getDaoSession());
            ((List)mediaItemsToDupSet.get(Long.valueOf(l))).add(mediaitem);
        }

        duplicatesSets = GalleryDoctorDBHelper.getDuplicates(source);
        return i;
    }

    protected void updateAdapterSelection(Collection collection)
    {
        for(Iterator iterator = mediaItemsToDupSet.values().iterator(); iterator.hasNext();)
        {
            Iterator iterator1 = ((List)iterator.next()).iterator();
            while(iterator1.hasNext()) 
            {
                MediaItem mediaitem = (MediaItem)iterator1.next();
                if(collection.contains(mediaitem.getId()))
                {
                    ((GalleryDoctorDuplicatesAdapter)photoAdapter).addUnselectedItem(mediaitem);
                } else
                {
                    ((GalleryDoctorDuplicatesAdapter)photoAdapter).removeUnselectedItem(mediaitem);
                }
            }
        }

        ((GalleryDoctorDuplicatesAdapter)photoAdapter).notifyDataSetChanged();
    }

    public void updateDBItems()
    {
        checkCollection(DBManager.getInstance().getDaoSession().getDuplicatesSetsToPhotosDao().queryBuilder().build().forCurrentThread().list(), duplicatesSets);
    }
}
