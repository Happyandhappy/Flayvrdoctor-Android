package com.flayvr.screens.intro;

import android.os.Bundle;
import android.view.View;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.AnalyticsUtils;

public class GDStartIntroTextFragment extends IntroTextFragment
{

    public GDStartIntroTextFragment()
    {
    }

    public static IntroTextFragment init(int i, int j, int k)
    {
        GDStartIntroTextFragment gdstartintrotextfragment = new GDStartIntroTextFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("back", i);
        bundle.putInt("title", j);
        bundle.putInt("desc", k);
        gdstartintrotextfragment.setArguments(bundle);
        return gdstartintrotextfragment;
    }

    public int layout()
    {
        return R.layout.gallery_doctor_intro_text_start;
    }

    public void onViewCreated(View view, Bundle bundle)
    {
        super.onViewCreated(view, bundle);
        view.findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnalyticsUtils.trackEventWithKISS("skipped intro");
                getActivity().finish();
            }
        });
    }
}
