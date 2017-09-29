package com.flayvr.screens.duplicates;

import com.flayvr.screens.ItemsListActivity;
import com.flayvr.screens.SelectionPhotosFragment;

public class GalleryDoctorDuplicatePhotosActivity extends ItemsListActivity
{

    public GalleryDoctorDuplicatePhotosActivity()
    {
    }

    protected SelectionPhotosFragment getFragment(int i)
    {
        return DuplicatePhotosFragment.newInstance(i);
    }
}
