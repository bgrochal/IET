package com.github.bgrochal.algorithm.ch.common.model;

import com.google.common.math.DoubleMath;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;

/**
 * @author Bartłomiej Grochal
 */
public class Point extends com.github.bgrochal.middleware.model.Point implements Comparable<Point> {

    private Point startingPoint;


    public Point(double x, double y) {
        this(x, y, null);
    }

    public Point(double x, double y, Point startingPoint) {
        super(x, y);
        this.startingPoint = startingPoint;
    }


    @Override
    public int compareTo(Point point) {
        int comparatorCosine = DoubleMath.fuzzyCompare(point.getAngleCosine(), getAngleCosine(), EPSILON);
        return comparatorCosine == 0 ? DoubleMath.fuzzyCompare(getHypotenuse(), point.getHypotenuse(), EPSILON) : comparatorCosine;
    }


    public void setStartingPoint(Point startingPoint) {
        this.startingPoint = startingPoint;
    }

    public double getAngleCosine() {
        return getOXLeg() / getHypotenuse();
    }

    public double getHypotenuse() {
        return Math.sqrt(Math.pow(getOXLeg(), 2) + Math.pow(getOYLeg(), 2));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    private double getOXLeg() {
        return x - startingPoint.x;
    }

    private double getOYLeg() {
        return y - startingPoint.y;
    }

}
