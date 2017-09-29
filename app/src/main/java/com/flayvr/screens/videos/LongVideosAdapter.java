package com.flayvr.screens.videos;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.imageloading.AndroidImagesUtils;
import com.flayvr.myrollshared.imageloading.ImageLoaderAsyncTask;
import com.flayvr.util.SectionedGridRecyclerViewAdapter;
import com.flayvr.views.GDEditMediaItemView;

import java.util.*;

public class LongVideosAdapter extends SectionedGridRecyclerViewAdapter
{

    private List headerString;
    private List headers;
    private List items;

    public LongVideosAdapter(RecyclerView recyclerview, Map map)
    {
        super(recyclerview);
        items = new LinkedList();
        headers = new LinkedList(map.keySet());
        headerString = new LinkedList();
        Collections.sort(headers, Collections.reverseOrder());
        Iterator iterator = headers.iterator();
        while(iterator.hasNext())
        {
            Long long1 = (Long)iterator.next();
            List list = (List)map.get(long1);
            items.add(list);
            if(long1.longValue() > 0L)
            {
                String s = String.format(recyclerview.getContext().getResources().getString(R.string.video_bucket_header), new Object[] {
                    long1
                });
                headerString.add(s);
            } else
            {
                headerString.add(recyclerview.getContext().getResources().getString(R.string.video_bucket_header_short));
            }
            addUnselectedItems(list);
        }
    }

    public int getHeaderItemCount()
    {
        return headers.size();
    }

    public int getHeaderSpanCount(int i)
    {
        return recyclerView.getResources().getInteger(R.integer.gd_span_size);
    }

    public int getItemCountForHeader(int i)
    {
        return ((List)items.get(i)).size();
    }

    public int getItemSpanCountForHeader(int i, int j)
    {
        return recyclerView.getResources().getInteger(R.integer.gd_span_size);
    }

    public HashSet getSelectedItems()
    {
        HashSet hashset = new HashSet();
        for(Iterator iterator = items.iterator(); iterator.hasNext();)
        {
            Iterator iterator1 = ((List)iterator.next()).iterator();
            while(iterator1.hasNext())
            {
                MediaItem mediaitem = (MediaItem)iterator1.next();
                if(!mUnselectedAssets.contains(mediaitem))
                {
                    hashset.add(mediaitem);
                }
            }
        }

        return hashset;
    }

    public void onBindHeaderViewHolder(VideoViewHolder videoviewholder, int i)
    {
        ((VideoHeaderViewHolder)videoviewholder).textView.setText((CharSequence)headerString.get(i));
    }

    public void onBindHintViewHolder(VideoViewHolder videoviewholder, int i)
    {
    }

    public void onBindItemViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i, int j)
    {
        onBindItemViewHolder((VideoViewHolder)viewholder, i, j);
    }

    public void onBindItemViewHolder(VideoViewHolder videoviewholder, int i, int j)
    {
        MediaItem mediaitem = (MediaItem)((List)items.get(i)).get(j);
        LongVideoMediaItemView longvideomediaitemview = (LongVideoMediaItemView)videoviewholder.itemView;
        if(longvideomediaitemview.getItem() != null && !longvideomediaitemview.getItem().equals(mediaitem))
        {
            longvideomediaitemview.clearImage();
        }
        boolean flag;
        if(!mUnselectedAssets.contains(mediaitem))
        {
            flag = true;
        } else
        {
            flag = false;
        }
        longvideomediaitemview.setIsSelected(flag);
        AndroidImagesUtils.getBitmapForItem(longvideomediaitemview, mediaitem, com.flayvr.myrollshared.imageloading.ImageCacheBitmap.ThumbnailSize.Normal);
    }

    protected VideoHeaderViewHolder onCreateViewHolderForHeader(ViewGroup viewgroup)
    {
        return new VideoHeaderViewHolder(LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.gallery_doctor_videos_header_item, viewgroup, false));
    }

    protected VideoViewHolder onCreateViewHolderForItem(ViewGroup viewgroup)
    {
        return new VideoItemViewHolder(new LongVideoMediaItemView(viewgroup.getContext()));
    }

    public void onViewRecycled(android.support.v7.widget.RecyclerView.ViewHolder viewholder)
    {
        onViewRecycled((VideoViewHolder)viewholder);
    }

    public void onViewRecycled(VideoViewHolder videoviewholder)
    {
        if(videoviewholder.itemView instanceof LongVideoMediaItemView)
        {
            LongVideoMediaItemView longvideomediaitemview = (LongVideoMediaItemView)videoviewholder.itemView;
            ImageLoaderAsyncTask imageloaderasynctask = (ImageLoaderAsyncTask)longvideomediaitemview.getTag();
            if(imageloaderasynctask != null)
            {
                imageloaderasynctask.cancel(true);
                longvideomediaitemview.setTag(null);
            }
            longvideomediaitemview.clearImage();
        }
        onViewRecycled(videoviewholder);
    }

    public boolean shoudShowHint()
    {
        return false;
    }

    private class VideoViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
    {
        public VideoViewHolder(View view)
        {
            super(view);
        }
    }


    private class VideoHeaderViewHolder extends VideoViewHolder
    {

        TextView textView;

        public VideoHeaderViewHolder(View view)
        {
            super(view);
            textView = (TextView)view.findViewById(R.id.title);
        }
    }


    private class VideoItemViewHolder extends VideoViewHolder
    {

        GDEditMediaItemView itemView;

        public VideoItemViewHolder(GDEditMediaItemView gdeditmediaitemview)
        {
            super(gdeditmediaitemview);
            itemView = gdeditmediaitemview;
            View view = gdeditmediaitemview.findViewById(R.id.thumbnail_view);
            View view1 = gdeditmediaitemview.findViewById(R.id.selection_frame);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(itemView, getItemPosition(LongVideosAdapter.VideoItemViewHolder.this), com.flayvr.screens.SelectionAdapter.Source.THUMBNAIL);
                }
            });
            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mUnselectedAssets.contains(itemView.getItem()))
                        mUnselectedAssets.remove(itemView.getItem());
                    else
                        mUnselectedAssets.add(itemView.getItem());
                    mItemClickListener.onItemClick(itemView, getItemPosition(LongVideosAdapter.VideoItemViewHolder.this), com.flayvr.screens.SelectionAdapter.Source.SELECTION);
                }
            });
        }
    }
}
