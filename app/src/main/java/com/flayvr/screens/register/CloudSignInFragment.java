package com.flayvr.screens.register;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;

import com.flayvr.doctor.R;

public class CloudSignInFragment extends Fragment
{

    private static final String TAG = CloudSignInFragment.class.getSimpleName();
    private static final String WITH_BACK = "back";
    private static final String WITH_SKIP = "skip";
    private CloudSignInFragmentListener listener;

    public CloudSignInFragment()
    {
    }

    public static CloudSignInFragment newInstance(boolean flag, boolean flag1)
    {
        CloudSignInFragment cloudsigninfragment = new CloudSignInFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(WITH_SKIP, flag);
        bundle.putBoolean(WITH_BACK, flag1);
        cloudsigninfragment.setArguments(bundle);
        return cloudsigninfragment;
    }

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        listener = (CloudSignInFragmentListener)activity;
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        View view = layoutinflater.inflate(R.layout.cloud_signin, viewgroup, false);
        view.findViewById(R.id.cloud_sign_in_picasa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.picasaLogin();
            }
        });
        view.findViewById(R.id.cloud_sign_in_dropbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.dropboxLogin();
            }
        });
        View view1 = view.findViewById(R.id.signin_skip);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.skipRegister();
            }
        });
        if(getArguments() != null)
        {
            if(!getArguments().getBoolean("skip", true))
            {
                view1.setVisibility(View.GONE);
            }
            if(!getArguments().getBoolean("back", true))
            {
                view.setBackgroundResource(0);
            }
        }
        return view;
    }



    public interface CloudSignInFragmentListener
    {
        public abstract void dropboxLogin();

        public abstract void picasaLogin();

        public abstract void skipRegister();
    }
}
