package com.github.bgrochal.algorithm.location.separators.visualisation;

import com.github.bgrochal.algorithm.location.separators.model.Segment;

import java.util.List;

/**
 * @author Bartłomiej Grochal
 */
public class LocationSnapshot implements Snapshot {

    private final List<Segment> leftSeparator;
    private final List<Segment> rightSeparator;
    private final List<Segment> currentSeparator;


    public LocationSnapshot(List<Segment> leftSeparator, List<Segment> rightSeparator, List<Segment> currentSeparator) {
        this.leftSeparator = leftSeparator;
        this.rightSeparator = rightSeparator;
        this.currentSeparator = currentSeparator;
    }


    public List<Segment> getLeftSeparator() {
        return leftSeparator;
    }

    public List<Segment> getRightSeparator() {
        return rightSeparator;
    }

    public List<Segment> getCurrentSeparator() {
        return currentSeparator;
    }

}
