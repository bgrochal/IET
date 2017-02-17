package com.github.bgrochal.algorithm.sweep.model;

import com.google.common.math.DoubleMath;

import java.util.List;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;

/**
 * @author Bartłomiej Grochal
 */
public class Event implements Comparable<Event> {

    private final List<Segment> segments;
    private final EventType type;
    private final Point point;


    public Event(List<Segment> segments, EventType type, Point point) {
        this.type = type;

        this.segments = segments;
        this.point = point;
    }


    @Override
    public int compareTo(Event anotherEvent) {
        int comparatorResult = comparePoints(point, anotherEvent.getPoint());
        if (comparatorResult != 0) {
            return comparatorResult;
        }

        if (type.equals(anotherEvent.getType())) {
            if (type.equals(EventType.START)) {
                comparatorResult = comparePoints(anotherEvent.getSegments().get(0).getEnd(), segments.get(0).getEnd());
                if (comparatorResult != 0) {
                    return comparatorResult;
                }
            } else {
                comparatorResult = comparePoints(anotherEvent.getSegments().get(0).getStart(), segments.get(0).getStart());
                if (comparatorResult != 0) {
                    return comparatorResult;
                }
            }
        } else {
            return type.equals(EventType.START) ? -1 : type.equals(EventType.INTERSECTION) ? -1 : 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Event)) {
            return false;
        } else {
            Event castedObj = (Event) obj;
            return point.equals(castedObj.getPoint()) && type.equals(castedObj.getType()) &&
                    segments.equals(castedObj.segments);
        }
    }

    @Override
    public int hashCode() {
        return 31 * point.hashCode() + 37 * type.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(point).
                append(" of type ").
                append(type);

        return builder.toString();
    }


    public EventType getType() {
        return type;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public Point getPoint() {
        return point;
    }

    public String getSegmentsAsString() {
        StringBuilder builder = new StringBuilder();
        segments.forEach(segment -> builder.append(segment).append(" "));

        return builder.toString().substring(0, builder.toString().length() - 1);
    }


    private int comparePoints(Point firstPoint, Point secondPoint) {
        int comparatorResultOX = DoubleMath.fuzzyCompare(firstPoint.getX(), secondPoint.getX(), EPSILON);
        if (comparatorResultOX != 0) {
            return comparatorResultOX;
        }

        return DoubleMath.fuzzyCompare(firstPoint.getY(), secondPoint.getY(), EPSILON);
    }

}
