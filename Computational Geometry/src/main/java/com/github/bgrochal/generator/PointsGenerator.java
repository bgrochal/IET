package com.github.bgrochal.generator;

import com.github.bgrochal.middleware.model.Point;
import com.github.bgrochal.parser.FileWriter;
import com.google.common.math.DoubleMath;

import java.util.*;
import java.util.stream.IntStream;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;
import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.getDistance;


/**
 * @author Bartłomiej Grochal
 */
public class PointsGenerator {

    private final Random random;


    public PointsGenerator() {
        random = new Random();
    }


    public List<Point> randomInRange(double minX, double maxX, double minY, double maxY, int number) {
        List<Point> list = new LinkedList<>();

        IntStream.rangeClosed(1, number).forEach(value ->
                list.add(new Point(minX + (maxX - minX) * random.nextDouble(), minY + (maxY - minY) * random.nextDouble()))
        );

        return list;
    }

    public List<Point> randomOnCircle(Point center, double radius, int number) {
        List<Point> list = new LinkedList<>();

        IntStream.rangeClosed(1, number).forEach(value -> {
            double angle = 2 * Math.PI * random.nextDouble();
            list.add(new Point(center.getX() + radius * Math.cos(angle), center.getY() + radius * Math.sin(angle)));
        });

        return list;
    }

    public List<Point> randomOnRectangle(Point bottomLeft, Point bottomRight, Point topRight, Point topLeft, int number) {
        // TODO: Validate correctness of arguments.
        List<Point> list = new LinkedList<>();

        double horizontalSide = getDistance(bottomLeft, bottomRight);
        double verticalSide = getDistance(bottomLeft, topLeft);
        double perimeter = 2 * (horizontalSide + verticalSide);

        IntStream.rangeClosed(1, number).forEach(value -> {
            double perimeterRandom = perimeter * random.nextDouble();
            Point point;
            if (perimeterRandom <= horizontalSide) {
                point = new Point(bottomLeft.getX() + perimeterRandom, bottomLeft.getY());
            } else if (perimeterRandom <= horizontalSide + verticalSide) {
                point = new Point(bottomRight.getX(), bottomRight.getY() + (perimeterRandom - horizontalSide));
            } else if (perimeterRandom <= 2 * horizontalSide + verticalSide) {
                point = new Point(topRight.getX() - (perimeterRandom - horizontalSide - verticalSide), topRight.getY());
            } else {
                point = new Point(bottomLeft.getX(), bottomLeft.getY() + (perimeter - perimeterRandom));
            }

            list.add(point);
        });

        return list;
    }

    public List<Point> randomOnSquareLikeSet(Point bottomLeft, Point bottomRight, Point topRight, Point topLeft, int numberOnSide, int numberOnDiagonal) {
        // TODO: Validate correctness of arguments.
        List<Point> list = new LinkedList<>();
        double side = getDistance(bottomLeft, bottomRight);

        list.addAll(Arrays.asList(bottomLeft, bottomRight, topRight, topLeft));

        IntStream.rangeClosed(1, numberOnSide).forEach(value -> {
            double horizontalSide = side * random.nextDouble();
            double verticalSide = side * random.nextDouble();

            list.add(new Point(bottomLeft.getX() + horizontalSide, bottomLeft.getY()));
            list.add(new Point(bottomLeft.getX(), bottomLeft.getY() + verticalSide));
        });

        IntStream.rangeClosed(1, numberOnDiagonal).forEach(value -> {
            double positiveDiagonal = side * random.nextDouble();
            double negativeDiagonal = side * random.nextDouble();

            list.add(new Point(bottomLeft.getX() + positiveDiagonal, bottomLeft.getY() + positiveDiagonal));
            list.add(new Point(topLeft.getX() + negativeDiagonal, topLeft.getY() - negativeDiagonal));
        });
        // return list;

        Set<Point> sortedPoints = new TreeSet<>((first, second) -> {
            int comparatorY = DoubleMath.fuzzyCompare(first.getY(), second.getY(), EPSILON);
            return comparatorY == 0 ? DoubleMath.fuzzyCompare(first.getX(), second.getX(), EPSILON) : comparatorY;
        });
        sortedPoints.addAll(list);
        return new LinkedList<>(sortedPoints);
    }


    public static void main(String[] args) {
        PointsGenerator generator = new PointsGenerator();
        new FileWriter("3A.txt").savePoints(generator.randomInRange(-100, 100, -100, 100, 10000));
        new FileWriter("3B.txt").savePoints(generator.randomOnCircle(new Point(0, 0), 100, 2000));
        new FileWriter("3C.txt").savePoints(generator.randomOnRectangle(
                new Point(-10, -10), new Point(10, -10), new Point(10, 10), new Point(-10, 10), 10000));
        new FileWriter("3D.txt").savePoints(generator.randomOnSquareLikeSet(
                new Point(0, 0), new Point(10, 0), new Point(10, 10), new Point(0, 10), 2500, 2000));
    }

}
