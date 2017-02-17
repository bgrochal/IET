package com.github.bgrochal.middleware.model;

import java.util.Locale;

/**
 * @author Bartłomiej Grochal
 */
public class Point {

    protected final double x;
    protected final double y;


    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        } else {
            Point castedObj = (Point) obj;
            return castedObj.x == x && castedObj.y == y;
        }
    }

    @Override
    public int hashCode() {
        return 31 * new Double(x).hashCode() + 37 * new Double(y).hashCode();
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }


    public String toExactString() {
        return String.format(Locale.US, "%.15f %.15f", x, y);
    }

    public double[] getAsVector() {
        return new double[]{x, y, 1};
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
