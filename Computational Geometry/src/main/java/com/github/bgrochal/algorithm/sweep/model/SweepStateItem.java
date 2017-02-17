package com.github.bgrochal.algorithm.sweep.model;

import com.github.bgrochal.middleware.algorithm.utils.PointsOrientation;
import com.github.bgrochal.middleware.model.Vector;

/**
 * @author Bartłomiej Grochal
 */
public class SweepStateItem implements Comparable<SweepStateItem> {

    private final Segment segment;


    public SweepStateItem(Segment segment) {
        this.segment = segment;
    }


    @Override
    public int compareTo(SweepStateItem anotherSweepItem) {
        PointsOrientation.Direction direction = PointsOrientation.getDirection(
                new Vector<>(segment.getEnd(), segment.getStart()), anotherSweepItem.getSegment().getStart());

        switch (direction) {
            case LEFT:
                return 1;
            case RIGHT:
                return -1;
            case ON:
                // TODO: Check correctness.
                return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SweepStateItem)) {
            return false;
        } else {
            SweepStateItem castedObj = (SweepStateItem) obj;
            return segment.equals(castedObj.getSegment());
        }
    }

    @Override
    public int hashCode() {
        return 31 * segment.hashCode();
    }

    @Override
    public String toString() {
        return segment.toString();
    }


    public Segment getSegment() {
        return segment;
    }

}
