package com.github.bgrochal.geomvisualiser.model;

import com.github.bgrochal.geomvisualiser.model.utils.Color;
import com.github.bgrochal.geomvisualiser.model.utils.ModelUtils;

/**
 * @author Bartłomiej Grochal
 */
public class Point extends PlotableModel {

    private final double x;
    private final double y;

    private int size;


    public Point(Color color, double x, double y) {
        this(x, y);
        this.color = color;
    }

    public Point(String label, double x, double y) {
        this(x, y);
        this.label = label.toUpperCase();
    }

    public Point(double x, double y) {
        this.size = 5;
        this.x = x;
        this.y = y;
    }


    public int getSize() {
        return size;
    }

    @Override
    public String getPlotCommand() {
        StringBuilder builder = new StringBuilder();
        return builder.
                append(getLabel()).
                append("=").
                append(getAnonymousPointScript()).
                toString();
    }

    @Override
    public String getLabel() {
        if (label == null) {
            label = ModelUtils.generatePointLabel();
        }

        return label;
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    String getAnonymousPointScript() {
        StringBuilder builder = new StringBuilder();
        return builder.
                append("(").
                append(x).
                append(",").
                append(y).
                append(")").
                toString();
    }

}
