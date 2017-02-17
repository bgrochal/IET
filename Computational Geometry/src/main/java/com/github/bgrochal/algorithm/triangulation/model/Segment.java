package com.github.bgrochal.algorithm.triangulation.model;

import com.google.common.math.DoubleMath;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;

/**
 * @author Bartłomiej Grochal
 */
public class Segment extends com.github.bgrochal.middleware.model.Segment<Point> {

    public Segment(Point start, Point end) {
        super(start, end);
    }


    public Point getLeftPoint() {
        return DoubleMath.fuzzyCompare(getStart().getX(), getEnd().getX(), EPSILON) <= 0 ? getStart() : getEnd();
    }

}
