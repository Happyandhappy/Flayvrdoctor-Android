package com.flayvr.screens.intro;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.util.GalleryDoctorDefaultActivity;
import com.viewpagerindicator.CirclePageIndicator;
import java.util.ArrayList;
import java.util.List;

public class GDIntroActivity extends GalleryDoctorDefaultActivity
{

    private IntroAdapter adapter;
    private int colors[];
    private List fragments;
    private ViewPager pager;

    public GDIntroActivity()
    {
    }

    public int getIntFromColor(int i, int j, int k)
    {
        int l = 0xff0000 & i << 16;
        int i1 = 0xff00 & j << 8;
        return k & 0xff | (i1 | (l | 0xff000000));
    }

    public void onBackPressed()
    {
        finish();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        getSupportActionBar().hide();
        int ai[] = new int[5];
        ai[0] = getResources().getColor(R.color.md_grey_300);
        ai[1] = getResources().getColor(R.color.md_grey_300);
        ai[2] = getResources().getColor(R.color.md_grey_300);
        ai[3] = getResources().getColor(R.color.md_grey_300);
        ai[4] = getResources().getColor(R.color.md_grey_300);
        colors = ai;
        setContentView(R.layout.intro_layout);
        pager = (ViewPager)findViewById(R.id.intro_pager);
        fragments = new ArrayList();
        fragments.add(GDStartIntroTextFragment.init(R.drawable.intro_001, R.string.gallery_doctor_name, R.string.gallery_doctor_intro1_text));
        fragments.add(GDIntroTextFragment.init(R.drawable.intro_002, R.string.gallery_doctor_intro2_heading));
        fragments.add(GDIntroTextFragment.init(R.drawable.intro_003, R.string.gallery_doctor_intro3_heading));
        fragments.add(GDEndIntroTextFragment.init(R.string.gallery_doctor_intro4_heading));
        adapter = new IntroAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        CirclePageIndicator circlepageindicator = (CirclePageIndicator)findViewById(R.id.intro_indications);
        circlepageindicator.setPageColor(getResources().getColor(R.color.white));
        circlepageindicator.setStrokeColor(getResources().getColor(R.color.white));
        circlepageindicator.setFillColor(getResources().getColor(R.color.md_grey_500));
        circlepageindicator.setViewPager(pager);
        circlepageindicator.setOnPageChangeListener(new IntroPageChangeListener());
        AnalyticsUtils.trackEventWithKISS("viewed intro");
    }

    protected void onDestroy()
    {
        AnalyticsUtils.trackEventWithKISS("finished intro");
        super.onDestroy();
    }

    protected void onStart()
    {
        super.onStart();
    }

    protected void onStop()
    {
        super.onStop();
    }

    public void skip()
    {
        if(1 + pager.getCurrentItem() < fragments.size())
            pager.setCurrentItem(1 + pager.getCurrentItem(), true);
        else
            finish();
    }

    private class IntroAdapter extends FragmentPagerAdapter
    {
        public int getCount()
        {
            return fragments.size();
        }

        public Fragment getItem(int i)
        {
            return (Fragment)fragments.get(i);
        }

        public IntroAdapter(FragmentManager fragmentmanager)
        {
            super(fragmentmanager);
        }
    }


    private class IntroPageChangeListener implements android.support.v4.view.ViewPager.OnPageChangeListener
    {

        public void onPageScrollStateChanged(int i)
        {
            if(i == 0 && pager.getCurrentItem() == 3)
            {
                ((GDEndIntroTextFragment)fragments.get(3)).startAnimation();
            } else
            if(i == 0 && pager.getCurrentItem() < 3 || pager.getCurrentItem() < 2)
            {
                ((GDEndIntroTextFragment)fragments.get(3)).reset();
                return;
            }
        }

        public void onPageScrolled(int i, float f, int j)
        {
            int k = colors[i];
            int l = 0xff & k >> 16;
            int i1 = 0xff & k >> 8;
            int j1 = 0xff & k >> 0;
            if(i + 1 < colors.length)
            {
                int k1 = colors[i + 1];
                int l1 = 0xff & k1 >> 16;
                int i2 = 0xff & k1 >> 8;
                int j2 = 0xff & k1 >> 0;
                l = (int)((float)l + f * (float)(l1 - l));
                i1 = (int)((float)i1 + f * (float)(i2 - i1));
                j1 = (int)((float)j1 + f * (float)(j2 - j1));
            }
            pager.setBackgroundColor(getIntFromColor(l, i1, j1));
        }

        public void onPageSelected(int i)
        {
            AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("viewed intro page ").append(i).toString());
        }

        public IntroPageChangeListener()
        {
            super();
        }
    }

}
