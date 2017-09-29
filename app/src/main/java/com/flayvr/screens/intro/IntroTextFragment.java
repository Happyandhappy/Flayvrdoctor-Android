package com.flayvr.screens.intro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import com.flayvr.doctor.R;

public abstract class IntroTextFragment extends Fragment
{

    private int back;
    private int desc;
    private int title;

    public IntroTextFragment()
    {
    }

    public abstract int layout();

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        int i;
        int j;
        Bundle bundle1;
        int k;
        if(getArguments() != null)
        {
            i = getArguments().getInt("title");
        } else
        {
            i = 0;
        }
        title = i;
        if(getArguments() != null)
        {
            j = getArguments().getInt("desc");
        } else
        {
            j = 0;
        }
        desc = j;
        bundle1 = getArguments();
        k = 0;
        if(bundle1 != null)
        {
            k = getArguments().getInt("back");
        }
        back = k;
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        View view = layoutinflater.inflate(layout(), viewgroup, false);
        ImageView imageview = (ImageView)view.findViewById(R.id.intro_back);
        if(imageview != null)
        {
            imageview.setImageResource(back);
        }
        TextView textview = (TextView)view.findViewById(R.id.intro_title);
        if(textview != null)
        {
            textview.setText(title);
        }
        TextView textview1 = (TextView)view.findViewById(R.id.intro_text);
        if(textview1 != null)
        {
            textview1.setText(desc);
        }
        return view;
    }
}
