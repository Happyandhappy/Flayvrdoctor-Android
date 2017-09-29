package com.flayvr.myrollshared.screens.fullscreen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.data.URIMediaItem;
import com.flayvr.myrollshared.imageloading.AndroidImagesUtils;
import com.flayvr.myrollshared.imageloading.ImageLoaderAsyncTask;
import com.flayvr.myrollshared.views.itemview.SubsamplingScaleImageView;
import com.flayvr.myrollshared.views.itemview.fullscreen.FullscreenVideoView;
import java.util.*;

public class ImagePagerAdapter extends PagerAdapter
{

    android.view.View.OnClickListener clickListener;
    private final FullScreenFragment fullScreenFragment;
    private boolean isZoomable;
    private List items;
    private ImageLoaderAsyncTask loaderAsyncTask;
    private final Class videoActivityClass;
    private Map views;

    protected ImagePagerAdapter(FullScreenFragment fullscreenfragment, List list, Class class1)
    {
        views = new HashMap();
        clickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(fullScreenFragment.isMetaDataShown)
                {
                    fullScreenFragment.hideMetadataView();
                } else {
                    fullScreenFragment.showMetadataView();
                    fullScreenFragment.delayedHide();
                }
            }
        };
        videoActivityClass = class1;
        fullScreenFragment = fullscreenfragment;
        items = list;
    }

    private View getAnimatedGifView(ViewGroup viewgroup, MediaItem mediaitem, int i)
    {
        ViewGroup viewgroup1 = (ViewGroup)((LayoutInflater)FlayvrApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fullscreen_item_view_gif, null);
        WebView webview = (WebView)viewgroup1.findViewById(R.id.fullscreen_image_view);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        viewgroup1.setOnClickListener(clickListener);
        webview.loadUrl((new StringBuilder()).append("file://").append(mediaitem.getPath()).toString());
        ((ViewPager)viewgroup).addView(viewgroup1, 0);
        return viewgroup1;
    }

    private View getImageView(ViewGroup viewgroup, MediaItem mediaitem, int i)
    {
        SubsamplingScaleImageView subsamplingscaleimageview = (SubsamplingScaleImageView)((LayoutInflater)FlayvrApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fullscreen_item_view_zoom, null);
        subsamplingscaleimageview.setMinimumDpi(0);
        subsamplingscaleimageview.isZoomable = isZoomable;
        subsamplingscaleimageview.setDoubleTapZoomScale(2.5F);
        subsamplingscaleimageview.setMaxScale(5F);
        subsamplingscaleimageview.setOnClickListener(clickListener);
        AndroidImagesUtils.getBitmapForItem(subsamplingscaleimageview, mediaitem, com.flayvr.myrollshared.imageloading.ImageCacheBitmap.ThumbnailSize.Normal, false, false);
        if(loaderAsyncTask != null)
        {
            loaderAsyncTask.cancel(true);
            loaderAsyncTask = null;
        }
        loaderAsyncTask = (ImageLoaderAsyncTask)subsamplingscaleimageview.getTag();
        ((ViewPager)viewgroup).addView(subsamplingscaleimageview, 0);
        return subsamplingscaleimageview;
    }

    private View getVideoView(ViewGroup viewgroup, final MediaItem item, int i)
    {
        FullscreenVideoView fullscreenvideoview = new FullscreenVideoView(FlayvrApplication.getAppContext());
        fullscreenvideoview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(fullScreenFragment.getActivity(), videoActivityClass);
                if(item instanceof URIMediaItem)
                    intent.putExtra("ITEM_URI", ((URIMediaItem)item).getUri());
                else
                    intent.putExtra("ITEM_URI", Uri.parse(item.getPath()));
                fullScreenFragment.startActivity(intent);
            }
        });
        AndroidImagesUtils.getBitmapForItem(fullscreenvideoview, item);
        ((ViewPager)viewgroup).addView(fullscreenvideoview, 0);
        return fullscreenvideoview;
    }

    public void destroyItem(ViewGroup viewgroup, int i, Object obj)
    {
        View view = (View)obj;
        ((ViewPager)viewgroup).removeView(view);
        views.remove(Integer.valueOf(i));
        if(view instanceof SubsamplingScaleImageView)
        {
            final SubsamplingScaleImageView image = (SubsamplingScaleImageView)view;
            FlayvrApplication.runAction(new Runnable() {
                @Override
                public void run() {
                    image.reset(true);
                }
            });
            ImageLoaderAsyncTask imageloaderasynctask = (ImageLoaderAsyncTask)image.getTag();
            if(imageloaderasynctask != null)
            {
                imageloaderasynctask.cancel(false);
                image.setTag(null);
            }
        }
    }

    public int getCount()
    {
        return items.size();
    }

    public int getItemPosition(Object obj)
    {
        for(Iterator iterator = views.entrySet().iterator(); iterator.hasNext();)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            if(obj.equals(entry.getValue()))
            {
                return ((Integer)entry.getKey()).intValue();
            }
        }

        return -2;
    }

    public Object instantiateItem(ViewGroup viewgroup, int i)
    {
        MediaItem mediaitem = (MediaItem)items.get(i);
        View view;
        if(mediaitem.getType().intValue() == 1)
        {
            if(mediaitem.isGif())
            {
                view = getAnimatedGifView(viewgroup, mediaitem, i);
            } else
            {
                view = getImageView(viewgroup, mediaitem, i);
            }
        } else
        if(mediaitem.getType().intValue() == 2)
        {
            view = getVideoView(viewgroup, mediaitem, i);
        } else
        {
            view = null;
        }
        views.put(Integer.valueOf(i), view);
        return view;
    }

    public boolean isViewFromObject(View view, Object obj)
    {
        return view == obj;
    }

    public int removeView(ViewPager viewpager, int i)
    {
        viewpager.setAdapter(null);
        views.remove(Integer.valueOf(i));
        notifyDataSetChanged();
        viewpager.setAdapter(this);
        return i;
    }

    public void setIsZoomable(boolean flag)
    {
        isZoomable = flag;
    }

    public void setPrimaryItem(ViewGroup viewgroup, int i, Object obj)
    {
        super.setPrimaryItem(viewgroup, i, obj);
        if(obj instanceof SubsamplingScaleImageView)
        {
            SubsamplingScaleImageView subsamplingscaleimageview = (SubsamplingScaleImageView)obj;
            subsamplingscaleimageview.setImageFile(subsamplingscaleimageview.getItem());
        }
    }
}
