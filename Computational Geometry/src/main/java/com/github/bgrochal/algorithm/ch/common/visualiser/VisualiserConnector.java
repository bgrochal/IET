package com.github.bgrochal.algorithm.ch.common.visualiser;

import com.github.bgrochal.algorithm.ch.common.model.Point;
import com.github.bgrochal.geomvisualiser.geogebra.api.CommandExecutor;
import com.github.bgrochal.geomvisualiser.model.Polygon;
import com.github.bgrochal.geomvisualiser.model.Segment;
import com.github.bgrochal.geomvisualiser.model.utils.Color;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Bartłomiej Grochal
 */
public class VisualiserConnector {

    private CommandExecutor executor;


    public VisualiserConnector() {
        executor = new CommandExecutor();
    }


    public void visualiseConvexHull(Iterable<Point> allPoints, Iterable<Point> convexHullPoints) {
        List<com.github.bgrochal.geomvisualiser.model.Point> allPointsList = new LinkedList<>();
        List<com.github.bgrochal.geomvisualiser.model.Point> convexHullPointsList = new LinkedList<>();

        allPoints.forEach(point -> allPointsList.
                add(new com.github.bgrochal.geomvisualiser.model.Point(point.getX(), point.getY())));

        convexHullPoints.forEach(point -> allPointsList.
                add(new com.github.bgrochal.geomvisualiser.model.Point(new Color(0, 255, 0), point.getX(), point.getY())));

        convexHullPoints.forEach(point -> convexHullPointsList.
                add(new com.github.bgrochal.geomvisualiser.model.Point(point.getX(), point.getY())));

        executor.plotMultipleObjects(allPointsList);
        executor.plotObject(new Polygon(new Color(0, 0, 255), convexHullPointsList));
    }

    public void visualiseAlgorithmStep(Iterable<Point> allPoints, Iterable<Point> convexHullCandidates) {
        List<com.github.bgrochal.geomvisualiser.model.Point> allPointsList = new LinkedList<>();
        List<Segment> convexHullSegments = new LinkedList<>();

        allPoints.forEach(point -> allPointsList.
                add(new com.github.bgrochal.geomvisualiser.model.Point(point.getX(), point.getY()))
        );

        Iterator<Point> iterator = convexHullCandidates.iterator();
        if (!iterator.hasNext()) {
            executor.plotMultipleObjects(allPointsList);
            return;
        }

        Point start = iterator.next();
        while (iterator.hasNext()) {
            Point end = iterator.next();
            convexHullSegments.add(new Segment(new Color(255, 0, 0),
                    new com.github.bgrochal.geomvisualiser.model.Point(start.getX(), start.getY()),
                    new com.github.bgrochal.geomvisualiser.model.Point(end.getX(), end.getY())));
            start = end;
        }

        executor.plotMultipleObjects(Stream.
                concat(allPointsList.stream(), convexHullSegments.stream()).
                collect(Collectors.toList()));
    }

    public void clearPlot() {
        executor.clearPanel();
    }

}
