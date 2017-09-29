package com.flayvr.screens.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.managers.GalleryDoctorServicesProgress;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.flayvr.screens.cards.GalleryDoctorCardsActivity;
import com.flayvr.util.GalleryDoctorStatsUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import java.util.concurrent.Future;

public class GalleryDoctorDashboardFragment extends Fragment
{

    private static final String SOURCE = "SOURCE";
    private static final String TAG = GalleryDoctorDashboardFragment.class.getSimpleName();
    private ValueAnimator an;
    private ImageView cloud;
    private ViewGroup container;
    private DashboardBarsView dashboardBarsView;
    private View fabView;
    private TextView galleryHealth;
    private TextView galleryHealthRank;
    private DialChartView healthDial;
    private LayoutInflater inflater;
    private boolean isFirstTime;
    private GalleryDoctorDashboardFragmentListener listener;
    int maxPercentage;
    private com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaItemStat;
    private TextView progressText;
    private DashboardProgressContainerView progressView;
    private View rootView;
    private int source;
    private DialChartView spaceChart;
    private TextView spaceLeft;
    private ViewSwitcher switcher;

    public GalleryDoctorDashboardFragment()
    {
        maxPercentage = 0;
    }

    private void animateFab()
    {
        fabView.setTranslationX(300F);
        fabView.animate().translationX(0.0F).setStartDelay(1000L).start();
    }

    public static String getRankString(int i)
    {
        if(i >= 0 && i < 10)
        {
            return FlayvrApplication.getAppContext().getResources().getString(R.string.gallery_doctor_rank6);
        }
        if(10 <= i && i < 25)
        {
            return FlayvrApplication.getAppContext().getResources().getString(R.string.gallery_doctor_rank5);
        }
        if(25 <= i && i < 50)
        {
            return FlayvrApplication.getAppContext().getResources().getString(R.string.gallery_doctor_rank4);
        }
        if(50 <= i && i < 75)
        {
            return FlayvrApplication.getAppContext().getResources().getString(R.string.gallery_doctor_rank3);
        }
        if(75 <= i && i < 90)
        {
            return FlayvrApplication.getAppContext().getResources().getString(R.string.gallery_doctor_rank2);
        }
        if(90 <= i && i <= 100)
        {
            return FlayvrApplication.getAppContext().getResources().getString(R.string.gallery_doctor_rank1);
        } else
        {
            return null;
        }
    }

    private void initialize()
    {
        spaceLeft = (TextView)rootView.findViewById(R.id.space_left);
        galleryHealth = (TextView)rootView.findViewById(R.id.gallery_health);
        galleryHealthRank = (TextView)rootView.findViewById(R.id.gallery_health_rank);
        spaceChart = (DialChartView)rootView.findViewById(R.id.space_dial).findViewById(R.id.dial);
        healthDial = (DialChartView)rootView.findViewById(R.id.health_dial).findViewById(R.id.dial);
        healthDial.setWidth(getResources().getDimension(R.dimen.gd_health_gauge_thickness));
        healthDial.setColor(getResources().getColor(R.color.md_yellow_400));
        spaceChart.setWidth(getResources().getDimension(R.dimen.gd_space_gauge_thickness));
        spaceChart.setColor(getResources().getColor(R.color.md_grey_100));
        dashboardBarsView = (DashboardBarsView)rootView.findViewById(R.id.distribution_chart);
        fabView = rootView.findViewById(R.id.dashboard_fab);
        fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GalleryDoctorCardsActivity.class);
                intent.putExtra("ITEMS_SOURCE", source);
                startActivity(intent);
                AnalyticsUtils.trackEventWithKISS("chose to clean from dashboard");
            }
        });
        progressView = (DashboardProgressContainerView)rootView.findViewById(R.id.progress_view);
        View view;
        ToggleButton togglebutton;
        boolean flag;
        if(source == 2)
        {
            progressView.setImageResource(R.drawable.cloud_identifying_mask);
        } else
        {
            progressView.setImageResource(R.drawable.first_scan_mask);
        }
        switcher = (ViewSwitcher)rootView.findViewById(R.id.switcher);
        progressText = (TextView)rootView.findViewById(R.id.progress_text);
        spaceChart.setPercentage(0.0F);
        healthDial.setPercentage(0.0F);
        updateRank(0.0F);
        view = rootView.findViewById(R.id.dashboard_toggle);
        cloud = (ImageView)rootView.findViewById(R.id.dashboard_toggle_cloud);
        updateLoginStatus();
        togglebutton = (ToggleButton)view.findViewById(R.id.dashboard_toggle_button);
        if(source == 1)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        togglebutton.setSelected(flag);
        togglebutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                listener.toggleSource(source);
            }
        });
        isFirstTime = true;
    }

    private void makeAnimation()
    {
        final Future driveStat = GalleryDoctorStatsUtils.getDriveStat(source);
        try
        {
            if(!driveStat.isDone())
            {
                FlayvrApplication.runAction(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            final com.flayvr.util.GalleryDoctorStatsUtils.DriveStat stat = (com.flayvr.util.GalleryDoctorStatsUtils.DriveStat)driveStat.get();
                            (new Handler(Looper.getMainLooper())).post(new Runnable() {
                                @Override
                                public void run() {
                                    animateSpaceLeft(stat);
                                }
                            });
                        }
                        catch(Exception exception)
                        {
                            Log.e(GalleryDoctorDashboardFragment.TAG, exception.getMessage(), exception);
                        }
                    }
                });
            }else
                animateSpaceLeft((com.flayvr.util.GalleryDoctorStatsUtils.DriveStat)driveStat.get());
        }
        catch(Exception exception)
        {
            Log.e(TAG, exception.getMessage(), exception);
        }
        if(GalleryDoctorServicesProgress.didClassifierServiceFinish(source) && GalleryDoctorServicesProgress.didCVServiceFinish(source) && GalleryDoctorServicesProgress.didDuplicatesServiceFinish(source))
        {
            galleryHealthRank.setText("");
            if(isFirstTime)
            {
                isFirstTime = false;
                switcher.showNext();
            }
            animateHealth(driveStat);
        } else {
            startAnalysisTimer();
            galleryHealth.setText("...");
            galleryHealthRank.setText("");
            fabView.setVisibility(View.GONE);
            animateProgressUntilProcessingFinished();
        }

    }

    public static GalleryDoctorDashboardFragment newInstance(int i)
    {
        GalleryDoctorDashboardFragment gallerydoctordashboardfragment = new GalleryDoctorDashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SOURCE", i);
        gallerydoctordashboardfragment.setArguments(bundle);
        return gallerydoctordashboardfragment;
    }

    private void setRootView(Configuration configuration, boolean flag)
    {
        if(!getResources().getBoolean(R.bool.gd_dashboard_portrait_only))
        {
            switch(configuration.orientation)
            {
            default:
                return;

            case 1: // '\001'
                rootView = inflater.inflate(R.layout.gallery_doctor_dashboard_fragment, container, flag);
                return;

            case 2: // '\002'
                rootView = inflater.inflate(R.layout.gallery_doctor_dashboard_fragment_landscape, container, flag);
                break;
            }
        } else {
            rootView = inflater.inflate(R.layout.gallery_doctor_dashboard_fragment, container, flag);
        }
    }

    public void animateHealth(Future future)
    {
        ValueAnimator valueanimator = ValueAnimator.ofFloat(new float[] {
            0.0F, 1.0F
        });
        final float f = healthDial.getPercentage();
        final float f1 = GalleryDoctorStatsUtils.getGalleryHealth(mediaItemStat, future) / 100.0f;

        valueanimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                healthDial.setPercentage(animation.getAnimatedFraction() * (f1 - f) + f);
                galleryHealth.setText((new StringBuilder()).append((int)(100F * ((f1 - f) * animation.getAnimatedFraction() + f))).append("%").toString());
                updateRank((f1 - f) * animation.getAnimatedFraction() + f);
            }
        });
        valueanimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueanimator.setDuration(1000L);
        valueanimator.start();
    }

    public void animateProgressUntilProcessingFinished()
    {
        if(an != null && an.isRunning())
        {
            an.end();
        } else
        {
            an = ValueAnimator.ofFloat(new float[] {
                0.0F, 1.0F
            });
            an.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progressView.setProgress(animation.getAnimatedFraction());
                }
            });
            an.setInterpolator(new AccelerateDecelerateInterpolator());
            an.addListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(!isAdded())
                        return;
                    if(GalleryDoctorServicesProgress.didClassifierServiceFinish(source) && GalleryDoctorServicesProgress.didCVServiceFinish(source) && GalleryDoctorServicesProgress.didDuplicatesServiceFinish(source))
                    {
                        mediaItemStat = GalleryDoctorStatsUtils.getMediaItemStat(source);
                        dashboardBarsView.updateValues(mediaItemStat);
                        fabView.setVisibility(View.VISIBLE);
                        animateFab();
                        if(isFirstTime)
                        {
                            isFirstTime = false;
                            switcher.showNext();
                        }
                        Future future = GalleryDoctorStatsUtils.getDriveStat(source);
                        animateHealth(future);
                    } else {
                        an = null;
                        animateProgressUntilProcessingFinished();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            an.setDuration(2000L);
            an.start();
        }
    }

    public void animateSpaceLeft(com.flayvr.util.GalleryDoctorStatsUtils.DriveStat drivestat)
    {
        ValueAnimator valueanimator = ValueAnimator.ofFloat(new float[] {
            0.0F, 1.0F
        });
        final float startSpace = spaceChart.getPercentage();
        final float spaceVal = 1.0F - (float)drivestat.getFreeSpace() / (1.0F * (float)drivestat.getTotalSpace());
        final long freespace = drivestat.getFreeSpace();
        if(Math.round(startSpace * 10000F) != Math.round(spaceVal * 10000F))
        {
            valueanimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    spaceChart.setPercentage(animation.getAnimatedFraction() * (spaceVal - startSpace) + startSpace);
                    spaceLeft.setText(GeneralUtils.humanReadableByteCountIntro(freespace));
                }
            });
            valueanimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueanimator.setDuration(1000L);
            valueanimator.start();
        }
    }

    public int getSource()
    {
        return source;
    }

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        listener = (GalleryDoctorDashboardFragmentListener)activity;
    }

    public void onConfigurationChanged(Configuration configuration)
    {
        super.onConfigurationChanged(configuration);
        container.removeAllViews();
        setRootView(configuration, true);
        if(rootView != null)
        {
            initialize();
        }
        rootView.findViewById(R.id.dashboard_toggle).setVisibility(View.GONE);
        updateFragment();
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        container = viewgroup;
        inflater = layoutinflater;
        source = getArguments().getInt("SOURCE");
        if(rootView == null)
        {
            setRootView(getResources().getConfiguration(), false);
        }
        if(rootView != null)
        {
            initialize();
        }
        rootView.findViewById(R.id.dashboard_toggle).setVisibility(View.GONE);
        return rootView;
    }

    public void onStart()
    {
        super.onStart();
        updateFragment();
    }

    public void onViewCreated(View view, Bundle bundle)
    {
        super.onViewCreated(view, bundle);
        if(bundle == null)
        {
            animateFab();
        }
    }

    public void startAnalysisTimer()
    {
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(GalleryDoctorDashboardFragment.this != null && isAdded())
                {
                    updateAnalysisPercentage();
                    if(!GalleryDoctorServicesProgress.didClassifierServiceFinish(source) || !GalleryDoctorServicesProgress.didCVServiceFinish(source) || !GalleryDoctorServicesProgress.didDuplicatesServiceFinish(source))
                    {
                        startAnalysisTimer();
                    }
                }
            }
        }, 500L);
    }

    public void updateAnalysisPercentage()
    {
        int i = Math.max(GalleryDoctorServicesProgress.getTotalPrecentage(source), maxPercentage);
        maxPercentage = i;
        progressText.setText((new StringBuilder()).append(getResources().getString(R.string.gallery_doctor_analyzing_gallery)).append("... ").append(i).append("%").toString());
    }

    public void updateFragment()
    {
        mediaItemStat = GalleryDoctorStatsUtils.getMediaItemStat(source);
        dashboardBarsView.updateValues(mediaItemStat);
        makeAnimation();
    }

    public void updateLoginStatus()
    {
        if(PicasaSessionManager.getInstance().hasUser())
        {
            cloud.setImageResource(R.drawable.cloud_toggle_cloud);
        } else {
            cloud.setImageResource(R.drawable.cloud_toggle_cloud_setup);
        }
    }

    public void updateRank(float f)
    {
        if(isAdded())
        {
            float f1 = 100F * f;
            galleryHealthRank.setText(getRankString((int)f1));
        }
    }

    public interface GalleryDoctorDashboardFragmentListener
    {
        public abstract void toggleSource(int i);
    }
}
