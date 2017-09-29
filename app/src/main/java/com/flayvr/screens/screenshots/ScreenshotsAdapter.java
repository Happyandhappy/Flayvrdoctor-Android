package com.flayvr.screens.screenshots;

import android.support.v7.widget.RecyclerView;
import com.flayvr.screens.bad.GalleryDoctorBadPhotosAdapter;
import java.util.List;

public class ScreenshotsAdapter extends GalleryDoctorBadPhotosAdapter
{

    public ScreenshotsAdapter(RecyclerView recyclerview, List list)
    {
        super(recyclerview, list);
    }

    public boolean shoudShowHint()
    {
        return false;
    }
}
