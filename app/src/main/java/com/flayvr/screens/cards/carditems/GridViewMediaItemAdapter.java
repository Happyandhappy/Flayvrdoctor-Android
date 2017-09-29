package com.flayvr.screens.cards.carditems;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.imageloading.AndroidImagesUtils;
import java.util.List;

public class GridViewMediaItemAdapter extends BaseAdapter
{

    private Context mContext;
    private List mItems;
    private int mMax;
    private int mTotal;

    public GridViewMediaItemAdapter(Context context, List list, int i, int j)
    {
        mContext = context;
        mItems = list;
        mMax = i;
        mTotal = j;
    }

    public boolean areAllItemsEnabled()
    {
        return false;
    }

    public int getCount()
    {
        return Math.min(mItems.size(), mMax);
    }

    public Object getItem(int i)
    {
        return null;
    }

    public long getItemId(int i)
    {
        return 0L;
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        GalleryDoctorCardMediaItemView gallerydoctorcardmediaitemview;
        if(view == null)
        {
            gallerydoctorcardmediaitemview = new GalleryDoctorCardMediaItemView(mContext);
            gallerydoctorcardmediaitemview.setProp(Float.valueOf(1.0F));
        } else
        {
            gallerydoctorcardmediaitemview = (GalleryDoctorCardMediaItemView)view;
        }
        if(i + 1 == mMax && mTotal > mMax)
        {
            gallerydoctorcardmediaitemview.setShouldShowOverlay(true);
            gallerydoctorcardmediaitemview.setOverlayExtraPhotos(mTotal - mMax);
        } else
        {
            gallerydoctorcardmediaitemview.setShouldShowOverlay(false);
        }
        AndroidImagesUtils.getBitmapForItem(gallerydoctorcardmediaitemview, (MediaItem)mItems.get(i));
        return gallerydoctorcardmediaitemview;
    }

    public boolean isEnabled(int i)
    {
        return false;
    }
}
