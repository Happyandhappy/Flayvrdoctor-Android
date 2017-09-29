package com.flayvr.screens.review;

import android.os.Bundle;
import com.flayvr.screens.ForReviewFragment;
import com.flayvr.util.GalleryDoctorDBHelper;
import java.util.List;

public class PhotosForReviewFragment extends ForReviewFragment
{

    public PhotosForReviewFragment()
    {
    }

    public static ForReviewFragment newInstance(int i)
    {
        PhotosForReviewFragment photosforreviewfragment = new PhotosForReviewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SOURCE", i);
        photosforreviewfragment.setArguments(bundle);
        return photosforreviewfragment;
    }

    protected long getItemCount(int i)
    {
        return GalleryDoctorDBHelper.getPhotosForReviewCount(i);
    }

    protected List getItemList(int i)
    {
        return GalleryDoctorDBHelper.getPhotosForReviewLazy(i);
    }
}
