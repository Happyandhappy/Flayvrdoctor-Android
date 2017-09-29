package com.flayvr.pojos;


public class TileSettings
{
    private Frame maximizeFrame;
    private Frame minimizeFrame;
    private Frame normalFrame;

    public TileSettings(Frame frame, Frame frame1, Frame frame2)
    {
        normalFrame = frame;
        minimizeFrame = frame1;
        maximizeFrame = frame2;
    }

    public Frame getMaximizeFrame()
    {
        return maximizeFrame;
    }

    public Frame getMinimizeFrame()
    {
        return minimizeFrame;
    }

    public Frame getNormalFrame()
    {
        return normalFrame;
    }

    public class Frame
    {

        private float weight;
        private float width;
        private float x;
        private float y;

        public float getHeight()
        {
            return weight;
        }

        public float getWidth()
        {
            return width;
        }

        public float getX()
        {
            return x;
        }

        public float getY()
        {
            return y;
        }

        public Frame(float f, float f1, float f2, float f3)
        {
            x = f;
            y = f1;
            width = f2;
            weight = f3;
        }
    }
}
