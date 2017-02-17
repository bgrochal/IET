package com.github.bgrochal.geomvisualiser.model;

import com.github.bgrochal.geomvisualiser.model.utils.Color;

/**
 * @author Bartłomiej Grochal
 */
public class Ray extends PlotableModel {

    private Point start;
    private Point second;


    public Ray(Color color, Point start, Point second) {
        this(start, second);
        this.color = color;
    }

    public Ray(String label, Point start, Point second) {
        this(start, second);
        this.label = label;
    }

    public Ray(Point start, Point second) {
        this.start = start;
        this.second = second;
    }


    @Override
    public String getPlotCommand() {
        StringBuilder builder = new StringBuilder();
        return builder.
                append(getLabel()).
                append(": Ray[").
                append(start.getAnonymousPointScript()).
                append(",").
                append(second.getAnonymousPointScript()).
                append("]").
                toString();
    }
}
