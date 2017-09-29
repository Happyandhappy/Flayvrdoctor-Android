package com.flayvr.myrollshared.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;

public class MessageDialog extends DialogFragment
{

    private static final String TAG = MessageDialog.class.getSimpleName();
    private android.content.DialogInterface.OnDismissListener dismissListener;
    private CharSequence msg;
    private String negativeText;
    private android.content.DialogInterface.OnClickListener negetiveListener;
    private android.content.DialogInterface.OnClickListener neturalListener;
    private CharSequence neturalText;
    private android.content.DialogInterface.OnClickListener positiveListener;
    private String positiveText;
    private CharSequence title;
    private View view;

    public MessageDialog()
    {
        positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        };
        positiveText = FlayvrApplication.getAppContext().getString(R.string.dialog_ok);
        negativeText = FlayvrApplication.getAppContext().getString(R.string.dialog_cancel);
    }

    public void onCancel(DialogInterface dialoginterface)
    {
        super.onDismiss(dialoginterface);
        if(dismissListener != null)
        {
            dismissListener.onDismiss(dialoginterface);
        } else if(negetiveListener != null)
        {
            negetiveListener.onClick(dialoginterface, -1);
        }
    }

    public Dialog onCreateDialog(Bundle bundle)
    {
        com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity());
        builder.content(msg).positiveText(positiveText);
        if(title != null)
        {
            builder.title(title);
        }
        if(view != null)
        {
            builder.customView(view, true);
        }
        if(negetiveListener != null)
        {
            builder.cancelable(true).negativeText(negativeText);
        }
        if(neturalListener != null && neturalText != null)
        {
            builder.neutralText(neturalText);
        }
        builder.callback(new com.afollestad.materialdialogs.MaterialDialog.ButtonCallback(){
            public void onNegative(MaterialDialog materialdialog)
            {
                negetiveListener.onClick(materialdialog, 0);
            }

            public void onNeutral(MaterialDialog materialdialog)
            {
                neturalListener.onClick(materialdialog, 0);
            }

            public void onPositive(MaterialDialog materialdialog)
            {
                positiveListener.onClick(materialdialog, 0);
            }
        });
        MaterialDialog materialdialog = builder.build();
        materialdialog.setCanceledOnTouchOutside(true);
        materialdialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialoginterface, int i, KeyEvent keyevent)
            {
                if(i != 4)
                    return false;
                if(dismissListener == null) {
                    if(negetiveListener != null)
                    {
                        negetiveListener.onClick(dialoginterface, -1);
                    }
                }else
                    dismissListener.onDismiss(dialoginterface);
                dialoginterface.dismiss();
                return true;
            }
        });
        return materialdialog;
    }

    public void setDismissListener(android.content.DialogInterface.OnDismissListener ondismisslistener)
    {
        dismissListener = ondismisslistener;
    }

    public void setMsg(CharSequence charsequence)
    {
        msg = charsequence;
    }

    public void setNegativeText(String s)
    {
        negativeText = s;
    }

    public void setNegetiveListener(android.content.DialogInterface.OnClickListener onclicklistener)
    {
        negetiveListener = onclicklistener;
    }

    public void setNeturalListener(android.content.DialogInterface.OnClickListener onclicklistener)
    {
        neturalListener = onclicklistener;
    }

    public void setNeturalText(CharSequence charsequence)
    {
        neturalText = charsequence;
    }

    public void setPositiveListener(android.content.DialogInterface.OnClickListener onclicklistener)
    {
        positiveListener = onclicklistener;
    }

    public void setPositiveText(String s)
    {
        positiveText = s;
    }

    public void setTitle(CharSequence charsequence)
    {
        title = charsequence;
    }

    public void setView(View view1)
    {
        view = view1;
    }

    public void show(FragmentManager fragmentmanager, String s)
    {
        try
        {
            super.show(fragmentmanager, s);
            return;
        }
        catch(IllegalStateException illegalstateexception)
        {
            Log.e(TAG, illegalstateexception.getMessage(), illegalstateexception);
        }
    }
}
