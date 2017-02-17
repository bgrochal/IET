package com.github.bgrochal.parser;

import com.github.bgrochal.middleware.model.Point;
import com.github.bgrochal.middleware.model.Segment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Bartłomiej Grochal
 */
public class FileParser extends FileHandler {

    public FileParser(String path) {
        super(path);
    }


    public List<Point> parsePoints() {
        List<Point> points = new LinkedList<>();
        int linesNumber = -1;

        try {
            try {
                linesNumber = Integer.parseInt(Files.lines(Paths.get(path), StandardCharsets.UTF_8).
                        limit(1).
                        findFirst().
                        orElseThrow(() -> new IllegalArgumentException("File: " + path + " does not contain number of points.")));
            } catch (NumberFormatException exc) {
                throw new IllegalArgumentException("Illegal data in file: " + path + ".", exc);
            }

            Files.lines(Paths.get(path), StandardCharsets.UTF_8).skip(1).forEach(line -> {
                String[] arguments = line.trim().split("\\s+");
                assert arguments.length != 2;

                try {
                    points.add(new Point(Double.parseDouble(arguments[0]), Double.parseDouble(arguments[1])));
                } catch (NumberFormatException exc) {
                    throw new IllegalArgumentException("Illegal data in file: " + path + ".", exc);
                }
            });
        } catch (IOException exc) {
            Logger.getAnonymousLogger().warning("Unable to read from a file: " + path + ".");
        }

        assert linesNumber == points.size();
        return points;
    }

    public List<Segment> parseSegments() {
        List<Segment> segments = new LinkedList<>();

        try {
            Files.lines(Paths.get(path), StandardCharsets.UTF_8).forEach(line -> {
                String[] arguments = line.trim().split("\\s+");
                assert arguments.length != 4;

                try {
                    segments.add(new Segment(
                            new Point(Double.parseDouble(arguments[0]), Double.parseDouble(arguments[1])),
                            new Point(Double.parseDouble(arguments[2]), Double.parseDouble(arguments[3]))));
                } catch (NumberFormatException exc) {
                    throw new IllegalArgumentException("Illegal data in file: " + path + ".", exc);
                }
            });
        } catch (IOException exc) {
            Logger.getAnonymousLogger().warning("Unable to read from a file: " + path + ".");
        }

        return segments;
    }

    public List<Point> parsePolygonVertices() {
        return parsePoints();
    }

}
