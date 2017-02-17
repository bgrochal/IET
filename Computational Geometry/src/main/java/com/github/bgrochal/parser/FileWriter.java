package com.github.bgrochal.parser;

import com.github.bgrochal.middleware.model.Point;
import com.github.bgrochal.middleware.model.Segment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Bartłomiej Grochal
 */
public class FileWriter extends FileHandler {

    public FileWriter(String path) {
        super(path);
    }


    public void savePoints(List<? extends Point> points) {
        List<String> dataToSave = new LinkedList<>();
        dataToSave.add(String.valueOf(points.size()));
        dataToSave.addAll(points.
                stream().
                map(Point::toExactString).
                collect(Collectors.toList()));
        save(dataToSave);
    }

    public void saveSegments(List<? extends Segment> segments) {
        List<String> dataToSave = new LinkedList<>();
        dataToSave.addAll(segments.
                stream().
                map(Segment::toExactString).
                collect(Collectors.toList()));
        save(dataToSave);
    }

    public void savePolygonVertices(List<? extends Point> vertices) {
        savePoints(vertices);
    }


    private void save(List<String> dataToSave) {
        try {
            Files.write(Paths.get(path), dataToSave);
        } catch (IOException exc) {
            Logger.getAnonymousLogger().warning("Unable to write to a file: " + path + ".");
        }

    }

}
