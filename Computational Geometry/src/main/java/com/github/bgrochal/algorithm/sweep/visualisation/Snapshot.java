package com.github.bgrochal.algorithm.sweep.visualisation;

import com.github.bgrochal.algorithm.sweep.model.Point;
import com.github.bgrochal.algorithm.sweep.model.Segment;

/**
 * @author Bartłomiej Grochal
 */
public class Snapshot {

    private final Point currentPoint;

    private final Iterable<Segment> segments;
    private final Iterable<Point> intersections;


    public Snapshot(Point currentPoint, Iterable<Segment> segments, Iterable<Point> intersections) {
        this.currentPoint = currentPoint;

        this.segments = segments;
        this.intersections = intersections;
    }


    public Point getCurrentPoint() {
        return currentPoint;
    }

    public Iterable<Segment> getSegments() {
        return segments;
    }

    public Iterable<Point> getIntersections() {
        return intersections;
    }

}
