package com.github.bgrochal.algorithm.ch.graham.algorithm;

import com.github.bgrochal.algorithm.ch.common.algorithm.ConvexHullAlgorithm;
import com.github.bgrochal.algorithm.ch.common.model.Point;
import com.github.bgrochal.algorithm.ch.common.visualiser.Snapshot;
import com.github.bgrochal.middleware.algorithm.utils.PointsOrientation;
import com.github.bgrochal.middleware.algorithm.utils.PointsOrientation.Direction;
import com.github.bgrochal.middleware.model.Vector;
import com.google.common.math.DoubleMath;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;

/**
 * @author Bartłomiej Grochal
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class GrahamAlgorithm extends ConvexHullAlgorithm {

    private final List<Point> points;
    private final Stack<Point> pointsStack;


    public GrahamAlgorithm(List<com.github.bgrochal.middleware.model.Point> points) {
        assert points.size() >= 3;
        this.points = points.stream().map(point -> new Point(point.getX(), point.getY())).collect(Collectors.toList());

        pointsStack = new Stack<>();
    }


    @Override
    protected Iterable<Point> getAllPoints() {
        return points.stream().filter(point -> !pointsStack.contains(point)).collect(Collectors.toList());
    }

    @Override
    protected Iterable<Point> getConvexHullPoints() {
        return pointsStack;
    }

    @Override
    protected void computeConvexHull() {
        Set<Point> convexHull = new TreeSet<>();

        /* Taking the first point. */
        Point first = getFirstPoint(points);
        points.forEach(point -> point.setStartingPoint(first));

        /* Sorting by angle and removing redundant points. */
        convexHull.addAll(points.stream().filter(point -> !point.equals(first)).collect(Collectors.toList()));

        for (double cosine : convexHull.stream().map(Point::getAngleCosine).collect(Collectors.toSet())) {
            List<Point> list = convexHull.
                    stream().
                    filter(p -> DoubleMath.fuzzyCompare(p.getAngleCosine(), cosine, EPSILON) == 0).
                    collect(Collectors.toList());

            if (list.size() > 1) {
                Point max = list.stream().max((p1, p2) -> DoubleMath.fuzzyCompare(p1.getHypotenuse(), p2.getHypotenuse(), EPSILON)).get();
                convexHull.removeAll(list.stream().filter(point -> !point.equals(max)).collect(Collectors.toList()));
            }
        }

        /* Stack initialization. */
        pointsStack.push(first);
        convexHull.stream().limit(2).forEach(pointsStack::push);

        /* Iteration. */
        makeSnapshot(new Snapshot(convexHull, new LinkedList<>(pointsStack)));
        convexHull.stream().skip(2).forEach(point -> {
            Vector<Point> vector = new Vector<>(pointsStack.pop(), pointsStack.pop());

            while (true) {
                Direction orientation = PointsOrientation.getDirection(vector, point);
                if (orientation == Direction.RIGHT || orientation == Direction.ON) {
                    vector = new Vector<>(vector.getStart(), pointsStack.pop());
                } else {
                    break;
                }
            }

            pointsStack.push(vector.getStart());
            pointsStack.push(vector.getEnd());
            pointsStack.push(point);

            makeSnapshot(new Snapshot(convexHull, new LinkedList<>(pointsStack)));
        });
    }

}
