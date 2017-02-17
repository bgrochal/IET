package com.github.bgrochal.algorithm.ch.jarvis.algorithm;

import com.github.bgrochal.algorithm.ch.common.algorithm.ConvexHullAlgorithm;
import com.github.bgrochal.algorithm.ch.common.model.Point;
import com.github.bgrochal.algorithm.ch.common.visualiser.Snapshot;
import com.github.bgrochal.middleware.algorithm.utils.PointsOrientation.Direction;
import com.github.bgrochal.middleware.model.Vector;
import com.google.common.math.DoubleMath;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;
import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.getDistance;
import static com.github.bgrochal.middleware.algorithm.utils.PointsOrientation.Direction.LEFT;
import static com.github.bgrochal.middleware.algorithm.utils.PointsOrientation.Direction.ON;
import static com.github.bgrochal.middleware.algorithm.utils.PointsOrientation.getDirection;

/**
 * @author Bartłomiej Grochal
 */
public class JarvisAlgorithm extends ConvexHullAlgorithm {

    private final LinkedList<Point> points;
    private List<Point> convexHull;


    public JarvisAlgorithm(LinkedList<com.github.bgrochal.middleware.model.Point> points) {
        assert points.size() >= 3;

        this.points = new LinkedList<>();
        this.points.addAll(points.stream().map(point -> new Point(point.getX(), point.getY())).collect(Collectors.toList()));

        convexHull = new LinkedList<>();
    }


    @Override
    protected Iterable<Point> getAllPoints() {
        return points.stream().filter(point -> !convexHull.contains(point)).collect(Collectors.toList());
    }

    @Override
    protected Iterable<Point> getConvexHullPoints() {
        return convexHull;
    }

    @Override
    protected void computeConvexHull() {
        /* Taking the first point. */
        Point first = getFirstPoint(points);

        /* Iteration. */
        Point convexHullPoint;
        Point lastAddedPoint = first;

        do {
            convexHull.add(lastAddedPoint);
            makeSnapshot(new Snapshot(points, new LinkedList<>(convexHull)));

            convexHullPoint = points.getFirst() != first ? points.getFirst() : points.get(1);
            for (Point point : points) {
                Direction direction = getDirection(new Vector<>(convexHullPoint, lastAddedPoint), point);
                if (!point.equals(convexHullPoint) && !point.equals(lastAddedPoint) && direction != LEFT) {
                    if (direction == ON && DoubleMath.fuzzyCompare(getDistance(lastAddedPoint, convexHullPoint), getDistance(lastAddedPoint, point), EPSILON) != -1) {
                        continue;
                    }
                    List<Point> currentConvexHull = new LinkedList<>(convexHull);
                    currentConvexHull.add(convexHullPoint);
                    makeSnapshot(new Snapshot(points, currentConvexHull));
                    convexHullPoint = point;
                }
            }

            lastAddedPoint = convexHullPoint;
        }
        while (convexHullPoint != null && !convexHullPoint.equals(first));
    }

}
