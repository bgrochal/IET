package com.github.bgrochal.geomvisualiser.model;

import com.github.bgrochal.geomvisualiser.model.utils.Color;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author Bartłomiej Grochal
 */
public class Polygon extends PlotableModel {

    private List<Point> vertices;


    public Polygon(Color color, List<Point> vertices) {
        this(vertices);
        this.color = color;
    }

    public Polygon(List<Point> vertices) {
        this.vertices = vertices;
    }


    @Override
    public String getPlotCommand() {
        StringJoiner joiner = new StringJoiner(",");
        vertices.forEach(point -> joiner.add(point.getAnonymousPointScript()));

        StringBuilder builder = new StringBuilder();
        return builder.
                append(getLabel()).
                append(": Polygon[").
                append(joiner.toString()).
                append("]").
                toString();
    }

}
