package com.flayvr.myrollshared.screens.fullscreen;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.MediaController;
import android.widget.VideoView;

import com.flayvr.doctor.R;

public class VideoFragment extends Fragment
{

    private static final int AUTO_HIDE_DELAY_MILLIS = 2000;
    public static final String ITEM_URI = "ITEM_URI";
    Handler mHideHandler;
    Runnable mHideRunnable;
    private int stopPosition;
    private Uri uri;
    private VideoView video;

    public VideoFragment()
    {
        stopPosition = 0;
        mHideHandler = new Handler();
        mHideRunnable = new Runnable() {
            @Override
            public void run() {
                ActionBar actionbar = getActionBar();
                if(actionbar != null)
                    actionbar.hide();
            }
        };
    }

    private void delayedHide(int i)
    {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, i);
    }

    private ActionBar getActionBar()
    {
        ActionBarActivity actionbaractivity = (ActionBarActivity)getActivity();
        if(actionbaractivity != null)
        {
            return actionbaractivity.getSupportActionBar();
        } else
        {
            return null;
        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        ActionBar actionbar = getActionBar();
        if(actionbar != null)
        {
            actionbar.hide();
        }
        if(bundle != null)
            uri = (Uri)bundle.getParcelable("ITEM_URI");
        else if(getActivity().getIntent().getExtras() != null)
            uri = (Uri)getActivity().getIntent().getExtras().getParcelable("ITEM_URI");
        if(uri == null)
            getActivity().finish();
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.activity_video, null);
        view.findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionBar actionbar = getActionBar();
                if(actionbar != null)
                {
                    if(actionbar.isShowing())
                        actionbar.hide();
                    else {
                        actionbar.show();
                        delayedHide(2000);
                    }
                }
            }
        });
        video = (VideoView)view.findViewById(R.id.video_view_test);
        MediaController mediacontroller = new MediaController(getActivity());
        mediacontroller.setAnchorView(video);
        video.setMediaController(mediacontroller);
        video.setVideoURI(uri);
        final View placeholder = view.findViewById(R.id.placeholder);
        video.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                video.start();
                placeholder.setVisibility(View.GONE);
            }
        });
        return view;
    }

    public void onDestroy()
    {
        video.stopPlayback();
        super.onDestroy();
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch(menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case android.R.id.home: 
            getActivity().finish();
            break;
        }
        return true;
    }

    public void onPause()
    {
        super.onPause();
        stopPosition = video.getCurrentPosition();
        video.pause();
    }

    public void onResume()
    {
        super.onResume();
        video.seekTo(stopPosition);
        video.start();
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("ITEM_URI", uri);
    }

    public void onViewCreated(View view, Bundle bundle)
    {
        super.onViewCreated(view, bundle);
        delayedHide(2000);
    }
}
