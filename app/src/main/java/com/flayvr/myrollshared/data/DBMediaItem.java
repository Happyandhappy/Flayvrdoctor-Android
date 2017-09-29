package com.flayvr.myrollshared.data;


public class DBMediaItem
{

    private Double blurry;
    private Float centerX;
    private Float centerY;
    private Double color;
    private Double dark;
    private Integer facesCount;
    private String facesJson;
    private Long id;
    private Float prop;
    private Boolean shouldRunHeavyProcessing;
    private String type;

    public DBMediaItem()
    {
    }

    public DBMediaItem(Long long1)
    {
        id = long1;
    }

    public DBMediaItem(Long long1, Double double1, Double double2, Double double3, Float float1, Boolean boolean1, String s, 
            String s1, Float float2, Float float3, Integer integer)
    {
        id = long1;
        blurry = double1;
        color = double2;
        dark = double3;
        prop = float1;
        shouldRunHeavyProcessing = boolean1;
        facesJson = s;
        type = s1;
        centerX = float2;
        centerY = float3;
        facesCount = integer;
    }

    public Double getBlurry()
    {
        return blurry;
    }

    public Float getCenterX()
    {
        return centerX;
    }

    public Float getCenterY()
    {
        return centerY;
    }

    public Double getColor()
    {
        return color;
    }

    public Double getDark()
    {
        return dark;
    }

    public Integer getFacesCount()
    {
        return facesCount;
    }

    public String getFacesJson()
    {
        return facesJson;
    }

    public Long getId()
    {
        return id;
    }

    public Float getProp()
    {
        return prop;
    }

    public Boolean getShouldRunHeavyProcessing()
    {
        return shouldRunHeavyProcessing;
    }

    public String getType()
    {
        return type;
    }

    public void setBlurry(Double double1)
    {
        blurry = double1;
    }

    public void setCenterX(Float float1)
    {
        centerX = float1;
    }

    public void setCenterY(Float float1)
    {
        centerY = float1;
    }

    public void setColor(Double double1)
    {
        color = double1;
    }

    public void setDark(Double double1)
    {
        dark = double1;
    }

    public void setFacesCount(Integer integer)
    {
        facesCount = integer;
    }

    public void setFacesJson(String s)
    {
        facesJson = s;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setProp(Float float1)
    {
        prop = float1;
    }

    public void setShouldRunHeavyProcessing(Boolean boolean1)
    {
        shouldRunHeavyProcessing = boolean1;
    }

    public void setType(String s)
    {
        type = s;
    }
}
