package com.github.bgrochal.geomvisualiser.model;

import com.github.bgrochal.geomvisualiser.model.utils.Color;

/**
 * @author Bartłomiej Grochal
 */
public class Line extends PlotableModel {

    private Point first;
    private Point second;


    public Line(Color color,Point first, Point second) {
        this(first, second);
        this.color = color;
    }

    public Line(String label, Point first, Point second) {
        this(first, second);
        this.label = label;
    }

    public Line(Point first, Point second) {
        this.first = first;
        this.second = second;
    }


    @Override
    public String getPlotCommand() {
        StringBuilder builder = new StringBuilder();
        return builder.
                append(getLabel()).
                append(": Line[").
                append(first.getAnonymousPointScript()).
                append(",").
                append(second.getAnonymousPointScript()).
                append("]").
                toString();

    }

}
