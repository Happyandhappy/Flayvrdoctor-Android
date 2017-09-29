package com.flayvr.myrollshared.screens.fullscreen;

import android.content.*;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.*;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.imageloading.AndroidImagesUtils;
import com.flayvr.myrollshared.imageloading.ImageLoaderAsyncTask;
import com.flayvr.myrollshared.imageloading.ImagesCache;
import com.flayvr.myrollshared.utils.DefaultAnimatorListener;
import com.flayvr.myrollshared.views.itemview.MediaItemView;
import com.flayvr.myrollshared.views.itemview.fullscreen.FullscreenThumbnailView;
import com.nineoldandroids.animation.*;
import com.nineoldandroids.view.ViewHelper;
import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;
import it.sephiroth.android.library.widget.HListView;
import java.util.*;

public abstract class FullScreenFragment extends Fragment
{

    private static final String ACTION_REVIEW = "com.android.camera.action.REVIEW";
    public static final String ANIMATION_START = "animation_start";
    private static final int AUTO_HIDE_DELAY_MILLIS = 2000;
    public static final String FOLDERS_SELECTED = "FOLDERS_SELECTED";
    public static final String HIDE_BAD_PHOTOS = "HIDE_BAD_PHOTOS";
    public static final String ITEMS_REVERESED = "ITEMS_REVERESED";
    public static final String ITEMS_SELECTED = "ITEMS_SELECTED";
    public static final String ITEM_SELECTED = "ITEM_SELECTED";
    public static final String LAST_POSITION = "LAST_POSITION";
    public static final String OPEN_SHARE = "OPEN_SHARE";
    public static final String SOURCE = "fullscreen_source";
    protected static final String TAG = "flayvr_fullscreen";
    protected MediaItemView animationImage;
    public ViewPager contentView;
    private View decorView;
    public final Handler hideHandler = new Handler(){
        @Override
        public void handleMessage(Message message)
        {
            hideMetadataView();
        }
    };
    protected boolean isMetaDataShown;
    protected MediaItem item;
    protected List items;
    protected View metadataView;
    private int prevIndex;
    protected FullscreenSource source;
    protected HListView thunbmails;

    public FullScreenFragment()
    {
    }

    private MediaItem getMediaItemFromURI(Uri uri)
    {
        Cursor cursor;
        String s;
        byte byte0;
        int j;
        QueryBuilder querybuilder;
        String s1;
        MediaItem mediaitem2;
        int l;
        MediaItem mediaitem;
        if (!"file".equals(uri.getScheme())) {
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                s = getActivity().getContentResolver().getType(uri);
                if (s == null) {
                    cursor.close();
                    return null;
                }
            } else {
                Log.w("flayvr_fullscreen", (new StringBuilder()).append("problem while reading URI: ").append(uri).toString());
                return null;
            }
            if (!s.contains("video")) {
                byte0 = 1;
                int i;
                try {
                    i = cursor.getColumnIndexOrThrow("_data");
                } catch (IllegalArgumentException illegalargumentexception) {
                    Log.e("flayvr_fullscreen", "error loading item", illegalargumentexception);
                    return null;
                }
                j = i;
            } else {
                byte0 = 2;
                try {
                    l = cursor.getColumnIndexOrThrow("_data");
                } catch (IllegalArgumentException illegalargumentexception1) {
                    Log.e("flayvr_fullscreen", "error loading item", illegalargumentexception1);
                    return null;
                }
                j = l;
            }
            s1 = cursor.getString(j);
            querybuilder = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder();
            try {
                querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
                querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.Path.eq(s1), new WhereCondition[0]);
                querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.eq(Integer.valueOf(byte0)), new WhereCondition[0]);
                mediaitem2 = (MediaItem) querybuilder.build().unique();
                return mediaitem2;
            }catch(DaoException daoexception){
                List list = querybuilder.build().list();
                MediaItem mediaitem1;
                int k = list.size();
                mediaitem = null;
                if(k > 0)
                    return (MediaItem)list.get(0);
                return mediaitem;
            }
        }
        QueryBuilder querybuilder1 = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder();
        querybuilder1.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
        querybuilder1.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.Path.eq(null), new WhereCondition[0]);
        Query query = querybuilder1.build().forCurrentThread();
        query.setParameter(0, uri.getPath());
        MediaItem mediaitem3 = (MediaItem) query.unique();
        if (mediaitem3 == null) {
            query.setParameter(0, uri.getPath().replaceFirst("sdcard", "emulated/"));
            mediaitem3 = (MediaItem) query.unique();
        }
        mediaitem = mediaitem3;
        return mediaitem;
    }

    public void delayedHide()
    {
        hideHandler.removeMessages(0);
        hideHandler.sendEmptyMessageDelayed(0, 2000L);
    }

    public MediaItem getCurrentItem()
    {
        int i = contentView.getCurrentItem();
        if(i < items.size())
        {
            return (MediaItem)items.get(i);
        } else
        {
            return null;
        }
    }

    protected Pair getItemDime(MediaItem mediaitem)
    {
        if(mediaitem.getType().intValue() == 1)
        {
            android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mediaitem.getPath(), options);
            if(mediaitem.getOrientation().intValue() % 180 == 0)
            {
                return new Pair(Integer.valueOf(options.outWidth), Integer.valueOf(options.outHeight));
            } else
            {
                return new Pair(Integer.valueOf(options.outHeight), Integer.valueOf(options.outWidth));
            }
        }
        if(mediaitem.getType().intValue() == 2 && android.os.Build.VERSION.SDK_INT >= 14)
        {
            MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();
            mediametadataretriever.setDataSource(mediaitem.getPath());
            String s = mediametadataretriever.extractMetadata(19);
            Integer integer = Integer.valueOf(0);
            if(s != null)
            {
                integer = Integer.valueOf(s);
            }
            String s1 = mediametadataretriever.extractMetadata(18);
            Integer integer1 = Integer.valueOf(0);
            if(s1 != null)
            {
                integer1 = Integer.valueOf(s1);
            }
            return new Pair(integer1, integer);
        } else
        {
            return null;
        }
    }

    protected abstract int getLayout();

    protected abstract Class getVideoActivityClass();

    protected void hideMetadataView()
    {
        if(android.os.Build.VERSION.SDK_INT >= 19)
        {
            decorView.setSystemUiVisibility(3847);
            return;
        }
        ActionBarActivity actionbaractivity = (ActionBarActivity)getActivity();
        if(actionbaractivity != null)
        {
            actionbaractivity.getSupportActionBar().hide();
        }
        toggleMetadataView();
        isMetaDataShown = false;
    }

    protected void initAnimationImage(MediaItem mediaitem)
    {
        animationImage.setItem(mediaitem);
        animationImage.removePlaceholder();
        com.flayvr.myrollshared.imageloading.ImageCacheBitmap.ThumbnailSize thumbnailsize = com.flayvr.myrollshared.imageloading.ImageCacheBitmap.ThumbnailSize.Normal;
        ImagesCache imagescache = FlayvrApplication.getImagesCache();
        android.graphics.Bitmap bitmap = imagescache.get(mediaitem, thumbnailsize);
        if(bitmap == null && mediaitem.getSource().intValue() == 1)
        {
            bitmap = AndroidImagesUtils.createBitmapForItem(FlayvrApplication.getAppContext().getContentResolver(), mediaitem, thumbnailsize);
            if(bitmap != null)
            {
                imagescache.put(mediaitem.getId(), bitmap, thumbnailsize);
            }
        }
        animationImage.setImage(bitmap);
    }

    protected void initContentView(List list, int i)
    {
        ImagePagerAdapter imagepageradapter = new ImagePagerAdapter(this, list, getVideoActivityClass());
        imagepageradapter.setIsZoomable(isZoomable());
        contentView.setAdapter(imagepageradapter);
        contentView.setPageMargin(getActivity().getResources().getDimensionPixelSize(R.dimen.fullscreen_margin));
        contentView.setCurrentItem(i);
        prevIndex = 0;
        contentView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int i)
            {
            }

            @Override
            public void onPageScrolled(int i, float f, int j)
            {
            }

            @Override
            public void onPageSelected(int i)
            {
                int j = thunbmails.getLastVisiblePosition() - thunbmails.getFirstVisiblePosition();
                if(thunbmails.getFirstVisiblePosition() <= i && i <= thunbmails.getLastVisiblePosition())
                {
                    if(prevIndex < i)
                    {
                        thunbmails.smoothScrollToPosition(i + j / 2);
                    } else
                    {
                        thunbmails.smoothScrollToPosition(i - j / 2);
                    }
                } else
                if(thunbmails.getLastVisiblePosition() < i)
                {
                    thunbmails.smoothScrollToPosition(i + j / 2);
                } else
                {
                    thunbmails.smoothScrollToPosition(i - j / 2);
                }
                ((ThumbnailsAdapter)thunbmails.getAdapter()).notifyDataSetChanged();
                FullScreenFragment.this.onPageSelected(i);
            }
        });
    }

    protected void initThumbnailsView(List list)
    {
        thunbmails.setAdapter(new ThumbnailsAdapter(getActivity(), list));
        thunbmails.setSelection(-2 + contentView.getCurrentItem());
        thunbmails.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                delayedHide();
                return false;
            }
        });
        thunbmails.setRecyclerListener(new it.sephiroth.android.library.widget.AbsHListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                if(view instanceof FullscreenThumbnailView)
                {
                    FullscreenThumbnailView fullscreenthumbnailview = (FullscreenThumbnailView)view;
                    ImageLoaderAsyncTask imageloaderasynctask = (ImageLoaderAsyncTask)fullscreenthumbnailview.getTag(R.id.async_task);
                    if(imageloaderasynctask != null)
                    {
                        imageloaderasynctask.cancel(false);
                        fullscreenthumbnailview.setTag(null);
                    }
                    TimerTask timertask = (TimerTask)fullscreenthumbnailview.getTag(R.id.timer);
                    if(timertask != null)
                    {
                        timertask.cancel();
                    }
                }
            }
        });
    }

    public boolean isZoomable()
    {
        return true;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        item = setItems(bundle);
        if(item == null)
        {
            getActivity().finish();
        } else
        {
            if(getActivity().getIntent().hasExtra("fullscreen_source"))
            {
                source = (FullscreenSource)getActivity().getIntent().getExtras().getSerializable("fullscreen_source");
            } else
            {
                source = FullscreenSource.NONE;
            }
            isMetaDataShown = true;
            decorView = getActivity().getWindow().getDecorView();
            if(android.os.Build.VERSION.SDK_INT >= 11)
            {
                decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int i) {
                        boolean flag = true;
                        FullScreenFragment fullscreenfragment = FullScreenFragment.this;
                        boolean flag1;
                        FullScreenFragment fullscreenfragment1;
                        if((i & 2) != 0)
                            flag1 = flag;
                        else
                            flag1 = false;
                        fullscreenfragment.isMetaDataShown = flag1;
                        toggleMetadataView();
                        fullscreenfragment1 = FullScreenFragment.this;
                        if(isMetaDataShown)
                            flag = false;
                        fullscreenfragment1.isMetaDataShown = flag;
                        if(isMetaDataShown)
                            delayedHide();
                    }
                });
            }
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        return layoutinflater.inflate(getLayout(), null);
    }

    protected void onPageSelected(int i)
    {
        prevIndex = i;
        delayedHide();
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putInt("LAST_POSITION", contentView.getCurrentItem());
    }

    protected void runEnterAnimation(MediaItem mediaitem, View view, Rect rect)
    {
        Rect rect1 = new Rect();
        Point point = new Point();
        view.getGlobalVisibleRect(rect1, point);
        rect.offset(-point.x, -point.y);
        ExpandAnimation expandanimation;
        if((float)rect1.width() / (float)rect1.height() <= mediaitem.getNullableProp().floatValue())
        {
            int k = (int)((float)rect1.width() / mediaitem.getNullableProp().floatValue());
            int l = (int)((double)(rect1.height() - k) / 2D);
            rect1.set(0, l, rect1.width(), k + l);
        } else
        {
            int i = (int)((float)rect1.height() * mediaitem.getNullableProp().floatValue());
            int j = (int)((double)(rect1.width() - i) / 2D);
            rect1.set(j, 0, i + j, rect1.height());
        }
        contentView.setVisibility(View.INVISIBLE);
        ViewHelper.setAlpha(view, 0.0F);
        ViewHelper.setAlpha(metadataView, 0.0F);
        ViewHelper.setTranslationY(metadataView, metadataView.getHeight());
        expandanimation = new ExpandAnimation(500L, rect, rect1, view);
        expandanimation.setAnimationListener(new DefaultAnimatorListener(){
            @Override
            public void onAnimationEnd(Animation animation)
            {
                contentView.setVisibility(View.VISIBLE);
                AnimatorSet animatorset = new AnimatorSet();
                Animator aanimator[] = new Animator[2];
                aanimator[0] = ObjectAnimator.ofFloat(metadataView, "translationY", new float[] {
                        0.0F
                });
                aanimator[1] = ObjectAnimator.ofFloat(metadataView, "alpha", new float[] {
                        1.0F
                });
                animatorset.playTogether(aanimator);
                animatorset.start();
                ViewHelper.setAlpha(animationImage, 0.0F);
                animationImage.setVisibility(View.GONE);
            }
        });
        animationImage.startAnimation(expandanimation);
    }

    protected MediaItem setItems(Bundle bundle)
    {
        int i = 0;
        DaoSession daosession = DBManager.getInstance().getDaoSession();
        Object obj1;
        if("android.intent.action.VIEW".equals(getActivity().getIntent().getAction()) || "com.android.camera.action.REVIEW".equals(getActivity().getIntent().getAction()))
        {
            Uri uri = getActivity().getIntent().getData();
            Object obj = getMediaItemFromURI(uri);
            if(obj == null)
            {
                obj = new URIMediaItem(uri, getActivity().getContentResolver());
                items = new ArrayList(1);
                items.add(obj);
            } else {
                QueryBuilder querybuilder = daosession.getMediaItemDao().queryBuilder();
                querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.eq(((MediaItem) (obj)).getFolder().getId()), new WhereCondition[0]);
                querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
                Property aproperty[] = new Property[1];
                aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
                querybuilder.orderDesc(aproperty);
                Query query = querybuilder.limit(1000).offset(0).build();
                items = new LinkedList();
                int j = 1000;
                while(j == '\u03E8') 
                {
                    query.setOffset(i);
                    List list = query.list();
                    items.addAll(list);
                    j = list.size();
                    i += 1000;
                }
            }
            obj1 = obj;
        } else
        {
            Bundle bundle1 = getActivity().getIntent().getExtras();
            Long long1 = Long.valueOf(bundle1.getLong("ITEM_SELECTED"));
            if(bundle1.containsKey("FOLDERS_SELECTED"))
            {
                Set set = (Set)bundle1.get("FOLDERS_SELECTED");
                QueryBuilder querybuilder2 = daosession.getMediaItemDao().queryBuilder();
                Property aproperty2[];
                Query query1;
                if(bundle1.containsKey("HIDE_BAD_PHOTOS"))
                {
                    WhereCondition wherecondition1 = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.in(set);
                    WhereCondition awherecondition1[] = new WhereCondition[2];
                    awherecondition1[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull();
                    awherecondition1[1] = querybuilder2.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.IsBad.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.IsBad.eq(Boolean.valueOf(false)), new WhereCondition[0]);
                    querybuilder2.where(wherecondition1, awherecondition1);
                } else
                {
                    WhereCondition wherecondition = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.in(set);
                    WhereCondition awherecondition[] = new WhereCondition[1];
                    awherecondition[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull();
                    querybuilder2.where(wherecondition, awherecondition);
                }
                aproperty2 = new Property[1];
                aproperty2[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
                querybuilder2.orderDesc(aproperty2);
                query1 = querybuilder2.limit(1000).offset(0).build();
                items = new LinkedList();
                for(int k1 = 1000; k1 == '\u03E8';)
                {
                    query1.setOffset(i);
                    List list1 = query1.list();
                    items.addAll(list1);
                    k1 = list1.size();
                    i += 1000;
                }

            } else
            if(bundle1.containsKey("ITEMS_SELECTED"))
            {
                long al[] = bundle1.getLongArray("ITEMS_SELECTED");
                Long along[] = new Long[al.length];
                for(int k = 0; k < al.length; k++)
                {
                    along[k] = Long.valueOf(al[k]);
                }

                LinkedList linkedlist = new LinkedList();
                for(int l = 0; l <= (-1 + along.length) / 900; l++)
                {
                    int i1 = l * 900;
                    int j1 = 900 * (l + 1);
                    if(j1 > along.length)
                    {
                        j1 = along.length;
                    }
                    Long along1[] = (Long[])Arrays.copyOfRange(along, i1, j1);
                    QueryBuilder querybuilder1 = daosession.getMediaItemDao().queryBuilder();
                    querybuilder1.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.in((Object[])along1), new WhereCondition[0]);
                    querybuilder1.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
                    Property aproperty1[] = new Property[1];
                    aproperty1[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
                    querybuilder1.orderDesc(aproperty1);
                    linkedlist.addAll(querybuilder1.build().list());
                }

                items = linkedlist;
                final boolean reversed = bundle1.getBoolean("ITEMS_REVERESED", false);
                Collections.sort(items, new Comparator<MediaItem>() {
                    @Override
                    public int compare(MediaItem mediaitem, MediaItem mediaitem1)
                    {
                        if(reversed)
                        {
                            if(!mediaitem1.getDate().equals(mediaitem.getDate()))
                            {
                                return mediaitem1.getDate().compareTo(mediaitem.getDate());
                            } else
                            {
                                return mediaitem1.getId().compareTo(mediaitem.getId());
                            }
                        }
                        if(!mediaitem.getDate().equals(mediaitem1.getDate()))
                        {
                            return mediaitem.getDate().compareTo(mediaitem1.getDate());
                        } else
                        {
                            return mediaitem.getId().compareTo(mediaitem1.getId());
                        }
                    }
                });
            }
            Iterator iterator = items.iterator();
            obj1 = null;
            while(iterator.hasNext()) 
            {
                Object obj2 = (MediaItem)iterator.next();
                if(!((MediaItem) (obj2)).getId().equals(long1))
                {
                    obj2 = obj1;
                }
                obj1 = obj2;
            }
        }
        return ((MediaItem) (obj1));
    }

    protected void showMetadataView()
    {
        if(android.os.Build.VERSION.SDK_INT >= 19)
        {
            if(decorView != null)
            {
                decorView.setSystemUiVisibility(1792);
            }
            return;
        } else
        {
            ((ActionBarActivity)getActivity()).getSupportActionBar().show();
            toggleMetadataView();
            isMetaDataShown = true;
            return;
        }
    }

    protected void toggleMetadataView()
    {
        AnimatorSet animatorset = new AnimatorSet();
        Animator aanimator[] = new Animator[2];
        View view = metadataView;
        float af[] = new float[1];
        float f;
        View view1;
        float af1[];
        boolean flag;
        float f1;
        if(!isMetaDataShown)
            f = 0.0F;
        else
            f = metadataView.getHeight();
        af[0] = f;
        aanimator[0] = ObjectAnimator.ofFloat(view, "translationY", af);
        view1 = metadataView;
        af1 = new float[1];
        flag = isMetaDataShown;
        f1 = 0.0F;
        if(!flag)
            f1 = 1.0F;
        af1[0] = f1;
        aanimator[1] = ObjectAnimator.ofFloat(view1, "alpha", af1);
        animatorset.playTogether(aanimator);
        animatorset.start();
    }

    private class ThumbnailsAdapter extends ArrayAdapter
    {
        public View getView(int i, View view, ViewGroup viewgroup)
        {
            FullscreenThumbnailView fullscreenthumbnailview;
            boolean flag;
            if(view == null)
            {
                fullscreenthumbnailview = new FullscreenThumbnailView(getContext());
                fullscreenthumbnailview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contentView.setCurrentItem(((FullscreenThumbnailView)view).getId(), true);
                        notifyDataSetChanged();
                        delayedHide();
                    }
                });
            } else
                fullscreenthumbnailview = (FullscreenThumbnailView)view;
            fullscreenthumbnailview.setId(i);
            if(contentView.getCurrentItem() == fullscreenthumbnailview.getId())
                flag = true;
            else
                flag = false;
            fullscreenthumbnailview.setIsSelected(flag);
            AndroidImagesUtils.getBitmapForItem(fullscreenthumbnailview, (MediaItem)getItem(i), com.flayvr.myrollshared.imageloading.ImageCacheBitmap.ThumbnailSize.Small);
            return fullscreenthumbnailview;
        }

        ThumbnailsAdapter(Context context, List list)
        {
            super(context, 0, list);
        }
    }

    public enum FullscreenSource
    {
        GALLERY("GALLERY", 0),
        PLAYER("PLAYER", 1),
        NOTIFICATION("NOTIFICATION", 2),
        STICKY_NOTIFICATION_MIN("STICKY_NOTIFICATION_MIN", 3),
        STICKY_NOTIFICATION_EXP("STICKY_NOTIFICATION_EXP", 4),
        NONE("NONE", 5);

        private FullscreenSource(String s, int i)
        {
        }
    }

    private class ExpandAnimation extends Animation
    {
        private View back;
        private Rect end;
        private android.widget.FrameLayout.LayoutParams lp;
        private Rect start;

        protected void applyTransformation(float f, Transformation transformation)
        {
            super.applyTransformation(f, transformation);
            lp.height = (int)((float)start.height() + f * (float)(end.height() - start.height()));
            lp.topMargin = (int)((float)start.top + f * (float)(end.top - start.top));
            lp.width = (int)((float)start.width() + f * (float)(end.width() - start.width()));
            lp.leftMargin = (int)((float)start.left + f * (float)(end.left - start.left));
            animationImage.requestLayout();
            ViewHelper.setAlpha(back, f);
        }

        public ExpandAnimation(long l, Rect rect, Rect rect1, View view)
        {
            super();
            setDuration(l);
            lp = (android.widget.FrameLayout.LayoutParams)animationImage.getLayoutParams();
            lp.width = rect.width();
            lp.height = rect.height();
            lp.topMargin = rect.top;
            lp.leftMargin = rect.left;
            start = rect;
            end = rect1;
            back = view;
        }
    }
}
