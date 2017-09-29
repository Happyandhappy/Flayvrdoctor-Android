package com.flayvr.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.*;

import com.flayvr.doctor.R;

public class StyledProgressBarView extends RelativeLayout
{

    public static final int DEFAUL_DURATION = 1000;
    private float currProgress;
    private ImageView doneIcon;
    private TextView doneText;
    private View doneView;
    private TextView progressText;
    private View progressTextFrame;
    private View progressView;

    public StyledProgressBarView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init(attributeset);
    }

    public StyledProgressBarView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init(attributeset);
    }

    private void init(AttributeSet attributeset)
    {
        TypedArray typedarray;
        inflate(getContext(), R.layout.styled_progress_bar, this);
        progressView = findViewById(R.id.progess_view);
        progressText = (TextView)findViewById(R.id.progess_text);
        progressTextFrame = findViewById(R.id.progess_text_frame);
        doneView = findViewById(R.id.done_text_frame);
        doneText = (TextView)findViewById(R.id.done_text);
        doneIcon = (ImageView)findViewById(R.id.done_icon);
        typedarray = getContext().getTheme().obtainStyledAttributes(attributeset, R.styleable.StyledProgressBarView, 0, 0);
        setText(typedarray.getText(R.styleable.StyledProgressBarView_text));
        setColor(typedarray.getColor(R.styleable.StyledProgressBarView_progressColor, -1));
        typedarray.recycle();
        currProgress = 0.0F;
    }

    public void animateProgress(float f)
    {
        animateProgress(f, 1000);
    }

    public void animateProgress(float f, int i)
    {
        float af[] = new float[2];
        af[0] = currProgress;
        af[1] = f;
        ValueAnimator valueanimator = ValueAnimator.ofFloat(af);
        valueanimator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setProgress(((Float)valueAnimator.getAnimatedValue()).floatValue());
            }
        });
        valueanimator.setDuration(i);
        valueanimator.setInterpolator(new LinearInterpolator());
        valueanimator.start();
    }

    public void setColor(int i)
    {
        progressView.setBackgroundColor(i);
        doneIcon.setColorFilter(i);
    }

    public void setDone(boolean flag)
    {
        if(flag)
        {
            doneView.setVisibility(View.VISIBLE);
            progressTextFrame.setVisibility(View.GONE);
            return;
        } else
        {
            progressTextFrame.setVisibility(View.VISIBLE);
            doneView.setVisibility(View.GONE);
            return;
        }
    }

    public void setDoneText(CharSequence charsequence)
    {
        doneText.setText(charsequence);
    }

    public void setProgress(float f)
    {
        currProgress = f;
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)progressView.getLayoutParams();
        layoutparams.width = (int)((float)getWidth() * currProgress);
        progressView.setLayoutParams(layoutparams);
    }

    public void setText(CharSequence charsequence)
    {
        progressText.setText(charsequence);
    }
}
