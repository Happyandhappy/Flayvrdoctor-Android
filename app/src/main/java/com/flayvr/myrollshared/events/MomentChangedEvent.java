package com.flayvr.myrollshared.events;

import com.flayvr.myrollshared.data.Moment;

public class MomentChangedEvent
{

    private Moment moment;
    private final Type type;

    public MomentChangedEvent(Moment moment1, Type type1)
    {
        moment = moment1;
        type = type1;
    }

    public Moment getMoment()
    {
        return moment;
    }

    public Type getType()
    {
        return type;
    }

    public enum Type {
        All("All", 0),
        Favorite("Favorite", 1),
        Hidden("Hidden", 2),
        Cover("Cover", 3),
        Items("Items", 4),
        Title("Title", 5),
        Location("Location", 6);

        Type(String s, int i) {
        }
    }
}
