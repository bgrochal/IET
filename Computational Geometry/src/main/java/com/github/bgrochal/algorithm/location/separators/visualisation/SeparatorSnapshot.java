package com.github.bgrochal.algorithm.location.separators.visualisation;

import com.github.bgrochal.algorithm.location.separators.model.Point;

import java.util.List;

/**
 * @author Bartłomiej Grochal
 */
public class SeparatorSnapshot implements Snapshot {

    private final List<Point> separator;


    public SeparatorSnapshot(List<Point> separator) {
        this.separator = separator;
    }


    public List<Point> getSeparator() {
        return separator;
    }

}
