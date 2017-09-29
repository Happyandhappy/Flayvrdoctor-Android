package com.flayvr.myrollshared.data;

import android.location.Location;

public class User
{

    public static final Location UNDEIFINED_LOCATION = new Location("undefined");
    private static final long USER_ID = 1L;
    private Double homeLatitude;
    private Double homeLongitude;
    private Long id;
    private Double workLatitude;
    private Double workLongitude;

    public User()
    {
    }

    public User(Long long1)
    {
        id = long1;
    }

    public User(Long long1, Double double1, Double double2, Double double3, Double double4)
    {
        id = long1;
        homeLatitude = double1;
        homeLongitude = double2;
        workLatitude = double3;
        workLongitude = double4;
    }

    public static User getMyRollUser()
    {
        synchronized (User.class) {
            UserDao userdao;
            User user;
            userdao = DBManager.getInstance().getDaoSession().getUserDao();
            user = (User)userdao.load(Long.valueOf(1L));
            if(user == null)
            {
                user = new User(Long.valueOf(1L));
                userdao.insert(user);
            }
            return user;
        }
    }

    public Double getHomeLatitude()
    {
        return homeLatitude;
    }

    public Location getHomeLocation()
    {
        if(getHomeLongitude() == null || getHomeLatitude() == null)
        {
            return null;
        } else
        {
            Location location = new Location((new StringBuilder()).append(getId()).append("").toString());
            location.setLongitude(getHomeLongitude().doubleValue());
            location.setLatitude(getHomeLatitude().doubleValue());
            return location;
        }
    }

    public Double getHomeLongitude()
    {
        return homeLongitude;
    }

    public Long getId()
    {
        return id;
    }

    public Double getWorkLatitude()
    {
        return workLatitude;
    }

    public Location getWorkLocation()
    {
        if(getWorkLongitude() == null || getWorkLatitude() == null)
        {
            return null;
        } else
        {
            Location location = new Location((new StringBuilder()).append(getId()).append("").toString());
            location.setLongitude(getWorkLongitude().doubleValue());
            location.setLatitude(getWorkLatitude().doubleValue());
            return location;
        }
    }

    public Double getWorkLongitude()
    {
        return workLongitude;
    }

    public void setHomeLatitude(Double double1)
    {
        homeLatitude = double1;
    }

    public void setHomeLocation(Location location)
    {
        setHomeLatitude(Double.valueOf(location.getLatitude()));
        setHomeLongitude(Double.valueOf(location.getLongitude()));
    }

    public void setHomeLongitude(Double double1)
    {
        homeLongitude = double1;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setWorkLatitude(Double double1)
    {
        workLatitude = double1;
    }

    public void setWorkLocation(Location location)
    {
        setWorkLatitude(Double.valueOf(location.getLatitude()));
        setWorkLongitude(Double.valueOf(location.getLongitude()));
    }

    public void setWorkLongitude(Double double1)
    {
        workLongitude = double1;
    }
}
