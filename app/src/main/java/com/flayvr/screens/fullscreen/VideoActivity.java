package com.flayvr.screens.fullscreen;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.screens.fullscreen.VideoFragment;
import com.flayvr.util.GalleryDoctorDefaultActivity;

public class VideoActivity extends GalleryDoctorDefaultActivity
{

    private VideoFragment fragment;

    public VideoActivity()
    {
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.empty_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragment = new VideoFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
}
