package org.bitbucket.eirlis.conetc.render;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elena on 29.03.2016.
 */
public class Gradient {
    private PApplet applet;
    List<Integer> colors;
    double min = 0, max = 0;

    // Constructor
    public Gradient(PApplet applet) {
        this.applet = applet;
        colors = new ArrayList<Integer>();
    }

    public void addColor(int c)
    {
        colors.add(c);
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public double getColorValue(int colorIndex) {
        double step = (max - min) / (colors.size() - 1);
        return min + step * colorIndex;
    }

    public int getGradient(double value)
    {
        // make sure there are colors to use
        if(colors.size() == 0)
            return applet.color(0, 0, 0);

        // if its too low, use the lowest value
        if(value <= min) {
            return colors.get(0);
        }

        // if its too high, use the highest value
        if(value >= max)
            return colors.get(colors.size() -  1);
        double step = (max - min) / (colors.size() - 1);

        // lerp between the two needed colors
        int color_index = (int)((value - min) / step);
        int c1 = colors.get(color_index);
        int c2 = colors.get(color_index + 1);

        return applet.lerpColor(c1, c2, (float) ((value - min) / step) - color_index);
    }

}