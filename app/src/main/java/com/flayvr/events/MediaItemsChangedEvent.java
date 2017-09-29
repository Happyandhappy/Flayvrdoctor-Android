package com.flayvr.events;

import java.util.Set;

public class MediaItemsChangedEvent
{

    private Set<Long> added;
    private Set<Long> deleted;

    public MediaItemsChangedEvent(Set<Long> set, Set<Long> set1)
    {
        added = set;
        deleted = set1;
    }

    public Set<Long> getAdded()
    {
        return added;
    }

    public Set<Long> getDeleted()
    {
        return deleted;
    }
}
