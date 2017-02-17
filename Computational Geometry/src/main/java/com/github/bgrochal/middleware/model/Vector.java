package com.github.bgrochal.middleware.model;

/**
 * @author Bartłomiej Grochal
 */
public class Vector<E extends Point> {

    private final E start;
    private final E end;


    public Vector(E end, E start) {
        this.end = end;
        this.start = start;
    }


    public E getStart() {
        return start;
    }

    public E getEnd() {
        return end;
    }

}
