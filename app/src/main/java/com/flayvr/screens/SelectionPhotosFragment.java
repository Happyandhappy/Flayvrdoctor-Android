package com.flayvr.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.fragments.MessageDialog;
import com.flayvr.myrollshared.imageloading.ImageLoaderAsyncTask;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.flayvr.myrollshared.views.itemview.EditMediaItemView;
import com.flayvr.screens.fullscreen.GalleryDoctorSelectionFullScreenActivity;
import com.flayvr.util.GalleryDoctorAnalyticsSender;
import com.flayvr.util.SectionedGridRecyclerViewAdapter;
import java.util.*;

public abstract class SelectionPhotosFragment extends Fragment
{

    protected static final String SOURCE = "SOURCE";
    private static final int UNSELECTED_ITEMS = 1;
    private ImageView actionIcon;
    private TextView actionText;
    private MenuItem deleteItem;
    protected String fragmentType;
    protected RecyclerView grid;
    protected int numberOfPhotosCleaned;
    protected SectionedGridRecyclerViewAdapter photoAdapter;
    protected long sizeOfPhotosCleaned;
    protected int source;
    protected boolean userMadeAction;

    public SelectionPhotosFragment()
    {
        userMadeAction = false;
        numberOfPhotosCleaned = 0;
        sizeOfPhotosCleaned = 0L;
    }

    protected abstract void addItemsData(Intent intent, MediaItem mediaitem);

    protected abstract SectionedGridRecyclerViewAdapter createAdapter();

    public void deleteAction()
    {
        MessageDialog messagedialog = new MessageDialog();
        final HashSet selectedItems = photoAdapter.getSelectedItems();
        if(selectedItems.size() == 0)
        {
            if(isPhotos())
            {
                messagedialog.setMsg(getResources().getString(R.string.gallery_doctor_deletion_popup_text_keep_all));
                messagedialog.setTitle(getResources().getString(R.string.gallery_doctor_deletion_popup_heading_keep_all));
            } else
            {
                messagedialog.setMsg(getResources().getString(R.string.gallery_doctor_deletion_popup_text_keep_all_videos));
                messagedialog.setTitle(getResources().getString(R.string.gallery_doctor_deletion_popup_heading_keep_all_videos));
            }
            messagedialog.setPositiveText(getResources().getString(R.string.gallery_doctor_deletion_popup_keep_all_action));
        } else
        {
            messagedialog.setPositiveText(getResources().getString(R.string.gallery_doctor_bad_deletion_popup_button));
            if(isPhotos())
            {
                Resources resources1 = getResources();
                int j;
                if(source == 1)
                {
                    j = R.string.gallery_doctor_similar_deletion_popup_text;
                } else
                {
                    j = R.string.similar_deletion_popup_text_cloud;
                }
                messagedialog.setMsg(resources1.getString(j));
            } else
            {
                Resources resources = getResources();
                int i;
                if(source == 1)
                {
                    i = R.string.gallery_doctor_similar_deletion_popup_text_videos;
                } else
                {
                    i = R.string.similar_deletion_popup_text_cloud_videos;
                }
                messagedialog.setMsg(resources.getString(i));
            }
            if(selectedItems.size() == 1)
            {
                if(isPhotos())
                {
                    messagedialog.setTitle(getResources().getString(R.string.gallery_doctor_bad_deletion_popup_heading_single));
                } else
                {
                    messagedialog.setTitle(getResources().getString(R.string.gallery_doctor_bad_deletion_popup_heading_single_videos));
                }
            } else
            if(isPhotos())
            {
                String s1 = getResources().getString(R.string.gallery_doctor_bad_deletion_popup_heading);
                Object aobj1[] = new Object[1];
                aobj1[0] = GeneralUtils.humanReadableNumber(selectedItems.size());
                messagedialog.setTitle(String.format(s1, aobj1));
            } else
            {
                String s = getResources().getString(R.string.gallery_doctor_bad_deletion_popup_heading_videos);
                Object aobj[] = new Object[1];
                aobj[0] = GeneralUtils.humanReadableNumber(selectedItems.size());
                messagedialog.setTitle(String.format(s, aobj));
            }
        }
        messagedialog.setNegativeText(getResources().getString(R.string.cancel_label));
        messagedialog.setNegetiveListener(new android.content.DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HashMap hashmap = new HashMap();
                hashmap.put((new StringBuilder()).append("total ").append(fragmentType).append(" to be deleted").toString(), (new StringBuilder()).append(photoAdapter.getSelectedItems().size()).append("").toString());
                hashmap.put((new StringBuilder()).append("total ").append(fragmentType).append(" to be kept").toString(), (new StringBuilder()).append(photoAdapter.getUnselectedItems().size()).append("").toString());
                AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("canceled deleting ").append(fragmentType).toString(), hashmap, true);
            }
        });
        messagedialog.setPositiveListener(new android.content.DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                userMadeAction = true;
                numberOfPhotosCleaned = selectedItems.size();
                sizeOfPhotosCleaned = 0L;
                for(Iterator iterator = selectedItems.iterator(); iterator.hasNext();)
                {
                    MediaItem mediaitem = (MediaItem)iterator.next();
                    SelectionPhotosFragment selectionphotosfragment = SelectionPhotosFragment.this;
                    selectionphotosfragment.sizeOfPhotosCleaned = selectionphotosfragment.sizeOfPhotosCleaned + mediaitem.getFileSizeBytesSafe().longValue();
                }

                deleteSelectedAssets();
                updateDBItems();
                Iterator iterator1 = photoAdapter.getSelectedItems().iterator();
                long l = 0L;
                HashMap hashmap;
                int j;
                int k;
                long l1;
                while(iterator1.hasNext())
                {
                    Object obj = iterator1.next();
                    if(obj instanceof MediaItem)
                    {
                        l1 = l + ((MediaItem)obj).getFileSizeBytesSafe().longValue();
                    } else
                    {
                        l1 = l;
                    }
                    l = l1;
                }
                hashmap = new HashMap();
                j = photoAdapter.getSelectedItems().size();
                k = photoAdapter.getUnselectedItems().size();
                hashmap.put((new StringBuilder()).append("total ").append(fragmentType).append(" deleted").toString(), (new StringBuilder()).append(j).append("").toString());
                hashmap.put("total photos deleted", (new StringBuilder()).append(j).append("").toString());
                hashmap.put((new StringBuilder()).append("total ").append(fragmentType).append(" kept").toString(), (new StringBuilder()).append(k).append("").toString());
                hashmap.put("total space saved", (new StringBuilder()).append(l / 0x100000L).append("").toString());
                hashmap.put((new StringBuilder()).append("total space saved for ").append(fragmentType).append(" photos").toString(), (new StringBuilder()).append(l / 0x100000L).append("").toString());
                AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("accepted deleting ").append(fragmentType).append(" photos").toString(), hashmap, true);
                GalleryDoctorAnalyticsSender.sendDoctorUserActionAsync(getActionType(), j, k, l);
                getActivity().finish();
            }
        });
        messagedialog.show(getFragmentManager(), "dialog");
    }

    protected abstract void deleteCollection(Collection collection);

    public void deleteSelectedAssets()
    {
        deleteCollection(photoAdapter.getSelectedItems());
    }

    public boolean didUserMakeAction()
    {
        return userMadeAction;
    }

    protected abstract com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType getActionType();

    protected abstract String getFragmentName();

    public String getFragmentType()
    {
        return fragmentType;
    }

    public int getNumberOfPhotosCleaned()
    {
        return numberOfPhotosCleaned;
    }

    public int getNumberOfSelectedItems()
    {
        return photoAdapter.getTotalItemCount() - photoAdapter.getUnselectedItems().size();
    }

    public long getSizeOfPhotosCleaned()
    {
        return sizeOfPhotosCleaned;
    }

    public String getSizeOfSelectedItems()
    {
        return GeneralUtils.humanReadableByteCountForItems(photoAdapter.getSelectedItems());
    }

    protected void headerClick(int i, List list, int j)
    {
        throw new UnsupportedOperationException("No support for headers here!");
    }

    protected boolean isPhotos()
    {
        return true;
    }

    protected void itemClick(EditMediaItemView editmediaitemview)
    {
        Rect rect = new Rect();
        Point point = new Point();
        editmediaitemview.getGlobalVisibleRect(rect);
        rect.offset(-point.x, -point.y);
        Intent intent = new Intent(getActivity(), GalleryDoctorSelectionFullScreenActivity.class);
        intent.putExtra("animation_start", rect);
        intent.putExtra("ITEM_SELECTED", editmediaitemview.getItem().getId());
        intent.putExtra("fullscreen_source", com.flayvr.myrollshared.screens.fullscreen.FullScreenFragment.FullscreenSource.GALLERY);
        HashSet hashset = photoAdapter.getUnselectedItems();
        HashSet hashset1 = new HashSet();
        for(Iterator iterator = hashset.iterator(); iterator.hasNext(); hashset1.add(((MediaItem)iterator.next()).getId())) { }
        intent.putExtra("UNSELECTED_ITEMS", hashset1);
        addItemsData(intent, editmediaitemview.getItem());
        startActivityForResult(intent, 1);
        AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("viewed ").append(fragmentType).append(" in fullscreen").toString());
    }

    public void onActivityResult(int i, int j, Intent intent)
    {
        if(j == -1)
        {
            updateAdapterSelection((Collection)intent.getSerializableExtra("UNSELECTED_ITEMS"));
            updateActionText();
        }
        super.onActivityResult(i, j, intent);
    }

    public void onConfigurationChanged(Configuration configuration)
    {
        super.onConfigurationChanged(configuration);
        ((GridLayoutManager)grid.getLayoutManager()).setSpanCount(getResources().getInteger(R.integer.gd_span_size));
        grid.requestLayout();
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        source = getArguments().getInt("SOURCE");
        fragmentType = getFragmentName();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        deleteItem = menu.findItem(R.id.delete_action);
        super.onCreateOptionsMenu(menu, menuinflater);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        setHasOptionsMenu(true);
        View view = layoutinflater.inflate(R.layout.gallery_doctor_duplicate_photos_fragment, viewgroup, false);
        View view1 = view.findViewById(R.id.action_button);
        actionText = (TextView)view.findViewById(R.id.action_text);
        actionIcon = (ImageView)view.findViewById(R.id.action_icon);
        view1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sendClickDeleteAnalytics("bottom bar");
                deleteAction();
            }
        });
        grid = (RecyclerView)view.findViewById(R.id.gallery_grid);
        return view;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch(menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case android.R.id.home: 
            getActivity().onBackPressed();
            return true;

        case R.id.selectall: 
            selectAll();
            return true;

        case R.id.selectnone: 
            selectNone();
            return true;

        case R.id.delete_action: 
            deleteAction();
            break;
        }
        sendClickDeleteAnalytics("trash icon");
        return true;
    }

    public void onViewCreated(View view, Bundle bundle)
    {
        super.onViewCreated(view, bundle);
        GridLayoutManager gridlayoutmanager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.gd_span_size));
        grid.setLayoutManager(gridlayoutmanager);
        photoAdapter = createAdapter();
        photoAdapter.setOnItemClickListener(new GalleryDoctorSelectionItemClickListener());
        gridlayoutmanager.setSpanSizeLookup(photoAdapter.getSpanSizeLookup());
        grid.addItemDecoration(photoAdapter.getItemDecoration(getResources().getInteger(R.integer.gd_span_size), (int)GeneralUtils.pxFromDp(getActivity(), 3F)));
        grid.setAdapter(photoAdapter);
        grid.setRecyclerListener(new android.support.v7.widget.RecyclerView.RecyclerListener(){
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                if(holder.itemView.getClass().equals(EditMediaItemView.class))
                {
                    EditMediaItemView editmediaitemview = (EditMediaItemView)holder.itemView;
                    ImageLoaderAsyncTask imageloaderasynctask = (ImageLoaderAsyncTask)editmediaitemview.getTag();
                    if(imageloaderasynctask != null)
                    {
                        imageloaderasynctask.cancel(false);
                        editmediaitemview.setTag(null);
                    }
                    editmediaitemview.setImage(null);
                }
            }
        });
        updateActionText();
    }

    public void selectAll()
    {
        photoAdapter.removeUnselectedItems(photoAdapter.getUnselectedItems());
        updateActionText();
        photoAdapter.notifyDataSetChanged();
        AnalyticsUtils.trackEventWithKISS("chose to select all");
    }

    public void selectNone()
    {
        photoAdapter.addUnselectedItems(photoAdapter.getSelectedItems());
        updateActionText();
        photoAdapter.notifyDataSetChanged();
        AnalyticsUtils.trackEventWithKISS("chose to deselect all");
    }

    protected void selectionClick(EditMediaItemView editmediaitemview)
    {
        editmediaitemview.toggleSelect();
        updateActionText();
    }

    public void sendClickDeleteAnalytics(String s)
    {
        HashMap hashmap = new HashMap();
        hashmap.put((new StringBuilder()).append("deleting ").append(fragmentType).append(" source").toString(), s);
        hashmap.put((new StringBuilder()).append("total ").append(fragmentType).append(" to be deleted").toString(), (new StringBuilder()).append(photoAdapter.getSelectedItems().size()).append("").toString());
        hashmap.put((new StringBuilder()).append("total ").append(fragmentType).append(" to be kept").toString(), (new StringBuilder()).append(photoAdapter.getUnselectedItems().size()).append("").toString());
        AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("chose to delete ").append(fragmentType).toString(), hashmap, true);
    }

    protected void updateActionText()
    {
        int i = getNumberOfSelectedItems();
        if(i == 0)
        {
            if(isPhotos())
            {
                TextView textview5 = actionText;
                String s5 = getResources().getString(R.string.gallery_doctor_deletion_popup_heading_keep_all);
                Object aobj5[] = new Object[2];
                aobj5[0] = Integer.valueOf(getNumberOfSelectedItems());
                aobj5[1] = getSizeOfSelectedItems();
                textview5.setText(String.format(s5, aobj5));
            } else
            {
                TextView textview4 = actionText;
                String s4 = getResources().getString(R.string.gallery_doctor_deletion_popup_heading_keep_all_videos);
                Object aobj4[] = new Object[2];
                aobj4[0] = Integer.valueOf(getNumberOfSelectedItems());
                aobj4[1] = getSizeOfSelectedItems();
                textview4.setText(String.format(s4, aobj4));
            }
            actionIcon.setImageDrawable(getResources().getDrawable(R.drawable.gd_keep_all_black));
            if(deleteItem != null)
            {
                deleteItem.setEnabled(false);
                deleteItem.setIcon(getResources().getDrawable(R.drawable.gd_delete_white_dis));
            }
            return;
        }
        if(deleteItem != null)
        {
            deleteItem.setEnabled(true);
            deleteItem.setIcon(getResources().getDrawable(R.drawable.gd_delete_white));
        }
        actionIcon.setImageDrawable(getResources().getDrawable(R.drawable.gd_delete_black));
        if(i == 1)
        {
            if(isPhotos())
            {
                TextView textview3 = actionText;
                String s3 = getResources().getString(R.string.gallery_doctor_deletion_cta_single);
                Object aobj3[] = new Object[2];
                aobj3[0] = Integer.valueOf(getNumberOfSelectedItems());
                aobj3[1] = getSizeOfSelectedItems();
                textview3.setText(String.format(s3, aobj3));
                return;
            } else
            {
                TextView textview2 = actionText;
                String s2 = getResources().getString(R.string.gallery_doctor_deletion_cta_single_videos);
                Object aobj2[] = new Object[2];
                aobj2[0] = Integer.valueOf(getNumberOfSelectedItems());
                aobj2[1] = getSizeOfSelectedItems();
                textview2.setText(String.format(s2, aobj2));
                return;
            }
        }
        if(isPhotos())
        {
            TextView textview1 = actionText;
            String s1 = getResources().getString(R.string.gallery_doctor_similar_deletion_cta);
            Object aobj1[] = new Object[2];
            aobj1[0] = GeneralUtils.humanReadableNumber(getNumberOfSelectedItems());
            aobj1[1] = getSizeOfSelectedItems();
            textview1.setText(String.format(s1, aobj1));
            return;
        } else
        {
            TextView textview = actionText;
            String s = getResources().getString(R.string.gallery_doctor_similar_deletion_cta_videos);
            Object aobj[] = new Object[2];
            aobj[0] = GeneralUtils.humanReadableNumber(getNumberOfSelectedItems());
            aobj[1] = getSizeOfSelectedItems();
            textview.setText(String.format(s, aobj));
            return;
        }
    }

    protected abstract void updateAdapterSelection(Collection collection);

    protected abstract void updateDBItems();

    private class GalleryDoctorSelectionItemClickListener implements SelectionAdapter.OnItemClickListener
    {
        public void onHeaderClick(int i, List list, int j)
        {
            headerClick(i, list, j);
        }

        public void onItemClick(EditMediaItemView editmediaitemview, int i, SelectionAdapter.Source source1)
        {
            if(source1 == SelectionAdapter.Source.THUMBNAIL)
            {
                itemClick(editmediaitemview);
            } else
            if(source1 == SelectionAdapter.Source.SELECTION)
            {
                selectionClick(editmediaitemview);
                return;
            }
        }

        protected GalleryDoctorSelectionItemClickListener()
        {
            super();
        }
    }
}
