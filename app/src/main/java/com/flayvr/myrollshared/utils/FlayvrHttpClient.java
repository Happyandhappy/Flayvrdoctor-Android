package com.flayvr.myrollshared.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.exceptions.UploadingException;
import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

public class FlayvrHttpClient extends DefaultHttpClient
{

    public static final int NUM_RETRIES = 3;

    public FlayvrHttpClient()
    {
        initRetries();
    }

    public FlayvrHttpClient(HttpParams httpparams)
    {
        super(httpparams);
        initRetries();
    }

    private void initRetries()
    {
        HttpProtocolParams.setUseExpectContinue(getParams(), false);
        setHttpRequestRetryHandler(new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException ioexception, int i, HttpContext httpcontext)
            {
                return i < 3;
            }
        });
    }

    private boolean isInternetOn()
    {
        ConnectivityManager connectivitymanager;
        NetworkInfo anetworkinfo[];
        connectivitymanager = (ConnectivityManager)FlayvrApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivitymanager != null) {
            anetworkinfo = connectivitymanager.getAllNetworkInfo();
            if(anetworkinfo != null) {
                int i = 0;
                int j;
                j = anetworkinfo.length;
                while(i < j) {
                    if (anetworkinfo[i].getState() == android.net.NetworkInfo.State.CONNECTED)
                        return true;
                    i++;
                }
            }
        }
        return false;
    }

    public HttpResponse executeWithRetries(HttpPost httppost)
    {
        int i;
        HttpResponse httpresponse;
        Object obj;
        i = 0;
        httpresponse = null;
        while(i < 3)
        {
            int j;
            try
            {
                httpresponse = super.execute(httppost);
                j = httpresponse.getStatusLine().getStatusCode();
                if(j == 200)
                    break;
                Thread.sleep(6000L);
            } catch(InterruptedException interruptedexception) {
            } catch (Exception e) {}
            i++;
        }
        if(httpresponse.getStatusLine().getStatusCode() != 200)
            Log.e("flayvr_http_client", "Error while execute "+httppost.getURI()+" : "+httpresponse.getStatusLine());
        return httpresponse;
    }
}
