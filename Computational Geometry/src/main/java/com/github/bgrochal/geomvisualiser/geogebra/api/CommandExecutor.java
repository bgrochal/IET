package com.github.bgrochal.geomvisualiser.geogebra.api;

import com.github.bgrochal.geomvisualiser.geogebra.panel.GGPanelFactory;
import com.github.bgrochal.geomvisualiser.model.PlotableModel;
import com.github.bgrochal.geomvisualiser.model.Point;
import com.github.bgrochal.geomvisualiser.model.Segment;
import org.geogebra.common.plugin.GgbAPI;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.github.bgrochal.geomvisualiser.model.utils.ModelUtils.isInvisibleLabel;

/**
 * @author Bartłomiej Grochal
 */
public class CommandExecutor {

    private final GgbAPI geogebraApi;


    public CommandExecutor() {
        geogebraApi = GGPanelFactory.getInstance().getGeoGebraAPI();
    }


    public void plotObject(PlotableModel model) {
        execute(model.getPlotCommand());

        if (model instanceof Point) {
            setPointSize((Point) model);
        }
        if (model instanceof Segment) {
            setSegmentWidth((Segment) model);
        }

        hideLabel(model);
        setColor(model);
    }

    public void plotMultipleObjects(List<? extends PlotableModel> models) {
        models.forEach(this::plotObject);
    }

    public void clearPanel() {
        Arrays.stream(geogebraApi.getAllObjectNames()).forEach(geogebraApi::deleteObject);
    }

    public List<Point> getAllPoints() {
        List<Point> points = new LinkedList<>();

        Arrays.stream(geogebraApi.getAllObjectNames())
                .filter(objectName -> geogebraApi.getObjectType(objectName).equals("point"))
                .forEach(pointName -> points.add(
                        new Point(geogebraApi.getXcoord(pointName), geogebraApi.getYcoord(pointName))));

        return points;
    }

    public List<Segment> getAllSegments() {
        List<Segment> segments = new LinkedList<>();

        Arrays.stream(geogebraApi.getAllObjectNames()).
                filter(objectName -> geogebraApi.getObjectType(objectName).equals("segment")).
                forEach(segmentName -> {
                    execute("first: Vertex[" + segmentName + "," + 1 + "]");
                    execute("second: Vertex[" + segmentName + "," + 2 + "]");

                    Point start = new Point(geogebraApi.getXcoord("first"), geogebraApi.getYcoord("first"));
                    Point end = new Point(geogebraApi.getXcoord("second"), geogebraApi.getYcoord("second"));

                    segments.add(new Segment(start, end));
                });

        geogebraApi.deleteObject("first");
        geogebraApi.deleteObject("second");

        return segments;
    }


    private void execute(String command) {
        geogebraApi.evalCommand(command);
    }

    private void hideLabel(PlotableModel model) {
        if (isInvisibleLabel(model.getLabel())) {
            geogebraApi.setLabelVisible(model.getLabel(), false);
        }
    }

    private void setColor(PlotableModel model) {
        geogebraApi.setColor(model.getLabel(), model.getColor().getRed(), model.getColor().getGreen(),
                model.getColor().getBlue());
    }

    private void setPointSize(Point point) {
        geogebraApi.setPointSize(point.getLabel(), point.getSize());
    }

    private void setSegmentWidth(Segment segment) {
        geogebraApi.setLineThickness(segment.getLabel(), 5);
    }

}
