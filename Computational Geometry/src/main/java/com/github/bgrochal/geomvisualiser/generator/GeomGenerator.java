package com.github.bgrochal.geomvisualiser.generator;

import com.github.bgrochal.geomvisualiser.model.*;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;

/**
 * @author Bartłomiej Grochal
 */
public class GeomGenerator {

    private Random generator;


    public GeomGenerator() {
        this.generator = new Random();
    }


    public Pair<List<Line>, List<Point>> generateLines(int number, double xMin, double xMax, double yMin, double yMax) {
        List<Line> lines = new LinkedList<>();
        List<Point> points = new LinkedList<>();

        for (int i = 0; i < number; i++) {
            Point first = generatePoint(xMin, xMax, yMin, yMax);
            Point second = generatePoint(xMin, xMax, yMin, yMax);

            lines.add(new Line(first, second));
            points.addAll(asList(first, second));
        }

        return new Pair<>(lines, points);
    }

    public List<Point> generatePoints(int number, double xMin, double xMax, double yMin, double yMax) {
        List<Point> list = new LinkedList<>();

        for (int i = 0; i < number; i++) {
            list.add(generatePoint(xMin, xMax, yMin, yMax));
        }

        return list;
    }

    public Pair<List<Polygon>, List<Point>> generatePolygons(int number, int vertices, double xMin, double xMax, double yMin, double yMax) {
        List<Polygon> polygons = new LinkedList<>();
        List<Point> points = new LinkedList<>();

        for (int i = 0; i < number; i++) {
            List<Point> verts = new LinkedList<>();

            for (int j = 0; j < vertices; j++) {
                verts.add(generatePoint(xMin, xMax, yMin, yMax));
            }

            points.addAll(verts);
            polygons.add(new Polygon(verts));
        }

        return new Pair<>(polygons, points);
    }

    public Pair<List<Ray>, List<Point>> generateRays(int number, double xMin, double xMax, double yMin, double yMax) {
        List<Ray> rays = new LinkedList<>();
        List<Point> points = new LinkedList<>();

        for (int i = 0; i < number; i++) {
            Point first = generatePoint(xMin, xMax, yMin, yMax);
            Point second = generatePoint(xMin, xMax, yMin, yMax);

            rays.add(new Ray(first, second));
            points.addAll(asList(first, second));
        }

        return new Pair<>(rays, points);
    }

    public Pair<List<Segment>, List<Point>> generateSegments(int number, double xMin, double xMax, double yMin, double yMax) {
        List<Segment> segments = new LinkedList<>();
        List<Point> points = new LinkedList<>();

        for (int i = 0; i < number; i++) {
            Point first = generatePoint(xMin, xMax, yMin, yMax);
            Point second = generatePoint(xMin, xMax, yMin, yMax);

            segments.add(new Segment(first, second));
            points.addAll(asList(first, second));
        }

        return new Pair<>(segments, points);
    }


    private Point generatePoint(double xMin, double xMax, double yMin, double yMax) {
        return new Point(generateDouble(xMin, xMax), generateDouble(yMin, yMax));
    }

    private double generateDouble(double min, double max) {
        return min + (max - min) * generator.nextDouble();
    }

}
