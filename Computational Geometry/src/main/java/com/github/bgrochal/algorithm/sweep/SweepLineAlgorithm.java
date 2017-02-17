package com.github.bgrochal.algorithm.sweep;

import com.github.bgrochal.algorithm.GeomAlgorithm;
import com.github.bgrochal.algorithm.sweep.model.Event;
import com.github.bgrochal.algorithm.sweep.model.Point;
import com.github.bgrochal.algorithm.sweep.model.Segment;
import com.github.bgrochal.algorithm.sweep.model.SweepStateItem;
import com.github.bgrochal.algorithm.sweep.visualisation.Snapshot;
import com.github.bgrochal.algorithm.sweep.visualisation.VisualiserConnector;
import com.github.bgrochal.middleware.algorithm.utils.PointsOrientation;
import com.github.bgrochal.middleware.model.Vector;
import com.google.common.math.DoubleMath;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.bgrochal.algorithm.sweep.model.EventType.*;
import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;

/**
 * @author Bartłomiej Grochal
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class SweepLineAlgorithm implements GeomAlgorithm {
    /*
     * TODO: At least three segments intersect in the same point - probably works, but tests needed.
     * TODO: Dealing with vertical segments.
     * TODO: Not all intersections are discovered when multiple points have the same OX, but different OY coordinate(s).
     */

    private List<Snapshot> visualisationSnapshots;
    private VisualiserConnector connector;
    private int currentSnapshot;

    private Set<Event> intersections;
    private List<Segment> segments;


    public SweepLineAlgorithm() {
        visualisationSnapshots = new LinkedList<>();
        connector = new VisualiserConnector();
        currentSnapshot = 0;

        intersections = new HashSet<>();
    }


    @Override
    public void run() {
        List<Segment> segments = getData();

        TreeSet<Event> eventsStructure = initializeEventsStructure(segments);
        LinkedList<SweepStateItem> sweepStateStructure = new LinkedList<>();

        while (!eventsStructure.isEmpty()) {
            Event event = eventsStructure.first();

            if (event.getType().equals(START)) {
                /* Updating sweep state structure. */
                // Creating new sweet state structure item with a segment, which start point is current event. //
                SweepStateItem newItem = new SweepStateItem(event.getSegments().get(0));

                // Inserting new segment into sweep state structure. //
                int position = Collections.binarySearch(sweepStateStructure, newItem);
                if (position < 0) {
                    position = -(position + 1);
                    sweepStateStructure.add(position, newItem);
                } else if (!sweepStateStructure.get(position).equals(newItem)) {
                    sweepStateStructure.add(position, newItem);
                }

                /* Updating neighbors events structure. */
                List<Event> intersections = getIntersections(newItem, sweepStateStructure, eventsStructure, event);
                eventsStructure.addAll(intersections);
            } else if (event.getType().equals(END)) {
                /* Updating sweep state structure. */
                // Getting a segment, which end is current point. //
                SweepStateItem correspondingItem = getCorrespondingSweepStateItem(event, sweepStateStructure);
                int position = sweepStateStructure.indexOf(correspondingItem);

                // Updating neighbors. //
                SweepStateItem upperNeighbor =
                        position == sweepStateStructure.size() - 1 ? null : sweepStateStructure.get(position + 1);
                SweepStateItem lowerNeighbor =
                        position == 0 ? null : sweepStateStructure.get(position - 1);

                // Removing current segment from sweep state structure. //
                sweepStateStructure.remove(correspondingItem);


                /* Updating events structure. */
                if (upperNeighbor != null && lowerNeighbor != null &&
                        areIntersected(upperNeighbor.getSegment(), lowerNeighbor.getSegment())) {
                    getIntersection(upperNeighbor.getSegment(), lowerNeighbor.getSegment(), eventsStructure, event.getPoint().getX()).
                            ifPresent(eventsStructure::add);
                }
            } else {
                /* Updating sweep state structure. */
                // Getting segments intersected in given point. //
                SweepStateItem firstItem = sweepStateStructure.stream().
                        filter(sweepStateItem -> sweepStateItem.getSegment().equals(event.getSegments().get(0))).
                        findFirst().
                        get();

                SweepStateItem secondItem = sweepStateStructure.stream().
                        filter(sweepStateItem -> sweepStateItem.getSegment().equals(event.getSegments().get(1))).
                        findFirst().
                        get();

                // Swapping neighbors. //
                int firstItemIndex = sweepStateStructure.indexOf(firstItem);
                int secondItemIndex = sweepStateStructure.indexOf(secondItem);
                Collections.swap(sweepStateStructure, firstItemIndex, secondItemIndex);

                int tempIndex = firstItemIndex;
                firstItemIndex = secondItemIndex;
                secondItemIndex = tempIndex;


                /* Updating events structure. */
                if (firstItemIndex < secondItemIndex) {
                    if (firstItemIndex > 0 && areIntersected(firstItem.getSegment(), sweepStateStructure.get(firstItemIndex - 1).getSegment())) {
                        getIntersection(firstItem.getSegment(), sweepStateStructure.get(firstItemIndex - 1).getSegment(), eventsStructure, event.getPoint().getX()).
                                ifPresent(eventsStructure::add);
                    }
                    if (secondItemIndex < sweepStateStructure.size() - 1 && areIntersected(secondItem.getSegment(), sweepStateStructure.get(secondItemIndex + 1).getSegment())) {
                        getIntersection(secondItem.getSegment(), sweepStateStructure.get(secondItemIndex + 1).getSegment(), eventsStructure, event.getPoint().getX()).
                                ifPresent(eventsStructure::add);
                    }
                } else {
                    if (secondItemIndex > 0 && areIntersected(secondItem.getSegment(), sweepStateStructure.get(secondItemIndex - 1).getSegment())) {
                        getIntersection(secondItem.getSegment(), sweepStateStructure.get(secondItemIndex - 1).getSegment(), eventsStructure, event.getPoint().getX()).
                                ifPresent(eventsStructure::add);
                    }
                    if (firstItemIndex < sweepStateStructure.size() - 1 && areIntersected(firstItem.getSegment(), sweepStateStructure.get(firstItemIndex + 1).getSegment())) {
                        getIntersection(firstItem.getSegment(), sweepStateStructure.get(firstItemIndex + 1).getSegment(), eventsStructure, event.getPoint().getX()).
                                ifPresent(eventsStructure::add);
                    }
                }
            }

            /*
             * eventsStructure.forEach(System.out::println);
             * System.out.println();
             * sweepStateStructure.forEach(System.out::println);
             * System.out.println();
             */

            eventsStructure.remove(event);
            makeSnapshot(new Snapshot(event.getPoint(), segments, new LinkedList<>(intersections.stream().map(Event::getPoint).collect(Collectors.toList()))));
        }

        printSummary();
        displayResult();
    }

    @Override
    public void displayResult() {
        connector.clearPlot();
        connector.visualiseSegments(segments);
        connector.visualiseIntersectionPoints(intersections.stream().map(Event::getPoint).collect(Collectors.toList()));
    }

    @Override
    public void displayNextSnapshot() {
        // TODO: Extract to common interface.
        if (currentSnapshot == visualisationSnapshots.size()) {
            displayResult();
            return;
        }

        plot();
        currentSnapshot++;
    }

    @Override
    public void displayPreviousSnapshot() {
        // TODO: Extract to common interface.
        if (currentSnapshot == 0) {
            return;
        }

        currentSnapshot--;
        plot();

    }


    private List<Segment> getData() {
        /*
         * segments = new LinkedList<>();
         * segments.addAll(Arrays.asList(
         * new Segment(new Point(0, 0), new Point(1, 1)),
         * new Segment(new Point(0.2, 0.1), new Point(0.5, 0.9))
         * ));
         */

        /*
         * segments = new LinkedList<>();
         * segments.addAll(new FileParser("segments.txt").parseSegments().
         *        stream().
         *        map(segment -> new Segment(
         *                new Point(segment.getStart().getX(), segment.getStart().getY()),
         *                new Point(segment.getEnd().getX(), segment.getEnd().getY())
         *        )).
         *        collect(Collectors.toList()));
         */

        segments = connector.getVisualisedObjects();

        return segments;
    }

    private TreeSet<Event> initializeEventsStructure(List<Segment> segments) {
        TreeSet<Event> eventsStructure = new TreeSet<>();

        segments.forEach(segment -> eventsStructure.addAll(Arrays.asList(
                new Event(Collections.singletonList(segment), START, segment.getStart()),
                new Event(Collections.singletonList(segment), END, segment.getEnd()))));
        return eventsStructure;
    }

    private boolean isPointCollinear(Segment segment, Point point) {
        return PointsOrientation.getDirection(new Vector<>(segment.getEnd(), segment.getStart()), point).
                equals(PointsOrientation.Direction.ON);
    }

    private boolean areIntersected(Segment currentSegment, Segment neighbor) {
        PointsOrientation.Direction startPointDirection = PointsOrientation.getDirection(
                new Vector<>(currentSegment.getEnd(), currentSegment.getStart()), neighbor.getStart());

        if (isPointOnSegment(currentSegment, neighbor.getStart(), startPointDirection)) {
            return true;
        }

        PointsOrientation.Direction endPointDirection = PointsOrientation.getDirection(
                new Vector<>(currentSegment.getEnd(), currentSegment.getStart()), neighbor.getEnd());

        if (isPointOnSegment(currentSegment, neighbor.getEnd(), endPointDirection)) {
            return true;
        }

        if (startPointDirection.equals(endPointDirection)) {
            return startPointDirection.equals(PointsOrientation.Direction.ON);
        }


        startPointDirection = PointsOrientation.getDirection(
                new Vector<>(neighbor.getEnd(), neighbor.getStart()), currentSegment.getStart());

        if (isPointOnSegment(neighbor, currentSegment.getStart(), startPointDirection)) {
            return true;
        }

        endPointDirection = PointsOrientation.getDirection(
                new Vector<>(neighbor.getEnd(), neighbor.getStart()), currentSegment.getEnd());

        if (isPointOnSegment(neighbor, currentSegment.getEnd(), endPointDirection)) {
            return true;
        }

        return !startPointDirection.equals(endPointDirection);
    }

    private boolean isPointOnSegment(Segment segment, Point point, PointsOrientation.Direction direction) {
        if (!direction.equals(PointsOrientation.Direction.ON)) {
            return false;
        }
        return isPointOnSegment(segment, point);
    }

    private boolean isPointOnSegment(Segment segment, Point point) {
        double maxOY = Math.max(segment.getStart().getY(), segment.getEnd().getY());
        double minOY = Math.min(segment.getStart().getY(), segment.getEnd().getY());

        return DoubleMath.fuzzyCompare(segment.getStart().getX(), point.getX(), EPSILON) <= 0 &&
                DoubleMath.fuzzyCompare(segment.getEnd().getX(), point.getX(), EPSILON) >= 0 &&
                DoubleMath.fuzzyCompare(minOY, point.getY(), EPSILON) <= 0 &&
                DoubleMath.fuzzyCompare(maxOY, point.getY(), EPSILON) >= 0;
    }

    private List<Event> getIntersections(SweepStateItem sweepStateItem, LinkedList<SweepStateItem> sweepStateStructure, TreeSet<Event> eventsStructure, Event event) {
        List<Event> intersections = new LinkedList<>();

        SweepStateItem upperNeighbor = sweepStateStructure.indexOf(sweepStateItem) < sweepStateStructure.size() - 1 ?
                sweepStateStructure.get(sweepStateStructure.indexOf(sweepStateItem) + 1) : null;

        SweepStateItem lowerNeighbor = sweepStateStructure.indexOf(sweepStateItem) > 0 ?
                sweepStateStructure.get(sweepStateStructure.indexOf(sweepStateItem) - 1) : null;

        if (upperNeighbor != null && areIntersected(sweepStateItem.getSegment(), upperNeighbor.getSegment())) {
            getIntersection(sweepStateItem.getSegment(), upperNeighbor.getSegment(), eventsStructure, event.getPoint().getX()).
                    ifPresent(intersections::add);
        }

        if (lowerNeighbor != null && areIntersected(sweepStateItem.getSegment(), lowerNeighbor.getSegment())) {
            getIntersection(sweepStateItem.getSegment(), lowerNeighbor.getSegment(), eventsStructure, event.getPoint().getX()).
                    ifPresent(intersections::add);
        }

        return intersections;
    }

    private Optional<Event> getIntersection(Segment currentSegment, Segment neighbor, TreeSet<Event> eventsStructure, double currentX) {
        Point pointA = neighbor.getStart();
        Point pointB = neighbor.getEnd();
        Point pointC = currentSegment.getStart();
        Point pointD = currentSegment.getEnd();

        double determinant = (pointB.getX() - pointA.getX()) * (pointC.getY() - pointD.getY()) -
                (pointC.getX() - pointD.getX()) * (pointB.getY() - pointA.getY());

        if (DoubleMath.fuzzyCompare(determinant, 0, EPSILON) == 0) {
            if (isPointCollinear(currentSegment, neighbor.getStart()) && isPointOnSegment(currentSegment, neighbor.getStart())) {
                intersections.add(new Event(Arrays.asList(currentSegment, neighbor), INTERSECTION, neighbor.getStart()));
            }
            if (isPointCollinear(currentSegment, neighbor.getEnd()) && isPointOnSegment(currentSegment, neighbor.getEnd())) {
                intersections.add(new Event(Arrays.asList(currentSegment, neighbor), INTERSECTION, neighbor.getEnd()));
            }
            if (isPointCollinear(neighbor, currentSegment.getStart()) && isPointOnSegment(neighbor, currentSegment.getStart())) {
                intersections.add(new Event(Arrays.asList(currentSegment, neighbor), INTERSECTION, currentSegment.getStart()));
            }
            if (isPointCollinear(neighbor, currentSegment.getEnd()) && isPointOnSegment(neighbor, currentSegment.getEnd())) {
                intersections.add(new Event(Arrays.asList(currentSegment, neighbor), INTERSECTION, currentSegment.getEnd()));
            }

            return Optional.empty();
        }
        determinant = 1 / determinant;

        double[][] coefficientsMatrix = {
                {determinant * (pointC.getY() - pointD.getY()), -determinant * (pointC.getX() - pointD.getX())},
                {-determinant * (pointB.getY() - pointA.getY()), determinant * (pointB.getX() - pointA.getX())}
        };

        double[] freeTermsVector = {
                pointC.getX() - pointA.getX(), pointC.getY() - pointA.getY()
        };

        double[] results = {
                dotProduct(coefficientsMatrix[0], freeTermsVector), dotProduct(coefficientsMatrix[1], freeTermsVector)
        };


        Point intersection = new Point(
                pointA.getX() + results[0] * (pointB.getX() - pointA.getX()),
                pointA.getY() + results[0] * (pointB.getY() - pointA.getY()));

        int comparisonResult = DoubleMath.fuzzyCompare(intersection.getX(), currentX, EPSILON);
        if (comparisonResult < 0) {
            return Optional.empty();
        }

        if (!eventsStructure.stream().
                filter(event -> event.getSegments().size() == 2).
                filter(event -> event.getSegments().equals(Arrays.asList(currentSegment, neighbor)) || event.getSegments().equals(Arrays.asList(neighbor, currentSegment))).
                findFirst().
                isPresent()) {
            Event intersectionEvent = new Event(Arrays.asList(currentSegment, neighbor), INTERSECTION, intersection);
            intersections.add(intersectionEvent);

            return comparisonResult == 0 ? Optional.empty() :
                    Optional.of(new Event(Arrays.asList(currentSegment, neighbor), INTERSECTION, intersection));
        }

        return Optional.empty();
    }

    private double dotProduct(double[] row, double[] column) {
        return IntStream.range(0, row.length).mapToDouble(index -> row[index] * column[index]).sum();
    }

    private SweepStateItem getCorrespondingSweepStateItem(Event event, List<SweepStateItem> sweepStateStructure) {
        return sweepStateStructure.stream().
                filter(sweepStateItem -> sweepStateItem.getSegment().equals(event.getSegments().get(0))).
                findFirst().
                get();
    }

    private void makeSnapshot(Snapshot visualisationSnapshot) {
        // TODO: Extract to common interface.
        visualisationSnapshots.add(visualisationSnapshot);
    }

    private void plot() {
        // TODO: Extract to common interface.
        connector.clearPlot();
        connector.visualiseSweepLine(visualisationSnapshots.get(currentSnapshot).getCurrentPoint());
        connector.visualiseSegments(visualisationSnapshots.get(currentSnapshot).getSegments());
        connector.visualiseIntersectionPoints(visualisationSnapshots.get(currentSnapshot).getIntersections());
    }

    private void printSummary() {
        StringBuilder coordinates = new StringBuilder();
        intersections.forEach(event -> coordinates.
                append(event).
                append(" of: ").
                append(event.getSegmentsAsString()).
                append("\n"));

        StringBuilder builder = new StringBuilder();
        builder.append("===== SUMMARY =====\n").
                append(String.format("Intersections found: %d\n", intersections.size())).
                append("Coordinates of intersections and intersected segments:\n").
                append(coordinates.toString());

        System.out.println(builder.toString());
    }

}
