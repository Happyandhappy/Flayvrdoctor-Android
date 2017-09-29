package com.flayvr.screens.intro;

import android.os.Bundle;

import com.flayvr.doctor.R;

public class GDIntroTextFragment extends IntroTextFragment
{

    public GDIntroTextFragment()
    {
    }

    public static IntroTextFragment init(int i, int j)
    {
        GDIntroTextFragment gdintrotextfragment = new GDIntroTextFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("back", i);
        bundle.putInt("title", j);
        gdintrotextfragment.setArguments(bundle);
        return gdintrotextfragment;
    }

    public int layout()
    {
        return R.layout.gallery_doctor_intro_text;
    }
}
