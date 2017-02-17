package com.github.bgrochal.algorithm.location.separators.visualisation;

import com.github.bgrochal.algorithm.location.separators.model.Point;
import com.github.bgrochal.algorithm.location.separators.model.Segment;
import com.github.bgrochal.geomvisualiser.geogebra.api.CommandExecutor;
import com.github.bgrochal.geomvisualiser.model.Polygon;
import com.github.bgrochal.geomvisualiser.model.utils.Color;
import com.google.common.math.DoubleMath;
import javafx.util.Pair;

import java.util.*;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;

/**
 * @author Bartłomiej Grochal
 */
public class VisualiserConnector {

    private final CommandExecutor executor;


    public VisualiserConnector() {
        this.executor = new CommandExecutor();
    }


    public Pair<Point, Pair<List<Point>, List<Segment>>> getVisualisedObjects() {
        List<Segment> segments = new LinkedList<>();
        Set<Point> points = new HashSet<>();

        executor.getAllSegments().forEach(visualisedSegment -> {
            Point startPoint = new Point(visualisedSegment.getStart().getX(), visualisedSegment.getStart().getY());
            Point endPoint = new Point(visualisedSegment.getEnd().getX(), visualisedSegment.getEnd().getY());
            Segment segment = new Segment(startPoint, endPoint);

            segments.add(segment);
            points.addAll(Arrays.asList(startPoint, endPoint));
        });

        boolean found = false;
        Point requestedPoint = null;

        for (com.github.bgrochal.geomvisualiser.model.Point point : executor.getAllPoints()) {
            for (Point segmentPoint : points) {
                if (DoubleMath.fuzzyCompare(point.getX(), segmentPoint.getX(), EPSILON) == 0 &&
                        DoubleMath.fuzzyCompare(point.getY(), segmentPoint.getY(), EPSILON) == 0) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                requestedPoint = new Point(point.getX(), point.getY());
                break;
            }

            found = false;
        }

        assert requestedPoint == null;

        Pair<List<Point>, List<Segment>> divisionPair = new Pair<>(new LinkedList<>(points), segments);
        return new Pair<>(requestedPoint, divisionPair);
    }

    public void visualiseSegments(Iterable<Segment> segments) {
        List<com.github.bgrochal.geomvisualiser.model.Segment> segmentsList = new LinkedList<>();

        segments.forEach(segment -> {
            segmentsList.add(new com.github.bgrochal.geomvisualiser.model.Segment(
                    new com.github.bgrochal.geomvisualiser.model.Point(segment.getStart().getX(), segment.getStart().getY()),
                    new com.github.bgrochal.geomvisualiser.model.Point(segment.getEnd().getX(), segment.getEnd().getY())
            ));
        });

        executor.plotMultipleObjects(segmentsList);
    }

    public void visualiseSegments(Iterable<Segment> segments, Color color) {
        List<com.github.bgrochal.geomvisualiser.model.Segment> segmentsList = new LinkedList<>();

        segments.forEach(segment -> {
            segmentsList.add(new com.github.bgrochal.geomvisualiser.model.Segment(
                    color,
                    new com.github.bgrochal.geomvisualiser.model.Point(segment.getStart().getX(), segment.getStart().getY()),
                    new com.github.bgrochal.geomvisualiser.model.Point(segment.getEnd().getX(), segment.getEnd().getY())
            ));
        });

        executor.plotMultipleObjects(segmentsList);
    }


    public void visualiseAddedSegments(Iterable<Segment> addedSegments) {
        List<com.github.bgrochal.geomvisualiser.model.Segment> segmentsList = new LinkedList<>();

        addedSegments.forEach(segment -> {
            segmentsList.add(new com.github.bgrochal.geomvisualiser.model.Segment(
                    new Color(255, 0, 0),
                    new com.github.bgrochal.geomvisualiser.model.Point(segment.getStart().getX(), segment.getStart().getY()),
                    new com.github.bgrochal.geomvisualiser.model.Point(segment.getEnd().getX(), segment.getEnd().getY())
            ));
        });

        executor.plotMultipleObjects(segmentsList);

    }

    public void visualisePoints(Iterable<Point> points) {
        List<com.github.bgrochal.geomvisualiser.model.Point> pointsList = new LinkedList<>();

        points.forEach(point -> pointsList.add(
                new com.github.bgrochal.geomvisualiser.model.Point(point.getX(), point.getY())
        ));

        executor.plotMultipleObjects(pointsList);
    }

    public void visualisePolygon(Iterable<Point> vertices) {
        List<com.github.bgrochal.geomvisualiser.model.Point> pointsList = new LinkedList<>();

        vertices.forEach(point -> pointsList.add(
                new com.github.bgrochal.geomvisualiser.model.Point(point.getX(), point.getY())));

        executor.plotObject(new Polygon(pointsList));
    }

    public void visualiseSeparators(List<List<Point>> separators) {
        visualiseSeparator(separators.get(0), new Color(255, 0, 125));
        visualiseSeparator(separators.get(1), new Color(0, 255, 0));
    }

    public void visualiseSeparator(List<Point> separator) {
        visualiseSeparator(separator, new Color(0, 255, 0));
    }

    public void visualiseSeparator(List<Point> separator, Color color) {
        List<com.github.bgrochal.geomvisualiser.model.Segment> separatorList = new LinkedList<>();

        for (int index = 0; index < separator.size() - 1; index++) {
            separatorList.add(new com.github.bgrochal.geomvisualiser.model.Segment(
                    color,
                    new com.github.bgrochal.geomvisualiser.model.Point(separator.get(index).getX(), separator.get(index).getY()),
                    new com.github.bgrochal.geomvisualiser.model.Point(separator.get(index + 1).getX(), separator.get(index + 1).getY())));
        }

        executor.plotMultipleObjects(separatorList);
    }

    public void visualiseSweepLine(Point point) {
        executor.plotObject(new com.github.bgrochal.geomvisualiser.model.Line(
                new Color(0, 0, 255),
                new com.github.bgrochal.geomvisualiser.model.Point(point.getX(), point.getY()),
                new com.github.bgrochal.geomvisualiser.model.Point(point.getX(), 0)
        ));
    }

    public void clearPlot() {
        executor.clearPanel();
    }

}
