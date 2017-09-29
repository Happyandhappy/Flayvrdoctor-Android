package com.flayvr.screens.intro;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.flayvr.screens.dashboard.PieChartView;
import com.nineoldandroids.animation.ValueAnimator;

public class GDEndIntroTextFragment extends IntroTextFragment
{

    boolean isReset;

    public GDEndIntroTextFragment()
    {
    }

    public static IntroTextFragment init(int i)
    {
        GDEndIntroTextFragment gdendintrotextfragment = new GDEndIntroTextFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("title", i);
        gdendintrotextfragment.setArguments(bundle);
        return gdendintrotextfragment;
    }

    public int layout()
    {
        return R.layout.gallery_doctor_intro_text_end;
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = super.onCreateView(layoutinflater, viewgroup, bundle);
        PieChartView piechartview = (PieChartView)view.findViewById(R.id.dial);
        TextView textview = (TextView)view.findViewById(R.id.button);
        piechartview.setColor(getResources().getColor(R.color.md_yellow_800));
        piechartview.setEmptyColor(getResources().getColor(R.color.md_grey_200));
        piechartview.setWidth(GeneralUtils.pxFromDp(getActivity(), 12F));
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        reset(view);
        return view;
    }

    public void reset()
    {
        if(getView() != null)
        {
            reset(getView());
        }
    }

    public void reset(View view)
    {
        if(!isReset)
        {
            isReset = true;
            ((PieChartView)view.findViewById(R.id.dial)).setPercentage(0.37F);
        }
    }

    public void startAnimation()
    {
        if(isReset)
        {
            isReset = false;
            final PieChartView spaceChart = (PieChartView)getView().findViewById(R.id.dial);
            ValueAnimator valueanimator = ValueAnimator.ofFloat(new float[] {
                0.0F, 1.0F
            });
            valueanimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    spaceChart.setPercentage(0.37F + -0.26F * animation.getAnimatedFraction());
                }
            });
            valueanimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueanimator.setStartDelay(250L);
            valueanimator.setDuration(1000L);
            valueanimator.start();
        }
    }
}
