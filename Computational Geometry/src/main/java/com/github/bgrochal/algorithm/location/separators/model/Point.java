package com.github.bgrochal.algorithm.location.separators.model;

import com.google.common.math.DoubleMath;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;
import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.calculateSine;

/**
 * @author Bartłomiej Grochal
 */
public class Point extends com.github.bgrochal.middleware.model.Point implements Comparable<Point> {

    private List<Segment> ingoingSegments;
    private List<Segment> outgoingSegments;

    private BiFunction<Segment, Segment, Integer> segmentsComparator =
            (first, second) -> (-1) * DoubleMath.fuzzyCompare(
                    calculateSine(first.getStart(), first.getEnd()), calculateSine(second.getStart(), second.getEnd()), EPSILON);


    public Point(double x, double y) {
        super(x, y);

        ingoingSegments = new LinkedList<>();
        outgoingSegments = new LinkedList<>();
    }


    @Override
    public int compareTo(Point another) {
        int comparatorOX = DoubleMath.fuzzyCompare(this.getX(), another.getX(), EPSILON);
        int comparatorOY = DoubleMath.fuzzyCompare(this.getY(), another.getY(), EPSILON);

        return comparatorOX != 0 ? comparatorOX : comparatorOY;
    }


    public void addIngoingSegment(Segment segment) {
        ingoingSegments.add(segment);
    }

    public void addOutgoingSegment(Segment segment) {
        outgoingSegments.add(segment);
    }

    public List<Segment> getIngoingSegments() {
        ingoingSegments.sort(segmentsComparator::apply);
        return ingoingSegments;
    }

    public List<Segment> getOutgoingSegments() {
        outgoingSegments.sort(segmentsComparator::apply);
        return outgoingSegments;
    }

}
