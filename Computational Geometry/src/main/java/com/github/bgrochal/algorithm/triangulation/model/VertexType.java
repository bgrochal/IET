package com.github.bgrochal.algorithm.triangulation.model;

import com.github.bgrochal.middleware.algorithm.utils.PointsOrientation;
import com.github.bgrochal.middleware.model.Vector;
import com.google.common.math.DoubleMath;

import static com.github.bgrochal.middleware.algorithm.utils.CommonUtils.EPSILON;

/**
 * @author Bartłomiej Grochal
 */
public enum VertexType {

    START, END, SPLIT, MERGE, REGULAR;


    public static VertexType getVertexType(Vertex before, Vertex current, Vertex after) {
        PointsOrientation.Direction direction = PointsOrientation.getDirection(
                new Vector<>(current.getPoint(), before.getPoint()), after.getPoint());

        VertexLocation locationBefore = VertexLocation.getVertexLocation(current, before);
        VertexLocation locationNext = VertexLocation.getVertexLocation(current, after);

        if (locationBefore.equals(VertexLocation.ABOVE) && locationNext.equals(VertexLocation.ABOVE)) {
            return direction == PointsOrientation.Direction.LEFT ? END : MERGE;
        }
        if (locationBefore.equals(VertexLocation.BELOW) && locationNext.equals(VertexLocation.BELOW)) {
            return direction == PointsOrientation.Direction.LEFT ? START : SPLIT;
        }

        return REGULAR;
    }


    private enum VertexLocation {
        ABOVE, BELOW;


        public static VertexLocation getVertexLocation(Vertex referenceVertex, Vertex testedVertex) {
            // TODO: Theoretically it is not possible to get 0 as a result of the comparison below.
            return DoubleMath.fuzzyCompare(referenceVertex.getPoint().getY(), testedVertex.getPoint().getY(), EPSILON) < 0 ?
                    ABOVE : BELOW;
        }
    }

}
