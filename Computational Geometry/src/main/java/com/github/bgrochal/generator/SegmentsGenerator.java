package com.github.bgrochal.generator;

import com.github.bgrochal.middleware.model.Point;
import com.github.bgrochal.middleware.model.Segment;
import com.github.bgrochal.parser.FileWriter;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author Bartłomiej Grochal
 */
public class SegmentsGenerator {

    private final PointsGenerator pointsGenerator;
    private final Random random;


    public SegmentsGenerator() {
        pointsGenerator = new PointsGenerator();
        random = new Random();
    }


    public List<Segment> randomInDoubleRange(double minX, double maxX, double minY, double maxY, double minOffsetX,
                                             double maxOffsetX, double minOffsetY, double maxOffsetY, int number) {
        List<Segment> list = new LinkedList<>();

        IntStream.rangeClosed(1, number).forEach(value -> {
            Point start = pointsGenerator.randomInRange(minX, maxX, minY, maxY, 1).get(0);
            Point end = pointsGenerator.randomInRange(start.getX() + minOffsetX, start.getX() + maxOffsetX,
                    start.getY() - minOffsetY, start.getY() + maxOffsetY, 1).get(0);

            list.add(new Segment(start, end));
        });

        return list;
    }


    public static void main(String[] args) {
        SegmentsGenerator generator = new SegmentsGenerator();
        new FileWriter("segments.txt").saveSegments(generator.randomInDoubleRange(-5, 5, -5, 5, 1, 5, 5, 5, 10));
    }

}
