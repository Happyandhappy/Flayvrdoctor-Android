package com.flayvr.myrollshared.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MultiListenersRecycleView extends RecyclerView
{

    private OnScrollListenerComposite listener;

    public MultiListenersRecycleView(Context context)
    {
        super(context);
        init();
    }

    public MultiListenersRecycleView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init();
    }

    public MultiListenersRecycleView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init();
    }

    private void init()
    {
        listener = new OnScrollListenerComposite(null);
        setOnScrollListener(listener);
    }

    public void addOnScrollListener(android.support.v7.widget.RecyclerView.OnScrollListener onscrolllistener)
    {
        listener.addListener(onscrolllistener);
    }

    public void removeOnScrollListener(android.support.v7.widget.RecyclerView.OnScrollListener onscrolllistener)
    {
        listener.removeListener(onscrolllistener);
    }

    public void setOnScrollListener(android.support.v7.widget.RecyclerView.OnScrollListener onscrolllistener)
    {
        if(onscrolllistener instanceof OnScrollListenerComposite)
        {
            super.setOnScrollListener(onscrolllistener);
        } else
        {
            throw new UnsupportedOperationException("Use addListener to add listeners to this view");
        }
    }

    private class OnScrollListenerComposite extends android.support.v7.widget.RecyclerView.OnScrollListener
    {

        private List<RecyclerView.OnScrollListener> listeners;

        public void addListener(android.support.v7.widget.RecyclerView.OnScrollListener onscrolllistener)
        {
            listeners.add(onscrolllistener);
        }

        public void onScrollStateChanged(RecyclerView recyclerview, int i)
        {
            for(Iterator iterator = listeners.iterator(); iterator.hasNext(); ((android.support.v7.widget.RecyclerView.OnScrollListener)iterator.next()).onScrollStateChanged(recyclerview, i)) { }
        }

        public void onScrolled(RecyclerView recyclerview, int i, int j)
        {
            for(Iterator iterator = listeners.iterator(); iterator.hasNext(); ((android.support.v7.widget.RecyclerView.OnScrollListener)iterator.next()).onScrolled(recyclerview, i, j)) { }
        }

        public void removeListener(android.support.v7.widget.RecyclerView.OnScrollListener onscrolllistener)
        {
            listeners.remove(onscrolllistener);
        }

        private OnScrollListenerComposite()
        {
            super();
            listeners = new LinkedList();
        }

        OnScrollListenerComposite(MultiListenersRecycleView paramMultiListenersRecycleView)
        {
        }
    }

}
