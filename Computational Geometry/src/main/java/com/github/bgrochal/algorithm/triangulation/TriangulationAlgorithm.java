package com.github.bgrochal.algorithm.triangulation;

import com.github.bgrochal.algorithm.GeomAlgorithm;
import com.github.bgrochal.algorithm.triangulation.model.*;
import com.github.bgrochal.algorithm.triangulation.visualisation.VisualiserConnector;
import com.github.bgrochal.middleware.algorithm.utils.PointsOrientation;
import com.github.bgrochal.middleware.model.Vector;
import com.github.bgrochal.parser.FileParser;
import com.google.common.math.DoubleMath;
import javafx.util.Pair;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.bgrochal.algorithm.triangulation.model.VertexType.MERGE;
import static com.github.bgrochal.algorithm.triangulation.model.VertexType.SPLIT;
import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;
import static com.github.bgrochal.middleware.algorithm.utils.PointsOrientation.Direction.*;

/**
 * @author Bartłomiej Grochal
 */
public class TriangulationAlgorithm implements GeomAlgorithm {

    private final VisualiserConnector connector;

    private List<Vertex> vertices;
    private List<Edge> edges;

    private List<Edge> addedEdges;
    private List<Vertex> removedVertices;
    private List<List<Vertex>> polygonsToTriangulation;


    public TriangulationAlgorithm() {
        connector = new VisualiserConnector();

        vertices = new LinkedList<>();
        edges = new LinkedList<>();

        addedEdges = new LinkedList<>();
        removedVertices = new LinkedList<>();
        polygonsToTriangulation = new LinkedList<>();
    }


    @Override
    public void run() {
        getData();

        /* Sweeping algorithm - splitting the polygon into OY-monotonic ones. */
        TreeSet<Vertex> eventsStructure = new TreeSet<>(vertices);
        TreeSet<Edge> sweepStateStructure = new TreeSet<>();

        while (!eventsStructure.isEmpty()) {
            Vertex event = eventsStructure.first();

            /* Setting type of each vertex. **/
            int index = vertices.indexOf(event);
            Vertex before = vertices.get(index == 0 ? vertices.size() - 1 : index - 1);
            Vertex after = vertices.get(index == vertices.size() - 1 ? 0 : index + 1);

            event.setType(VertexType.getVertexType(before, event, after));

            /* Invoking a handler corresponding to the type of current vertex. */
            switch (event.getType()) {
                case START:
                    handleStartVertex(event, sweepStateStructure);
                    break;
                case END:
                    handleEndVertex(event, sweepStateStructure);
                    break;
                case SPLIT:
                    handleSplitVertex(event, sweepStateStructure);
                    break;
                case MERGE:
                    handleMergeVertex(event, sweepStateStructure);
                    break;
                case REGULAR:
                    handleRegularVertex(event, sweepStateStructure);
                    break;
            }

            eventsStructure.remove(event);
        }

        polygonsToTriangulation.add(
                vertices.stream().filter(vertex -> !removedVertices.contains(vertex)).collect(Collectors.toList()));

        /* Reverting lists containing definition of a polygon in clockwise direction. */
        polygonsToTriangulation.forEach(polygon -> {
            if (isPolygonClockwise(polygon)) {
                Collections.reverse(polygon);
            }
        });

        /* Triangulation of successive OY-monotonic polygons. */
        polygonsToTriangulation.stream().filter(polygon -> polygon.size() > 3).forEach(polygon -> {
            polygon.forEach(System.out::println);
            System.out.println();

            int topIndex = polygon.indexOf(Collections.max(polygon));
            int bottomIndex = polygon.indexOf(Collections.min(polygon));

            TreeSet<Vertex> leftBoundary = new TreeSet<>(polygon.stream().
                    filter(vertex -> polygon.indexOf(vertex) < Math.max(topIndex, bottomIndex) && polygon.indexOf(vertex) > Math.min(topIndex, bottomIndex)).
                    collect(Collectors.toList()));
            TreeSet<Vertex> rightBoundary = new TreeSet<>(polygon.stream().
                    filter(vertex -> !leftBoundary.contains(vertex) && !vertex.equals(polygon.get(topIndex)) && !vertex.equals(polygon.get(bottomIndex))).
                    collect(Collectors.toList()));
            TreeSet<Vertex> sortedPolygon = new TreeSet<>(polygon);

            List<Edge> polygonEdges = new LinkedList<>();

            IntStream.range(0, polygon.size()).forEach(i -> polygonEdges.add(new Edge(new Segment(
                    new Point(polygon.get(i).getPoint().getX(), polygon.get(i).getPoint().getY()),
                    new Point(polygon.get((i + 1) % polygon.size()).getPoint().getX(), polygon.get((i + 1) % polygon.size()).getPoint().getY())
            ))));


            Stack<Vertex> verticesStack = new Stack<>();
            verticesStack.addAll(sortedPolygon.stream().limit(2).collect(Collectors.toList()));

            sortedPolygon.stream().skip(2).
                    forEach(vertex -> {
                        Vertex before = sortedPolygon.lower(vertex);
                        Vertex next = sortedPolygon.higher(vertex);
                        handleTriangulationVertex(vertex, before, polygon, verticesStack, leftBoundary, rightBoundary, polygonEdges);
                    });
        });

        displayResult();
    }


    @Override
    public void displayResult() {
        connector.visualisePolygon(vertices);
        connector.visualiseSegments(addedEdges);
    }

    @Override
    public void displayNextSnapshot() {

    }

    @Override
    public void displayPreviousSnapshot() {

    }


    private void getData() {
        /*
         * Pair<List<Vertex>, List<Edge>> data = connector.getVisualisedObjects();
         * vertices = data.getKey();
         * edges = data.getValue();
         *
         * FileWriter writer = new FileWriter("polygon3.txt");
         * writer.savePoints(data.getKey().stream().map(Vertex::getPoint).collect(Collectors.toList()));
         */


        FileParser parser = new FileParser("polygon.txt");
        List<com.github.bgrochal.middleware.model.Point> points = parser.parsePoints();

        points.forEach(point -> vertices.add(new Vertex(new Point(point.getX(), point.getY()))));
        IntStream.range(0, points.size()).forEach(i -> edges.add(new Edge(new Segment(
              new Point(points.get(i).getX(), points.get(i).getY()),
              new Point(points.get((i + 1) % points.size()).getX(), points.get((i + 1) % points.size()).getY())
        ))));
    }

    private void checkMonotonicity() {
        TreeSet<Vertex> eventsStructure = new TreeSet<>(vertices);

        while (!eventsStructure.isEmpty()) {
            Vertex event = eventsStructure.first();

            /* Getting type of current event (vertex). **/
            int index = vertices.indexOf(event);
            Vertex before = vertices.get(index == 0 ? vertices.size() - 1 : index - 1);
            Vertex after = vertices.get(index == vertices.size() - 1 ? 0 : index + 1);

            VertexType vertexType = VertexType.getVertexType(before, event, after);
            if (vertexType.equals(SPLIT) || vertexType.equals(MERGE)) {
                Logger.getAnonymousLogger().warning("Specified polygon is not OY-monotonic.");
                return;
            }

            eventsStructure.remove(event);
        }

        Logger.getAnonymousLogger().info("Specified polygon is OY-monotonic.");
    }

    private void handleStartVertex(Vertex vertex, TreeSet<Edge> sweepStateStructure) {
        Edge correspondingEdge = getEdgeByStartPoint(vertex);
        correspondingEdge.setHelper(vertex);

        sweepStateStructure.add(correspondingEdge);
    }

    private void handleEndVertex(Vertex vertex, TreeSet<Edge> sweepStateStructure) {
        Edge correspondingEdge = getEdgeByEndPoint(vertex);

        if (correspondingEdge.getHelper().getType().equals(MERGE)) {
            addedEdges.add(new Edge(new Segment(vertex.getPoint(), correspondingEdge.getHelper().getPoint())));
            restorePolygon(vertex, correspondingEdge.getHelper());
        }

        sweepStateStructure.remove(correspondingEdge);
    }

    private void handleSplitVertex(Vertex vertex, TreeSet<Edge> sweepStateStructure) {
        Edge leftEdge = getNearestLeftEdge(vertex, sweepStateStructure);
        addedEdges.add(new Edge(new Segment(vertex.getPoint(), leftEdge.getHelper().getPoint())));
        restorePolygon(vertex, leftEdge.getHelper());

        leftEdge.setHelper(vertex);

        Edge correspondingEdge = getEdgeByStartPoint(vertex);
        correspondingEdge.setHelper(vertex);

        sweepStateStructure.add(correspondingEdge);
    }

    private void handleMergeVertex(Vertex vertex, TreeSet<Edge> sweepStateStructure) {
        Edge correspondingEdge = getEdgeByEndPoint(vertex);

        if (correspondingEdge.getHelper().getType().equals(MERGE)) {
            addedEdges.add(new Edge(new Segment(vertex.getPoint(), correspondingEdge.getHelper().getPoint())));
            restorePolygon(vertex, correspondingEdge.getHelper());
        }

        sweepStateStructure.remove(correspondingEdge);

        Edge leftEdge = getNearestLeftEdge(vertex, sweepStateStructure);

        if (leftEdge.getHelper().getType().equals(MERGE)) {
            addedEdges.add(new Edge(new Segment(vertex.getPoint(), leftEdge.getHelper().getPoint())));
            restorePolygon(vertex, correspondingEdge.getHelper());
        }

        leftEdge.setHelper(vertex);
    }

    private void handleRegularVertex(Vertex vertex, TreeSet<Edge> sweepStateStructure) {
        Edge correspondingEdge = getEdgeByEndPoint(vertex);

        if (DoubleMath.fuzzyCompare(correspondingEdge.getSegment().getEnd().getY(), correspondingEdge.getSegment().getStart().getY(), EPSILON) < 0) {
            if (correspondingEdge.getHelper().getType().equals(MERGE)) {
                addedEdges.add(new Edge(new Segment(vertex.getPoint(), correspondingEdge.getHelper().getPoint())));
                restorePolygon(vertex, correspondingEdge.getHelper());
            }

            sweepStateStructure.remove(correspondingEdge);

            correspondingEdge = getEdgeByStartPoint(vertex);
            correspondingEdge.setHelper(vertex);

            sweepStateStructure.add(correspondingEdge);
        } else {
            Edge leftEdge = getNearestLeftEdge(vertex, sweepStateStructure);

            if (leftEdge.getHelper().getType().equals(MERGE)) {
                addedEdges.add(new Edge(new Segment(vertex.getPoint(), leftEdge.getHelper().getPoint())));
                restorePolygon(vertex, correspondingEdge.getHelper());
            }

            leftEdge.setHelper(vertex);
        }
    }

    private Edge getEdgeByStartPoint(Vertex startPoint) {
        return edges.stream().
                filter(edge -> edge.getSegment().getStart().equals(startPoint.getPoint())).
                findFirst().
                orElseThrow(IllegalArgumentException::new);
    }

    private Edge getEdgeByEndPoint(Vertex endPoint) {
        return edges.stream().
                filter(edge -> edge.getSegment().getEnd().equals(endPoint.getPoint())).
                findFirst().
                orElseThrow(IllegalArgumentException::new);
    }

    private Edge getNearestLeftEdge(Vertex vertex, TreeSet<Edge> sweepStateStructure) {
        List<Edge> leftEdges = sweepStateStructure.stream().
                filter(edge -> PointsOrientation.getDirection(new Vector<>(edge.getSegment().getEnd(),
                        edge.getSegment().getStart()), vertex.getPoint()).equals(LEFT)).
                collect(Collectors.toList());

        TreeSet<Edge> sortedLeftEdges = new TreeSet<>(leftEdges);
        return sortedLeftEdges.last();
    }

    private void restorePolygon(Vertex start, Vertex end) {
        Vertex currentStart, currentEnd;

        if (vertices.indexOf(start) < vertices.indexOf(end)) {
            currentStart = end;
            currentEnd = start;
        } else {
            currentStart = start;
            currentEnd = end;
        }

        List<Vertex> polygon = new LinkedList<>();

        while (!currentStart.equals(currentEnd)) {
            polygon.add(currentStart);

            int index = vertices.indexOf(currentStart);
            currentStart = vertices.get(index == 0 ? vertices.size() - 1 : index - 1);
        }

        polygon.add(currentEnd);

        polygonsToTriangulation.add(polygon);
        removedVertices.addAll(
                polygon.stream().filter(vertex -> !vertex.equals(start) && !vertex.equals(end)).collect(Collectors.toList()));
    }

    private void handleTriangulationVertex(Vertex vertex, Vertex before, List<Vertex> polygon, Stack<Vertex> verticesStack, TreeSet<Vertex> leftBoundary, TreeSet<Vertex> rightBoundary, List<Edge> polygonEdges) {
        if ((leftBoundary.contains(vertex) && rightBoundary.contains(verticesStack.peek())) ||
                (leftBoundary.contains(verticesStack.peek()) && rightBoundary.contains(vertex))) {
            while (verticesStack.size() > 1) {
                Vertex fromStack = verticesStack.pop();
                Segment segment = new Segment(vertex.getPoint(), fromStack.getPoint());

                int vertexIndex = polygon.indexOf(vertex);
                Point neighborBefore = polygon.get(vertexIndex == 0 ? polygon.size() - 1 : vertexIndex - 1).getPoint();
                Point neighborAfter = polygon.get(vertexIndex == polygon.size() - 1 ? 0 : vertexIndex + 1).getPoint();

                if (isSegmentInsidePolygon(segment, polygonEdges, neighborBefore, neighborAfter)) {
                    addedEdges.add(new Edge(segment));
                }
            }

            verticesStack.pop(); // There is one element on the stack.
            verticesStack.addAll(Arrays.asList(before, vertex));
        } else {
            Vertex lastPopped = verticesStack.pop();
            Segment segment;

            int vertexIndex = polygon.indexOf(vertex);
            Point neighborBefore = polygon.get(vertexIndex == 0 ? polygon.size() - 1 : vertexIndex - 1).getPoint();
            Point neighborAfter = polygon.get(vertexIndex == polygon.size() - 1 ? 0 : vertexIndex + 1).getPoint();

            while (!verticesStack.isEmpty() && isTriangle(vertex.getPoint(), verticesStack.peek().getPoint(), lastPopped.getPoint()) &&
                    isSegmentInsidePolygon((segment = new Segment(vertex.getPoint(), verticesStack.peek().getPoint())), polygonEdges, neighborBefore, neighborAfter)) {
                addedEdges.add(new Edge(segment));
                lastPopped = verticesStack.pop();
            }

            verticesStack.addAll(Arrays.asList(lastPopped, vertex));
        }
    }

    private boolean isTriangle(Point first, Point second, Point third) {
        return !PointsOrientation.getDirection(new Vector<>(second, first), third).equals(ON);
    }

    private boolean isSegmentInsidePolygon(Segment segment, List<Edge> polygonEdges, Point before, Point next) {
        long intersections = polygonEdges.stream().
                filter(edge -> !segment.equals(edge.getSegment())).
                map(edge -> areIntersected(segment, edge.getSegment())).
                filter(intersection -> intersection).
                count();

        if (intersections != 0) {
            return false;
        }

        /* http://stackoverflow.com/a/695847 */
        double[] firstVector = new double[]{next.getX() - segment.getStart().getX(), next.getY() - segment.getStart().getY()};
        double[] secondVector = new double[]{before.getX() - segment.getStart().getX(), before.getY() - segment.getStart().getY()};
        double[] thirdVector = new double[]{segment.getEnd().getX() - segment.getStart().getX(), segment.getEnd().getY() - segment.getStart().getY()};

        double[] crossProducts = new double[]{
                firstVector[0] * secondVector[1] - firstVector[1] * secondVector[0],
                firstVector[0] * thirdVector[1] - firstVector[1] * thirdVector[0],
                thirdVector[0] * secondVector[1] - thirdVector[1] * secondVector[0]
        };

        return ((Arrays.stream(crossProducts).allMatch(value -> value >= 0)) ||
                (crossProducts[0] < 0 && !Arrays.stream(crossProducts).skip(1).allMatch(value -> value < 0)));
    }

    private boolean areIntersected(Segment currentSegment, Segment neighbor) {
        PointsOrientation.Direction neighborStartPointDirection = PointsOrientation.getDirection(
                new Vector<>(currentSegment.getEnd(), currentSegment.getStart()), neighbor.getStart());

        PointsOrientation.Direction neighborEndPointDirection = PointsOrientation.getDirection(
                new Vector<>(currentSegment.getEnd(), currentSegment.getStart()), neighbor.getEnd());

        PointsOrientation.Direction currentStartPointDirection = PointsOrientation.getDirection(
                new Vector<>(neighbor.getEnd(), neighbor.getStart()), currentSegment.getStart());

        PointsOrientation.Direction currentEndPointDirection = PointsOrientation.getDirection(
                new Vector<>(neighbor.getEnd(), neighbor.getStart()), currentSegment.getEnd());

        boolean isNeighborIntersection = (neighborStartPointDirection.equals(LEFT) && neighborEndPointDirection.equals(RIGHT)) ||
                (neighborStartPointDirection.equals(RIGHT) && neighborEndPointDirection.equals(LEFT));

        boolean isCurrentIntersection = (currentStartPointDirection.equals(LEFT) && currentEndPointDirection.equals(RIGHT)) ||
                (currentStartPointDirection.equals(RIGHT) && currentEndPointDirection.equals(LEFT));

        return isNeighborIntersection && isCurrentIntersection;
    }

    private boolean isPolygonClockwise(List<Vertex> polygon) {
        double sum = IntStream.range(0, polygon.size()).
                mapToDouble(index -> {
                    Vertex current = polygon.get(index);
                    Vertex next = polygon.get((index + 1) % polygon.size());

                    return (next.getPoint().getX() - current.getPoint().getX()) * (next.getPoint().getY() + current.getPoint().getY());
                }).sum();

        return sum > 0;
    }

}
