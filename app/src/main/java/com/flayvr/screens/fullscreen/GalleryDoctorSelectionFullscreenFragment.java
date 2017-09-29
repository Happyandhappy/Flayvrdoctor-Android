package com.flayvr.screens.fullscreen;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.screens.fullscreen.FullScreenFragment;
import com.flayvr.myrollshared.views.itemview.MediaItemView;
import it.sephiroth.android.library.widget.HListView;
import java.util.HashSet;
import java.util.List;

public class GalleryDoctorSelectionFullscreenFragment extends FullScreenFragment
{

    public static final String UNSELECTED_ITEMS = "UNSELECTED_ITEMS";
    TextView markTextView;
    TextView selectedCount;
    ImageView selectionButton;
    HashSet unselectedItems;

    public GalleryDoctorSelectionFullscreenFragment()
    {
    }

    protected int getLayout()
    {
        return R.layout.activity_gallery_selection;
    }

    protected Class getVideoActivityClass()
    {
        return VideoActivity.class;
    }

    public boolean isZoomable()
    {
        return false;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(getResources().getBoolean(R.bool.gd_dashboard_portrait_only))
        {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if(bundle == null) {
            if(getActivity().getIntent().getExtras() != null && getActivity().getIntent().hasExtra("UNSELECTED_ITEMS"))
                unselectedItems = (HashSet)getActivity().getIntent().getExtras().getSerializable("UNSELECTED_ITEMS");
        } else
            unselectedItems = (HashSet)bundle.getSerializable("UNSELECTED_ITEMS");
        if(unselectedItems == null)
            unselectedItems = new HashSet();
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch(menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case android.R.id.home: 
            getActivity().onBackPressed();
            break;
        }
        return true;
    }

    protected void onPageSelected(int i)
    {
        if(i < items.size())
        {
            MediaItem mediaitem = (MediaItem)items.get(i);
            if(unselectedItems.contains(mediaitem.getId()))
            {
                selectionButton.setImageDrawable(getResources().getDrawable(R.drawable.gd_radio_off_white));
                markTextView.setText(getResources().getString(R.string.gallery_doctor_fullscreen_keep));
            } else {
                selectionButton.setImageDrawable(getResources().getDrawable(R.drawable.gd_radio_on_white));
                markTextView.setText(getResources().getString(R.string.gallery_doctor_fullscreen_delete));
            }
            updateIndexCount();
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable("UNSELECTED_ITEMS", unselectedItems);
    }

    public void onViewCreated(View view, Bundle bundle)
    {
        super.onViewCreated(view, bundle);
        int i;
        final View back;
        if(bundle != null)
        {
            i = bundle.getInt("LAST_POSITION");
        } else
        {
            i = items.indexOf(item);
        }
        contentView = (ViewPager)getView().findViewById(R.id.pager);
        if(contentView != null)
        {
            initContentView(items, i);
        }
        thunbmails = (HListView)getView().findViewById(R.id.thumbnail_image_scroller);
        initThumbnailsView(items);
        selectedCount = (TextView)view.findViewById(R.id.gallery_action_count);
        updateIndexCount();
        selectionButton = (ImageView)view.findViewById(R.id.gallery_select_button);
        markTextView = (TextView)view.findViewById(R.id.gallery_action_title);
        selectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaItem localMediaItem = (MediaItem)items.get(contentView.getCurrentItem());
                if (!unselectedItems.contains(localMediaItem.getId()))
                    unselectedItems.add(localMediaItem.getId());
                else
                    unselectedItems.remove(localMediaItem.getId());
                onPageSelected(contentView.getCurrentItem());
            }
        });
        onPageSelected(contentView.getCurrentItem());
        metadataView = view.findViewById(R.id.gallery_metadata_view);
        animationImage = (MediaItemView)view.findViewById(R.id.expanded_image);
        back = view.findViewById(R.id.black_back);
        if(bundle == null && item != null)
        {
            Bundle bundle1 = getActivity().getIntent().getExtras();
            if(bundle1 != null)
            {
                final Rect startBounds = (Rect)bundle1.get("animation_start");
                if(startBounds != null)
                {
                    initAnimationImage(item);
                    animationImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            animationImage.getViewTreeObserver().removeOnPreDrawListener(this);
                            runEnterAnimation(item, back, startBounds);
                            return true;
                        }
                    });
                }
            }
        }
        showMetadataView();
    }

    public void updateIndexCount()
    {
        if(selectedCount != null)
        {
            selectedCount.setText((new StringBuilder()).append(1 + contentView.getCurrentItem()).append("/").append(items.size()).toString());
        }
    }
}
