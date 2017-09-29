package com.flayvr.screens.bad;

import com.flayvr.screens.ItemsListActivity;
import com.flayvr.screens.SelectionPhotosFragment;

public class GalleryDoctorBadPhotosActivity extends ItemsListActivity
{

    public GalleryDoctorBadPhotosActivity()
    {
    }

    protected SelectionPhotosFragment getFragment(int i)
    {
        return BadPhotosFragment.newInstance(i);
    }
}
