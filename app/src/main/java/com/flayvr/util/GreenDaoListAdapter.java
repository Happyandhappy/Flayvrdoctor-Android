package com.flayvr.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import de.greenrobot.dao.query.LazyList;

public abstract class GreenDaoListAdapter extends BaseAdapter
{

    protected Context context;
    protected LazyList lazyList;

    public GreenDaoListAdapter(Context context1, LazyList lazylist)
    {
        lazyList = lazylist;
        context = context1;
    }

    public abstract void bindView(View view, Context context1, Object obj);

    public int getCount()
    {
        if(lazyList != null)
        {
            return lazyList.size();
        } else
        {
            return 0;
        }
    }

    public View getDropDownView(int i, View view, ViewGroup viewgroup)
    {
        if(lazyList != null)
        {
            Object obj = lazyList.get(i);
            if(view == null)
            {
                view = newDropDownView(context, obj, viewgroup);
            }
            bindView(view, context, obj);
            return view;
        } else
        {
            return null;
        }
    }

    protected abstract long getIdForItem(Object obj);

    public Object getItem(int i)
    {
        if(lazyList != null)
        {
            return lazyList.get(i);
        } else
        {
            return null;
        }
    }

    public long getItemId(int i)
    {
        long l = 0L;
        if(lazyList != null)
        {
            Object obj = lazyList.get(i);
            if(obj != null)
            {
                l = getIdForItem(obj);
            }
        }
        return l;
    }

    public LazyList getLazyList()
    {
        return lazyList;
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        if(lazyList == null)
        {
            throw new IllegalStateException("this should only be called when lazylist is populated");
        }
        Object obj = lazyList.get(i);
        if(obj == null)
        {
            throw new IllegalStateException((new StringBuilder()).append("Item at position ").append(i).append(" is null").toString());
        }
        if(view == null)
        {
            view = newView(context, obj, viewgroup);
        }
        bindView(view, context, obj);
        return view;
    }

    public boolean hasStableIds()
    {
        return true;
    }

    public View newDropDownView(Context context1, Object obj, ViewGroup viewgroup)
    {
        return newView(context1, obj, viewgroup);
    }

    public abstract View newView(Context context1, Object obj, ViewGroup viewgroup);

    public void setLazyList(LazyList lazylist)
    {
        lazyList = lazylist;
    }
}
