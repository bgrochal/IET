package com.github.bgrochal.algorithm.ch.common.algorithm;

import com.github.bgrochal.algorithm.GeomAlgorithm;
import com.github.bgrochal.algorithm.ch.common.model.Point;
import com.github.bgrochal.algorithm.ch.common.visualiser.Snapshot;
import com.github.bgrochal.algorithm.ch.common.visualiser.VisualiserConnector;
import com.google.common.math.DoubleMath;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;

/**
 * @author Bartłomiej Grochal
 */
public abstract class ConvexHullAlgorithm implements GeomAlgorithm {

    private final List<Snapshot> visualisationSnapshots;

    private VisualiserConnector connector;
    private int currentSnapshot;


    protected ConvexHullAlgorithm() {
        visualisationSnapshots = new LinkedList<>();
        connector = new VisualiserConnector();
        currentSnapshot = 0;
    }


    @Override
    public void run() {
        long startTime = System.nanoTime();
        computeConvexHull();
        long endTime = System.nanoTime();

        Logger.getAnonymousLogger().info("Time of execution: " + ((endTime - startTime) / 1e6) + " ms.");
    }

    @Override
    public void displayResult() {
        connector.clearPlot();
        connector.visualiseConvexHull(getAllPoints(), getConvexHullPoints());
    }

    @Override
    public void displayNextSnapshot() {
        if (currentSnapshot == visualisationSnapshots.size()) {
            displayResult();
            return;
        }

        plot();
        currentSnapshot++;
    }

    @Override
    public void displayPreviousSnapshot() {
        if (currentSnapshot == 0) {
            return;
        }

        currentSnapshot--;
        plot();
    }


    protected abstract void computeConvexHull();

    protected abstract Iterable<Point> getAllPoints();

    protected abstract Iterable<Point> getConvexHullPoints();


    protected void makeSnapshot(Snapshot visualisationSnapshot) {
        visualisationSnapshots.add(visualisationSnapshot);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    protected Point getFirstPoint(List<Point> points) {
        return points.stream().min((first, second) -> {
            int comparatorY = DoubleMath.fuzzyCompare(first.getY(), second.getY(), EPSILON);
            return comparatorY == 0 ? DoubleMath.fuzzyCompare(first.getX(), second.getX(), EPSILON) : comparatorY;
        }).get();
    }


    private void plot() {
        connector.clearPlot();
        connector.visualiseAlgorithmStep(
                visualisationSnapshots.get(currentSnapshot).getAllPoints(),
                visualisationSnapshots.get(currentSnapshot).getConvexHullCandidates());
    }

}
