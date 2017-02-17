package com.github.bgrochal.algorithm.ch.common.visualiser;

import com.github.bgrochal.algorithm.ch.common.model.Point;

/**
 * @author Bartłomiej Grochal
 */
public class Snapshot {

    private final Iterable<Point> allPoints;
    private final Iterable<Point> convexHullCandidates;


    public Snapshot(Iterable<Point> allPoints, Iterable<Point> convexHullCandidates) {
        this.allPoints = allPoints;
        this.convexHullCandidates = convexHullCandidates;
    }


    public Iterable<Point> getAllPoints() {
        return allPoints;
    }

    public Iterable<Point> getConvexHullCandidates() {
        return convexHullCandidates;
    }

}
