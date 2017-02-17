package com.github.bgrochal.middleware.model;

/**
 * @author Bartłomiej Grochal
 */
public class Segment<E extends Point> {

    private final E start;
    private final E end;


    public Segment(E start, E end) {
        this.start = start;
        this.end = end;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Segment)) {
            return false;
        } else {
            Segment castedObj = (Segment) obj;
            return (start.equals(castedObj.getStart()) && end.equals(castedObj.getEnd())) ||
                    (start.equals(castedObj.getEnd()) && end.equals(castedObj.getStart()));
        }
    }

    @Override
    public int hashCode() {
        return 31 * start.hashCode() + 37 * end.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[").
                append(start).
                append(", ").
                append(end).
                append("]");

        return builder.toString();
    }


    public String toExactString() {
        StringBuilder builder = new StringBuilder();
        builder.append(start.toExactString()).
                append(" ").
                append(end.toExactString());

        return builder.toString();
    }

    public E getStart() {
        return start;
    }

    public E getEnd() {
        return end;
    }

}
