package com.github.bgrochal.algorithm.location.separators.model;

import com.github.bgrochal.middleware.algorithm.utils.PointsOrientation;
import com.github.bgrochal.middleware.model.Vector;
import com.google.common.math.DoubleMath;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;
import static com.github.bgrochal.middleware.algorithm.utils.PointsOrientation.Direction.LEFT;
import static com.github.bgrochal.middleware.algorithm.utils.PointsOrientation.Direction.RIGHT;

/**
 * @author Bartłomiej Grochal
 */
public class SweepInterval implements Comparable<SweepInterval> {

    private final Segment lowerBound;
    private final Segment upperBound;

    private Point generator;


    public SweepInterval(Segment lowerBound, Segment upperBound, Point generator) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.generator = generator;
    }


    @Override
    public int compareTo(SweepInterval another) {
        int endComparator = DoubleMath.fuzzyCompare(lowerBound.getEnd().getY(), another.lowerBound.getEnd().getY(), EPSILON);
        int startComparator = DoubleMath.fuzzyCompare(lowerBound.getStart().getY(), another.lowerBound.getStart().getY(), EPSILON);

        return endComparator == 0 ? startComparator : endComparator;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        return builder
                .append('[')
                .append(lowerBound)
                .append(' ')
                .append(upperBound)
                .append(']')
                .toString();
    }


    public boolean containsPoint(Point point) {
        return !PointsOrientation.getDirection(new Vector<>(lowerBound.getEnd(), lowerBound.getStart()), point).equals(RIGHT) &&
                !PointsOrientation.getDirection(new Vector<>(upperBound.getEnd(), upperBound.getStart()), point).equals(LEFT);
    }

    public Segment getLowerBound() {
        return lowerBound;
    }

    public Segment getUpperBound() {
        return upperBound;
    }

    public Point getGenerator() {
        return generator;
    }

}
