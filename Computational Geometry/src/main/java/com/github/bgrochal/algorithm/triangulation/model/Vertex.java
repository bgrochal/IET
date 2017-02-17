package com.github.bgrochal.algorithm.triangulation.model;

import com.google.common.math.DoubleMath;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;

/**
 * @author Bartłomiej Grochal
 */
public class Vertex implements Comparable<Vertex> {

    private final Point point;

    private VertexType type;


    public Vertex(Point point) {
        this.point = point;
    }


    @Override
    public int compareTo(Vertex anotherVertex) {
        // TODO: What if two points have the same OY coordinate?
        return DoubleMath.fuzzyCompare(anotherVertex.getPoint().getY(), point.getY(), EPSILON);
    }

    @Override
    public String toString() {
        return point.toString();
    }


    public Point getPoint() {
        return point;
    }

    public VertexType getType() {
        return type;
    }

    public void setType(VertexType type) {
        this.type = type;
    }

}
