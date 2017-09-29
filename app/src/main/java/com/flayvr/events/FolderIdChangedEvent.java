package com.flayvr.events;


public class FolderIdChangedEvent
{

    private Long newId;
    private Long oldId;

    public FolderIdChangedEvent(Long long1, Long long2)
    {
        newId = long2;
        oldId = long1;
    }

    public Long getNewId()
    {
        return newId;
    }

    public Long getOldId()
    {
        return oldId;
    }
}
