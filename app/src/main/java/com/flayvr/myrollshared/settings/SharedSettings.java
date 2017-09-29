package com.flayvr.myrollshared.settings;

import java.util.HashSet;
import java.util.Set;

public class SharedSettings
{

    public static Set calendarTitlesBlackList;

    public SharedSettings()
    {
    }

    static 
    {
        calendarTitlesBlackList = new HashSet();
        calendarTitlesBlackList.add("(?i).*(available)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(call)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(reminder)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*\\d{5,}?.*");
        calendarTitlesBlackList.add("(?i).*(saved for)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(appointment)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(buy)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(saved)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(pay)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(bills)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(monthly)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(weekly)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(meeting)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(tentative)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(conference)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(conf)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(email)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(send)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(purchase)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(todo)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(fix)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(invitation)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(daily)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(drive)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(remind)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(out of office)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(notification)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(renew)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(search)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(canceled)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(buy)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(invitation)(\\s|$).*");
    }
}
