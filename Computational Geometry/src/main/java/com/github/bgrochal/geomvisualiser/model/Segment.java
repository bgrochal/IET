package com.github.bgrochal.geomvisualiser.model;

import com.github.bgrochal.geomvisualiser.model.utils.Color;

/**
 * @author Bartłomiej Grochal
 */
public class Segment extends PlotableModel {

    private final Point start;
    private final Point end;


    public Segment(Color color, Point start, Point end) {
        this(start, end);
        this.color = color;
    }


    public Segment(Point start, Point end) {
        this.start = start;
        this.end = end;
    }


    @Override
    public String getPlotCommand() {
        StringBuilder builder = new StringBuilder();
        return builder.
                append(getLabel()).
                append(": Segment[").
                append(start.getAnonymousPointScript()).
                append(",").
                append(end.getAnonymousPointScript()).
                append("]").
                toString();
    }


    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

}
