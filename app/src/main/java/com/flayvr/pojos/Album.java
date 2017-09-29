package com.flayvr.pojos;

import java.util.Date;

public class Album implements Comparable<Album>
{

    private String id;
    private Date maxDate;
    private Date minDate;
    private String name;
    private String path;
    private int photos;
    private int videos;

    public Album(String s, String s1)
    {
        id = s;
        name = s1;
        photos = 0;
        videos = 0;
    }

    public Album(String s, String s1, String s2)
    {
        this(s, s1);
        setPath(s2);
    }

    public void addPhoto()
    {
        photos = 1 + photos;
    }

    public void addVideo()
    {
        videos = 1 + videos;
    }

    public int compareTo(Album album)
    {
        return Integer.valueOf(album.getPhotos() + album.getVideos()).compareTo(Integer.valueOf(getPhotos() + getVideos()));
    }

    public String getId()
    {
        return id;
    }

    public Date getMaxDate()
    {
        return maxDate;
    }

    public Date getMinDate()
    {
        return minDate;
    }

    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public int getPhotos()
    {
        return photos;
    }

    public int getVideos()
    {
        return videos;
    }

    public void setMaxDate(Date date)
    {
        maxDate = date;
    }

    public void setMinDate(Date date)
    {
        minDate = date;
    }

    public void setPath(String s)
    {
        path = s;
    }

    public String toString()
    {
        return name;
    }
}
