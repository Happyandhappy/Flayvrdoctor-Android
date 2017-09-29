package com.flayvr.screens.duplicates;

import android.content.res.Resources;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.flayvr.application.PreferencesManager;
import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.imageloading.AndroidImagesUtils;
import com.flayvr.myrollshared.imageloading.ImageLoaderAsyncTask;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.flayvr.util.GalleryDoctorUtils;
import com.flayvr.util.SectionedGridRecyclerViewAdapter;
import java.util.*;

public class GalleryDoctorDuplicatesAdapter extends SectionedGridRecyclerViewAdapter
{

    public static final int CLEAN_ACTION = 1;
    public static final int IGNORE_ACTION = 0;
    private List bestPhotos;
    private List dupSet;
    private HashMap mediaItemsToDupSet;

    public GalleryDoctorDuplicatesAdapter(RecyclerView recyclerview, List list, HashMap hashmap)
    {
        super(recyclerview);
        dupSet = new ArrayList();
        mUnselectedAssets = new HashSet();
        reloadItems(list, hashmap);
    }

    public int getHeaderItemCount()
    {
        return dupSet.size();
    }

    public int getHeaderSpanCount(int i)
    {
        return recyclerView.getResources().getInteger(R.integer.gd_span_size);
    }

    public int getHintSpanCount()
    {
        return recyclerView.getResources().getInteger(R.integer.gd_span_size);
    }

    public int getItemCountForHeader(int i)
    {
        if(i < dupSet.size())
        {
            DuplicatesSet duplicatesset = (DuplicatesSet)dupSet.get(i);
            if(duplicatesset != null && mediaItemsToDupSet != null)
            {
                List list = (List)mediaItemsToDupSet.get(duplicatesset.getId());
                if(list != null)
                {
                    return list.size();
                }
            }
        }
        return 0;
    }

    public int getItemSpanCountForHeader(int i, int j)
    {
        return 1;
    }

    public HashSet getSelectedItems()
    {
        HashSet hashset = new HashSet();
        if(dupSet != null)
        {
            for(Iterator iterator = dupSet.iterator(); iterator.hasNext();)
            {
                DuplicatesSet duplicatesset = (DuplicatesSet)iterator.next();
                List list = (List)mediaItemsToDupSet.get(duplicatesset.getId());
                if(list != null)
                {
                    Iterator iterator1 = list.iterator();
                    while(iterator1.hasNext()) 
                    {
                        MediaItem mediaitem = (MediaItem)iterator1.next();
                        if(!mUnselectedAssets.contains(mediaitem))
                        {
                            hashset.add(mediaitem);
                        }
                    }
                }
            }

        }
        return hashset;
    }

    public void onBindHeaderViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i)
    {
        onBindHeaderViewHolder((DuplicateViewHolder)viewholder, i);
    }

    public void onBindHeaderViewHolder(DuplicateViewHolder duplicateviewholder, int i)
    {
        TextView textview;
label0:
        {
            DuplicatesSet duplicatesset = (DuplicatesSet)dupSet.get(i);
            List list = (List)mediaItemsToDupSet.get(duplicatesset.getId());
            if(list != null && list.size() > 0)
            {
                MediaItem mediaitem = (MediaItem)list.get(0);
                textview = ((DuplicateHeaderViewHolder)duplicateviewholder).textView;
                Folder folder = mediaitem.getFolder();
                if(folder == null)
                {
                    break label0;
                }
                textview.setText(folder.getName());
            }
            return;
        }
        textview.setText("");
    }

    public void onBindItemViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i, int j)
    {
        onBindItemViewHolder((DuplicateViewHolder)viewholder, i, j);
    }

    public void onBindItemViewHolder(DuplicateViewHolder duplicateviewholder, int i, int j)
    {
        DuplicatesSet duplicatesset = (DuplicatesSet)dupSet.get(i);
        MediaItem mediaitem = (MediaItem)((List)mediaItemsToDupSet.get(duplicatesset.getId())).get(j);
        DuplicateEditMediaItemView duplicateeditmediaitemview = (DuplicateEditMediaItemView)duplicateviewholder.itemView;
        if(duplicateeditmediaitemview.getItem() != null && !duplicateeditmediaitemview.getItem().equals(mediaitem))
        {
            duplicateeditmediaitemview.clearImage();
        }
        boolean flag;
        if(!mUnselectedAssets.contains(mediaitem))
        {
            flag = true;
        } else
        {
            flag = false;
        }
        duplicateeditmediaitemview.setIsSelected(flag);
        duplicateeditmediaitemview.setIsBest(bestPhotos.contains(mediaitem.getId()));
        AndroidImagesUtils.getBitmapForItem(duplicateeditmediaitemview, mediaitem, com.flayvr.myrollshared.imageloading.ImageCacheBitmap.ThumbnailSize.Normal);
    }

    protected DuplicateViewHolder onCreateViewHolderForHeader(ViewGroup viewgroup)
    {
        return new DuplicateHeaderViewHolder(LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.gallery_doctor_duplicates_header_item, viewgroup, false));
    }

    protected DuplicateViewHolder onCreateViewHolderForHint(ViewGroup viewgroup)
    {
        return new DuplicateHintViewHolder(LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.gallery_doctor_duplicates_hint_card, viewgroup, false));
    }

    protected DuplicateViewHolder onCreateViewHolderForItem(ViewGroup viewgroup)
    {
        DuplicateEditMediaItemView duplicateeditmediaitemview = new DuplicateEditMediaItemView(viewgroup.getContext());
        duplicateeditmediaitemview.setShouldCenterOnFace(false);
        int i = GeneralUtils.getSize(viewgroup.getContext())[0];
        android.support.v7.widget.GridLayoutManager.LayoutParams layoutparams = new android.support.v7.widget.GridLayoutManager.LayoutParams(0, 0);
        duplicateeditmediaitemview.setLayoutParams(layoutparams);
        int j = duplicateeditmediaitemview.getResources().getInteger(R.integer.gd_span_size);
        layoutparams.height = i / j - (int)GeneralUtils.pxFromDp(duplicateeditmediaitemview.getContext(), 3F) * (j - 1);
        duplicateeditmediaitemview.setLayoutParams(layoutparams);
        return new DuplicateItemViewHolder(duplicateeditmediaitemview);
    }

    public void onViewRecycled(DuplicateViewHolder duplicateviewholder)
    {
        if(duplicateviewholder.itemView instanceof DuplicateEditMediaItemView)
        {
            DuplicateEditMediaItemView duplicateeditmediaitemview = (DuplicateEditMediaItemView)duplicateviewholder.itemView;
            ImageLoaderAsyncTask imageloaderasynctask = (ImageLoaderAsyncTask)duplicateeditmediaitemview.getTag();
            if(imageloaderasynctask != null)
            {
                imageloaderasynctask.cancel(true);
                duplicateeditmediaitemview.setTag(null);
            }
            duplicateeditmediaitemview.clearImage();
        }
        onViewRecycled(duplicateviewholder);
    }

    public void reloadItems(List list, HashMap hashmap)
    {
        position2result = new HashMap();
        dupSet = list;
        mediaItemsToDupSet = hashmap;
        bestPhotos = new ArrayList();
        mUnselectedAssets.clear();
        boolean flag;
        Iterator iterator;
        if(mUnselectedAssets.size() == 0)
            flag = true;
        else
            flag = false;
        iterator = hashmap.values().iterator();
        while(iterator.hasNext())
        {
            List list1 = (List)iterator.next();
            MediaItem mediaitem = null;
            double d = -1D;
            Iterator iterator1 = list1.iterator();
            while(iterator1.hasNext())
            {
                MediaItem mediaitem1 = (MediaItem)iterator1.next();
                if(mediaitem1 != null && (mediaitem == null || mediaitem1.getScore() != null && mediaitem1.getScore().doubleValue() > d))
                {
                    if(mediaitem1.getScore() != null)
                    {
                        d = mediaitem1.getScore().doubleValue();
                    }
                } else
                    mediaitem1 = mediaitem;
                mediaitem = mediaitem1;
            }
            if(mediaitem != null)
            {
                bestPhotos.add(mediaitem.getId());
                if(flag)
                {
                    mUnselectedAssets.add(mediaitem);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean shoudShowHint()
    {
        return !PreferencesManager.isDuplicatePhotosHintsShown();
    }

    private class DuplicateViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
    {
        public DuplicateViewHolder(View view)
        {
            super(view);
        }
    }

    private class DuplicateHeaderViewHolder extends DuplicateViewHolder
    {
        TextView textView;

        public DuplicateHeaderViewHolder(View view)
        {
            super(view);
            textView = (TextView)view.findViewById(R.id.title);
            Toolbar toolbar = (Toolbar)view.findViewById(R.id.menu);
            toolbar.setTitleTextColor(view.getResources().getColor(R.color.flayvr_color));
            toolbar.setSubtitleTextColor(view.getResources().getColor(R.color.flayvr_color));
            toolbar.setPopupTheme(R.style.GalleryDoctorThemeToolbarDarkOverflow);
            toolbar.inflateMenu(R.menu.menu_gallery_doctor_duplicates_menu);
            TextView textview = (TextView)toolbar.findViewById(R.id.clean);
            textview.setTextColor(view.getContext().getResources().getColor(R.color.flayvr_color));

            textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = getHeaderPosition(GalleryDoctorDuplicatesAdapter.DuplicateHeaderViewHolder.this);
                    List list = (List)mediaItemsToDupSet.get(((DuplicatesSet)dupSet.get(i)).getId());
                    mItemClickListener.onHeaderClick(i, list, 1);
                }
            });

            ((AppCompatImageView)((ActionMenuView)toolbar.getChildAt(0)).getChildAt(1)).setColorFilter(view.getContext().getResources().getColor(R.color.flayvr_color));

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuitem) {
                    int i = getHeaderPosition(GalleryDoctorDuplicatesAdapter.DuplicateHeaderViewHolder.this);
                    List list = (List)mediaItemsToDupSet.get(((DuplicatesSet)dupSet.get(i)).getId());
                    switch(menuitem.getItemId())
                    {
                        default:
                            return false;

                        case R.id.ignore:
                            mItemClickListener.onHeaderClick(getHeaderPosition(GalleryDoctorDuplicatesAdapter.DuplicateHeaderViewHolder.this), list, 0);
                            return true;

                        case R.id.report:
                            GalleryDoctorUtils.sendDuplicateReport(recyclerView.getContext(), list);
                            break;
                    }
                    return true;
                }
            });
        }
    }

    private class DuplicateHintViewHolder extends DuplicateViewHolder
    {
        public DuplicateHintViewHolder(View view)
        {
            super(view);
            view.findViewById(R.id.action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PreferencesManager.setIsDuplicatePhotosHintsShown();
                    dismissHint();
                }
            });
        }
    }

    private class DuplicateItemViewHolder extends DuplicateViewHolder
    {

        DuplicateEditMediaItemView itemView;
        View selectionView;
        View thumbnailView;

        public DuplicateItemViewHolder(DuplicateEditMediaItemView duplicateeditmediaitemview)
        {
            super(duplicateeditmediaitemview);
            itemView = duplicateeditmediaitemview;
            thumbnailView = duplicateeditmediaitemview.findViewById(R.id.thumbnail_view);
            selectionView = duplicateeditmediaitemview.findViewById(R.id.selection_frame);

            thumbnailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(itemView, getItemPosition(DuplicateItemViewHolder.this), com.flayvr.screens.SelectionAdapter.Source.THUMBNAIL);
                }
            });

            selectionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mUnselectedAssets.contains(itemView.getItem()))
                        mUnselectedAssets.remove(itemView.getItem());
                    else
                        mUnselectedAssets.add(itemView.getItem());
                    mItemClickListener.onItemClick(itemView, getItemPosition(GalleryDoctorDuplicatesAdapter.DuplicateItemViewHolder.this), com.flayvr.screens.SelectionAdapter.Source.SELECTION);
                }
            });
        }
    }

}
