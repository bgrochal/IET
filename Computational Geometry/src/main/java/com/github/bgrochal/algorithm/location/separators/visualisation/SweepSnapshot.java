package com.github.bgrochal.algorithm.location.separators.visualisation;

import com.github.bgrochal.algorithm.location.separators.model.Point;
import com.github.bgrochal.algorithm.location.separators.model.Segment;
import com.github.bgrochal.algorithm.location.separators.model.SweepInterval;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Bartłomiej Grochal
 */
public class SweepSnapshot implements Snapshot {

    private final Point sweepPosition;

    private final List<Segment> divisionSegments;
    private final List<Segment> addedSegments;
    private final List<Segment> sweepingSegments;


    public SweepSnapshot(Point sweepPosition, List<Segment> divisionSegments, List<Segment> addedSegments,
                         Set<SweepInterval> sweepingSegments) {
        this.sweepPosition = sweepPosition;
        this.divisionSegments = divisionSegments;
        this.addedSegments = addedSegments;
        this.sweepingSegments = new ArrayList<>(Stream.concat(
                sweepingSegments.stream().map(SweepInterval::getLowerBound),
                sweepingSegments.stream().map(SweepInterval::getUpperBound))
                .collect(Collectors.toSet()));
    }


    public Point getSweepPosition() {
        return sweepPosition;
    }

    public List<Segment> getDivisionSegments() {
        return divisionSegments;
    }

    public List<Segment> getAddedSegments() {
        return addedSegments;
    }

    public List<Segment> getSweepingSegments() {
        return sweepingSegments;
    }

}
