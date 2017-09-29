package com.flayvr.screens.screenshots;

import com.flayvr.screens.ItemsListActivity;
import com.flayvr.screens.SelectionPhotosFragment;

public class ScreenshotsActivity extends ItemsListActivity
{

    public ScreenshotsActivity()
    {
    }

    protected SelectionPhotosFragment getFragment(int i)
    {
        return ScreenshotsFragment.newInstance(i);
    }
}
