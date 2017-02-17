package com.github.bgrochal.middleware.algorithm.utils;


import com.github.bgrochal.middleware.model.Point;
import com.github.bgrochal.middleware.model.Vector;
import com.google.common.math.DoubleMath;

import java.util.Arrays;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;

/**
 * @author Bartłomiej Grochal
 */
public abstract class PointsOrientation {

    public static Direction getDirection(Vector vector, Point point) {
        double matrix[][] = {vector.getStart().getAsVector(), vector.getEnd().getAsVector(), point.getAsVector()};

        return Direction.getDirectionByComparisonResult(DoubleMath.fuzzyCompare(0,
                (matrix[0][0] * matrix[1][1] * matrix[2][2]) + (matrix[0][1] * matrix[1][2] * matrix[2][0]) +
                        (matrix[0][2] * matrix[1][0] * matrix[2][1]) - (matrix[0][2] * matrix[1][1] * matrix[2][0]) -
                        (matrix[0][0] * matrix[1][2] * matrix[2][1]) - (matrix[0][1] * matrix[1][0] * matrix[2][2]),
                EPSILON));
    }


    public enum Direction {
        LEFT(-1), ON(0), RIGHT(1);


        private int direction;

        Direction(int direction) {
            this.direction = direction;
        }

        public static Direction getDirectionByComparisonResult(int comparisonResult) {
            return Arrays.stream(Direction.values()).
                    filter(direction -> direction.direction == comparisonResult).
                    findFirst().
                    orElseThrow(IllegalArgumentException::new);
        }

        public int getDirection() {
            return direction;
        }
    }

}
