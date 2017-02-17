package com.github.bgrochal.algorithm.triangulation.model;

import com.google.common.math.DoubleMath;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;

/**
 * @author Bartłomiej Grochal
 */
public class Edge implements Comparable<Edge> {

    private final Segment segment;

    private Vertex helper;


    public Edge(Segment segment) {
        this.segment = segment;
    }


    @Override
    public int compareTo(Edge anotherEdge) {
        // TODO: Is it possible that a result of comparing two different edges will be 0?
        return DoubleMath.fuzzyCompare(segment.getLeftPoint().getX(), anotherEdge.getSegment().getLeftPoint().getX(), EPSILON);
    }

    @Override
    public String toString() {
        return segment.toString();
    }


    public Segment getSegment() {
        return segment;
    }

    public Vertex getHelper() {
        return helper;
    }

    public void setHelper(Vertex helper) {
        this.helper = helper;
    }

}
