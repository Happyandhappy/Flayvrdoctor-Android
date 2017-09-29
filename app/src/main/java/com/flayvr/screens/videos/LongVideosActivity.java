package com.flayvr.screens.videos;

import com.flayvr.screens.ItemsListActivity;
import com.flayvr.screens.SelectionPhotosFragment;

public class LongVideosActivity extends ItemsListActivity
{

    public LongVideosActivity()
    {
    }

    protected SelectionPhotosFragment getFragment(int i)
    {
        return LongVideosFragment.newInstance(i);
    }
}
