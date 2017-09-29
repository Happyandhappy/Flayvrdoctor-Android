package com.flayvr.myrollshared.data;


public class ClassifierThreshold
{

    private Double badBlurry;
    private Double badDark;
    private Double badScore;
    private Double bestDirectoryScore;
    private Double bestScore;
    private Double forReviewScore;
    private Double goodEnoughScore;
    private Long id;
    private Integer source;

    public ClassifierThreshold()
    {
    }

    public ClassifierThreshold(Long long1)
    {
        id = long1;
    }

    public ClassifierThreshold(Long long1, Double double1, Double double2, Double double3, Double double4, Double double5, Double double6, 
            Double double7, Integer integer)
    {
        id = long1;
        badBlurry = double1;
        badDark = double2;
        badScore = double3;
        forReviewScore = double4;
        goodEnoughScore = double5;
        bestScore = double6;
        bestDirectoryScore = double7;
        source = integer;
    }

    public Double getBadBlurry()
    {
        return badBlurry;
    }

    public Double getBadDark()
    {
        return badDark;
    }

    public Double getBadScore()
    {
        return badScore;
    }

    public Double getBestDirectoryScore()
    {
        return bestDirectoryScore;
    }

    public Double getBestScore()
    {
        return bestScore;
    }

    public Double getForReviewScore()
    {
        return forReviewScore;
    }

    public Double getGoodEnoughScore()
    {
        return goodEnoughScore;
    }

    public Long getId()
    {
        return id;
    }

    public Integer getSource()
    {
        return source;
    }

    public void setBadBlurry(Double double1)
    {
        badBlurry = double1;
    }

    public void setBadDark(Double double1)
    {
        badDark = double1;
    }

    public void setBadScore(Double double1)
    {
        badScore = double1;
    }

    public void setBestDirectoryScore(Double double1)
    {
        bestDirectoryScore = double1;
    }

    public void setBestScore(Double double1)
    {
        bestScore = double1;
    }

    public void setForReviewScore(Double double1)
    {
        forReviewScore = double1;
    }

    public void setGoodEnoughScore(Double double1)
    {
        goodEnoughScore = double1;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setSource(Integer integer)
    {
        source = integer;
    }
}
