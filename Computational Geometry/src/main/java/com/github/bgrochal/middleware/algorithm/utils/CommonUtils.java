package com.github.bgrochal.middleware.algorithm.utils;

import com.github.bgrochal.middleware.model.Point;

/**
 * @author Bartłomiej Grochal
 */
public abstract class CommonUtils {

    public static final double EPSILON = 1e-13;

    public static double getDistance(Point first, Point second) {
        return Math.sqrt(Math.pow(first.getX() - second.getX(), 2) + Math.pow(first.getY() - second.getY(), 2));
    }

    public static double calculateSine(Point first, Point second) {
        double sideOY = second.getY() - first.getY();
        double hypotenuse = Math.sqrt(Math.pow(second.getX() - first.getX(), 2) + Math.pow(second.getY() - first.getY(), 2));

        return sideOY / hypotenuse;

    }

}
