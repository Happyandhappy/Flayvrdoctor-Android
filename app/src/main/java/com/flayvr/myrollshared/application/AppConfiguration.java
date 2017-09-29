package com.flayvr.myrollshared.application;

public abstract class AppConfiguration
{

    private static AppConfiguration conf;

    public AppConfiguration()
    {
    }

    public static AppConfiguration getConfiguration()
    {
        if(conf == null)
        {
            conf = FlayvrApplication.getApplicationConfiguration();
        }
        return conf;
    }

    public abstract String getCreateAppEventUrl();

    public abstract String getCreateAppSessionUrl();

    public abstract String getCreateUserUrl();

    public abstract String getKissKey();

    public abstract String getParseApiKey();

    public abstract String getParseAppKey();

    public abstract String getServerUrl();

    public String getSharedPrefFile()
    {
        return "flayvr-shared-preferences";
    }
}
