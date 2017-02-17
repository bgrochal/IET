package com.github.bgrochal.algorithm.sweep.visualisation;

import com.github.bgrochal.algorithm.sweep.model.Point;
import com.github.bgrochal.algorithm.sweep.model.Segment;
import com.github.bgrochal.geomvisualiser.geogebra.api.CommandExecutor;
import com.github.bgrochal.geomvisualiser.model.utils.Color;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Bartłomiej Grochal
 */
public class VisualiserConnector {

    private final CommandExecutor executor;

    public VisualiserConnector() {
        executor = new CommandExecutor();
    }


    public List<Segment> getVisualisedObjects() {
        List<Segment> segments = new LinkedList<>();

        executor.getAllSegments().forEach(segment -> {
            Point start = new Point(segment.getStart().getX(), segment.getStart().getY());
            Point end = new Point(segment.getEnd().getX(), segment.getEnd().getY());

            if(start.getX() > end.getX()) {
                Point temp = new Point(start.getX(), start.getY());

                start = end;
                end = temp;
            }

            segments.add(new Segment(start, end));
        });

        return segments;
    }

    public void visualiseSegments(Iterable<Segment> segments) {
        List<com.github.bgrochal.geomvisualiser.model.Segment> segmentsList = new LinkedList<>();
        List<com.github.bgrochal.geomvisualiser.model.Point> pointsList = new LinkedList<>();

        segments.forEach(segment -> {
            com.github.bgrochal.geomvisualiser.model.Point startPoint =
                    new com.github.bgrochal.geomvisualiser.model.Point(segment.getStart().getX(), segment.getStart().getY());
            com.github.bgrochal.geomvisualiser.model.Point endPoint =
                    new com.github.bgrochal.geomvisualiser.model.Point(segment.getEnd().getX(), segment.getEnd().getY());

            segmentsList.add(new com.github.bgrochal.geomvisualiser.model.Segment(startPoint, endPoint));
            pointsList.addAll(Arrays.asList(startPoint, endPoint));
        });

        executor.plotMultipleObjects(segmentsList);
        executor.plotMultipleObjects(pointsList);
    }

    public void visualiseIntersectionPoints(Iterable<Point> intersections) {
        List<com.github.bgrochal.geomvisualiser.model.Point> intersectionsList = new LinkedList<>();

        intersections.forEach(point -> intersectionsList.add(
                new com.github.bgrochal.geomvisualiser.model.Point(new Color(255, 0, 0), point.getX(), point.getY())
        ));

        executor.plotMultipleObjects(intersectionsList);
    }

    public void visualiseSweepLine(Point point) {
        executor.plotObject(new com.github.bgrochal.geomvisualiser.model.Line(
                new Color(0, 255, 0),
                new com.github.bgrochal.geomvisualiser.model.Point(point.getX(), point.getY()),
                new com.github.bgrochal.geomvisualiser.model.Point(point.getX(), 0)
        ));
    }

    public void clearPlot() {
        executor.clearPanel();
    }

}
