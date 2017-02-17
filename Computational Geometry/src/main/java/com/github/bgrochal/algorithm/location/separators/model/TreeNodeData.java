package com.github.bgrochal.algorithm.location.separators.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Bartłomiej Grochal
 */
public class TreeNodeData implements Comparable<TreeNodeData> {

    private final int separatorNumber;
    private final List<Segment> separator;


    public TreeNodeData(int separatorNumber, List<Point> separator) {
        this.separatorNumber = separatorNumber;
        this.separator = createSegmentsList(separator);
    }


    @Override
    public int compareTo(TreeNodeData another) {
        return separatorNumber < another.separatorNumber ? -1 :
                separatorNumber == another.separatorNumber ? 0 : 1;
    }

    @Override
    public String toString() {
        return separatorNumber + ": " + separator;
    }


    public void removeDuplicatedSegments(TreeNodeData anotherData) {
        for (Segment segment : anotherData.separator) {
            if (separator.contains(segment)) {
                separator.remove(segment);
            }
        }
    }

    public int getSeparatorNumber() {
        return separatorNumber;
    }

    public List<Segment> getSeparator() {
        return separator;
    }


    private List<Segment> createSegmentsList(List<Point> separator) {
        List<Segment> separatorWithSegments = new LinkedList<>();

        for (int index = 0; index < separator.size() - 1; index++) {
            separatorWithSegments.add(new Segment(separator.get(index), separator.get(index + 1)));
        }


        return separatorWithSegments;
    }

}
