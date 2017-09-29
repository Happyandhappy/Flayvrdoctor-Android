package com.flayvr.screens.bad;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.flayvr.application.PreferencesManager;
import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.imageloading.AndroidImagesUtils;
import com.flayvr.myrollshared.imageloading.ImageLoaderAsyncTask;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.flayvr.util.SectionedGridRecyclerViewAdapter;
import com.flayvr.views.GDEditMediaItemView;
import java.util.*;

public class GalleryDoctorBadPhotosAdapter extends SectionedGridRecyclerViewAdapter
{

    private List mAssets;

    public GalleryDoctorBadPhotosAdapter(RecyclerView recyclerview, List list)
    {
        super(recyclerview);
        mAssets = list;
    }

    public int getHintSpanCount()
    {
        return recyclerView.getResources().getInteger(R.integer.gd_span_size);
    }

    public int getItemCountForHeader(int i)
    {
        if(mAssets != null)
        {
            return mAssets.size();
        } else
        {
            return 0;
        }
    }

    public HashSet getSelectedItems()
    {
        HashSet hashset = new HashSet(mAssets);
        HashSet hashset1 = new HashSet();
        Iterator iterator = hashset.iterator();
        while(iterator.hasNext())
        {
            MediaItem mediaitem = (MediaItem)iterator.next();
            if(mUnselectedAssets.contains(mediaitem))
            {
                hashset1.add(mediaitem);
            }
        }
        hashset.removeAll(hashset1);
        return hashset;
    }

    public void onBindItemViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i, int j)
    {
        onBindItemViewHolder((BadViewHolder)viewholder, i, j);
    }

    public void onBindItemViewHolder(BadViewHolder badviewholder, int i, int j)
    {
        MediaItem mediaitem = (MediaItem)mAssets.get(j);
        GDEditMediaItemView gdeditmediaitemview = ((BadItemViewHelper)badviewholder).itemView;
        boolean flag;
        if(!mUnselectedAssets.contains(mediaitem))
        {
            flag = true;
        } else
        {
            flag = false;
        }
        gdeditmediaitemview.setIsSelected(flag);
        AndroidImagesUtils.getBitmapForItem(gdeditmediaitemview, mediaitem, com.flayvr.myrollshared.imageloading.ImageCacheBitmap.ThumbnailSize.Normal);
    }

    protected BadViewHolder onCreateViewHolderForHint(ViewGroup viewgroup)
    {
        return new BadHintViewHelper(LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.gallery_doctor_bad_photos_hint_card, viewgroup, false));
    }

    protected BadViewHolder onCreateViewHolderForItem(ViewGroup viewgroup)
    {
        GDEditMediaItemView gdeditmediaitemview = new GDEditMediaItemView(viewgroup.getContext());
        int i = GeneralUtils.getSize(viewgroup.getContext())[0];
        android.support.v7.widget.GridLayoutManager.LayoutParams layoutparams = new android.support.v7.widget.GridLayoutManager.LayoutParams(0, 0);
        gdeditmediaitemview.setLayoutParams(layoutparams);
        int j = gdeditmediaitemview.getResources().getInteger(R.integer.gd_span_size);
        layoutparams.height = i / j - (int)GeneralUtils.pxFromDp(gdeditmediaitemview.getContext(), 3F) * (j - 1);
        gdeditmediaitemview.setLayoutParams(layoutparams);
        return new BadItemViewHelper(gdeditmediaitemview);
    }

    public void onViewRecycled(android.support.v7.widget.RecyclerView.ViewHolder viewholder)
    {
        onViewRecycled((BadViewHolder)viewholder);
    }

    public void onViewRecycled(BadViewHolder badviewholder)
    {
        if(badviewholder.itemView instanceof GDEditMediaItemView)
        {
            GDEditMediaItemView gdeditmediaitemview = (GDEditMediaItemView)badviewholder.itemView;
            ImageLoaderAsyncTask imageloaderasynctask = (ImageLoaderAsyncTask)gdeditmediaitemview.getTag();
            if(imageloaderasynctask != null)
            {
                imageloaderasynctask.cancel(true);
                gdeditmediaitemview.setTag(null);
            }
            gdeditmediaitemview.clearImage();
        }
        onViewRecycled(badviewholder);
    }

    public boolean shoudShowHint()
    {
        return !PreferencesManager.isBadPhotosHintsShown();
    }

    private class BadViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
    {
        public BadViewHolder(View view)
        {
            super(view);
        }
    }


    private class BadItemViewHelper extends BadViewHolder
    {

        GDEditMediaItemView itemView;
        View selectionView;
        View thumbnailView;

        public BadItemViewHelper(GDEditMediaItemView gdeditmediaitemview)
        {
            super(gdeditmediaitemview);
            itemView = gdeditmediaitemview;
            thumbnailView = gdeditmediaitemview.findViewById(R.id.thumbnail_view);
            selectionView = gdeditmediaitemview.findViewById(R.id.selection_frame);
            thumbnailView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    GalleryDoctorBadPhotosAdapter.this.mItemClickListener.onItemClick(itemView, getItemPosition(GalleryDoctorBadPhotosAdapter.BadItemViewHelper.this), com.flayvr.screens.SelectionAdapter.Source.THUMBNAIL);
                }
            });

            selectionView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(mUnselectedAssets.contains(itemView.getItem()))
                    {
                        mUnselectedAssets.remove(itemView.getItem());
                    } else
                    {
                        mUnselectedAssets.add(itemView.getItem());
                    }
                    GalleryDoctorBadPhotosAdapter.this.mItemClickListener.onItemClick(itemView, getItemPosition(GalleryDoctorBadPhotosAdapter.BadItemViewHelper.this), com.flayvr.screens.SelectionAdapter.Source.SELECTION);
                }
            });
        }
    }


    private class BadHintViewHelper extends BadViewHolder
    {

        View hintDismiss;

        public BadHintViewHelper(View view)
        {
            super(view);
            hintDismiss = view.findViewById(R.id.action);

            hintDismiss.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    PreferencesManager.setIsBadPhotosHintsShown();
                    dismissHint();
                }
            });
        }
    }
}
