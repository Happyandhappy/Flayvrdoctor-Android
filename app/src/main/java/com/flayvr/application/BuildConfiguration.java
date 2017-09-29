package com.flayvr.application;

import com.flayvr.myrollshared.application.AppConfiguration;
import com.flayvr.myrollshared.server.ServerUrls;

public class BuildConfiguration extends AppConfiguration
{

    public BuildConfiguration()
    {
    }

    public String getCreateAppEventUrl()
    {
        return ServerUrls.CREATE_APP_EVENT_URL;
    }

    public String getCreateAppSessionUrl()
    {
        return ServerUrls.CREATE_APP_SESSION_URL;
    }

    public String getCreateUserUrl()
    {
        return ServerUrls.CREATE_NEW_USER_URL;
    }

    public String getKissKey()
    {
        return "eeb4a4d7151746a80791514972a4603a70ae69a4";
    }

    public String getParseApiKey()
    {
        return "NeAj5ydbRJfBEISpNqxGQPxfDsjj4aDUtjP3EKV2";
    }

    public String getParseAppKey()
    {
        return "bkk1KORkIH9iL3XYsVwrc5PgmfeIDRLwspDBiFyH";
    }

    public String getServerUrl()
    {
        return "http://dws.flayvr.com/";
    }
}
