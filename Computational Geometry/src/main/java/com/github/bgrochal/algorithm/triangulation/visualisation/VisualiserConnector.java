package com.github.bgrochal.algorithm.triangulation.visualisation;

import com.github.bgrochal.algorithm.triangulation.model.*;
import com.github.bgrochal.geomvisualiser.geogebra.api.CommandExecutor;
import com.github.bgrochal.geomvisualiser.model.Polygon;
import com.github.bgrochal.geomvisualiser.model.utils.Color;
import com.google.common.collect.ImmutableMap;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.bgrochal.algorithm.triangulation.model.VertexType.*;

/**
 * @author Bartłomiej Grochal
 */
public class VisualiserConnector {

    private final CommandExecutor executor;

    private final Map<VertexType, Color> verticesColors = ImmutableMap.of(
            START, new Color(0, 255, 0), END, new Color(255, 0, 0), MERGE, new Color(0, 0, 255),
            SPLIT, new Color(0, 0, 0), REGULAR, new Color(255, 255, 255));


    public VisualiserConnector() {
        executor = new CommandExecutor();
    }


    public Pair<List<Vertex>, List<Edge>> getVisualisedObjects() {
        List<Vertex> vertices = new LinkedList<>();
        List<Edge> edges = new LinkedList<>();

        executor.getAllSegments().forEach(visualisedSegment -> {
            Point startPoint = new Point(visualisedSegment.getStart().getX(), visualisedSegment.getStart().getY());
            Point endPoint = new Point(visualisedSegment.getEnd().getX(), visualisedSegment.getEnd().getY());
            Segment segment = new Segment(startPoint, endPoint);

            vertices.add(new Vertex(startPoint));
            edges.add(new Edge(segment));
        });

        return new Pair<>(vertices, edges);
    }

    public void visualisePolygon(List<Vertex> vertices) {
        List<com.github.bgrochal.geomvisualiser.model.Point> pointsList = new LinkedList<>();

        vertices.stream().map(Vertex::getPoint).forEach(point -> pointsList.add(
                new com.github.bgrochal.geomvisualiser.model.Point(point.getX(), point.getY())
        ));


        executor.plotObject(new Polygon(pointsList));
        executor.plotMultipleObjects(pointsList);


        Arrays.stream(VertexType.values()).forEach(vertexType -> {
            executor.plotMultipleObjects(
                    vertices.stream().
                            filter(vertex -> vertex.getType().equals(vertexType)).
                            map(vertex ->
                                    new com.github.bgrochal.geomvisualiser.model.Point(
                                            verticesColors.get(vertexType),
                                            vertex.getPoint().getX(), vertex.getPoint().getY())).
                            collect(Collectors.toList()));
        });

//        List<com.github.bgrochal.geomvisualiser.model.Segment> sidesList = new LinkedList<>();
//        IntStream.range(0, pointsList.size()).forEach(i -> sidesList.add(
//                new com.github.bgrochal.geomvisualiser.model.Segment(pointsList.get(i), pointsList.get((i + 1) % pointsList.size()))
//        ));
//
//        executor.plotMultipleObjects(pointsList);
//        executor.plotMultipleObjects(sidesList);
    }

    public void visualiseSegments(List<Edge> edges) {
        List<com.github.bgrochal.geomvisualiser.model.Segment> segmentsList = new LinkedList<>();

        edges.stream().map(Edge::getSegment).forEach(segment -> segmentsList.add(
                new com.github.bgrochal.geomvisualiser.model.Segment(
                        new Color(255, 0, 255),
                        new com.github.bgrochal.geomvisualiser.model.Point(segment.getStart().getX(), segment.getStart().getY()),
                        new com.github.bgrochal.geomvisualiser.model.Point(segment.getEnd().getX(), segment.getEnd().getY()))
        ));

        executor.plotMultipleObjects(segmentsList);
    }

}
