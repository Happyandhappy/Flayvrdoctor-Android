package com.flayvr.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flayvr.myrollshared.views.itemview.EditMediaItemView;
import com.flayvr.screens.SelectionAdapter;
import java.util.HashMap;
import java.util.Map;

public abstract class SectionedGridRecyclerViewAdapter extends SelectionAdapter
{

    protected static final int HEADER_VIEW_TYPE = 1;
    protected static final int HINT_VIEW_TYPE = 2;
    protected static final int ITEM_VIEW_TYPE = 0;
    private static final String TAG = SectionedGridRecyclerViewAdapter.class.getSimpleName();
    protected Map position2result;
    protected RecyclerView recyclerView;

    public SectionedGridRecyclerViewAdapter(RecyclerView recyclerview)
    {
        recyclerView = recyclerview;
        position2result = new HashMap();
    }

    private PositionResult getHeaderAndPosition(int i)
    {
        int j = 1;
        int k = 0;
        if(shoudShowHint() && i == 0)
        {
            return new PositionResult(-1, i);
        }
        if(getHeaderItemCount() == -1)
        {
            if(!shoudShowHint())
            {
                j = 0;
            }
            return new PositionResult(i, i - j);
        }
        if(!position2result.containsKey(Integer.valueOf(i)))
        {
            int l;
            int i1;
            if(shoudShowHint())
            {
                l = j;
            } else
            {
                l = 0;
            }
            for(i1 = i - l; i1 >= 1 + getItemCountForHeader(k) && k < getHeaderItemCount(); k++)
            {
                i1 -= 1 + getItemCountForHeader(k);
            }

            if(i1 < j)
            {
                position2result.put(Integer.valueOf(i), new PositionResult(k));
            } else
            {
                position2result.put(Integer.valueOf(i), new PositionResult(k, i1 - 1));
            }
        }
        return (PositionResult)position2result.get(Integer.valueOf(i));
    }

    private int getPosition(android.support.v7.widget.RecyclerView.ViewHolder viewholder)
    {
        PositionResult positionresult = getHeaderAndPosition(viewholder.getPosition());
        if(positionresult.isHeader)
        {
            return positionresult.headerIndex;
        } else
        {
            return positionresult.itemIndexInHeader;
        }
    }

    public void dismissHint()
    {
        position2result = new HashMap();
        notifyItemRemoved(0);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.invalidateItemDecorations();
            }
        });
    }

    public int getHeaderItemCount()
    {
        return -1;
    }

    public int getHeaderPosition(android.support.v7.widget.RecyclerView.ViewHolder viewholder)
    {
        return getPosition(viewholder);
    }

    public int getHeaderSpanCount(int i)
    {
        return 1;
    }

    public int getHeaderViewType(int i)
    {
        return 1;
    }

    public int getHintSpanCount()
    {
        return 1;
    }

    public int getHintViewType(int i)
    {
        return 2;
    }

    public int getItemCount()
    {
        int j;
        boolean flag;
        int k;
        if(getHeaderItemCount() == -1)
        {
            j = getItemCountForHeader(0);
        } else
        {
            int i = 0;
            j = 0;
            while(i < getHeaderItemCount()) 
            {
                j += 1 + getItemCountForHeader(i);
                i++;
            }
        }
        flag = shoudShowHint();
        k = 0;
        if(flag)
        {
            k = 1;
        }
        return j + k;
    }

    public abstract int getItemCountForHeader(int i);

    public android.support.v7.widget.RecyclerView.ItemDecoration getItemDecoration(final int spanCount, final int margin)
    {
        return new android.support.v7.widget.RecyclerView.ItemDecoration(){
            @Override
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerview, android.support.v7.widget.RecyclerView.State state)
            {
                if(view instanceof EditMediaItemView)
                {
                    int i = recyclerview.getChildPosition(view);
                    PositionResult positionresult = getHeaderAndPosition(i);
                    int j = positionresult.itemIndexInHeader;
                    int k;
                    int l;
                    int i1;
                    int j1;
                    if(j % spanCount == 0)
                    {
                        k = margin;
                    } else
                    {
                        k = margin / 2;
                    }
                    if(j < spanCount)
                    {
                        l = margin;
                    } else
                    {
                        l = margin / 2;
                    }
                    if(j % spanCount == -1 + spanCount)
                    {
                        i1 = margin;
                    } else
                    {
                        i1 = margin / 2;
                    }
                    if(j >= getItemCountForHeader(positionresult.headerIndex) - spanCount)
                    {
                        j1 = margin;
                    } else
                    {
                        j1 = margin / 2;
                    }
                    rect.set(k, l, i1, j1);
                    return;
                } else
                {
                    super.getItemOffsets(rect, view, recyclerview, state);
                    return;
                }
            }
        };
    }

    public int getItemPosition(android.support.v7.widget.RecyclerView.ViewHolder viewholder)
    {
        return getPosition(viewholder);
    }

    public int getItemSpanCountForHeader(int i, int j)
    {
        return 1;
    }

    public int getItemViewType(int i)
    {
        if(i == 0 && shoudShowHint())
        {
            return getHintViewType(i);
        }
        PositionResult positionresult = getHeaderAndPosition(i);
        if(positionresult.isHeader)
        {
            return getHeaderViewType(positionresult.headerIndex);
        } else
        {
            return getItemViewTypeInHeader(positionresult.headerIndex, positionresult.itemIndexInHeader);
        }
    }

    public int getItemViewTypeInHeader(int i, int j)
    {
        return 0;
    }

    public android.support.v7.widget.GridLayoutManager.SpanSizeLookup getSpanSizeLookup()
    {
        return new android.support.v7.widget.GridLayoutManager.SpanSizeLookup(){

            @Override
            public int getSpanSize(int i) {
                if(i == 0 && shoudShowHint())
                {
                    return getHintSpanCount();
                }
                PositionResult positionresult = getHeaderAndPosition(i);
                if(positionresult.isHeader)
                {
                    return getHeaderSpanCount(positionresult.headerIndex);
                } else
                {
                    return getItemSpanCountForHeader(positionresult.headerIndex, positionresult.itemIndexInHeader);
                }
            }
        };
    }

    public int getTotalItemCount()
    {
        int i = 0;
        int j;
        if(getHeaderItemCount() > 0)
        {
            j = 0;
            for(; i < getHeaderItemCount(); i++)
            {
                j += getItemCountForHeader(i);
            }

        } else
        {
            j = getItemCountForHeader(0);
        }
        return j;
    }

    public void onBindHeaderViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i)
    {
    }

    public void onBindHintViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i)
    {
    }

    public abstract void onBindItemViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i, int j);

    public void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i)
    {
        if(i == 0 && shoudShowHint())
        {
            onBindHintViewHolder(viewholder, i);
            return;
        }
        PositionResult positionresult = getHeaderAndPosition(i);
        if(positionresult.isHeader)
        {
            onBindHeaderViewHolder(viewholder, positionresult.headerIndex);
            return;
        } else
        {
            onBindItemViewHolder(viewholder, positionresult.headerIndex, positionresult.itemIndexInHeader);
            return;
        }
    }

    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
    {
        if(i == 0)
        {
            return onCreateViewHolderForItem(viewgroup);
        }
        if(i == 1)
        {
            return onCreateViewHolderForHeader(viewgroup);
        }
        if(i == 2)
        {
            return onCreateViewHolderForHint(viewgroup);
        } else
        {
            Log.w(TAG, (new StringBuilder()).append("Unkwon view type: ").append(i).toString());
            return null;
        }
    }

    protected android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolderForHeader(ViewGroup viewgroup)
    {
        return null;
    }

    protected android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolderForHint(ViewGroup viewgroup)
    {
        return null;
    }

    protected abstract android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolderForItem(ViewGroup viewgroup);

    public abstract boolean shoudShowHint();



    private class PositionResult
    {

        public int headerIndex;
        public boolean isHeader;
        public int itemIndexInHeader;

        public PositionResult(int i)
        {
            super();
            headerIndex = i;
            isHeader = true;
        }

        public PositionResult(int i, int j)
        {
            super();
            headerIndex = i;
            itemIndexInHeader = j;
            isHeader = false;
        }
    }
}
