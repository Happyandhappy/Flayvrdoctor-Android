package com.flayvr.screens;

import com.flayvr.myrollshared.views.itemview.EditMediaItemView;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class SelectionAdapter extends android.support.v7.widget.RecyclerView.Adapter
{

    protected OnItemClickListener mItemClickListener;
    protected HashSet mUnselectedAssets;

    public SelectionAdapter()
    {
        mUnselectedAssets = new HashSet();
    }

    public void addUnselectedItem(Object obj)
    {
        mUnselectedAssets.add(obj);
    }

    public void addUnselectedItems(Collection collection)
    {
        mUnselectedAssets.addAll(collection);
    }

    public abstract HashSet getSelectedItems();

    public HashSet getUnselectedItems()
    {
        return mUnselectedAssets;
    }

    public void removeUnselectedItem(Object obj)
    {
        mUnselectedAssets.remove(obj);
    }

    public void removeUnselectedItems(Collection collection)
    {
        mUnselectedAssets.removeAll(collection);
    }

    public void setOnItemClickListener(OnItemClickListener onitemclicklistener)
    {
        mItemClickListener = onitemclicklistener;
    }


    public interface OnItemClickListener
    {
        public abstract void onHeaderClick(int i, List list, int j);

        public abstract void onItemClick(EditMediaItemView editmediaitemview, int i, Source source);
    }

    public enum Source {
        SELECTION("SELECTION", 0),
        THUMBNAIL("THUMBNAIL", 1),
        CLEAN_FOLDER("CLEAN_FOLDER", 2);

        Source(String s, int i)
        {
        }
    }
}
