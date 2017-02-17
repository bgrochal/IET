package com.github.bgrochal.algorithm.location.separators;

import com.github.bgrochal.algorithm.GeomAlgorithm;
import com.github.bgrochal.algorithm.location.separators.lib.AvlTree;
import com.github.bgrochal.algorithm.location.separators.model.Point;
import com.github.bgrochal.algorithm.location.separators.model.Segment;
import com.github.bgrochal.algorithm.location.separators.model.SweepInterval;
import com.github.bgrochal.algorithm.location.separators.model.TreeNodeData;
import com.github.bgrochal.algorithm.location.separators.visualisation.*;
import com.github.bgrochal.geomvisualiser.model.utils.Color;
import com.github.bgrochal.middleware.algorithm.utils.PointsOrientation;
import com.github.bgrochal.middleware.model.Vector;
import com.github.bgrochal.parser.FileParser;
import com.google.common.math.DoubleMath;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;
import static com.github.bgrochal.middleware.algorithm.utils.PointsOrientation.Direction.LEFT;
import static com.github.bgrochal.middleware.algorithm.utils.PointsOrientation.Direction.RIGHT;

/**
 * @author Bartłomiej Grochal
 */
public class PointLocationAlgorithm implements GeomAlgorithm {

    private VisualiserConnector connector;

    private Point requestedPoint;
    private List<Point> requestedPolygon;

    private List<Point> points;
    private List<Segment> segments;
    private List<Segment> addedSegments;

    private List<Snapshot> snapshots;
    private int currentSnapshot;


    public PointLocationAlgorithm() {
        connector = new VisualiserConnector();

        points = new LinkedList<>();
        segments = new LinkedList<>();
        addedSegments = new LinkedList<>();

        snapshots = new LinkedList<>();
        currentSnapshot = -1;
    }


    @Override
    public void run() {
        getData();

        /*
         * points.forEach(System.out::println);
         * System.out.println();
         * segments.forEach(System.out::println);
         */

        TreeSet<Point> sortedPoints = new TreeSet<>();
        points.forEach(sortedPoints::add);

        regulariseDivision(sortedPoints);

        HashMap<Segment, Integer> weights = getWeights(sortedPoints);

        Pair<List<List<Point>>, AvlTree<TreeNodeData>> separators = getSeparators(sortedPoints, weights);
        List<List<Point>> separatorsList = separators.getKey();
        AvlTree<TreeNodeData> separatorsTree = separators.getValue();

        /* Separators list. */
//        Pair<List<Point>, List<Point>> borderSeparators = findRequestedPoint(separatorsList);
//        requestedPolygon = getRequestedPolygon(borderSeparators);
//        assert requestedPolygon == null;

        /* Separators tree. */
        int separatorAboveNumber = findRequestedPoint(separatorsTree);
        // TODO: It shouldn't be implemented this way.
        requestedPolygon = getRequestedPolygon(
                new Pair<>(separatorsList.get(separatorAboveNumber), separatorsList.get(separatorAboveNumber + 1)));
        assert requestedPolygon == null;

        displayResult();
    }

    @Override
    public void displayResult() {
        connector.visualiseSegments(segments);
        connector.visualiseAddedSegments(addedSegments);
        connector.visualisePoints(points);
        connector.visualisePoints(Collections.singleton(requestedPoint));
        connector.visualisePolygon(requestedPolygon);
    }

    @Override
    public void displayNextSnapshot() {
        if (currentSnapshot == snapshots.size()) {
            return;
        }
        if (currentSnapshot == snapshots.size() - 1) {
            displayResult();
            currentSnapshot++;
            return;
        }

        currentSnapshot++;
        plot();
    }

    @Override
    public void displayPreviousSnapshot() {
        if (currentSnapshot <= 0) {
            return;
        }

        currentSnapshot--;
        plot();
    }


    private void plot() {
        Snapshot snapshot = snapshots.get(currentSnapshot);
        connector.clearPlot();
        connector.visualisePoints(points);
        connector.visualisePoints(Collections.singleton(requestedPoint));

        if (snapshot instanceof SweepSnapshot) {
            connector.visualiseSegments(((SweepSnapshot) snapshot).getDivisionSegments());
            connector.visualiseAddedSegments(((SweepSnapshot) snapshot).getAddedSegments());
            connector.visualiseSweepLine(((SweepSnapshot) snapshot).getSweepPosition());
            connector.visualiseSegments(((SweepSnapshot) snapshot).getSweepingSegments(), new Color(0, 255, 0));
            return;
        }

        connector.visualiseSegments(segments);
        connector.visualiseAddedSegments(addedSegments);

        if (snapshot instanceof SeparatorSnapshot) {
            if (currentSnapshot > 0 && snapshots.get(currentSnapshot - 1) instanceof SeparatorSnapshot) {
                connector.visualiseSeparators(Arrays.asList(
                        ((SeparatorSnapshot) snapshots.get(currentSnapshot - 1)).getSeparator(),
                        ((SeparatorSnapshot) snapshot).getSeparator()));
            } else {
                connector.visualiseSeparator(((SeparatorSnapshot) snapshot).getSeparator());
            }
        }
        if (snapshot instanceof LocationSnapshot) {
            connector.visualiseSegments(((LocationSnapshot) snapshot).getLeftSeparator(), new Color(0, 128, 224));
            connector.visualiseSegments(((LocationSnapshot) snapshot).getRightSeparator(), new Color(255, 224, 32));
            connector.visualiseSegments(((LocationSnapshot) snapshot).getCurrentSeparator(), new Color(255, 0, 255));
        }
    }

    private void makeSnapshot(Snapshot snapshot) {
        snapshots.add(snapshot);
    }

    private void getData() {
//        Pair<Point, Pair<List<Point>, List<Segment>>> visualisedObjects = connector.getVisualisedObjects();
//        Point requestedPointToSave = visualisedObjects.getKey();
//        List<Segment> segmentsToSave = visualisedObjects.getValue().getValue();
//
//        FileWriter divisionWriter = new FileWriter("test_division.txt");
//        FileWriter pointWriter = new FileWriter("test_point.txt");
//        divisionWriter.saveSegments(segmentsToSave);
//        pointWriter.savePoints(Collections.singletonList(requestedPointToSave));

        FileParser reader = new FileParser("second_division.txt");
        HashSet<Point> readPoints = new HashSet<>();

        reader.parseSegments().forEach(readSegment -> {
            Point startPoint = readPoints.stream().filter(point ->
                    DoubleMath.fuzzyCompare(point.getX(), readSegment.getStart().getX(), EPSILON) == 0 &&
                            DoubleMath.fuzzyCompare(point.getY(), readSegment.getStart().getY(), EPSILON) == 0
            ).findFirst().orElse(new Point(readSegment.getStart().getX(), readSegment.getStart().getY()));

            Point endPoint = readPoints.stream().filter(point ->
                    DoubleMath.fuzzyCompare(point.getX(), readSegment.getEnd().getX(), EPSILON) == 0 &&
                            DoubleMath.fuzzyCompare(point.getY(), readSegment.getEnd().getY(), EPSILON) == 0
            ).findFirst().orElse(new Point(readSegment.getEnd().getX(), readSegment.getEnd().getY()));

            if (startPoint.compareTo(endPoint) == 1) {
                Point temp = startPoint;
                startPoint = endPoint;
                endPoint = temp;
            }

            Segment segment = new Segment(startPoint, endPoint);
            startPoint.addOutgoingSegment(segment);
            endPoint.addIngoingSegment(segment);

            segments.add(segment);
            readPoints.addAll(Arrays.asList(startPoint, endPoint));
        });

        points = new LinkedList<>(readPoints);

        reader = new FileParser("second_point.txt");
        List<com.github.bgrochal.middleware.model.Point> point = reader.parsePoints();

        requestedPoint = new Point(point.get(0).getX(), point.get(0).getY());
    }

    private void regulariseDivision(TreeSet<Point> sortedPoints) {
        if (!sortedPoints.stream().limit(sortedPoints.size() - 1).skip(1)
                .anyMatch(point -> point.getIngoingSegments().size() == 0 || point.getOutgoingSegments().size() == 0)) {
            return;
        }

        TreeSet<SweepInterval> sweepStateStructure = new TreeSet<>();
        for (int index = 0; index < sortedPoints.first().getOutgoingSegments().size() - 1; index++) {
            sweepStateStructure.add(new SweepInterval(sortedPoints.first().getOutgoingSegments().get(index + 1),
                    sortedPoints.first().getOutgoingSegments().get(index), sortedPoints.first()));
        }

        makeSnapshot(new SweepSnapshot(sortedPoints.first(), new ArrayList<>(segments), new ArrayList<>(addedSegments),
                sweepStateStructure));
        sortedPoints.stream().skip(1).forEach(point -> {
            List<SweepInterval> intervalsToDelete = new LinkedList<>();
            List<Segment> newSegments = new LinkedList<>();
            newSegments.addAll(point.getOutgoingSegments());

            sweepStateStructure.forEach(interval -> {
                if (point.getIngoingSegments().contains(interval.getLowerBound()) &&
                        point.getIngoingSegments().contains(interval.getUpperBound())) {
                    intervalsToDelete.add(interval);
                } else if (point.getIngoingSegments().contains(interval.getLowerBound())) {
                    newSegments.add(interval.getUpperBound());
                    intervalsToDelete.add(interval);
                } else if (point.getIngoingSegments().contains(interval.getUpperBound())) {
                    newSegments.add(interval.getLowerBound());
                    intervalsToDelete.add(interval);
                }
            });

            if (point.getIngoingSegments().size() == 0) {
                SweepInterval containingInterval = sweepStateStructure.stream()
                        .filter(interval -> interval.containsPoint(point))
                        .findFirst()
                        .get();

                newSegments.addAll(Arrays.asList(containingInterval.getLowerBound(), containingInterval.getUpperBound()));
                intervalsToDelete.add(containingInterval);
            }

            intervalsToDelete.forEach(interval -> {
                if (interval.getGenerator().getOutgoingSegments().isEmpty() || point.getIngoingSegments().isEmpty()) {
                    addedSegments.add(new Segment(interval.getGenerator(), point));
                }
            });
            sweepStateStructure.removeAll(intervalsToDelete);

            newSegments.sort((first, second) -> {
//                int endComparator = DoubleMath.fuzzyCompare(first.getEnd().getY(), second.getEnd().getY(), EPSILON);
//                int startComparator = DoubleMath.fuzzyCompare(first.getStart().getY(), second.getStart().getY(), EPSILON);
                PointsOrientation.Direction startDirection = PointsOrientation.getDirection(
                        new Vector<>(first.getEnd(), first.getStart()), second.getStart());
                PointsOrientation.Direction endDirection = PointsOrientation.getDirection(
                        new Vector<>(first.getEnd(), first.getStart()), second.getEnd());

                return startDirection.getDirection() == 0 ? endDirection.getDirection() : startDirection.getDirection();
            });
            for (int index = 0; index < newSegments.size() - 1; index++) {
                sweepStateStructure.add(new SweepInterval(newSegments.get(index), newSegments.get(index + 1), point));
            }

            makeSnapshot(new SweepSnapshot(point, new ArrayList<>(segments), new ArrayList<>(addedSegments),
                    sweepStateStructure));
        });

        segments.addAll(addedSegments);
        addedSegments.forEach(segment -> {
            segment.getStart().addOutgoingSegment(segment);
            segment.getEnd().addIngoingSegment(segment);
        });
    }

    private HashMap<Segment, Integer> getWeights(TreeSet<Point> sortedPoints) {
        HashMap<Segment, Integer> weights = new HashMap<>();
        segments.forEach(segment -> weights.put(segment, 1));

        sortedPoints.stream().limit(sortedPoints.size() - 1).forEach(point -> {
            int inWeight = point.getIngoingSegments().stream().mapToInt(weights::get).sum();
            int outWeight = point.getOutgoingSegments().stream().mapToInt(weights::get).sum();

            if (inWeight > outWeight) {
                Segment firstOutgoing = point.getOutgoingSegments().get(0);
                weights.put(firstOutgoing, (inWeight - outWeight + weights.get(firstOutgoing)));
            }
        });

        sortedPoints.descendingSet().stream().limit(sortedPoints.size() - 1).forEach(point -> {
            int inWeight = point.getIngoingSegments().stream().mapToInt(weights::get).sum();
            int outWeight = point.getOutgoingSegments().stream().mapToInt(weights::get).sum();

            if (outWeight > inWeight) {
                Segment firstIngoing = point.getIngoingSegments().get(0);
                weights.put(firstIngoing, (outWeight - inWeight + weights.get(firstIngoing)));
            }
        });

        return weights;
    }

    private Pair<List<List<Point>>, AvlTree<TreeNodeData>> getSeparators(TreeSet<Point> sortedPoints,
                                                                         HashMap<Segment, Integer> weights) {
        List<List<Point>> separators = new LinkedList<>();
        AvlTree<TreeNodeData> tree = new AvlTree<>();
        int separatorNumber = 0;

        for (Point point : sortedPoints) {
            while (hasAnyUnvisitedNeighbor(point, weights)) {
                List<Point> separator = new LinkedList<>(Collections.singleton(point));
                findSeparator(point, weights, separator);

                separators.add(separator);
                makeSnapshot(new SeparatorSnapshot(separator));

                tree.insert(new TreeNodeData(separatorNumber, separator));
                separatorNumber++;
            }
        }

        removeDuplicatedSegments(tree.root);        // TODO: it shouldn't be implemented this way
        return new Pair<>(separators, tree);
    }

    private void findSeparator(Point currentPoint, HashMap<Segment, Integer> weights, List<Point> separator) {
        if (currentPoint.getOutgoingSegments().size() == 0 || !hasAnyUnvisitedNeighbor(currentPoint, weights)) {
            return;
        }

        Segment currentSegment = currentPoint.getOutgoingSegments().stream()
                .filter(segment -> weights.get(segment) > 0)
                .collect(Collectors.toCollection(LinkedList::new))
                .getFirst();

        separator.add(currentSegment.getEnd());
        weights.put(currentSegment, weights.get(currentSegment) - 1);

        findSeparator(currentSegment.getEnd(), weights, separator);
    }

    private boolean hasAnyUnvisitedNeighbor(Point currentPoint, HashMap<Segment, Integer> weights) {
        return currentPoint.getOutgoingSegments().stream()
                .mapToInt(weights::get)
                .anyMatch(unvisitedNeighbors -> unvisitedNeighbors > 0);
    }

    private void removeDuplicatedSegments(AvlTree.AvlNode<TreeNodeData> root) {
        if (root.getLeft() != null) {
            removeDuplicatedSegments(root.getLeft());
            root.getLeft().getElement().removeDuplicatedSegments(root.getElement());
        }
        if (root.getRight() != null) {
            removeDuplicatedSegments(root.getRight());
            root.getRight().getElement().removeDuplicatedSegments(root.getElement());
        }
    }

    private Pair<List<Point>, List<Point>> findRequestedPoint(List<List<Point>> separators) {
        for (int index = 0; index < separators.size() - 1; index++) {
            if (isSeparatorAbove(separators.get(index)) && isSeparatorBelow(separators.get(index + 1))) {
                return new Pair<>(separators.get(index), separators.get(index + 1));
            }
        }

        return null;
    }

    private int findRequestedPoint(AvlTree<TreeNodeData> separatorsTree) {
        AvlTree.AvlNode<TreeNodeData> leftPointer = separatorsTree.getFirst();
        AvlTree.AvlNode<TreeNodeData> rightPointer = separatorsTree.getLast();
        AvlTree.AvlNode<TreeNodeData> treeLevel = separatorsTree.root;

        PointsOrientation.Direction previousDirection = null;
        while (leftPointer.getElement().getSeparatorNumber() < rightPointer.getElement().getSeparatorNumber()) {
            makeSnapshot(new LocationSnapshot(leftPointer.getElement().getSeparator(),
                    rightPointer.getElement().getSeparator(), treeLevel.getElement().getSeparator()));
            if (leftPointer.getElement().getSeparatorNumber() < treeLevel.getElement().getSeparatorNumber() &&
                    treeLevel.getElement().getSeparatorNumber() <= rightPointer.getElement().getSeparatorNumber()) {
                Segment correspondingSegment = null;
                // TODO: change to binsearch
                for (int index = 0; index < treeLevel.getElement().getSeparator().size(); index++) {
                    Segment currentSegment = treeLevel.getElement().getSeparator().get(index);
                    if (DoubleMath.fuzzyCompare(currentSegment.getStart().getX(), requestedPoint.getX(), EPSILON) != 1 &&
                            DoubleMath.fuzzyCompare(currentSegment.getEnd().getX(), requestedPoint.getX(), EPSILON) != -1) {
                        correspondingSegment = currentSegment;
                        break;
                    }
                }

                PointsOrientation.Direction requestedPointLocation = correspondingSegment == null ? previousDirection :
                        PointsOrientation.getDirection(new Vector<>(correspondingSegment.getEnd(), correspondingSegment.getStart()), requestedPoint);
                previousDirection = requestedPointLocation != previousDirection ? requestedPointLocation : previousDirection;

                if (requestedPointLocation.equals(RIGHT)) {
                    // leftPointer = separatorsTree.getNext(treeLevel);
                    leftPointer = treeLevel;
                } else if (requestedPointLocation.equals(LEFT)) {
                    rightPointer = separatorsTree.getPrevious(treeLevel);
                }
                // TODO: Handle "on".
            } else {
                if (treeLevel.getElement().getSeparatorNumber() > rightPointer.getElement().getSeparatorNumber()) {
                    treeLevel = treeLevel.getLeft();
                } else {
                    treeLevel = treeLevel.getRight();
                }
            }
        }

        makeSnapshot(new LocationSnapshot(leftPointer.getElement().getSeparator(),
                rightPointer.getElement().getSeparator(), treeLevel.getElement().getSeparator()));

        return leftPointer.getElement().getSeparatorNumber();
    }

    private boolean isSeparatorAbove(List<Point> separator) {
        Segment correspondingSegment = findCorrespondingSegment(separator);
        assert correspondingSegment == null;

        return PointsOrientation.getDirection(
                new Vector<>(correspondingSegment.getEnd(), correspondingSegment.getStart()), requestedPoint) != LEFT;
    }

    private boolean isSeparatorBelow(List<Point> separator) {
        Segment correspondingSegment = findCorrespondingSegment(separator);
        assert correspondingSegment == null;

        return PointsOrientation.getDirection(
                new Vector<>(correspondingSegment.getEnd(), correspondingSegment.getStart()), requestedPoint) != RIGHT;
    }

    private Segment findCorrespondingSegment(List<Point> separator) {
        for (int index = 0; index < separator.size() - 1; index++) {
            if (DoubleMath.fuzzyCompare(separator.get(index).getX(), requestedPoint.getX(), EPSILON) != 1 &&
                    DoubleMath.fuzzyCompare(separator.get(index + 1).getX(), requestedPoint.getX(), EPSILON) != -1) {
                return new Segment(separator.get(index), separator.get(index + 1));
            }
        }

        return null;
    }

    private List<Point> getRequestedPolygon(Pair<List<Point>, List<Point>> borderSeparators) {
        List<Point> firstSeparator = borderSeparators.getKey();
        List<Point> secondSeparator = borderSeparators.getValue();
        Segment correspondingSegment = findCorrespondingSegment(firstSeparator);

        Point boundaryLeftPoint = null;
        for (int index = firstSeparator.indexOf(correspondingSegment.getStart()); index >= 0; index--) {
            if (secondSeparator.contains(firstSeparator.get(index))) {
                boundaryLeftPoint = firstSeparator.get(index);
                break;
            }
        }

        Point boundaryRightPoint = null;
        for (int index = firstSeparator.indexOf(correspondingSegment.getEnd()); index < firstSeparator.size(); index++) {
            if (secondSeparator.contains(firstSeparator.get(index))) {
                boundaryRightPoint = firstSeparator.get(index);
                break;
            }
        }

        List<Point> polygon = new LinkedList<>();
        for (int index = secondSeparator.indexOf(boundaryLeftPoint); index <= secondSeparator.indexOf(boundaryRightPoint); index++) {
            polygon.add(secondSeparator.get(index));
        }
        for (int index = firstSeparator.indexOf(boundaryRightPoint) - 1; index > firstSeparator.indexOf(boundaryLeftPoint); index--) {
            polygon.add(firstSeparator.get(index));
        }


        return polygon;
    }

}
